package com.zwb.imagepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.zwb.imagepickerlibrary.utils.PickerHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
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
        pickerHelper.pickPhoto(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickerHelper.LIBRARY && resultCode == RESULT_OK) {
            Bitmap bitmap = pickerHelper.getPhotoBitmap(requestCode, resultCode, data);
            img.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        pickerHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
