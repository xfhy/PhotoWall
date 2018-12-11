package com.xfhy.photowall.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by feiyang on 2018/12/10 16:46
 * Description :
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private LoadListener mLoadListener;

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadBitmap(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mLoadListener.LoadSuccess(bitmap, this);
    }

    private Bitmap downloadBitmap(String imageUrl) {
        Bitmap bitmap = null;
        HttpURLConnection connection ;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5 * 10000);
            connection.setReadTimeout(10 * 10000);
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void setLoadListener(LoadListener loadListener) {
        mLoadListener = loadListener;
    }
}
