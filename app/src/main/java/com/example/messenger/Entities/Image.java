package com.example.messenger.Entities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Image {
    private Context context;
    private Uri uri;

    public Image(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }


    // Calculate the size of image can be approval to send
    public int calculateImageFile(BitmapFactory.Options options, int fileWidth, int fileHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if(fileHeight < height || fileWidth < width) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > fileHeight
                    && (halfWidth / inSampleSize) > fileWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeBitmapFromUrl(int fileWidth, int fileHeight) {
        InputStream inputStream;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            options.inSampleSize = calculateImageFile(options, fileWidth, fileHeight);
            inputStream.close();

            inputStream = context.getContentResolver().openInputStream(uri);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName(){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();

        return name;
    }

    public long getFileSize(){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        long size = cursor.getLong(sizeIndex);
        cursor.close();

        return size;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap){
//        Log.v(TAG, "Convert image to byte array");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

}
