package com.example.medproject.QRCode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewWeakReference;

    public ImageDownloaderTask(ImageView imageView) {
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadBitMap(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
       if(isCancelled()) {
           bitmap = null;
       }
       ImageView imageView = imageViewWeakReference.get();
       if(imageView != null) {
           if(bitmap != null) {
               imageView.setImageBitmap(bitmap);
           }
       }
    }

    private Bitmap downloadBitMap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int startCode = urlConnection.getResponseCode();
            if(startCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            urlConnection.disconnect();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }
}
