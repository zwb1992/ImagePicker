package com.zwb.imagepickerlibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.zwb.imagepickerlibrary.ImageCropActivity;
import com.zwb.imagepickerlibrary.ImageSelectorActivity;


/**
 * Created by zwb
 * Description 裁剪帮助
 * Date 2017/6/28.
 */

public class PickerHelper {
    public final static int CAMERA = 10001;
    public final static int LIBRARY = 10002;
    private int mMaxSize = 300;//默认压缩在300KB 以内
    private int width = 480;//压缩目标宽度
    private int height = 800;//压缩目标高度

    /**
     * 多选图片
     *
     * @param activity   上下文对象
     * @param photoCount 可以选择的最大图片张数
     */
    public void pickPhoto(Activity activity, int photoCount) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(ImageSelectorActivity.PHOTO_COUNT, photoCount);
        activity.startActivityForResult(intent, LIBRARY);
    }

    /**
     * 单选图片
     *
     * @param activity 上下文对象
     */
    public void pickPhoto(Activity activity) {
        pickPhoto(activity, 1);
    }

    /**
     * 从回传值内获取图片路径
     *
     * @return path
     */
    public String getPhotoPath(int requestCode, int resultCode, Intent data) {
        String path = null;
        if (resultCode == Activity.RESULT_OK) {
            path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
        }
        return path;
    }

    /**
     * 从回传值内获取图片路径
     *
     * @return path
     */
    public Bitmap getPhotoBitmap(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
            if (!TextUtils.isEmpty(path)) {
                bitmap = BitmapTools.compressBitmapFromPath(path, width, height);
                if (bitmap != null) {
                    bitmap = BitmapTools.getBitmap2TargetSize(bitmap, mMaxSize);
                }
            }
        }
        return bitmap;
    }

}
