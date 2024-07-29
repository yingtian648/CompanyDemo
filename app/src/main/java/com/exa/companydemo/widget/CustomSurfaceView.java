package com.exa.companydemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.exa.companydemo.R;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private Bitmap bitmap;

    public CustomSurfaceView(Context context) {
        super(context);
        // 获取SurfaceHolder并添加回调
        holder = getHolder();
        holder.addCallback(this);
        // 加载要显示的图片资源
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.win_bg);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 绘制图片
        Canvas canvas = holder.lockCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 绘制图片（如果需要在尺寸改变时更新）

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 释放资源
    }
}
