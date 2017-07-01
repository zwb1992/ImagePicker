package com.zwb.imagepickerlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by zwb
 * Description 图片处理类
 * Date 2017/6/29.
 */

public class BitmapTools {

    /**
     * 从文件路径得到压缩后的图片
     *
     * @param path     图片的路径
     * @param width    期望的宽度
     * @param height   期望的高度
     * @param mMaxSize 如果压缩之后大于目标大小，继续压缩至目标大小
     * @return bitmap
     */
    public static Bitmap getBitmap2TargetWithPathWH(String path, int width, int height, int mMaxSize) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path)) {
            bitmap = BitmapTools.compressBitmapFromPath(path, width, height);
            if (bitmap != null) {
                bitmap = BitmapTools.getBitmap2TargetSize(bitmap, mMaxSize);
            }
        }
        return bitmap;
    }

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
        //注意：重点中的重点，碰到某些奇葩手机，居然拿不到图片的宽高，需要用下面的方式去获取
        if (options.outHeight == -1 || options.outWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度
                Log.i("info", "exif height: " + height);
                Log.i("info", "exif width: " + width);
                options.outWidth = width;
                options.outHeight = height;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        options.inSampleSize = getInSampleSize(options, rqsW, rqsH);
        Log.e("info", "---inSampleSize---" + options.inSampleSize);
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
            inSampleSize = Math.min(heightRatio, widthRatio);
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
        Log.e("info", "---原图---size---" + size + "  kb");
        Log.e("info", "---原图----width---" + bitmap.getWidth());
        Log.e("info", "---原图-----height---" + bitmap.getHeight());
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

    public static int getBitmapSize(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int size = baos.toByteArray().length / 1024;
        Log.e("info", "--压缩后的图片--size---" + size + "  kb");
        Log.e("info", "--压缩后的图片--getWidth---" + bitmap.getWidth());
        Log.e("info", "--压缩后的图片--getHeight---" + bitmap.getHeight());
        return size;
    }
}
