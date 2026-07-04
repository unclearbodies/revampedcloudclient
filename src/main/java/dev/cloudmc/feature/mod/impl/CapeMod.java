package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.minecraft.util.ResourceLocation;

public class CapeMod extends Mod {
    public CapeMod() {
        super("Cape", "Shows the CloudClient cape.", Type.Visual);

        Cloud.INSTANCE.settingManager.addSetting(new Setting("Import Cape", this, () -> {
            new Thread(() -> {
                SwingUtilities.invokeLater(() -> {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Select a Cape PNG");
                        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
                        
                        int result = fileChooser.showOpenDialog(null);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            File destDir = new File(Cloud.INSTANCE.mc.mcDataDir, "cloudmc");
                            if (!destDir.exists()) destDir.mkdirs();
                            
                            File destFile = new File(destDir, "cape.png");
                            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            loadCustomCape();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }).start();
        }));
        
        loadCustomCape();
    }

    @Override
    public boolean hasKeybind() {
        return false;
    }
    
    public static ResourceLocation capeLocation = new ResourceLocation("cloudmc/cape.png");
    
    public static void loadCustomCape() {
        try {
            File capeFile = new File(Cloud.INSTANCE.mc.mcDataDir, "cloudmc/cape.png");
            if (capeFile.exists()) {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(capeFile);
                if (img != null) {
                    net.minecraft.client.renderer.texture.DynamicTexture dt = new net.minecraft.client.renderer.texture.DynamicTexture(img);
                    capeLocation = Cloud.INSTANCE.mc.getTextureManager().getDynamicTextureLocation("custom_cape", dt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
