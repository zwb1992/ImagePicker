package com.zwb.imagepickerlibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.zwb.imagepickerlibrary.ImageCropActivity;
import com.zwb.imagepickerlibrary.ImageSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by zwb
 * Description 裁剪帮助
 * Date 2017/6/28.
 */

public class PickerHelper {
    public final static int CAMERA = 10001;
    public final static int LIBRARY = 10002;
    public final static int REQUEST_SDCARD_FOR_PICK = 10003;
    public final static int REQUEST_SDCARD_FOR_CAPTURE = 10004;
    private int mMaxSize = 300;//默认压缩在300KB 以内
    private int width = 480;//压缩目标宽度
    private int height = 800;//压缩目标高度

    private int mPhotoCount = 1;
    private Activity mActivity;

    private String mCurrentPhotoPath;//点击拍照生成的路径

    /**
     * 手机拍照
     *
     * @param activity 上下文环境
     */
    public void takePhoto(Activity activity) {
        mActivity = activity;
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
            dispatchTakePictureIntent(activity);
        }
    }

    /**
     * 来至google文档--创建拍照存储的图片路径
     *
     * @param activity 上下文对象
     * @return 返回文件
     * @throws IOException
     */
    private File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 来至google文档--发送请求去拍照
     *
     * @param activity 上下文对象
     */
    private void dispatchTakePictureIntent(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

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
            if (requestCode == LIBRARY) {
                path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
            } else {
                path = mCurrentPhotoPath;
            }
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
            String path = null;
            if (requestCode == LIBRARY) {
                path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
            } else if (requestCode == CAMERA) {
                path = mCurrentPhotoPath;
            }
            bitmap = BitmapTools.getBitmap2TargetWithPathWH(path, width, height, mMaxSize);
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
                    dispatchTakePictureIntent(mActivity);
                    Toast.makeText(mActivity, "Permission Agreed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
