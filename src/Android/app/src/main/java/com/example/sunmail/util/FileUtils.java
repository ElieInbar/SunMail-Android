package com.example.sunmail.util;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.provider.MediaStore;

public class FileUtils {
    public static String getPath(Context context, Uri uri) {
        return null;
    }

    public static File copyToTempJpeg(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        File tempFile = File.createTempFile("profile_", ".jpg", context.getCacheDir());
        FileOutputStream out = new FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.close();
        return tempFile;
    }
}