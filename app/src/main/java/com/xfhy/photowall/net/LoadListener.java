package com.xfhy.photowall.net;

import android.graphics.Bitmap;

/**
 * Created by feiyang on 2018/12/10 18:18
 * Description :
 */
public interface LoadListener {
    void LoadSuccess(Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask);
}
