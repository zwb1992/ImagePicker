package com.zwb.imagepickerlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zwb.imagepickerlibrary.R;

/**
 * Created by zwb
 * Description
 * Date 17/6/27.
 */

public class PhotoDirHolder extends RecyclerView.ViewHolder {
    public ImageView mPhoto;
    public TextView tvName;
    public TextView tvCount;
    public View mView;

    public PhotoDirHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mPhoto = (ImageView) itemView.findViewById(R.id.img_dir_photo);
        tvName = (TextView) itemView.findViewById(R.id.tv_dir_name);
        tvCount = (TextView) itemView.findViewById(R.id.tv_dir_count);
    }
}
