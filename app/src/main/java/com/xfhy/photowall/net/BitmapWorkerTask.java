package com.xfhy.photowall.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.jakewharton.disklrucache.DiskLruCache;
import com.xfhy.photowall.util.MD5Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by feiyang on 2018/12/10 16:46
 * Description : 图片下载任务 AsyncTask
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private LoadListener mLoadListener;
    /**
     * 图片硬盘缓存核心类
     */
    private DiskLruCache mDiskLruCache;

    @Override
    protected Bitmap doInBackground(String... strings) {
        //1. 首先判断缓存里面是否有数据 有->用缓存  无->请求网络
        String key = MD5Util.hashKeyForDisk(strings[0]);
        DiskLruCache.Snapshot snapshot = null;
        FileInputStream fileInputStream = null;
        FileDescriptor fileDescriptor = null;
        try {
            snapshot = mDiskLruCache.get(key);
            if (snapshot == null) {
                //没有缓存  ->  请求网络呗
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (downloadUrlToStream(strings[0], outputStream)) {
                        //下载成功
                        editor.commit();
                    } else {
                        //下载失败
                        editor.abort();
                    }
                    //缓存被写入后,再次查找key所对应的缓存
                    snapshot = mDiskLruCache.get(key);
                }
            }
            if (snapshot != null) {
                //有缓存 -> 取缓存
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            // 将缓存数据解析成Bitmap对象
            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
            if (bitmap != null && mLoadListener != null) {
                // 将Bitmap对象添加到内存缓存当中
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mLoadListener.LoadSuccess(bitmap, this);
    }

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            in = new BufferedInputStream(inputStream, 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int n;
            while ((n = in.read()) != -1) {
                out.write(n);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void setLoadListener(LoadListener loadListener) {
        mLoadListener = loadListener;
    }

    public void setDiskLruCache(DiskLruCache diskLruCache) {
        mDiskLruCache = diskLruCache;
    }
}
