package com.zwb.imagepickerlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by zwb
 * Description 图片处理类
 * Date 2017/6/29.
 */

public class BitmapTools {

    /**
     * 从sd卡获取图片时，如果图片过大需要对图片进行压缩，防止oom
     *
     * @param path 图片sd卡路径
     * @param rqsW 需要压缩的目标宽度
     * @param rqsH 需要压缩的目标高度
     * @return 返回压缩后的图片
     */
    public final static Bitmap compressBitmapFromPath(String path, int rqsW, int rqsH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = getInSampleSize(options, rqsW, rqsH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 得出需要缩放的比例
     *
     * @param options options
     * @param rqsW    请求的宽
     * @param rqsH    请求的高
     * @return 返回压缩比例
     */
    public final static int getInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (rqsW == 0 || rqsH == 0)
            return 1;
        if (height > rqsH || width > rqsW) {
            final int heightRatio = Math.round((float) height / (float) rqsH);
            final int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = Math.max(heightRatio, widthRatio);
        }
        return inSampleSize;
    }

    /**
     * 压缩指定大小范围内
     *
     * @param bitmap     bitmap
     * @param targetSize 目标大小 单位kb
     * @return 返回压缩后的图片
     */
    public static Bitmap getBitmap2TargetSize(Bitmap bitmap, int targetSize) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int size = baos.toByteArray().length / 1024;
        Log.e("info", "--size---" + size + "  kb");
        if (baos.toByteArray().length / 1024.0f > targetSize) {
            float scale = 1024.0f * targetSize / baos.toByteArray().length;
            bitmap = zoomBitmapByScale(bitmap, (float) Math.sqrt(scale));
        }
        return bitmap;
    }

    /**
     * 等比图片缩放
     *
     * @param bitmap 对象
     * @param scale  等比缩放的比例
     * @return newBmp 新 Bitmap对象
     */
    public static Bitmap zoomBitmapByScale(Bitmap bitmap, float scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
