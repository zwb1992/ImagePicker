package com.zwb.imagepickerlibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zwb.imagepickerlibrary.utils.PickerHelper;


/**
 * 图片裁剪
 */
public class ImageCropActivity extends AppCompatActivity {
    private int mPhotoCount;//可以选择的图片最大数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        initData();
        goPickPhoto();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mPhotoCount = getIntent().getIntExtra(ImageSelectorActivity.PHOTO_COUNT, 1);
    }

    /**
     * 开始选择图片
     */
    private void goPickPhoto() {
        Intent intent = new Intent(this, ImageSelectorActivity.class);
        intent.putExtra(ImageSelectorActivity.PHOTO_COUNT, mPhotoCount);
        startActivityForResult(intent, PickerHelper.LIBRARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickerHelper.LIBRARY && resultCode == RESULT_OK) {
            setResult(resultCode, data);
            finish();
        }
    }
}
