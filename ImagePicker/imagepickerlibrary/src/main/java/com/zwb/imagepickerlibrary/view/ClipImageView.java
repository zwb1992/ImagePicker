package com.zwb.imagepickerlibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.zwb.imagepickerlibrary.help.ImageShapeType;

/**
 * Created by zwb
 * Description
 * Date 17/7/2.
 */

public class ClipImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    private boolean once = true;
    /**
     * 形状
     */
    private ImageShapeType imageShapeType;
    /**
     * 圆的半径或圆角矩形的边长的一半
     */
    private int mRadius;
    /**
     * 圆角矩形角度，角度越大，越圆
     */
    private int mCornerPix = 10;
    /**
     * 形状距离边框的距离
     */
    private int borderPadding = 40;
    /**
     * 画笔的宽度
     */
    private int mShapePaintWidth = 3;
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 形状画笔
     */
    private Paint mShapePaint;
    /**
     * 背景矩形
     */
    private Rect mBgRect;
    /**
     * 圆角矩形
     */
    private RectF mShapeRect;
    private Bitmap mShapeBitmap;
    private Canvas mShapeCanvas;
    private Matrix matrix;
    /**
     * 原始图片
     */
    private Bitmap srcBitmap;
    /**
     * 初始化时缩放比例
     */
    private float mInitScale;
    /**
     * 双击时最大缩放比例
     */
    private float mMaxScale;

    public ClipImageView(Context context) {
        this(context, null);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCornerPix = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCornerPix, getResources().getDisplayMetrics());
//        mShapePaintWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mShapePaintWidth, getResources().getDisplayMetrics());
        borderPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderPadding, getResources().getDisplayMetrics());
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();

        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setDither(true);
        mBgPaint.setColor(Color.parseColor("#77000000"));

        mBgRect = new Rect();
        mShapeRect = new RectF();

        mShapePaint = new Paint();
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setAntiAlias(true);
        mShapePaint.setDither(true);
        mShapePaint.setStrokeWidth(mShapePaintWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int minSize = Math.min(w, h);
        mRadius = minSize / 2 - borderPadding - mShapePaintWidth;
        mBgRect.set(0, 0, w, h);
        mShapeRect.set(w / 2.0f - mRadius, h / 2.0f - mRadius, w / 2.0f + mRadius, h / 2.0f + mRadius);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            matrix = new Matrix();
            float scale = 1.0f;
            int width = getWidth();
            int height = getHeight();
            Log.e("info", "==onGlobalLayout==width===" + width + ",==height===" + height);
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            drawable.setAlpha(255);
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            Log.e("info", "==onGlobalLayout==dw===" + dw + ",==dh===" + dh);
            int shapeHeight = mRadius * 2 + borderPadding * 2;
            //图片的宽度大于控价的宽度，且图片的高度小于控价的高度，放大高度
            if (dw > width && dh <= shapeHeight) {
                scale = shapeHeight * scale / dh;
            }
            //图片的宽度小于控价的宽度，且图片的高度大于控价的高度，放大宽度
            if (dw <= width && dh > shapeHeight) {
                scale = width * scale / dw;
            }
            //图片的宽高都小于或者都大于控件的宽高,取最大缩放比例
            if (dw < width && dh < shapeHeight) {
                scale = Math.max(width * scale / dw, shapeHeight * scale / dh);
            }
            //图片的宽高都大于控件的宽高,取最小缩放比例
            if (dw > width && dh > height) {
                scale = Math.min(width * scale / dw, height * scale / dh);
            }
            mInitScale = scale;
            mMaxScale = scale * 2;
            //先移动到中心点
            matrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            //以图片的中心点开始缩放
            matrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(matrix);
            once = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawShapeBg();
        canvas.drawBitmap(mShapeBitmap, 0, 0, null);
        drawShape(canvas);
    }

    /**
     * 绘制形状背景--中间镂空，但是看不到图案
     */
    private void drawShapeBg() {
        if (imageShapeType == null || imageShapeType == ImageShapeType.NO_CLIP) {
            return;
        }
        mShapeBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mShapeCanvas = new Canvas(mShapeBitmap);
        mShapeCanvas.drawRect(mBgRect, mBgPaint);
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mShapePaint.setColor(Color.TRANSPARENT);

        if (imageShapeType == ImageShapeType.ROUND) {
            mShapeCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mRadius, mShapePaint);
        } else if (imageShapeType == ImageShapeType.ROUND_CORNER) {
            mShapeCanvas.drawRoundRect(mShapeRect, mCornerPix, mCornerPix, mShapePaint);
        }
    }

    /**
     * 绘制形状，可以看到边框为白色
     *
     * @param canvas 画布
     */
    private void drawShape(Canvas canvas) {
        if (imageShapeType == null || imageShapeType == ImageShapeType.NO_CLIP) {
            return;
        }
        mShapePaint.setXfermode(null);
        mShapePaint.setStyle(Paint.Style.STROKE);
        mShapePaint.setColor(Color.WHITE);
        if (imageShapeType == ImageShapeType.ROUND) {
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mRadius, mShapePaint);
        } else if (imageShapeType == ImageShapeType.ROUND_CORNER) {
            canvas.drawRoundRect(mShapeRect, mCornerPix, mCornerPix, mShapePaint);
        }
    }

    /**
     * 设置裁剪类型
     *
     * @param imageShapeType 裁剪类型
     */
    public void setImageShapeType(ImageShapeType imageShapeType) {
        this.imageShapeType = imageShapeType;
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        once = true;
        super.setImageBitmap(bm);
        srcBitmap = bm;
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象
     *
     * @return bitmap
     */
    public Bitmap clipBitmap() {
        if (imageShapeType == null || imageShapeType == ImageShapeType.NO_CLIP) {
            return srcBitmap;
        }
        if (getDrawable() == null) {
            return null;
        }
        int borderLength = mRadius * 2;
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        Bitmap srcBitmap = Bitmap.createBitmap(bitmap, (getMeasuredWidth() - borderLength) / 2, (getMeasuredHeight() - borderLength) / 2, borderLength, borderLength);
        bitmap.recycle();//回收原来的图片

        Bitmap bitmap1 = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap1);
        mShapePaint.setStyle(Paint.Style.FILL);
        if (imageShapeType == ImageShapeType.ROUND) {
            canvas1.drawCircle(srcBitmap.getWidth() / 2, srcBitmap.getHeight() / 2, srcBitmap.getWidth() / 2, mShapePaint);
        } else {
            RectF rectF = new RectF(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
            canvas1.drawRoundRect(rectF, mCornerPix, mCornerPix, mShapePaint);
        }
        mShapePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas1.drawBitmap(srcBitmap, 0, 0, mShapePaint);
        return bitmap1;
    }
}
