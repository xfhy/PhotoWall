package com.xfhy.photowall;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.ViewTreeObserver;

import com.xfhy.photowall.adapter.PhotoAdapter;
import com.xfhy.photowall.net.BitmapWorkerTask;
import com.xfhy.photowall.net.ImageDataSource;
import com.xfhy.photowall.net.LoadListener;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private PhotoAdapter mPhotoAdapter;
    /**
     * 第一张可见图片的下标
     */
    private int mFirstVisibleItem;
    /**
     * 一屏有多少张图片可见
     */
    private int mLastVisibleItem;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mPhotoRv;

    /**
     * 记录所有正在下载或等待下载的任务
     */
    private Set<BitmapWorkerTask> mTaskCollection;
    /**
     * 图片缓存技术的核心类,用于缓存所有下载好的图片.在程序内存达到设定值时会将最少最近使用的图片移除掉
     */
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        mTaskCollection = new HashSet<>();

        //获取应用程序最大可用内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        //设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //一个bitmap所占字节数
                return value.getByteCount();
            }
        };
    }

    private void initView() {
        mPhotoRv = findViewById(R.id.rv_photo);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mPhotoRv.setLayoutManager(mGridLayoutManager);
        mPhotoAdapter = new PhotoAdapter(this,mMemoryCache);
        mPhotoRv.setAdapter(mPhotoAdapter);

        //解决bug: 第一次进入时不加载第一屏的数据
        mPhotoRv.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                //第一次进入
                mFirstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
                mLastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
                loadBitmaps(mFirstVisibleItem, mLastVisibleItem);
            }
        });

        //监听RecyclerView滑动事件,在停止滑动的时候去加载图片 其他的时候停止加载图片
        mPhotoRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //做点事情
                    loadBitmaps(mFirstVisibleItem, mLastVisibleItem);
                } else {
                    cancelAllTasks();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mFirstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
                mLastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * 加载图片,从firstVisibleItem到lastVisibleItem
     */
    private void loadBitmaps(int firstVisibleItem, int lastVisibleItem) {
        if (firstVisibleItem < 0 || lastVisibleItem < 0) {
            return;
        }
        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            final String imageUrl = ImageDataSource.imageThumbUrls[i];
            Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
            if (bitmap == null) {
                final BitmapWorkerTask task = new BitmapWorkerTask();
                mTaskCollection.add(task);
                final int index = i;
                task.setLoadListener(new LoadListener() {
                    @Override
                    public void LoadSuccess(Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
                        //缓存
                        addBitmapToMemoryCache(imageUrl, bitmap);
                        //更新item
                        mPhotoAdapter.notifyItemChanged(index);
                        //移除task
                        mTaskCollection.remove(bitmapWorkerTask);
                    }
                });
                task.execute(imageUrl);
            }
        }
        mPhotoAdapter.notifyItemRangeChanged(firstVisibleItem, lastVisibleItem - firstVisibleItem);
    }

    /**
     * 取消任务
     */
    private void cancelAllTasks() {
        if (mTaskCollection != null) {
            for (BitmapWorkerTask task : mTaskCollection) {
                task.cancel(false);
            }
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 将bitmap缓存到LruCache
     *
     * @param key    LruCache的键,就是url
     * @param bitmap LruCache的值,就是Bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        //缓存中没有->加入缓存
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
}
