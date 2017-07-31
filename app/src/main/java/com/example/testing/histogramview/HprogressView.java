package com.example.testing.histogramview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by wangchang on 2017/7/28.
 */

public class HprogressView extends View {
    private Paint bPaint;//背景画笔
    private Paint lPaint;//进度画笔
    private Paint tPaint;//文字画笔
    private int pro_color;//进度颜色
    private int bg_color;//背景颜色
    private int txt_color;//文字颜色
    private int txt_size;//文字大小
    private int mViewHeight;
    private int mViewWidth;
    private float progress = 0;//进度值

    public HprogressView(Context context) {
        this(context, null);
    }

    public HprogressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HprogressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HprogressView, defStyleAttr, 0);
        pro_color = typedArray.getColor(R.styleable.HprogressView_pro_color, Color.CYAN);
        bg_color = typedArray.getColor(R.styleable.HprogressView_bg_color, Color.GRAY);
        txt_color = typedArray.getColor(R.styleable.HprogressView_txt_color, Color.BLACK);
        txt_size = (int) typedArray.getDimension(R.styleable.HprogressView_txt_size, sp2px(12));
        typedArray.recycle();
        initPaint();
    }

    //初始化画笔
    private void initPaint() {
        bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bPaint.setColor(bg_color);
        lPaint.setColor(pro_color);
        tPaint.setColor(txt_color);
        tPaint.setAntiAlias(true);
        tPaint.setTextSize(txt_size);
        tPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        if (wMode == MeasureSpec.EXACTLY) {
            mViewWidth = wSize;
        } else {
            mViewWidth = dp2px(240);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            mViewHeight = hSize;
        } else {
            mViewHeight = dp2px(48);
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawPro(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (progress > 0) {
            String str = (int) progress + "%";
            Rect rect = new Rect();
            tPaint.getTextBounds(str, 0, str.length(), rect);
            float x = mViewHeight / 2 + rect.width() / 2 + progress * mViewWidth / 100;
            float y = mViewHeight / 2 + rect.height() / 2;
            float z = mViewWidth - mViewHeight / 2 - rect.width() / 2;
            if (x < z) {
                canvas.drawText(str, x, y, tPaint);
            } else {
                x = mViewWidth/2;
                canvas.drawText(str, x, y, tPaint);
            }
        }
    }

    private void drawPro(Canvas canvas) {
        if (progress > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, mViewWidth * progress / 100, mViewHeight, mViewHeight / 2, mViewHeight / 2, lPaint);
            }

            Log.e("TAG", "drawPro: " + progress);
        }
    }

    public void startAnim() {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(5000);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }

    private void drawBg(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(0, 0, mViewWidth, mViewHeight, mViewHeight / 2, mViewHeight / 2, bPaint);
        }
    }

    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }
}
