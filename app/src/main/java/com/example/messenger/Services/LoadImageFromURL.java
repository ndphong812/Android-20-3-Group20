package com.example.messenger.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.InputStream;

public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
    ShapeableImageView shapeableImageView;

    public LoadImageFromURL(ShapeableImageView shapeableImageView) {
        this.shapeableImageView = shapeableImageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap bitmap = null;

        try {
            InputStream inputStream = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        shapeableImageView.setImageBitmap(bitmap);
    }
}
