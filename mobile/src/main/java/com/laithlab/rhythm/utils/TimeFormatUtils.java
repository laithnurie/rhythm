package com.laithlab.rhythm.utils;

public class TimeFormatUtils {

    public static String secondsToTimer(long totalSeconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (totalSeconds / (60 * 60));
        int minutes = (int) (totalSeconds % (60 * 60)) / (60);
        int seconds = (int) ((totalSeconds % (60 * 60)) % (60));
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
