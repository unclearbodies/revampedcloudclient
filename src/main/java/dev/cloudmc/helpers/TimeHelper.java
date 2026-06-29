/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.helpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeHelper {

    private static final DateTimeFormatter dtfTimeMinute = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dtfTimeSecond = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("uuuu/MM/dd");

    /**
     * Returns the current Time in minutes
     */

    public static String getFormattedTimeMinute() {
        LocalTime localTime = LocalTime.now();
        return dtfTimeMinute.format(localTime);
    }

    /**
     * Returns the current Time in minutes and seconds
     */

    public static String getFormattedTimeSecond() {
        LocalTime localTime = LocalTime.now();
        return dtfTimeSecond.format(localTime);
    }

    /**
     * Returns the current Date in years, months and days
     */

    public static String getFormattedDate() {
        LocalDate localDate = LocalDate.now();
        return dtfDate.format(localDate);
    }
}
