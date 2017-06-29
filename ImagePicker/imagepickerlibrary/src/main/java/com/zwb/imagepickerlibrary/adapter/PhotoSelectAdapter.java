package com.zwb.imagepickerlibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zwb.imagepickerlibrary.R;
import com.zwb.imagepickerlibrary.bean.FolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zwb
 * Description
 * Date 2017/6/27.
 */

public class PhotoSelectAdapter extends RecyclerView.Adapter<PhotoSelectHolder> {
    private FolderBean folderBean;
    private Context mContext;
    private int mPhotoCount;
    private OnItemClick onItemClick;

    public PhotoSelectAdapter(FolderBean folderBean, Context mContext) {
        this(folderBean, mContext, 1);
    }

    public PhotoSelectAdapter(FolderBean folderBean, Context mContext, int photoCount) {
        this.folderBean = folderBean;
        this.mContext = mContext;
        this.mPhotoCount = photoCount;
    }

    @Override
    public PhotoSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoSelectHolder(View.inflate(mContext, R.layout.photo_select_item, null));
    }

    @Override
    public void onBindViewHolder(final PhotoSelectHolder holder, final int position) {
        String path = folderBean.getmImgs().get(position);
//        Log.e("TAG", "==adapter=====path====" + folderBean.getDir() + File.separator + path);
        Glide.with(mContext)
                .load(folderBean.getDir() + File.separator + path)
                .placeholder(R.mipmap.photo_no)
                .error(R.mipmap.photo_no)
                .dontAnimate()//取消淡入淡出的动画，减少内存消耗
                .into(holder.mPhoto);
        holder.mPhotoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "---" + position, Toast.LENGTH_SHORT).show();
                String path = folderBean.getmImgs().get(position);
                if (folderBean.getSelectedImgs().contains(path)) {
                    holder.mPhotoSelect.setSelected(false);
                    folderBean.getSelectedImgs().remove(path);
                    holder.mPhoto.setColorFilter(null);
                } else {
                    //已经超过了可以最大选择的图片数
                    if (folderBean.getSelectedImgs().size() >= mPhotoCount) {
                        return;
                    }
                    holder.mPhotoSelect.setSelected(true);
                    folderBean.getSelectedImgs().add(path);
                    holder.mPhoto.setColorFilter(0x77000000);
                }
                if (onItemClick != null) {
                    onItemClick.onItemClick(folderBean.getDir(), getSelected());
                }
            }
        });
        if (folderBean.getSelectedImgs().contains(path)) {
            holder.mPhotoSelect.setSelected(true);
            holder.mPhoto.setColorFilter(0x77000000);
        } else {
            holder.mPhotoSelect.setSelected(false);
            holder.mPhoto.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        if (folderBean == null) {
            return 0;
        }
        return folderBean.getmImgs().size();
    }

    /**
     * 获取被选中的图片
     *
     * @return 图片集合
     */
    public List<String> getSelected() {
        if (folderBean == null) {
            return new ArrayList<>();
        }
        return folderBean.getSelectedImgs();
    }

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onItemClick(String dir, @NonNull List<String> photos);
    }
}
