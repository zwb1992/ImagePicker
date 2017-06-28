package com.zwb.imagepickerlibrary.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.zwb.imagepickerlibrary.R;
import com.zwb.imagepickerlibrary.adapter.DividerItemDecoration;
import com.zwb.imagepickerlibrary.adapter.PhotoDirAdapter;
import com.zwb.imagepickerlibrary.bean.FolderBean;

import java.util.List;

/**
 * Created by zwb
 * Description
 * Date 17/6/27.
 */

public class PhotoDirListPopWindow extends PopupWindow {
    private List<FolderBean> mFolders;
    private RecyclerView recyclerView;
    private PhotoDirAdapter adapter;
    private Context mContext;

    public PhotoDirListPopWindow(Context context, View contentView, int width, int height, List<FolderBean> mFolders) {
        super(contentView, width, height);
        this.mContext = context;
        this.mFolders = mFolders;
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        initView(contentView);
        initEvent();
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new PhotoDirAdapter(mFolders, mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
    }

    private void initEvent() {

    }
}
