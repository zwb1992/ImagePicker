package com.zwb.imagepickerlibrary.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;

/***************************************
 * Author zhouweibin
 * Description .针对类似grideview的分割线
 * Date:2016/5/3
 ***************************************/
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {


    private int mWidth = 3;//分割线的宽度--dp

    public DividerGridItemDecoration(Context context) {
        mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mWidth, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
                               RecyclerView parent) {
        //设置每个item的上下左右内边距,达到透明分割线的效果
        int spanCount = getSpanCount(parent);
        if (itemPosition == 0) {
            outRect.set(0, mWidth, mWidth, mWidth);
        } else if (itemPosition < spanCount) {//第一行
            outRect.set(0, mWidth, mWidth, mWidth);
        } else if (itemPosition % spanCount == 0) {//第一列
            outRect.set(mWidth, 0, mWidth, mWidth);
        } else {
            outRect.set(0, 0, mWidth, mWidth);
        }
    }
}
