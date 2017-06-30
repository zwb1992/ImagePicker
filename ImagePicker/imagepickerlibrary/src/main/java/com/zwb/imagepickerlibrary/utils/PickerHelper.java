package com.zwb.imagepickerlibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

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
    public final static int REQUEST_SDCARD = 10003;
    private int mMaxSize = 300;//默认压缩在300KB 以内
    private int width = 480;//压缩目标宽度
    private int height = 800;//压缩目标高度

    private int mPhotoCount = 1;
    private Activity mActivity;

    /**
     * 多选图片
     *
     * @param activity   上下文对象
     * @param photoCount 可以选择的最大图片张数,大于或等于1
     */
    public void pickPhoto(Activity activity, int photoCount) {
        mPhotoCount = photoCount;
        mActivity = activity;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD);

        } else {
            goPickPhoto();
        }
    }

    /**
     * 开始去选择图片
     */
    private void goPickPhoto() {
        if (mActivity == null) {
            return;
        }
        if (mPhotoCount < 1) {
            mPhotoCount = 1;
        }
        Intent intent = new Intent(mActivity, ImageCropActivity.class);
        intent.putExtra(ImageSelectorActivity.PHOTO_COUNT, mPhotoCount);
        mActivity.startActivityForResult(intent, LIBRARY);
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

    /**
     * Android 6.0 权限验证
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SDCARD) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goPickPhoto();
                    Toast.makeText(mActivity, "Permission Agreed", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
