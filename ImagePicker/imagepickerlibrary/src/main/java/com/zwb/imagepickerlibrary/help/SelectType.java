package com.zwb.imagepickerlibrary.help;

/**
 * Created by zwb
 * Description 选择图片的类型
 * Date 2017/6/28.
 */

public enum SelectType {
    /**
     * 单选
     */
    SINGLE(1),
    /**
     * 多选
     */
    MULTIPLE(3);

    private int count;

    SelectType(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
