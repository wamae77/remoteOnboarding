package com.cll.FingerPrintModule.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {



    public static void logException(String tag, Exception e, File logFILE) {
        e.printStackTrace();
        addToLog(tag, Log.getStackTraceString(e), logFILE);
    }

    /**
     * Adding message to the Log file
     *
     * @param tag     tag
     * @param message message
     */
    public static void addToLog(String tag, String message, File logFile) {


        try {

            Log.d(tag,message);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss_SSS", Locale.US);
            String currentDateandTime = sdf.format(new Date());

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

            buf.write(currentDateandTime + ": " + message);
            //buf.append(message);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

