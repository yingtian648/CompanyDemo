package com.exa.companydemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.exa.companydemo.R


/**
 * @Author lsh
 * @Date 2023/7/25 17:17
 * @Description
 */
class MySeekBar(context: Context, attr: AttributeSet?) : View(context, attr, 0) {
    private val mWidth = context.resources.getDimension(R.dimen.panel_seekbar_width).toInt()
    private val mHeight = context.resources.getDimension(R.dimen.panel_seekbar_height).toInt()
    private val mRadius = context.resources.getDimension(R.dimen.panel_seekbar_radius).toInt()
    private val mBackgroundColor = context.getColor(R.color.panel_seekbar_bg)
    private val mProgressStartColor = context.getColor(R.color.panel_seekbar_progress_start)
    private val mProgressEndColor = context.getColor(R.color.panel_seekbar_progress_end)

    //设置无锯齿 也可以使用setAntiAlias(true)
    private val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val max = 300
    private var mProgress = 30

    private var mStartX = 0F
    private var mStartY = 0F
    private var mProgressX = 0F
    private var mCanSeek = false
    private var mListener: OnProgressChangeListener? = null
    private var mLastReportTime: Long = 0L

    private val mBgRect = RectF(0F, 0F, mWidth.toFloat(), mHeight.toFloat())
    private var mPRect = RectF(0F, 0F, mWidth.toFloat(), mHeight.toFloat())

    companion object {
        const val TAG = "MySeekBar"
        const val DEBUG = false

        /** 可滑动距离判断 */
        const val SLIDE_JUDGE_DISTANCE = 10

        /** 进度变化上报频率 */
        const val REPORT_CHANGE_RATE = 100
    }

    init {
        mBgPaint.color = mBackgroundColor
        mBgPaint.style = Paint.Style.FILL

        mProgressPaint.style = Paint.Style.FILL
        mProgressPaint.shader = LinearGradient(
            0F,
            0F,
            mWidth.toFloat() * mProgress / max,
            0f,
            mProgressStartColor, mProgressEndColor, Shader.TileMode.CLAMP
        )
    }

    interface OnProgressChangeListener {
        fun onChanged(max: Int, progress: Int)
    }

    fun setListener(listener: OnProgressChangeListener) {
        mListener = listener
    }

    fun setProgress(progress: Int) {
        var temp = progress
        if (progress > max) {
            temp = max
        } else if (progress < 0) {
            temp = 0
        }
        if (mProgress != temp) {
            mProgress = temp
            invalidate()
        }
    }

    /**
     * 上报滑动进度变化
     */
    private fun reportProgressChanged(progress: Int, complete: Boolean) {
        if (complete
            || (mLastReportTime == 0L
                    || ((System.currentTimeMillis() - mLastReportTime) > REPORT_CHANGE_RATE))
        ) {
            mLastReportTime = System.currentTimeMillis()
            mListener?.apply {
                post {
                    onChanged(max, progress)
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    checkCanSeek(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mCanSeek) {
                        mProgressX += (x - mStartX)
                        if (mProgressX > mWidth) {
                            mProgressX = mWidth.toFloat()
                        } else if (mProgressX < 0) {
                            mProgressX = 0F
                        }
                        mProgress = (mProgressX / mWidth * max).toInt()
                        if (DEBUG) {
                            Log.d(
                                TAG, "onTouchEvent ACTION_MOVE: + mProgressX=$mProgressX" +
                                        "mProgress=$mProgress dis=${((x - mStartX))}"
                            )
                        }
                        reportProgressChanged(mProgress, false)
                        mStartX = x
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (DEBUG) {
                        Log.d(TAG, "onTouchEvent ACTION_UP")
                    }
                    mCanSeek = false
                    reportProgressChanged(mProgress, true)
                }
            }
        }
        return true
    }

    /**
     * 校验是否可已滑动
     * 触摸点在当前进度前后10px之内则可以滑动，否则视为无效
     */
    private fun checkCanSeek(x: Float, y: Float) {
        mStartX = x
        mStartY = y
        mCanSeek = (x >= (mProgressX - SLIDE_JUDGE_DISTANCE)
                && x <= (mProgressX + SLIDE_JUDGE_DISTANCE))
        if (DEBUG) {
            Log.d(TAG, "checkCanSeek:$mCanSeek  mProgressX=${mProgressX} x=$x")
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw background
        canvas.drawRoundRect(mBgRect, mRadius.toFloat(), mRadius.toFloat(), mBgPaint)
        // draw progress
        if (!mCanSeek) {
            mProgressX = mWidth.toFloat() * mProgress / max
        }
        mPRect = RectF(0F, 0F, mProgressX, mHeight.toFloat())
        canvas.drawRoundRect(mPRect, mRadius.toFloat(), mRadius.toFloat(), mProgressPaint)
    }
}