package com.xfhy.photowall.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

/**
 * Created by feiyang on 2018/12/11 9:13
 * Description : 缓存工具类
 */
public class CacheUtil {

    /**
     * 获取缓存地址
     *
     * @param context    Context
     * @param uniqueName 不同类型的数据进行区分
     * @return File
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        //当 SD 卡存在或者 SD 卡不可被移除的时候
        //挂载状态 ||  外存不可移除(物理上的)
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            //外存缓存路径  /sdcard/Android/data/<application package>/cache
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir == null) {
                ///data/data/<application package>/cache
                cachePath = context.getCacheDir().getPath();
            } else {
                //我的测试机  /storage/emulated/0/Android/data/com.xfhy.disk/cache
                cachePath = externalCacheDir.getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @param context Context
     * @return 版本号
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }

}
