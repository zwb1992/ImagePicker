package com.zwb.imagepickerlibrary.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwb
 * Description 相册中的某一个文件
 * Date 2017/6/27.
 */

public class FolderBean {
    private String dir;//基准路径
    private String name;//文件夹的名称
    private String firstImagePath;//该文件夹的第一张图片的路径
    private int count;//文件夹中总共图片的张数
    private List<String> selectedImgs = new ArrayList<>();
    private List<String> mImgs = new ArrayList<>();

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    //第一张图片路径没有肤质的时候才进行赋值
    public void setFirstImagePath(String firstImagePath) {
        if (TextUtils.isEmpty(this.firstImagePath)) {
            this.firstImagePath = firstImagePath;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getSelectedImgs() {
        return selectedImgs;
    }

    public void setSelectedImgs(List<String> selectedImgs) {
        this.selectedImgs = selectedImgs;
    }

    public List<String> getmImgs() {
        return mImgs;
    }

    public void setmImgs(List<String> mImgs) {
        this.mImgs = mImgs;
    }

    @Override
    public String toString() {
        return "FolderBean{" +
                "dir='" + dir + '\'' +
                ", name='" + name + '\'' +
                ", firstImagePath='" + firstImagePath + '\'' +
                ", count=" + count +
                ", selectedImgs=" + selectedImgs +
                ", mImgs=" + mImgs +
                '}';
    }
}
