package com.exa.baselib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.exa.baselib.R;
import com.exa.baselib.utils.L;

import androidx.annotation.NonNull;

/**
 * @Author lsh
 * @Date 2023/3/20 19:23
 * @Description
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    //绘图的Canvas
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;
    private int x = 0, y = 0;
    private Paint mPaint;
    private Path mPath;
    private Bitmap bitmap;
    private int drawTimes = 0;

    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPath = new Path();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win_bg);
        //路径起始点(0, 100)
        mPath.moveTo(0, 100);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mSurfaceHolder = getHolder();
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        L.dd(getClass().getSimpleName());
        startDraw();
    }

    private void startDraw() {
        drawTimes = 0;
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        L.dd(getClass().getSimpleName() + " onLayout:(left,top)=(" + left + "," + top + ")");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        L.dd(getClass().getSimpleName());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        L.dd(getClass().getSimpleName());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mIsDrawing = false;
        L.dd(getClass().getSimpleName());
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawSomething();
            x += 1;
            y = (int) (100 * Math.sin(2 * x * Math.PI / 180) + 400);
            //加入新的坐标点
            mPath.lineTo(x, y);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void drawSomething() {
        try {
            //获得canvas对象
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mCanvas = mSurfaceHolder.lockHardwareCanvas();
            }else {
                mCanvas = mSurfaceHolder.lockCanvas();
            }
            //绘制背景
            mCanvas.drawColor(Color.WHITE);
            //绘制路径
//            mCanvas.drawPath(mPath, mPaint);
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectFT = new RectF(0, 0, getWidth(), getHeight());
            mCanvas.drawBitmap(bitmap, rect, rectFT, mPaint);
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
