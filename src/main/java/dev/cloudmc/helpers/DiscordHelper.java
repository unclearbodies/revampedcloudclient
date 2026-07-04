/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.helpers;

import com.google.gson.JsonObject;
import dev.cloudmc.Cloud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Pure-Java Discord Rich Presence via IPC pipe.
 * Communicates directly with the Discord client over a Windows named pipe,
 * avoiding any native library dependencies (no JNA/JNI).
 */
public class DiscordHelper {

    private static final String APPLICATION_ID = "1522860574453534761";
    private static final org.apache.logging.log4j.Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger("CloudDiscordRPC");

    // IPC opcodes
    private static final int OP_HANDSHAKE = 0;
    private static final int OP_FRAME = 1;
    private static final int OP_CLOSE = 2;

    private static volatile RandomAccessFile pipe;
    private static volatile long startTimestamp;
    private static volatile boolean connected = false;
    private static volatile boolean shouldRun = false;
    private static Thread workerThread;

    /**
     * Connects to Discord's IPC pipe and sends a handshake.
     * Manages reconnecting and draining the read buffer.
     */
    public static void start() {
        if (shouldRun) return;
        shouldRun = true;
        startTimestamp = System.currentTimeMillis() / 1000;

        workerThread = new Thread(() -> {
            while (shouldRun) {
                try {
                    pipe = connectPipe();
                    if (pipe == null) {
                        Thread.sleep(10000); // 10s backoff
                        continue;
                    }

                    // Send handshake
                    JsonObject handshake = new JsonObject();
                    handshake.addProperty("v", 1);
                    handshake.addProperty("client_id", APPLICATION_ID);
                    sendFrame(OP_HANDSHAKE, handshake.toString());

                    // Read handshake response and validate
                    String response = readFrame();
                    if (response == null || response.contains("\"code\":")) {
                        LOGGER.warn("Discord RPC handshake failed or returned an error.");
                        cleanup();
                        Thread.sleep(10000);
                        continue;
                    }

                    connected = true;
                    LOGGER.info("Discord RPC connected");

                    // Send initial presence
                    update();

                    // Continuous reader loop to drain pipe
                    while (connected && shouldRun) {
                        String frame = readFrame();
                        if (frame == null) break;
                    }

                } catch (Exception e) {
                    // Pipe broken or disconnected
                    LOGGER.debug("Discord RPC disconnected. Will retry.");
                }
                
                cleanup();

                if (shouldRun) {
                    try {
                        Thread.sleep(10000); // Wait before attempting reconnect
                    } catch (InterruptedException ignored) {}
                }
            }
        }, "Cloud-Discord-RPC");
        workerThread.start();
    }

    /**
     * Updates the Discord Rich Presence with current game state.
     */
    public static void update() {
        if (!connected || pipe == null) return;

        try {
            JsonObject activity = new JsonObject();
            
            Minecraft mc = Minecraft.getMinecraft();
            boolean isMultiplayer = mc != null && !mc.isSingleplayer() && mc.getCurrentServerData() != null;
            
            dev.cloudmc.feature.mod.impl.SessionStatsMod.GameMode mode = dev.cloudmc.feature.mod.impl.SessionStatsMod.getCurrentMode();
            if (isMultiplayer && mode != dev.cloudmc.feature.mod.impl.SessionStatsMod.GameMode.UNKNOWN) {
                activity.addProperty("details", "Playing " + mode.display);
            } else {
                activity.addProperty("details", "Playing Minecraft 1.8.9");
            }
            
            activity.addProperty("state", getCurrentState());

            JsonObject timestamps = new JsonObject();
            timestamps.addProperty("start", startTimestamp);
            activity.add("timestamps", timestamps);

            JsonObject largeImage = new JsonObject();
            largeImage.addProperty("large_image", "cloud_logo");
            largeImage.addProperty("large_text", Cloud.modName + " " + Cloud.modVersion);
            activity.add("assets", largeImage);

            JsonObject args = new JsonObject();
            args.addProperty("pid", getProcessId());
            args.add("activity", activity);

            JsonObject payload = new JsonObject();
            payload.addProperty("cmd", "SET_ACTIVITY");
            payload.add("args", args);
            payload.addProperty("nonce", UUID.randomUUID().toString());

            sendFrame(OP_FRAME, payload.toString());

        } catch (Exception e) {
            LOGGER.error("Failed to update Discord presence", e);
            cleanup();
        }
    }

    /**
     * Disconnects from Discord's IPC pipe and stops the worker thread.
     */
    public static void stop() {
        shouldRun = false;
        
        if (!connected && pipe == null) return;

        try {
            if (pipe != null) {
                // Send close frame
                try {
                    sendFrame(OP_CLOSE, "{}");
                } catch (Exception ignored) {
                }
                pipe.close();
            }
        } catch (Exception e) {
            LOGGER.error("Error closing Discord RPC pipe", e);
        } finally {
            cleanup();
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

    /**
     * Attempts to connect to Discord's named pipe (tries pipe indices 0-9).
     */
    private static RandomAccessFile connectPipe() {
        for (int i = 0; i < 10; i++) {
            try {
                return new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + i, "rw");
            } catch (FileNotFoundException e) {
                // Try next pipe index
            }
        }
        return null;
    }

    /**
     * Sends a framed message to the Discord IPC pipe.
     * Frame format: [opcode: uint32 LE][length: uint32 LE][data: UTF-8]
     */
    private static void sendFrame(int opcode, String data) throws IOException {
        byte[] dataBytes = data.getBytes("UTF-8");
        ByteBuffer buffer = ByteBuffer.allocate(8 + dataBytes.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(opcode);
        buffer.putInt(dataBytes.length);
        buffer.put(dataBytes);

        pipe.write(buffer.array());
    }

    /**
     * Reads a single frame from the Discord IPC pipe.
     */
    private static String readFrame() throws IOException {
        byte[] header = new byte[8];
        pipe.readFully(header);

        ByteBuffer headerBuf = ByteBuffer.wrap(header);
        headerBuf.order(ByteOrder.LITTLE_ENDIAN);
        int opcode = headerBuf.getInt();
        int length = headerBuf.getInt();

        byte[] data = new byte[length];
        pipe.readFully(data);

        return new String(data, "UTF-8");
    }

    private static void cleanup() {
        connected = false;
        pipe = null;
    }

    /**
     * Gets the current JVM process ID for the SET_ACTIVITY command.
     */
    private static int getProcessId() {
        try {
            // Java 8 compatible: parse PID from ManagementFactory
            String jvmName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            return Integer.parseInt(jvmName.split("@")[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Determines the current game state string for the presence.
     */
    private static String getCurrentState() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return "Main Menu";
        }

        if (mc.isSingleplayer()) {
            return "Singleplayer";
        }

        ServerData serverData = mc.getCurrentServerData();
        if (serverData == null) {
            return "Multiplayer";
        }

        dev.cloudmc.feature.mod.impl.SessionStatsMod.GameMode mode = dev.cloudmc.feature.mod.impl.SessionStatsMod.getCurrentMode();

        if (mode == dev.cloudmc.feature.mod.impl.SessionStatsMod.GameMode.UNKNOWN) {
            return "Lobby (" + serverData.serverIP + ")";
        }

        // Modes where K/D/W actually makes sense
        switch (mode) {
            case BEDWARS:
            case SKYWARS:
                int k = dev.cloudmc.feature.mod.impl.SessionStatsMod.kills;
                int d = dev.cloudmc.feature.mod.impl.SessionStatsMod.deaths;
                int w = dev.cloudmc.feature.mod.impl.SessionStatsMod.wins;
                return mode.display + " | K: " + k + " D: " + d + " W: " + w;
            case DUELS_BRIDGE:
            case DUELS_COMBO:
            case DUELS_POT:
            case DUELS_SUMO:
            case DUELS_GENERIC:
                return "Playing " + mode.display;
            default:
                return mode.display;
        }
    }
}
