package com.jintao.myview;

/**
 * Author: zhanghui
 * CreateDate: 2024/5/20 10:24
 * Description:
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.tj.smarthome.R;

public class ArcProgressBar extends View {

    private Paint paint;
    private RectF rectF;
    private float progress = 30; // 进度值，0-100之间
    private int paintWidth;
    private Bitmap originalBitmap;
    private Bitmap thumbBitmap;
    private Matrix matrix;
    private Path arcPath;
    private PathMeasure pathMeasure;
    private float[] thumbPos;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        paintWidth = ta.getDimensionPixelSize(R.styleable.ArcProgressBar_paint_width, SizeUtils.dp2px(10));
        ta.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(paintWidth); // 画笔宽度
        paint.setStrokeCap(Paint.Cap.ROUND); // 设置画笔为圆角
        matrix = new Matrix();
        rectF = new RectF();
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.air_progress_thumb_icon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int viewWidth;
        int viewHeight;
        // 获取宽度
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {// wrap_content
            viewWidth = SizeUtils.dp2px(280);
        }
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {// wrap_content
            viewHeight = SizeUtils.dp2px(120);
        }
        setMeasuredDimension(viewWidth, viewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 设置圆弧的位置和大小
        rectF.set(paintWidth, paintWidth, width - paintWidth, height * 3 - paintWidth * 4);

        // 绘制灰色背景圆弧
        paint.setColor(0xFFCCCCCC);
        //绘制圆弧总长度,180是半圆从左到右的起始角度，需要不到半圆所以 两边减去20
        int drawArcLenght = 180 - 20 - 20;
        //绘制开始角度
        int startDrawAngle = 180 + 20;
        //绘制进度角度
        float drawArcProgress = drawArcLenght * progress / 100f;

        canvas.drawArc(rectF, startDrawAngle, drawArcLenght, false, paint);
        // 绘制进度圆弧
        paint.setColor(0xFF00FF00); // 进度颜色
        canvas.drawArc(rectF, startDrawAngle, drawArcProgress, false, paint);

        matrix.reset();
        //+90是图片默认方向不对，相差90度，所以需要加上90度
        matrix.postRotate(90 + startDrawAngle + drawArcProgress);
        thumbBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

        arcPath = new Path();//将对应进度的圆弧添加到path
        arcPath.addArc(rectF, startDrawAngle, drawArcProgress);
        //使用 PathMeasure 对象获取圆弧上指定位置的坐标，并将其存储在 pos 数组中
        pathMeasure = new PathMeasure(arcPath, false);
        thumbPos = new float[2];
        pathMeasure.getPosTan(pathMeasure.getLength() - 1, thumbPos, null);

        canvas.drawBitmap(thumbBitmap, thumbPos[0] - thumbBitmap.getWidth() / 2, thumbPos[1] - thumbBitmap.getHeight() / 2, paint);
    }


    // 设置进度值
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100) {
            this.progress = progress;
            invalidate(); // 重新绘制
        }
    }
}

