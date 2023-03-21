package com.exa.companydemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.exa.baselib.utils.L;

import androidx.annotation.NonNull;

/**
 * @Author lsh
 * @Date 2023/3/20 19:23
 * @Description
 */
public class MySurfaceView extends GLSurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    //绘图的Canvas
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;
    private int x = 0, y = 0;
    private Paint mPaint;
    private Path mPath;

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        // super(context, attrs, defStyleAttr);
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPath = new Path();
        //路径起始点(0, 100)
        mPath.moveTo(0, 100);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        L.dd();
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        L.dd();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mIsDrawing = false;
        L.dd();
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawSomething();
            x += 1;
            y = (int) (100 * Math.sin(2 * x * Math.PI / 180) + 400);
            //加入新的坐标点
            mPath.lineTo(x, y);
        }
    }

    private void drawSomething() {
        try {
            //获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas();
            //绘制背景
            mCanvas.drawColor(Color.WHITE);
            //绘制路径
            mCanvas.drawPath(mPath, mPaint);
        } catch (Exception e) {
            L.e("drawSomething err", e);
        } finally {
            if (mCanvas != null) {
                //释放canvas对象并提交画布
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
