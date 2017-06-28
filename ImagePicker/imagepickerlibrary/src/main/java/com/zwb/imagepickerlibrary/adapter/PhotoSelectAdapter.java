package com.zwb.imagepickerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zwb.imagepickerlibrary.R;
import com.zwb.imagepickerlibrary.bean.FolderBean;

import java.io.File;


/**
 * Created by zwb
 * Description
 * Date 2017/6/27.
 */

public class PhotoSelectAdapter extends RecyclerView.Adapter<PhotoSelectHolder> {
    private FolderBean folderBean;
    private Context mContext;

    public PhotoSelectAdapter(FolderBean folderBean, Context mContext) {
        this.folderBean = folderBean;
        this.mContext = mContext;
    }

    @Override
    public PhotoSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoSelectHolder(View.inflate(mContext, R.layout.photo_select_item, null));
    }

    @Override
    public void onBindViewHolder(PhotoSelectHolder holder, final int position) {
        String path = folderBean.getmImgs().get(position);
//        Log.e("TAG", "==adapter=====path====" + folderBean.getDir() + File.separator + path);
        Glide.with(mContext).load(folderBean.getDir() + File.separator + path)
                .placeholder(R.mipmap.photo_no).error(R.mipmap.photo_no).into(holder.mPhoto);
        holder.mPhotoSelect.setTag(position + "");
        holder.mPhotoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Integer.parseInt(v.getTag().toString());
                Toast.makeText(mContext, "---" + index, Toast.LENGTH_SHORT).show();
                ImageButton imageButton = (ImageButton) v;
                String path = folderBean.getmImgs().get(position);
                if (folderBean.getSelectedImgs().contains(path)) {
                    imageButton.setSelected(false);
                    folderBean.getSelectedImgs().remove(path);
                } else {
                    imageButton.setSelected(true);
                    folderBean.getSelectedImgs().add(path);
                }
//                if (onItemClick != null) {
//                    onItemClick.onItemSelected(mFolders.get(index));
//                }
            }
        });
        if(folderBean.getSelectedImgs().contains(path)){
            holder.mPhotoSelect.setSelected(true);
        }else {
            holder.mPhotoSelect.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        if (folderBean == null) {
            return 0;
        }
        return folderBean.getmImgs().size();
    }
}
