package com.zwb.imagepickerlibrary;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zwb.imagepickerlibrary.adapter.DividerGridItemDecoration;
import com.zwb.imagepickerlibrary.adapter.PhotoSelectAdapter;
import com.zwb.imagepickerlibrary.bean.FolderBean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 从相册选择图片
 */
public class ImageSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TextView tvDirName;
    private TextView tvDirCount;
    private PhotoSelectAdapter adapter;
    private List<FolderBean> mFolders = new ArrayList<>();//所以文件夹
    private Set<String> mDirPath = new HashSet<>();//用来过滤已经扫描过的文件夹
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);
        initView();
        getImages();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvDirName = (TextView) findViewById(R.id.tv_dir_name);
        tvDirCount = (TextView) findViewById(R.id.tv_dir_count);
        findViewById(R.id.ll_bottom).setOnClickListener(this);

        adapter = new PhotoSelectAdapter(null, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "底部按钮", Toast.LENGTH_SHORT).show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressDialog.dismiss();
            data2View();
        }
    };

    private void data2View() {
        if (mFolders.isEmpty()) {
            Toast.makeText(this, "没有扫描到图片!", Toast.LENGTH_SHORT).show();
            return;
        }
        FolderBean folderBean = mFolders.get(0);
        adapter = new PhotoSelectAdapter(folderBean, this);
        recyclerView.setAdapter(adapter);
        tvDirName.setText(folderBean.getName());
        tvDirCount.setText(folderBean.getCount() + " 张");
    }

    /**
     * 开启线程获取图片
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "外部存储不可用!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageSelectorActivity.this.getContentResolver();
                //按照修改日期排序
                Cursor cursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/jpg"}, MediaStore.Images.Media.DATE_MODIFIED);
                Log.e("TAG", "===count====" + cursor.getCount());
                FolderBean folderBean;
                while (cursor.moveToNext()) {
                    //获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.e("TAG", "===path====" + path);
                    //获取该图片的父路径
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    if (mDirPath.contains(dirPath)) {//表示扫描过了
                        continue;
                    } else {
                        mDirPath.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setName(parentFile.getName());
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImagePath(path);
                    }
                    String[] imgs = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                                return true;
                            }
                            return false;
                        }
                    });
                    int picSize = imgs.length;
                    folderBean.setCount(picSize);
                    folderBean.setmImgs(Arrays.asList(imgs));
                    mFolders.add(folderBean);
                }
                cursor.close();
                mDirPath = null;
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }
}