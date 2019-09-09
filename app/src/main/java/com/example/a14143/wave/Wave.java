package com.example.a14143.wave;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * @author DrChen
 * @Date 2019/4/21.
 * qq:1414355045
 * 保存波浪的所有信息
 * 和绘制
 */
public class Wave {
 
    /**
     * 单个波浪宽
     */
    private float waveWith;
    /**
     * 绘制的方向
     */
    boolean isLeft = true;
    /**
     * 单个循环长度占屏幕宽度的百分比（浪的长度等）
     */
    private float waveWithF;
    /**
     * 绘制的区域
     */
    private RectF rectF;

    /**
     * 当前移动位置计次
     */
    private int currentMoveX;

    /**
     * 每次移动的距离
     */
    private int moveX;
    /**
     * 移动前的点位
     */
    private float[][] wavePoints;

    private boolean isDebug = false;
    /**
     * 切率
     */
    private float lineSmoothness = 0.12f;
    
    public Path path = new Path();



    private int mHeight;


    /**
     *
     * @param isLeft 向左向右
     * @param rectF  绘制区域
     * @param points
     */
    public Wave(boolean isLeft, RectF rectF,float lineSmoothness,int mHeight,int moveX,float[]... points) {

        this.isLeft = isLeft;
        this.mHeight = mHeight;
        if(lineSmoothness>0) this.lineSmoothness = lineSmoothness;
        setRectF(rectF);
        setPoints(points);
        //每次移动的距离
        if (isLeft) {
            this.moveX = - moveX;

        } else {
           this. moveX = moveX;
        }


        measurePath();
    }


    private void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    /**
     * 传入数据只有控制点，没有贝赛尔的起点，通过控制点计算各个点位
     */
    private void setPoints(float[]... points) {


        if (points.length < 4 ) {
            throw new RuntimeException("所有点不能小于4个");  //直接手动抛出异常
        }
        if (rectF == null) {
            throw new RuntimeException("没有绘制区域就没有绘制");
        }


        if (wavePoints == null) {
            wavePoints = new float[points.length][6];
        }
    
        waveWithF = points[points.length-1][0];
        waveWith = rectF.width()* waveWithF;
        // 下面是利用各个点连成一条平滑的曲线
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        int lineSize = points.length;
        for (int i = 0; i < points.length; i++) {
            if (Float.isNaN(currentPointX)) {

                currentPointX = points[i][0];
                currentPointY = points[i][1];
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (i > 0) {
                    previousPointX = points[i - 1][0];
                    previousPointY = points[i - 1][1];
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (i > 1) {

                    prePreviousPointX = points[i][0];
                    prePreviousPointY = points[i][1];
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (i < lineSize - 1) {

                nextPointX = points[i + 1][0];
                nextPointY = points[i + 1][1];
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (i == 0||i>=points.length-2) {//起点和最后面接头的地方，为了保证平滑单独处理
                // 将Path移动到开始点
                wavePoints[i][0] = currentPointX*rectF.width();
                wavePoints[i][1] = currentPointY*rectF.height()+rectF.top;

                 
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (lineSmoothness * firstDiffX);
                final float firstControlPointY = previousPointY + (lineSmoothness * firstDiffY);
                final float secondControlPointX = currentPointX - (lineSmoothness * secondDiffX);
                final float secondControlPointY = currentPointY - (lineSmoothness * secondDiffY);

                wavePoints[i][0] = firstControlPointX*rectF.width();
                wavePoints[i][1] = firstControlPointY*rectF.height()+rectF.top;
                wavePoints[i][2] = secondControlPointX*rectF.width();
                wavePoints[i][3] = secondControlPointY*rectF.height()+rectF.top;
                wavePoints[i][4] = currentPointX*rectF.width();
                wavePoints[i][5] = currentPointY*rectF.height()+rectF.top;
 
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }


    }

    /**
     * 测试时需要
     */
    public void testMoveTo(float moveX) {

        for (int i = 0; i < wavePoints.length; i++) {
            wavePoints[i][0] += moveX * waveWith;
            wavePoints[i][2] += moveX * waveWith;
            wavePoints[i][4] += moveX * waveWith;
        }
    }

    /**
     * 刷新每次移动的点位
     */
    public void measureMoveTo() {
       if(isLeft){
           if(waveWith + currentMoveX < Math.abs(moveX)){
               currentMoveX = 0;
           }else {
               currentMoveX+=moveX;
           }
       }else {
           if(waveWith - currentMoveX< moveX){
               currentMoveX = 0;
           }else {
               currentMoveX+= moveX;
           }

       }

    }



    /**
     *
     *  绘制path
     */
    private void measurePath() {

        path.reset();
        if(isLeft){
            path.moveTo(wavePoints[0][0], mHeight);
            path.lineTo(wavePoints[0][0], wavePoints[0][1]);

            for (int i = 1; i < wavePoints.length-2  ; i ++) {

                path.cubicTo(wavePoints[i][0], wavePoints[i][1],
                        wavePoints[i][2], wavePoints[i][3],
                        wavePoints[i][4], wavePoints[i ][5]);
            }
            //最后面接头处理
            path.quadTo(wavePoints[wavePoints.length-2][0],wavePoints[wavePoints.length-2][1],
                    wavePoints[wavePoints.length-1][0],wavePoints[wavePoints.length-1][1]
            );
            for (int i = 1; i < wavePoints.length-2  ; i ++) {

                path.cubicTo(wavePoints[i][0]+waveWith, wavePoints[i][1],
                        wavePoints[i][2]+waveWith, wavePoints[i][3],
                        wavePoints[i][4]+waveWith, wavePoints[i ][5]);
            }
            path.lineTo( waveWith+wavePoints[wavePoints.length-1][0], mHeight);
            path.close();

        }else {
            path.moveTo(wavePoints[0][0]-waveWith, mHeight);
            path.lineTo(wavePoints[0][0]-waveWith, wavePoints[0][1]);

            for (int i = 1; i < wavePoints.length-2  ; i ++) {

                path.cubicTo(wavePoints[i][0]-waveWith, wavePoints[i][1],
                        wavePoints[i][2]-waveWith, wavePoints[i][3],
                        wavePoints[i][4]-waveWith, wavePoints[i ][5]);
            }
            //最后面接头处理
            path.quadTo(wavePoints[wavePoints.length-2][0]-waveWith,wavePoints[wavePoints.length-2][1],
                    wavePoints[wavePoints.length-1][0]-waveWith,wavePoints[wavePoints.length-1][1]
            );

            for (int i = 1; i < wavePoints.length-2  ; i ++) {

                path.cubicTo(wavePoints[i][0], wavePoints[i][1],
                        wavePoints[i][2], wavePoints[i][3],
                        wavePoints[i][4], wavePoints[i ][5]);
            }
            path.lineTo( wavePoints[wavePoints.length-1][0], mHeight);
            path.close();
            }

    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.translate(currentMoveX,0);
        canvas.drawPath(path,paint);
        measureMoveTo();
        canvas.restore();

    }
}
