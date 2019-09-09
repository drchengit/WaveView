package com.example.a14143.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author DrChen
 * @Date 2019/9/9 0009.
 * qq:1414355045
 */
public class MyWaveView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    /**
     * 三段渐变的中点
     */
    private final float gradient_height_mid = 0.28f;
    private final float top_wave_top = 0.28f;
    private final float top_wave_bottom = top_wave_top + 0.06f;
    private final float bottom_wave_bottom = top_wave_bottom;
    private final float bottom_wave_top = top_wave_bottom - 0.05f;
    private SurfaceHolder mHolder;
    /**
     * 与surfaceHolder 绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制线程
     */
    private Thread t;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;
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
    private Wave topWave;

    public MyWaveView(Context context) {
        this(context, null);
    }

    public MyWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        // setZOrderOnTop(true);// 设置画布 背景透明
         mHolder.setFormat(PixelFormat.TRANSLUCENT);

        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);

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
        //防抖动
        wavePaint.setDither(true);


        if (testPaint == null) {
            testPaint = new Paint();
        }
        testPaint.setColor(Color.BLACK);
        testPaint.setStrokeWidth(2);
        testPaint.setStyle(Paint.Style.STROKE);

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
            bottomWave = new Wave(true, bottom_wave_RectF, 0.2f, mHeight, dip2px(6, getContext()),
                    new float[]{0 / 750f, 35 / 50f},
                    new float[]{214 / 750f, 6 / 50f},
                    new float[]{362 / 750f, 28 / 50f},
                    new float[]{582 / 750f, 6 / 50f},
                    new float[]{903 / 750f, 41 / 50f},
                    new float[]{1149 / 750f, 25 / 50f},
                    new float[]{1262 / 750f, 35 / 50f},
                    new float[]{1390 / 750f, 60 / 50f},//最后这个点可以适当调整，保证平滑
                    new float[]{1522 / 750f, 35 / 50f}
//                    new float[]{0 / 750f, 28 / 78f},
//                    new float[]{262 / 750f, 0 / 78f},
//                    new float[]{657 / 750f, 37 / 78f},
//                    new float[]{1052 / 750f, 0 / 78f},
//                    new float[]{1314 / 750f, 28 / 78f},
//                    new float[]{1520 / 750f, 60 / 78f},
//                    new float[]{1709/750f,28/78f}
            );

        }

        if (topWave == null) {
            //我的数据屏幕宽度是750，浪高50, 每个点位后面要加f 不然算出来的数据会全错，因为会丢失小数点
            topWave = new Wave(false, top_wave_RectF, 0.15f, mHeight, dip2px(6, getContext()),
                    new float[]{0 / 750f, 28 / 78f},
                    new float[]{262 / 750f, 0 / 78f},
                    new float[]{657 / 750f, 37 / 78f},
                    new float[]{1052 / 750f, 0 / 78f},
                    new float[]{1314 / 750f, 28 / 78f},
                    new float[]{1520 / 750f, 60 / 78f},
                    new float[]{1709 / 750f, 28 / 78f}

            );

        }


    }

    /**
     * dp转px
     *
     * @param dip     dp
     * @param context 上下文
     * @return
     */
    public static int dip2px(float dip, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);// 4.9->4, 4.1->4, 四舍五入
        return px;
    }



    /**每30帧刷新一次屏幕**/
    public static final int TIME_IN_FRAME = 30;
    @Override
    public void run() {
        while (isRunning) {

            /**取得更新之前的时间**/
            long startTime = System.currentTimeMillis();

            /**在这里加上线程安全锁**/
            synchronized (mHolder) {
                /**拿到当前画布 然后锁定**/
                mCanvas =mHolder.lockCanvas();
                draw();
                /**绘制结束后解锁显示在屏幕上**/
                mHolder.unlockCanvasAndPost(mCanvas);
            }

            /**取得更新结束的时间**/
            long endTime = System.currentTimeMillis();

            /**计算出一次更新的毫秒数**/
            int diffTime  = (int)(endTime - startTime);

            /**确保每次更新时间为30帧**/
            while(diffTime <=TIME_IN_FRAME) {
                diffTime = (int)(System.currentTimeMillis() - startTime);
                /**线程等待**/
                Thread.yield();
            }

        }
    }

    private void draw() {

            if (mCanvas != null) {
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                //绘制背景三段渐变
                drawBackdrop(mCanvas);
                if (isDebug) {
                    mCanvas.drawRect(bottom_wave_RectF, testPaint);
                }



                topWave.draw(mCanvas, wavePaint);
                bottomWave.draw(mCanvas, wavePaint);
            }

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 通知关闭线程
        isRunning = false;
    }
}
