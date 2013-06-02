package com.techan.profile;

import android.content.Context;
import android.util.Log;

import java.io.*;

public class ProfileManager {

    public static final String FILE_NAME = "techan_profile";
    public static int fileSize = 0;
    public static boolean initialized = false;

    private static boolean initialize(Context ctx) {
        initialized = false;
        try {
            // Will throw FileNotFoundException if file hasn't been created yet.
            // First time app starts we won't find the file.
            ctx.openFileInput(FILE_NAME).close();

            // File exists. Figure out its size.
            File file = new File(ctx.getFilesDir().getPath() + "/" + FILE_NAME);
            long fileSizeLong = file.length();
            if(fileSize > Integer.MAX_VALUE) {
                throw new RuntimeException("Should never happen.");
            }
            fileSize = (int)fileSizeLong;
            initialized = true;
        } catch( FileNotFoundException e1) {
            // File hasn't been created yet. Create it. First time this app is running on this device.
            try {
                FileOutputStream fos = ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                fos.close();
                initialized = true;
            } catch(FileNotFoundException e2) {
                Log.e(Constants.LOG_TAG, "Failed to create techan_profile.");
            } catch(IOException e2) {
                Log.e(Constants.LOG_TAG, "Failed to close file output stream.");
            }
        } catch( IOException e1) {
            Log.e(Constants.LOG_TAG, "Failed to close file after opening it to check for its existence.");
        }

        return initialized;
    }

    public static void forceDelete(Context ctx) {
        ctx.deleteFile(FILE_NAME);
    }

    public static boolean write(Context ctx, String s) {
        if(!initialized) {
            if(!initialize(ctx)) return false;
        }

        try {
            FileOutputStream fos = ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            // Get number of bytes being written to file. Always overwrites file!
            byte[] bytes = s.getBytes();

            // Update file size.
            fileSize = bytes.length;

            // Write to file and close the stream.
            fos.write(bytes);
            fos.close();

            return true;
        } catch(IOException e) {
            Log.e(Constants.LOG_TAG, "Failed to write string to file.");
            return false;
        }
    }

    public static String read(Context ctx) {
        if(!initialized) {
            if(!initialize(ctx)) return null;
        }

        if(fileSize == 0) {
            // Nothing in file. Just return empty string.
            return "";
        }

        String retStr = null;
        try {
            FileInputStream fis = ctx.openFileInput(FILE_NAME);
            byte[] bytes = new byte[fileSize];
            int read = 0;
            while (read != fileSize) {
                read += fis.read(bytes, read, fileSize - read);
            }

            retStr = new String(bytes, 0, fileSize);
        } catch(FileNotFoundException e) {
            Log.e(Constants.LOG_TAG, "Failed to read file.");
        } catch(IOException e) {
            Log.e(Constants.LOG_TAG, "Failed to read data from file.");
        }

        return retStr;
    }
}
