package com.zwb.imagepickerlibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.zwb.imagepickerlibrary.ImageCropActivity;
import com.zwb.imagepickerlibrary.ImageSelectorActivity;
import com.zwb.imagepickerlibrary.help.ImageShapeType;

import java.util.List;


/**
 * Created by zwb
 * Description 裁剪帮助
 * Date 2017/6/28.
 */

public class Cropper {
    public final static int CAMERA = 10001;
    public final static int LIBRARY = 10002;
    public final static int REQUEST_SDCARD_FOR_PICK = 10003;
    public final static int REQUEST_SDCARD_FOR_CAPTURE = 10004;
    private int mMaxSize = 300;//默认压缩在300KB 以内
    private int width = 480;//压缩目标宽度
    private int height = 800;//压缩目标高度

    private int mPhotoCount = 1;
    private Activity mActivity;
    private ImageShapeType imageShapeType;

    /**
     * 手机拍照
     *
     * @param activity 上下文环境
     */
    public void takePhoto(Activity activity) {
        this.takePhoto(activity, ImageShapeType.NO_CLIP);
    }

    /**
     * 手机拍照
     *
     * @param activity 上下文环境
     */
    public void takePhoto(Activity activity, ImageShapeType imageShapeType) {
        mActivity = activity;
        this.imageShapeType = imageShapeType;
        mPhotoCount = 1;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD_FOR_CAPTURE);

        } else {
            dispatchTakePictureIntent();
        }
    }

    /**
     * 来至google文档--发送请求去拍照
     */
    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(mActivity, ImageCropActivity.class);
        intent.putExtra(ImageSelectorActivity.PHOTO_COUNT, mPhotoCount);
        intent.putExtra(ImageCropActivity.IMAGE_SHAPE_TYPE, imageShapeType);
        intent.putExtra(ImageCropActivity.TYPE, ImageCropActivity.CAMERA);
        mActivity.startActivityForResult(intent, CAMERA);

    }

    //****************************************************************************************
    //***************************从图库里面选择图片**********************************************
    //****************************************************************************************


    /**
     * 多选图片
     *
     * @param activity   上下文对象
     * @param photoCount 可以选择的最大图片张数,大于或等于1
     */
    public void pickPhoto(Activity activity, int photoCount) {
        this.pickPhoto(activity, photoCount, ImageShapeType.NO_CLIP);
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
     * 单选图片
     *
     * @param activity 上下文对象
     */
    public void pickPhoto(Activity activity, ImageShapeType imageShapeType) {
        this.pickPhoto(activity, 1, imageShapeType);
    }

    /**
     * 多选图片
     *
     * @param activity   上下文对象
     * @param photoCount 可以选择的最大图片张数,大于或等于1
     */
    private void pickPhoto(Activity activity, int photoCount, ImageShapeType imageShapeType) {
        mPhotoCount = photoCount;
        mActivity = activity;
        this.imageShapeType = imageShapeType;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD_FOR_PICK);

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
        intent.putExtra(ImageCropActivity.IMAGE_SHAPE_TYPE, imageShapeType);
        intent.putExtra(ImageCropActivity.TYPE, ImageCropActivity.LIBRARY);
        mActivity.startActivityForResult(intent, LIBRARY);
    }

    /**
     * 从回传值内获取图片路径
     *
     * @return path 这里的路径代表的图片时原始尺寸的图片
     */
    public String getSinglePhotoPath(int requestCode, int resultCode, Intent data) {
        String path = null;
        if (resultCode == Activity.RESULT_OK) {
            path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
        }
        return path;
    }

    /**
     * 从回传值内获取图片路径--多选
     *
     * @return path 这里的路径代表的图片时原始尺寸的图片
     */
    public List<String> getMultiplePhotoPath(int requestCode, int resultCode, Intent data) {
        List<String> paths = null;
        if (resultCode == Activity.RESULT_OK) {
            paths = data.getStringArrayListExtra(ImageSelectorActivity.PHOTO_PATH);
        }
        return paths;
    }

    /**
     * 从回传值内获取图片
     *
     * @return bitmap
     */
    public Bitmap getBitmap(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            bitmap = data.getParcelableExtra(ImageCropActivity.IMAGE_BITMAP);
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
        if (requestCode == REQUEST_SDCARD_FOR_PICK) {//选择图片
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goPickPhoto();
                    Toast.makeText(mActivity, "Permission Agreed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_SDCARD_FOR_CAPTURE) {//拍照
            if (grantResults.length > 1) {
                //拍照的权限与sdcard的权限全部同意之后，才能进行下一步操作
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                    Toast.makeText(mActivity, "Permission Agreed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
