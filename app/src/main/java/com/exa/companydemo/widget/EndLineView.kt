package com.exa.companydemo.widget

import android.annotation.IntDef
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

/**
 * @Author lsh
 * @Date 2023/9/14 10:21
 * @Description
 * 可使用 android:orientation="vertical/horizontal" 来设置方向
 */
class EndLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    companion object {
        const val TAG = "EndLineView"
        const val DEBUG = true

        /** 方向-水平（横向 平行于X轴） */
        const val HORIZONTAL = LinearLayout.HORIZONTAL

        /** 方向-垂直（纵向 平行于Y轴） */
        const val VERTICAL = LinearLayout.VERTICAL
    }

    @IntDef(HORIZONTAL, VERTICAL)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OrientationMode

    //设置无锯齿 也可以使用setAntiAlias(true)
    private val mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mTextPaint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
    private var mLineColor = Color.RED
    private val mPoint = PointF(-1f, -1f)
    private var mLastDrawTime = 0L

    // 接收属性 android:orientation="vertical"
    private var mOrientation = VERTICAL

    init {
        focusable = FOCUSABLE_AUTO
        isClickable = true
        mLinePaint.color = mLineColor
        mLinePaint.style = Paint.Style.FILL
        mLinePaint.strokeWidth = 1F

        mTextPaint.color = mLineColor
        mTextPaint.textSize = 22F
        // 读取方向
        attrs?.apply {
            for (i in 0 until attributeCount) {
                if (getAttributeName(i) == "orientation") {
                    setOrientation(getAttributeValue(i).toInt())
                }
                if (DEBUG) {
                    Log.d(TAG, "attrs $i ${getAttributeName(i)}=${getAttributeValue(i)}")
                }
            }
        }

    }

    /**
     * Should the layout be a column or a row.
     * @param orientation Pass [.HORIZONTAL] or [.VERTICAL]. Default
     * value is [.HORIZONTAL].
     *
     * @attr ref android.R.styleable#LinearLayout_orientation
     */
    fun setOrientation(@OrientationMode orientation: Int) {
        if (mOrientation != orientation && (orientation == HORIZONTAL || orientation == VERTICAL)) {
            mOrientation = orientation
        }
    }

    fun setLincColor(color: Int) {
        mLineColor = color
        mLinePaint.color = color
        mTextPaint.color = color
    }

    fun clear() {
        mPoint.x = -1f
        mPoint.y = -1f
        invalidate()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (action) {
                MotionEvent.ACTION_DOWN -> checkAndDrawLine(this)
                MotionEvent.ACTION_MOVE -> checkAndDrawLine(this)
                else -> Unit
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun checkAndDrawLine(event: MotionEvent) {
        event.apply {
            if (mPoint.x != x || mPoint.y != y) {
                mPoint.x = x
                mPoint.y = y
                if (mLastDrawTime == 0L || (System.currentTimeMillis() - mLastDrawTime) > 100) {
                    mLastDrawTime = System.currentTimeMillis()
                    invalidate()
                    Log.i(TAG, "点击：(${x},${y}) " + "方向：${getDirection(mOrientation)}")
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mPoint.x >= 0 && mPoint.y >= 0) {
            if (mOrientation == VERTICAL) {
                canvas?.drawLine(mPoint.x, 0f, mPoint.x, height.toFloat(), mLinePaint)
                canvas?.drawText(
                    mPoint.x.toInt().toString() + "," + mPoint.y.toInt().toString(),
                    mPoint.x + 5,
                    height.toFloat() - 20,
                    mTextPaint
                )
            } else {
                canvas?.drawLine(0f, mPoint.y, width.toFloat(), mPoint.y, mLinePaint)
                canvas?.drawText(
                    mPoint.x.toInt().toString() + "," + mPoint.y.toInt().toString(),
                    10F,
                    mPoint.y - 5,
                    mTextPaint
                )
            }
        }
    }

    private fun getDirection(direction: Int) =
        if (direction == VERTICAL) {
            "VERTICAL"
        } else {
            "HORIZONTAL"
        }
}