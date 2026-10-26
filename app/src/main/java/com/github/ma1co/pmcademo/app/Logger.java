package com.github.ma1co.pmcademo.app;

import android.os.Environment;

import java.io.*;

public class Logger {
    /** Set true for diagnostic builds. Normal releases keep only IMAGE/WARN/ERROR. */
    private static final boolean WRITE_DEBUG = false;
    private static final boolean WRITE_INFO = true;

    public static File getFile() {
        return new File(Environment.getExternalStorageDirectory(), "LLEGACY/LOG.TXT");
    }

    protected static void log(String msg) {
        try {
            getFile().getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(), true));
            writer.append(msg);
            writer.newLine();
            writer.close();
        } catch (IOException e) {}
    }
    protected static void log(String type, String msg) { log("[" + type + "] " + msg); }

    public static void debug(String msg) { if (WRITE_DEBUG) log("DEBUG", msg); }
    public static void info(String msg) { if (WRITE_INFO) log("INFO", msg); }
    public static void warn(String msg) { log("WARN", msg); }
    public static void exif(String msg) { log("IMAGE", msg); }
    public static void error(String msg) { log("ERROR", msg); }
}
