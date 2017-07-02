package com.zwb.imagepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zwb.imagepickerlibrary.utils.BitmapTools;
import com.zwb.imagepickerlibrary.utils.Cropper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.img)
    ImageView img;
    private Cropper mCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCropper = new Cropper();
    }

    @OnClick({R.id.bt_select, R.id.bt_take})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_take:
                mCropper.takePhoto(this);
                break;
            case R.id.bt_select:
                mCropper.pickPhoto(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Cropper.LIBRARY && resultCode == RESULT_OK) {
            Bitmap bitmap = mCropper.getSingleBitmap(requestCode, resultCode, data);
            BitmapTools.getBitmapSize(bitmap);
            img.setImageBitmap(bitmap);
        } else if (requestCode == Cropper.CAMERA && resultCode == RESULT_OK) {
            Bitmap bitmap = mCropper.getSingleBitmap(requestCode, resultCode, data);
            BitmapTools.getBitmapSize(bitmap);
            img.setImageBitmap(bitmap);

//            Glide.with(this)
//                    .load(path)
//                    .asBitmap()
//                    .override(480, 800)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            BitmapTools.getBitmapSize(resource);
//                        }
//                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mCropper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
