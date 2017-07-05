package com.zwb.imagepickerlibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zwb.imagepickerlibrary.help.ImageShapeType;
import com.zwb.imagepickerlibrary.utils.BitmapTools;
import com.zwb.imagepickerlibrary.utils.PickerHelper;
import com.zwb.imagepickerlibrary.view.ClipImageView;


/**
 * 图片裁剪
 */
public class ImageCropActivity extends AppCompatActivity {
    public static final String IMAGE_SHAPE_TYPE = "imageShapeType";
    public static final String IMAGE_BITMAP = "imageBitmap";
    public static final String TYPE = "type";
    public final static int CAMERA = 10001;
    public final static int LIBRARY = 10002;

    private ClipImageView clipImageView;
    private LinearLayout loadingLayout;
    private int mPhotoCount;//可以选择的图片最大数量

    private ImageShapeType imageShapeType;
    private Bitmap mBitmap;//得到的单张图片
    private int type;//是拍照获取图片还是图库获取图片
    private PickerHelper pickerHelper;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        initView();
        initData();
    }

    private void initView() {
        clipImageView = (ClipImageView) findViewById(R.id.clipImageView);
        loadingLayout = (LinearLayout) findViewById(R.id.rl_loading_layout);
        loadingLayout.setVisibility(View.GONE);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = clipImageView.clipBitmap();
                bitmap = bitmap == null ? mBitmap : bitmap;
                if (bitmap == null) {
                    return;
                }
//                clipImageView.setVisibility(View.GONE);
//                testImg.setVisibility(View.VISIBLE);
                Log.e("info", "==tv_confirm==width=" + bitmap.getWidth());
                Log.e("info", "===tv_confirm==height=" + bitmap.getHeight());
                BitmapTools.getBitmapSize(bitmap);
//                testImg.setImageBitmap(bitmap);
                new BitmapWorkerTask().execute(bitmap);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        mPhotoCount = intent.getIntExtra(ImageSelectorActivity.PHOTO_COUNT, 1);
        type = intent.getIntExtra(TYPE, CAMERA);
        imageShapeType = initImageShapeType(intent);
        clipImageView.setImageShapeType(ImageShapeType.ROUND_CORNER);

        pickerHelper = new PickerHelper(this);

        if (type == CAMERA) {
            pickerHelper.takePhoto();
        } else {
            pickerHelper.goPickPhoto(mPhotoCount);
        }
    }

    /**
     * 初始化裁剪图片的类型，防止传空
     *
     * @param intent 意图对象
     * @return 返回特定的裁剪类型
     */
    private ImageShapeType initImageShapeType(Intent intent) {
        Object object = intent.getSerializableExtra(IMAGE_SHAPE_TYPE);
        if (object == null) {
            return ImageShapeType.NO_CLIP;
        } else {
            return (ImageShapeType) object;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            finish();
        } else {
            if (requestCode == PickerHelper.LIBRARY) {
                int count = data.getIntExtra(ImageSelectorActivity.PHOTO_COUNT, 1);
                //单张图片
                if (count == 1) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    String path = pickerHelper.getPhotoPath(requestCode, resultCode, data);
                    loadBitmap(path);
                }
                //多张图片，直接返回到上一页
                else {
                    setResult(resultCode, data);
                    finish();
                }
            }
            //拍照
            else if (requestCode == PickerHelper.CAMERA) {
                loadingLayout.setVisibility(View.VISIBLE);
                String path = pickerHelper.getPhotoPath(requestCode, resultCode, data);
                loadBitmap(path);
            }
        }
    }

    /**
     * 使用glide 异步加载图片
     *
     * @param path 图片地址
     */
    private void loadBitmap(String path) {
        Glide.with(this)
                .load(path)
                .asBitmap()
                .override(500, 500)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mBitmap = resource;
                        loadingLayout.setVisibility(View.GONE);
                        clipImageView.setImageBitmap(resource);
//                        clipImageView.setVisibility(View.GONE);
//                        testImg.setVisibility(View.VISIBLE);
//                        testImg.setImageBitmap(resource);
                    }
                });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            intent.putExtra(ImageSelectorActivity.PHOTO_PATH, pickerHelper.getmCachePhotoPath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    /**
     * 裁剪图片线程
     */
    class BitmapWorkerTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(ImageCropActivity.this, null, "正在裁剪中...");
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String path = null;
            try {
                pickerHelper.writeClipBitmap2File(params[0]);
                path = pickerHelper.getmCachePhotoPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return path;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            mProgressDialog.dismiss();
            if (handler != null && path != null) {
                handler.sendEmptyMessage(0x110);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
