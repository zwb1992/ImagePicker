package com.zwb.imagepickerlibrary;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zwb.imagepickerlibrary.adapter.DividerGridItemDecoration;
import com.zwb.imagepickerlibrary.adapter.PhotoSelectAdapter;
import com.zwb.imagepickerlibrary.bean.FolderBean;
import com.zwb.imagepickerlibrary.utils.PhotoDirListPopWindow;

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
    public static final String PHOTO_PATH = "photo_path";//图片的路径
    public static final String PHOTO_COUNT = "photo_count";//图片的张数
    private RecyclerView recyclerView;
    private TextView tvDirName;
    private TextView tvDirCount;
    private LinearLayout llBottom;
    private PhotoSelectAdapter adapter;
    private List<FolderBean> mFolders = new ArrayList<>();//所以文件夹
    private Set<String> mDirPath = new HashSet<>();//用来过滤已经扫描过的文件夹
    private ProgressDialog mProgressDialog;
    private PhotoDirListPopWindow dirListPopWindow;
    private float mScreenHeight;

    private int mPhotoCount;//单选还是多选

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);
        mPhotoCount = getIntent().getIntExtra(PHOTO_COUNT, 1);
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        initView();
        getImages();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvDirName = (TextView) findViewById(R.id.tv_dir_name);
        tvDirCount = (TextView) findViewById(R.id.tv_dir_count);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        llBottom.setOnClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        initAdapter(null);
    }

    private void initAdapter(FolderBean folderBean) {
        adapter = new PhotoSelectAdapter(folderBean, this, mPhotoCount);
        adapter.setOnItemClick(new PhotoSelectAdapter.OnItemClick() {
            @Override
            public void onItemClick(String dir, @NonNull List<String> photos) {
                if (!photos.isEmpty()) {
                    Intent intent = new Intent();
                    if (photos.size() == 1) {
                        String path = photos.get(0);
                        intent.putExtra(PHOTO_PATH, dir + File.separator + path);
                        intent.putExtra(PHOTO_COUNT, photos.size());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        ArrayList arrayList = convertPhotos(dir,photos);
                        intent.putStringArrayListExtra(PHOTO_PATH, arrayList);
                        intent.putExtra(PHOTO_COUNT, photos.size());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private ArrayList convertPhotos(String dir, @NonNull List<String> photos) {
        ArrayList arrayList = new ArrayList();
        for (String path : photos) {
            arrayList.add(dir + File.separator + path);
        }
        return arrayList;
    }


    @Override
    public void onClick(View view) {
        if (dirListPopWindow != null) {
            if (!dirListPopWindow.isShowing()) {
                dirListPopWindow.showAsDropDown(llBottom, 0, 0);
                lightOff();
            } else {
                dirListPopWindow.dismiss();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressDialog.dismiss();
            data2View();
            initPopup();
        }
    };

    private void data2View() {
        if (mFolders.isEmpty()) {
            Toast.makeText(this, "没有扫描到图片!", Toast.LENGTH_SHORT).show();
            return;
        }
        FolderBean folderBean = mFolders.get(0);
        initAdapter(folderBean);
        tvDirName.setText(folderBean.getName());
        tvDirCount.setText(folderBean.getCount() + " 张");
    }

    private void initPopup() {
        dirListPopWindow = new PhotoDirListPopWindow(this,
                LayoutInflater.from(this).inflate(R.layout.layout_list_dir, null),
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mFolders
        );
        dirListPopWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        dirListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        dirListPopWindow.setOnItemClick(new PhotoDirListPopWindow.OnItemClick() {
            @Override
            public void onItemSelected(FolderBean folderBean) {
                initAdapter(folderBean);
                tvDirName.setText(folderBean.getName());
                tvDirCount.setText(folderBean.getCount() + " 张");
            }
        });
    }

    /**
     * 点亮屏幕
     */
    private void lightOn() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 点亮屏幕
     */
    private void lightOff() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
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
//                Log.e("TAG", "===count====" + cursor.getCount());
                FolderBean folderBean;
                while (cursor.moveToNext()) {
                    //获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                    Log.e("TAG", "===path====" + path);
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
                            if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
                                    || name.endsWith(".JPG") || name.endsWith(".JPEG") || name.endsWith(".PNG")) {
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
