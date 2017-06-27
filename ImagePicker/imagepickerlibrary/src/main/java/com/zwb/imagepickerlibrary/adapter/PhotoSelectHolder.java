package com.zwb.imagepickerlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.zwb.imagepickerlibrary.R;

/**
 * Created by zwb
 * Description
 * Date 2017/6/27.
 */

public class PhotoSelectHolder extends RecyclerView.ViewHolder {
    public ImageView mPhoto;
    public ImageButton mPhotoSelect;

    public PhotoSelectHolder(View itemView) {
        super(itemView);
        mPhoto = (ImageView) itemView.findViewById(R.id.image);
        mPhotoSelect = (ImageButton) itemView.findViewById(R.id.imgSelect);
    }
}
