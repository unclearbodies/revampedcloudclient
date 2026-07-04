/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.gui;

import java.awt.*;

public class Style {

    private static boolean darkMode = false;
    private static boolean snapping = true;

    private static final Color[] blackColors = new Color[256];
    private static final Color[] whiteColors = new Color[256];

    static {
        for (int i = 0; i < 256; i++) {
            blackColors[i] = new Color(0, 0, 0, i);
            whiteColors[i] = new Color(255, 255, 255, i);
        }
    }

    /**
     * Returns a color with the given transparency depending on if dark mode is active
     *
     * @param transparency The transparency the returned color should have
     * @return The black or white color which is returned
     */

    public static Color getColor(int transparency) {
        int t = Math.max(0, Math.min(255, transparency));
        return isDarkMode() ? blackColors[t] : whiteColors[t];
    }

    /**
     * Returns a color with the given transparency depending on if dark mode is active but reversed
     *
     * @param transparency The transparency the returned color should have
     * @return The black or white color which is returned
     */

    public static Color getReverseColor(int transparency) {
        int t = Math.max(0, Math.min(255, transparency));
        return Style.isDarkMode() ? whiteColors[t] : blackColors[t];
    }

    /**
     * Returns a boolean which says if the client should be in dark or light mode
     *
     * @return Boolean stating if client should be dark or light
     */
    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean darkMode) {
        Style.darkMode = darkMode;
    }

    public static boolean isSnapping() {
        return snapping;
    }

    public static void setSnapping(boolean snapping) {
        Style.snapping = snapping;
    }
}
