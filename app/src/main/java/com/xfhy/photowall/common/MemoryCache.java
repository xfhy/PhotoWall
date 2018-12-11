package com.xfhy.photowall.common;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by feiyang on 2018/12/11 15:04
 * Description : 内存缓存
 */
public class MemoryCache extends LruCache<String, Bitmap> {
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    //计算该键值对 所占的内存大小
    @Override
    protected int sizeOf(String key, Bitmap value) {
        //一个bitmap所占字节数
        return value.getByteCount();
    }
}
