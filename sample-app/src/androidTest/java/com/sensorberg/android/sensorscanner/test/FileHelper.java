package com.sensorberg.android.sensorscanner.test;

import java.io.File;

public class FileHelper {

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (String child : dir.list()) {
                if (!deleteDir(new File(dir, child))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
