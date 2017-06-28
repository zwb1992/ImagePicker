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
    private OnItemClick onItemClick;

    public PhotoDirListPopWindow(Context context, View contentView, int width, int height, List<FolderBean> mFolders) {
        super(contentView, width, height);
        this.mContext = context;
        this.mFolders = mFolders;
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);//必须写,只有设置了这句话，点击按钮的时候才不会消失又弹出，而且能响应后退键
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initView(contentView);

    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new PhotoDirAdapter(mFolders, mContext);
        adapter.setOnItemClick(new PhotoDirAdapter.OnItemClick() {
            @Override
            public void onItemSelected(FolderBean folderBean) {
                if (onItemClick != null) {
                    onItemClick.onItemSelected(folderBean);
                    dismiss();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
    }

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onItemSelected(FolderBean folderBean);
    }
}
