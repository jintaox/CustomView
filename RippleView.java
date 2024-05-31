package com.jintao.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 水波纹扩散效果，涟漪扩散
 */
public class RippleView extends View {

    private Paint centerPaint; //中心圆paint
    private Paint spreadPaint; //扩散圆paint
    private float centerXY;//圆心x
    private int mSpeed;// 圆圈扩散的速度
    private int centerRadius = 100; //中心圆半径
    private int mCircleDensity = 100; //圆的密度
    private int delayMilliseconds = 8;//扩散延迟间隔，越大扩散越慢
    //扩散圆层级数，元素为扩散的距离
    private List<CircleInfo> spreadCircleList;
    private int mViewWidth;
    private int layer = 6;
    private boolean isStartDraw = true;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        spreadCircleList = new ArrayList<>();
        int mColor = 0xFFC8C8C8;

        centerPaint = new Paint();
        centerPaint.setColor(mColor);
        centerPaint.setAntiAlias(true);

        //最开始不透明且扩散距离为0
        spreadCircleList.add(new CircleInfo(0, 255));

        spreadPaint = new Paint();
        spreadPaint.setAntiAlias(true);
        spreadPaint.setAlpha(255);
        spreadPaint.setColor(mColor);
        setLayerType(View.LAYER_TYPE_HARDWARE, spreadPaint);
        // 设置View的圆为半透明
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        // 获取宽度
        if (myWidthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent
            mViewWidth = myWidthSpecSize;
        } else {
            // wrap_content
            mViewWidth = SizeUtils.dp2px(120);
        }
        //圆心位置
        centerXY = mViewWidth / 2;

        mCircleDensity = (int) ((mViewWidth / layer) * 0.8);
        centerRadius = mCircleDensity / 2;
        mSpeed = (int) Math.round(mCircleDensity / 45.0);

        setMeasuredDimension(mViewWidth, mViewWidth);
    }

    public void setDrawStatus(boolean isStartDraw) {
        this.isStartDraw = isStartDraw;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < spreadCircleList.size(); i++) {
            CircleInfo circleInfo = spreadCircleList.get(i);
            int alpha = circleInfo.alpha;
            spreadPaint.setAlpha(alpha);
            int width = circleInfo.width;
            //绘制扩散的圆
            canvas.drawCircle(centerXY, centerXY, centerRadius + width, spreadPaint);
            //每次扩散圆半径递增，圆透明度递减
            if (isStartDraw) {
                if (alpha > 0 && width < mViewWidth) {
                    double degree = 255 - circleInfo.width * (255 / ((double) mViewWidth / 2)) - 20;
                    if (degree < 0) {
                        degree = 0;
                    }
                    circleInfo.alpha = (int) degree;
                    circleInfo.width = width + mSpeed;
                    spreadCircleList.set(i, circleInfo);
                }
            }

        }

        //当最外层扩散圆半径达到最大半径时添加新扩散圆
        if (spreadCircleList.size() <= layer && spreadCircleList.get(spreadCircleList.size() - 1).width > mCircleDensity) {
            spreadCircleList.add(new CircleInfo(0, 255));
        }
        //超过8个扩散圆，删除最先绘制的圆，即最外层的圆
        if (spreadCircleList.size() > layer) {
            spreadCircleList.remove(0);
        }
        //中间的圆
        canvas.drawCircle(centerXY, centerXY, centerRadius, centerPaint);
        //延迟更新，达到扩散视觉差效果
        postInvalidateDelayed(delayMilliseconds);
    }

    class CircleInfo {
        CircleInfo(int width, int alpha) {
            this.width = width;
            this.alpha = alpha;
        }

        int width;
        int alpha;
    }
}