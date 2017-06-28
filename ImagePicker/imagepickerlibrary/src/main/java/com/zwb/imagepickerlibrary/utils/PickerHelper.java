package com.zwb.imagepickerlibrary.utils;

import android.content.Intent;

import com.zwb.imagepickerlibrary.ImageSelectorActivity;

/**
 * Created by zwb
 * Description 裁剪帮助
 * Date 2017/6/28.
 */

public class PickerHelper {
    public final static int CAMERA = 10001;
    public final static int LIBRARY = 10002;

    /**
     * 从回传值内获取图片路径
     *
     * @return path
     */
    public String getPhotoPath(int requestCode, int resultCode, Intent data) {
        String path = data.getStringExtra(ImageSelectorActivity.PHOTO_PATH);
        return path;
    }
}
