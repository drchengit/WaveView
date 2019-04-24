package com.example.a14143.wave;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author DrChen
 * @Date 2019/4/16 0016.
 * qq:1414355045
 * 三段渐变波纹
 */
public class WaveView extends View {
    /**
     * 三段渐变的中点
     */
    private final float gradient_height_mid = 0.28f;

    private final float top_wave_top = 0.28f;
    private final float top_wave_bottom = top_wave_top + 0.06f;
    private final float bottom_wave_bottom = top_wave_bottom ;
    private final float bottom_wave_top = top_wave_bottom - 0.05f;


    /**
     * 这个是背景的渐变色和波浪的颜色
     */
    private int topColor, midColor, bottomColor, waveColor;
    /**
     * 控件的宽高
     */
    private int mHeight, mWidth;
    /**
     * 渐变画笔
     */
    private Paint gradientPaint;
    /**
     * 波浪的画笔
     */
    private Paint wavePaint;
    /**
     * 绘制波浪式辅助的画笔（可以去掉）
     */
    private Paint testPaint;
    /**
     * 是否开启辅助的画笔
     */
    private boolean isDebug = false;
    /**
     * view 是否被删除
     */
    private boolean onPause = false;

    /**
     * 整个视图的背景是三种颜色的渐变，蓝到紫的区域
     */
    private RectF gradient_bottom;
    /**
     * 紫到白的区域
     */
    private RectF gradient_top;

    /**
     * 波浪绘制区域
     */
    private RectF bottom_wave_RectF;
    private RectF top_wave_RectF;


    private LinearGradient oneGradient;
    private LinearGradient twoGradient;

    private Wave bottomWave;
    private Path bottomPath = new Path();
    private Wave topWave;
    private Path topPath = new Path();


    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        topColor = Color.parseColor("#3851DF");
        midColor = Color.parseColor("#937EF7");
        bottomColor = Color.parseColor("#FFFFFF");
        waveColor = Color.parseColor("#80FFFFFF");
        if (gradientPaint == null) {
            gradientPaint = new Paint();
        }
        gradientPaint.setStyle(Paint.Style.FILL);
        if (gradient_bottom == null) gradient_bottom = new RectF();
        if (gradient_top == null) gradient_top = new RectF();
        if (bottom_wave_RectF == null) bottom_wave_RectF = new RectF();
        if (top_wave_RectF == null) top_wave_RectF = new RectF();

        if (wavePaint == null) wavePaint = new Paint();
        wavePaint.setColor(waveColor);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setAntiAlias(true);


        if (testPaint == null) {
            testPaint = new Paint();
        }
        testPaint.setColor(Color.BLACK);
        testPaint.setStrokeWidth(2);
        testPaint.setStyle(Paint.Style.STROKE);


    }

    public void onResume() {
        onPause = false;
        invalidate();
    }

    public void onPause() {
        onPause = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        gradient_bottom.right = mWidth;
        gradient_bottom.bottom = mHeight * gradient_height_mid;
        gradient_top.top = mHeight * gradient_height_mid;
        gradient_top.bottom = mHeight;
        gradient_top.right = mWidth;

        bottom_wave_RectF.top = mHeight * bottom_wave_top;
        bottom_wave_RectF.right = mWidth;
        bottom_wave_RectF.bottom = mHeight * bottom_wave_bottom;
        top_wave_RectF.top = mHeight * top_wave_top;
        top_wave_RectF.right = mWidth;
        top_wave_RectF.bottom = mHeight * top_wave_bottom;


        if (bottomWave == null) {
            //我的数据屏幕宽度是750，浪高50, 每个点位后面要加f 不然算出来的数据会全错，因为会丢失小数点
            bottomWave = new Wave( true, bottom_wave_RectF,0.12f,mHeight,dip2px(5,getContext()),
                    new float[]{0 / 750f, 35 / 50f},
                    new float[]{214 / 750f, 6 / 50f},
                    new float[]{362 / 750f, 28 / 50f},
                    new float[]{582 / 750f, 6 / 50f},
                    new float[]{903 / 750f, 41 / 50f},
                    new float[]{1149 / 750f, 25 / 50f},
                    new float[]{1262/750f,35/50f},
                    new float[]{1390/750f,60/50f},//最后这个点可以适当调整，保证平滑
            new float[]{1522/750f,35/50f}
//                    new float[]{0 / 750f, 28 / 78f},
//                    new float[]{262 / 750f, 0 / 78f},
//                    new float[]{657 / 750f, 37 / 78f},
//                    new float[]{1052 / 750f, 0 / 78f},
//                    new float[]{1314 / 750f, 28 / 78f},
//                    new float[]{1520 / 750f, 60 / 78f},
//                    new float[]{1709/750f,28/78f}
            );

        }

        if(topWave==null){
            //我的数据屏幕宽度是750，浪高50, 每个点位后面要加f 不然算出来的数据会全错，因为会丢失小数点
            topWave = new Wave(false, top_wave_RectF,0.1f,mHeight,dip2px(2,getContext()),
                    new float[]{0 / 750f, 28 / 78f},
                    new float[]{262 / 750f, 0 / 78f},
                    new float[]{657 / 750f, 37 / 78f},
                    new float[]{1052 / 750f, 0 / 78f},
                    new float[]{1314 / 750f, 28 / 78f},
                    new float[]{1520 / 750f, 60 / 78f},
                    new float[]{1709/750f,28/78f}

            );

        }



    }
    /**
     * dp转px
     * @param dip       dp
     * @param context   上下文
     * @return
     */
    public static int dip2px(float dip, Context context) { float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);// 4.9->4, 4.1->4, 四舍五入
        return px;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);


        //绘制背景三段渐变
        drawBackdrop(canvas);
        if (isDebug) {
            canvas.drawRect(bottom_wave_RectF, testPaint);
        }
//        topWave.testMoveTo(0.4f);
//        bottomWave.testMoveTo(0.6f);



        topWave.draw(canvas,wavePaint);
        bottomWave.draw(canvas,wavePaint);







        if (onPause) {
            return;
        }
      postInvalidateDelayed(10);


    }



    @Override
    protected void onDetachedFromWindow() {
        onPause = true;
        super.onDetachedFromWindow();

    }

    /**
     * 绘制三段渐变背景
     *
     * @param canvas
     */
    private void drawBackdrop(Canvas canvas) {
        if (oneGradient == null)
            oneGradient = new LinearGradient(gradient_bottom.centerX(), gradient_bottom.top, gradient_bottom.centerX(), gradient_bottom.bottom, topColor, midColor, Shader.TileMode.MIRROR);
        if (twoGradient == null)
            twoGradient = new LinearGradient(gradient_top.centerX(), gradient_top.top, gradient_top.centerX(), gradient_top.bottom, midColor, bottomColor, Shader.TileMode.MIRROR);
        gradientPaint.setShader(oneGradient);
        canvas.drawRect(gradient_bottom, gradientPaint);
        gradientPaint.setShader(twoGradient);
        canvas.drawRect(gradient_top, gradientPaint);
    }






}

