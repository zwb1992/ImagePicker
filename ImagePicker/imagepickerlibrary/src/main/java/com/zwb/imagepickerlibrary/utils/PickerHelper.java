package com.zwb.imagepickerlibrary.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import com.zwb.imagepickerlibrary.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;

/**
 * Created by zwb
 * Description 裁剪帮助
 * Date 2017/6/28.
 */

public class PickerHelper {
    public final static int CAMERA = 10001;
    public final static int LIBRARY = 10002;
    private int mMaxSize = 300;//默认压缩在300KB 以内

    /**
     * 从回传值内获取图片路径
     *
     * @return path
     */
    public String getPhotoPath(int requestCode, int resultCode, Intent data) {
        String path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
        return path;
    }

    /**
     * 从回传值内获取图片路径
     *
     * @return path
     */
    public Bitmap getPhotoBitmap(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        String path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
        if (!TextUtils.isEmpty(path)) {
            bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                bitmap = getBitmap2TargetSize(bitmap, mMaxSize);
            }
        }
        return bitmap;
    }

    /**
     * 压缩指定大小范围内
     *
     * @param image
     * @param maxSize
     * @return
     */
    public static Bitmap getBitmap2TargetSize(Bitmap image, int maxSize) {
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int size = baos.toByteArray().length / 1024;
        Log.e("info", "--size---" + size);
        if (baos.toByteArray().length / 1024 > maxSize) {
            float scale = 1024.0f * maxSize / baos.toByteArray().length;
            image = zoomBitmap(image, (float) Math.sqrt(scale));
        }
        return image;
    }

    /**
     * 等比图片缩放
     *
     * @param bitmap 对象
     * @param scale  等比缩放的比例
     * @return newBmp 新 Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
