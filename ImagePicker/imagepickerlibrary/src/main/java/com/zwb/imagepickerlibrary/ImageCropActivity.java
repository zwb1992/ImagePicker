package com.zwb.imagepickerlibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zwb.imagepickerlibrary.help.SelectType;

import static com.zwb.imagepickerlibrary.ImageSelectorActivity.SELECT_TYPE;

/**
 * 图片裁剪
 */
public class ImageCropActivity extends AppCompatActivity {
    private SelectType mSelectType;//单选还是多选
    private static final int CAMERA = 0X110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        createSelectType();
        Intent intent = new Intent(this, ImageSelectorActivity.class);
        intent.putExtra(ImageSelectorActivity.SELECT_TYPE, mSelectType);
        startActivityForResult(intent, CAMERA);
    }

    /**
     * 防止未设置单选还是多选
     */
    private void createSelectType() {
        Object object = getIntent().getSerializableExtra(SELECT_TYPE);
        if (object == null) {
            mSelectType = SelectType.SINGLE;
        } else {
            mSelectType = (SelectType) object;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }
}
