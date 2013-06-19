package com.techan.profile;

import android.content.Context;
import android.util.Log;

import java.io.*;

public class PersistenceManager {

    private static final String FILE_NAME = "techan_profile";
    private int fileSize = 0;
    private Context ctx;

    public PersistenceManager(Context ctx) {
        this.ctx = ctx;
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
        } catch( FileNotFoundException e1) {
            // File hasn't been created yet. Create it. First time this app is running on this device.
            try {
                FileOutputStream fos = ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                fos.close();
            } catch(FileNotFoundException e2) {
                Log.e(Constants.LOG_TAG, "Failed to create techan_profile.");
            } catch(IOException e2) {
                Log.e(Constants.LOG_TAG, "Failed to close file output stream.");
            }
        } catch( IOException e1) {
            Log.e(Constants.LOG_TAG, "Failed to close file after opening it to check for its existence.");
        }
    }

    public void forceDelete() {
        ctx.deleteFile(FILE_NAME);
    }

    public boolean write(String s) {
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

    public String read() {
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

    public boolean clear() {
        if(fileSize == 0) {
            return true;
        }

        try {
            FileOutputStream fos = ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            byte[] bytes = "".getBytes();
            // Update file size.
            fileSize = 0;

            // Write to file and close the stream.
            fos.write(bytes);
            fos.close();

            return true;
        } catch(IOException e) {
            Log.e(Constants.LOG_TAG, "Failed to delete profile file.");
            return false;
        }



    }
}
