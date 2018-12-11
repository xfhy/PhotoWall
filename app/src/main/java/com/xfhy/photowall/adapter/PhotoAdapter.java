package com.xfhy.photowall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xfhy.photowall.R;
import com.xfhy.photowall.net.ImageDataSource;
import com.xfhy.photowall.util.DisplayUtil;

/**
 * Created by feiyang on 2018/12/10 16:21
 * Description : 图片adapter
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private int mHalfScreenWidth;
    private LruCache<String, Bitmap> mMemoryCache;

    public PhotoAdapter(Context context, LruCache<String, Bitmap> memoryCache) {
        mLayoutInflater = LayoutInflater.from(context);
        mHalfScreenWidth = DisplayUtil.getScreenWidth(context) / 2;
        mMemoryCache = memoryCache;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_photo, viewGroup, false);

        //将item的宽度改成屏幕宽度的一般
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = mHalfScreenWidth;
        view.requestLayout();

        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int position) {
        setImageView(ImageDataSource.imageThumbUrls[position], photoViewHolder.photoIv);
    }

    /**
     * 设置ImageView的图片,首先从LruCache中取出图片的缓存,设置到ImageView上.
     * 如果LruCache中没有该图片的缓存,就给ImageView设置一张默认图片.
     *
     * @param imageUrl 图片地址,LruCache的键
     */
    private void setImageView(String imageUrl, ImageView photoIv) {
        Bitmap bitmap = mMemoryCache.get(imageUrl);
        if (bitmap == null) {
            photoIv.setImageResource(R.drawable.empty_photo);
        } else {
            photoIv.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return ImageDataSource.imageThumbUrls.length;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoIv;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoIv = itemView.findViewById(R.id.iv_photo);
        }
    }
}
