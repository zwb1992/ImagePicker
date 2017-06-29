package com.zwb.imagepickerlibrary.help;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by zwb
 * Description 配置glide缓存
 * Date 2016/12/24.
 */

public class MyGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取系统分配给应用的总内存大小
        int memoryCacheSize = maxMemory / 8;//设置图片内存缓存占用八分之一
        //设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));

        //设置Glide磁盘缓存大小
        int diskCacheSize = 1024 * 1024 * 30;//最多可以缓存多少字节的数据
        //设置磁盘缓存大小
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "imagePicker");
        file.mkdirs();
        Log.e("bitmap", "======" + file.getAbsolutePath());
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, file.getAbsolutePath(), diskCacheSize));

        //设置图片解码格式
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);

        //设置BitmapPool缓存内存大小
        builder.setBitmapPool(new LruBitmapPool(memoryCacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
