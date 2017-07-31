package com.example.testing.histogramview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;

/**
 * Created by wangchang on 2017/7/27.
 */

public class HistogramView extends View {
    private Paint xLinePaint;//画坐标轴
    private Paint rectPaint;//直方图
    private Paint tPaint;//文字
    private Paint hLinePaint;//水平虚线
    private int marginTop = dp2px(30);
    private int marginLeft = dp2px(30);
    private int marginRight = dp2px(30);
    private int marginBottom = dp2px(30);
    private int mWidth;//布局宽度
    private int mHeight;//布局高度
    private int mViewWidth;//柱状图宽度
    private int mMargin;//柱状图间隔
    private int itemHeight;//每份的高度
    private int[] aniProgress;// 实现动画的值
    private HistogramAnimation ani;
    //自定义属性
    private int line_color;
    private int text_color;
    private int line_dotted_line;
    private int rect_color;
    //
    private String[] str = {};
    private int[] next = {};
    private int[] progress = {};
    private int index;
    private boolean isShow=false;
    private int value;//分割的份数


    public HistogramView(Context context) {
        this(context, null);
        init();
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HistogramView, defStyleAttr, 0);
        line_color = array.getColor(R.styleable.HistogramView_line_color, Color.BLACK);
        text_color = array.getColor(R.styleable.HistogramView_text_color, Color.BLACK);
        line_dotted_line = array.getColor(R.styleable.HistogramView_line_dotted_line, Color.BLACK);
        rect_color = array.getColor(R.styleable.HistogramView_rect_color, Color.BLACK);
        mViewWidth = (int) array.getDimension(R.styleable.HistogramView_mViewWidth, dp2px(30));
        mMargin = (int) array.getDimension(R.styleable.HistogramView_mMargin, dp2px(0));
        array.recycle();
        init();
    }

    //初始化
    private void init() {
        xLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xLinePaint.setColor(line_color);
        xLinePaint.setStrokeWidth(dp2px(1));
        rectPaint.setColor(rect_color);
        tPaint.setColor(text_color);
        tPaint.setTextSize(sp2px(14));
        tPaint.setTextAlign(Paint.Align.CENTER);
        hLinePaint.setColor(line_dotted_line);
        hLinePaint.setStyle(Paint.Style.STROKE);
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        ani = new HistogramAnimation();
        ani.setDuration(2000);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);//得到模式
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);//得到大小
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);//得到模式
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);//得到大小
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            mWidth = widthSpecSize;
        } else {
            mWidth = dp2px(400);
        }
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = heightSpecSize;
        } else {
            mHeight = dp2px(200);
        }
        if (mMargin == 0) {
            mMargin = (mWidth - marginLeft - marginRight - str.length* mViewWidth) / (str.length+1);
        }
        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        itemHeight = (mHeight - marginTop - marginBottom) / value;
        //画坐标轴
        drawLine(canvas);
        //画直方图
        drawHistogramView(canvas);
        //画底部文字
        drawText(canvas);
        //画水平虚线
        drawDoteLine(canvas);
        //画梯度
        drawProgress(canvas);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(marginLeft, marginTop, marginLeft, mHeight - marginBottom, xLinePaint);
        canvas.drawLine(marginLeft, mHeight - marginBottom, mWidth - marginRight, mHeight - marginBottom, xLinePaint);
        //画箭头，第一种画线
        canvas.drawLine(marginLeft - dp2px(6), marginTop + dp2px(6), marginLeft, marginTop, xLinePaint);
        canvas.drawLine(marginLeft, marginTop, marginLeft + dp2px(6), marginTop + dp2px(6), xLinePaint);
        //第二种drawLines
        float[] points = {mWidth - marginRight - dp2px(6), mHeight - marginBottom - dp2px(6), mWidth - marginRight, mHeight - marginBottom, mWidth - marginRight, mHeight - marginBottom, mWidth - marginRight - dp2px(6), mHeight - marginBottom + dp2px(6)};
        canvas.drawLines(points, xLinePaint);
    }

    private void drawText(Canvas canvas) {
        int y = mHeight - marginBottom + dp2px(15);
        for (int i = 0; i < str.length; i++) {
            int x = (int) (marginLeft + (i + 1) * mMargin + (i + 0.5) * mViewWidth);
            canvas.drawText(str[i], x, y, tPaint);
        }
        if(isShow){
            int top = (int) (mHeight - marginBottom - itemHeight * next[index]);
            int x = (int) (marginLeft + (index + 1) * mMargin + (index + 0.5) * mViewWidth);
            canvas.drawText(next[index]+"", x, top-dp2px(8), tPaint);
        }
    }

    private void drawHistogramView(Canvas canvas) {
        for (int i = 0; i < str.length; i++) {
            int left = marginLeft + (i + 1) * mMargin + i * mViewWidth;
            int right = left + mViewWidth;
            int top = (int) (mHeight - marginBottom - itemHeight * aniProgress[i]);
            int bottom = getHeight() - marginBottom;
            canvas.drawRect(left, top, right, bottom, rectPaint);
        }
    }

    private void drawDoteLine(Canvas canvas) {
        for (int i = 0; i < progress.length; i++) {
            int x = marginLeft;
            int y = (int) (mHeight - marginBottom - progress[i] * itemHeight);
            Path path = new Path();
            path.moveTo(x, y);//起始坐标
            path.lineTo(mWidth - marginRight, y);//终点坐标
            PathEffect effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);//设置虚线的间隔和点的长度
            hLinePaint.setPathEffect(effects);
            canvas.drawPath(path, hLinePaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        for (int i = 0; i < progress.length; i++) {
            Rect rect = new Rect();
            String str = progress[i] + "";
            tPaint.getTextBounds(str, 0, str.length(), rect);
            int x = marginLeft - dp2px(15);
            int y = (int) (mHeight - marginBottom - progress[i] * itemHeight) + rect.height() / 2;
            canvas.drawText(progress[i] + "", x, y, tPaint);
        }
    }

    public void setData(String[] xStep, int[] pro,int[] progress,int max) {
        aniProgress=new int[xStep.length];
        this.str= xStep;
        this.next=pro;
        this.progress=progress;
        this.value=max;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        for (int i=0;i<str.length;i++){
            int left = marginLeft + (i + 1) * mMargin + i * mViewWidth;
            int right = left + mViewWidth;
            if(x>left&&x<right){
                isShow=true;
                index=i;
            }
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
        return super.onTouchEvent(event);
    }

    private class HistogramAnimation extends Animation {
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = (int) (next[i] * interpolatedTime);
                }
            } else {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = next[i];
                }
            }
            invalidate();
        }
    }

    public void startAnim() {
        this.startAnimation(ani);
    }
    private int dp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }

    private int sp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (v * value + 0.5f);
    }
}
