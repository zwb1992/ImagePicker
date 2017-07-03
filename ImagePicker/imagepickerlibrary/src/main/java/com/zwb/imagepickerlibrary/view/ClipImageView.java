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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
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
    private ImageShapeType imageShapeType;
    /**
     * 圆角矩形角度，角度越大，越圆
     */
    private int cornerPix = 10;
    /**
     * 形状距离边框的距离
     */
    private int borderPadding = 20;
    /**
     * 画笔的宽度
     */
    private int shapePaintWidth;
    private Paint mBgPaint;//背景画笔
    private Paint mShapePaint;//形状画笔
    private Rect mBgRect;
    private Bitmap mShapeBitmap;
    private Canvas mShapeCanvas;
    private Matrix matrix;

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
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();

        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setDither(true);
        mBgPaint.setColor(Color.parseColor("#77000000"));

        mBgRect = new Rect();

        mShapePaint = new Paint();
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setAntiAlias(true);
        mShapePaint.setDither(true);
        mShapePaint.setStrokeWidth(3);
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
        Log.e("info", "--onGlobalLayout-----getDrawable------" + getDrawable());
        if (false) {
            matrix = new Matrix();
            float scale = 1.0f;
            int width = getWidth();
            int height = getHeight();
            Log.e("info", "==onGlobalLayout==width===" + width + ",==height===" + height);
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            Log.e("info", "==onGlobalLayout==dw===" + dw + ",==dh===" + dh);
            //图片的宽度大于控价的宽度，且图片的高度小于控价的高度，缩放宽度
            if (dw > width && dh <= height) {
                scale = width * scale / dw;
            }
            //图片的宽度小于控价的宽度，且图片的高度大于控价的高度，缩放宽度
            if (dw <= width && dh > height) {
                scale = height * scale / dh;
            }
            //图片的宽高都小于或者都大于控件的宽高,取最小缩放比例
            if (dw < width && dh < height) {
                scale = Math.min(width * scale / dw, height * scale / dh);
            }
            if (dw > width && dh > height) {
                scale = Math.min(width * scale / dw, height * scale / dh);
            }
//            mInitScale = scale;
//            mMiddleScale = scale * 2;
//            mMaxScale = scale * 4;
            //先移动到中心点
            matrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            //以图片的中心点开始缩放
            matrix.postScale(scale, scale, width / 2, height / 2);
            setImageMatrix(matrix);
            once = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("info", "--onDraw------" + getDrawable());
        drawShape(canvas);
    }

    private void drawShape(Canvas canvas) {
        if (imageShapeType == null || imageShapeType == ImageShapeType.NO_CLIP) {
            return;
        }
        mShapeBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mShapeCanvas = new Canvas(mShapeBitmap);
        mBgRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mShapeCanvas.drawRect(mBgRect, mBgPaint);
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        mShapePaint.setColor(Color.TRANSPARENT);
        if (imageShapeType == ImageShapeType.ROUND) {
            mShapeCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2 - 40, mShapePaint);
        } else if (imageShapeType == ImageShapeType.ROUND_CORNER) {

        }
        canvas.drawBitmap(mShapeBitmap, 0, 0, null);
        mShapePaint.setXfermode(null);
        mShapePaint.setStyle(Paint.Style.STROKE);
        mShapePaint.setColor(Color.WHITE);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2 - 40, mShapePaint);
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
        Drawable drawable = getDrawable();
        Log.e("info", "--setImageBitmap------" + drawable);
        if (drawable != null) {
            Log.e("info", "--setImageBitmap---getIntrinsicHeight---" + drawable.getIntrinsicHeight());
            Log.e("info", "--setImageBitmap---getIntrinsicWidth---" + drawable.getIntrinsicWidth());
        }
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象
     *
     * @return
     */
    public Bitmap clipForCircle() {
        int borderlength = getMeasuredWidth() - 80;
        Log.e("info", "=====borderlength=======" + borderlength);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        Bitmap bitmap1 = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap1);
        mShapePaint.setStyle(Paint.Style.FILL);
        canvas1.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2 - 40, mShapePaint);
        mShapePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas1.drawBitmap(bitmap, 0, 0, mShapePaint);
        return bitmap1;
    }
}
