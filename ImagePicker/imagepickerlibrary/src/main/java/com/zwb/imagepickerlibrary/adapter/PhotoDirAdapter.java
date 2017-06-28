package com.zwb.imagepickerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zwb.imagepickerlibrary.R;
import com.zwb.imagepickerlibrary.bean.FolderBean;

import java.io.File;
import java.util.List;

/**
 * Created by zwb
 * Description
 * Date 17/6/27.
 */

public class PhotoDirAdapter extends RecyclerView.Adapter<PhotoDirHolder> {
    private List<FolderBean> mFolders;
    private Context mContext;

    public PhotoDirAdapter(List<FolderBean> mFolders, Context mContext) {
        this.mFolders = mFolders;
        this.mContext = mContext;
    }

    @Override
    public PhotoDirHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoDirHolder(View.inflate(mContext, R.layout.list_dir_item, null));
    }

    @Override
    public void onBindViewHolder(PhotoDirHolder holder, int position) {
        FolderBean folderBean = mFolders.get(position);
        String path = folderBean.getFirstImagePath();
        Log.e("TAG", "==adapter=====path====" + path);
        holder.tvName.setText(folderBean.getName());
        holder.tvCount.setText(folderBean.getCount()+" 张");
        Glide.with(mContext).load(path)
                .placeholder(R.mipmap.photo_no).error(R.mipmap.photo_no).into(holder.mPhoto);
        holder.mView.setTag(position + "");
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Integer.parseInt(v.getTag().toString());
                Toast.makeText(mContext, "---" + index, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFolders.size();
    }
}
