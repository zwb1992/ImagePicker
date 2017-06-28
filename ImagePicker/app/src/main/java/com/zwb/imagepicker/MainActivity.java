package com.zwb.imagepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.zwb.imagepickerlibrary.ImageCropActivity;
import com.zwb.imagepickerlibrary.ImageSelectorActivity;
import com.zwb.imagepickerlibrary.help.SelectType;
import com.zwb.imagepickerlibrary.utils.PickerHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA = 0X110;
    @Bind(R.id.img)
    ImageView img;
    private PickerHelper pickerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        pickerHelper = new PickerHelper();
    }

    @OnClick(R.id.bt_select)
    public void onClick() {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra(ImageSelectorActivity.SELECT_TYPE, SelectType.SINGLE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bitmap bitmap = pickerHelper.getPhotoBitmap(requestCode, resultCode, data);
            img.setImageBitmap(bitmap);
        }
    }
}
