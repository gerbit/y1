package me.gerbit.twitter.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import me.gerbit.twitter.ui.BuildConfig;

public class ImageFetcher extends ImageResizer {
    private static final String TAG = "ImageFetcher";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    public ImageFetcher(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
    }

    public ImageFetcher(Context context, int imageSize) {
        super(context, imageSize);
    }

    private Bitmap processBitmap(String data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }
        return downloadUrlToStream(data);
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        return processBitmap(String.valueOf(data));
    }

    private Bitmap downloadUrlToStream(String urlString) {
        HttpURLConnection urlConnection = null;

        Bitmap bitmap = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
            }

        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }
}
