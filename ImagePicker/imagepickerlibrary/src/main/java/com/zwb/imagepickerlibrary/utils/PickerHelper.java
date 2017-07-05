package com.zwb.imagepickerlibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.zwb.imagepickerlibrary.ImageCropActivity;
import com.zwb.imagepickerlibrary.ImageSelectorActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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

    private Activity mActivity;

    private String mCurrentPhotoPath;//点击拍照生成的路径
    private String mCachePhotoPath;//压缩后的文件路径

    public PickerHelper(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 开始选择图片
     */
    public void goPickPhoto(int photoCount) {
        Intent intent = new Intent(mActivity, ImageSelectorActivity.class);
        intent.putExtra(ImageSelectorActivity.PHOTO_COUNT, photoCount);
        mActivity.startActivityForResult(intent, PickerHelper.LIBRARY);
    }

    /**
     * 手机拍照
     */
    public void takePhoto() {
        dispatchTakePictureIntent();
    }

    /**
     * 来至google文档--发送请求去拍照
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI;
                //适配Android7.0
                photoURI = FileProvider.getUriForFile(mActivity,
                        "com.zwb.imagepicker.android.fileprovider",
                        photoFile);
                //在Android4.4上面需要进行授权处理，否则会抛出异常 Permission Denial
                List<ResolveInfo> resInfoList = mActivity.getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mActivity.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                Log.e("info", "=====photoURI=======" + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.startActivityForResult(takePictureIntent, CAMERA);
            } else {
                Toast.makeText(mActivity, "文件创建失败!", Toast.LENGTH_SHORT).show();
                mActivity.finish();
            }
        } else {
            Toast.makeText(mActivity, "无可用的照相机!", Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
    }

    /**
     * 来至google文档--创建拍照存储的图片路径
     *
     * @return 返回文件
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
     * 从回传值内获取图片路径
     *
     * @return path
     */
    public String getPhotoPath(int requestCode, int resultCode, Intent data) {
        String path = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LIBRARY) {
                path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
            } else if (requestCode == CAMERA) {
                path = mCurrentPhotoPath;
                galleryAddPic();
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
        String path = getPhotoPath(requestCode, resultCode, data);
        if (path != null) {
            bitmap = BitmapTools.getBitmap2TargetWithPathWH(path, width, height, mMaxSize);
        }
        return bitmap;
    }

    /**
     * 通知系统照片gallery去扫描该照片
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    /**
     * 把压缩后的图片写入文件
     */
    public void writeClipBitmap2File(Bitmap bitmap) throws Exception {
        bitmap = BitmapTools.getBitmap2TargetSize(bitmap, mMaxSize);
        BitmapTools.getBitmapSize(bitmap);
        createCacheFile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        FileOutputStream fileOutputStream = new FileOutputStream(mCachePhotoPath);
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        bos.write(baos.toByteArray());
        bitmap.recycle();
        bos.flush();
        baos.close();
    }

    /**
     * 来至google文档--创建拍照存储的图片路径
     *
     * @return 返回文件
     * @throws IOException
     */
    private File createCacheFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCachePhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getmCachePhotoPath() {
        return mCachePhotoPath;
    }
}
