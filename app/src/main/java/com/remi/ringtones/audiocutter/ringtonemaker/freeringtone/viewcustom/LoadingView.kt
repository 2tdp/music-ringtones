package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R

class LoadingView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rectF = RectF()
    private var path: Path
    private var start = 0f
    private var end = 0f
    private var w = 0f
    private var padding = 0f
    private var isCreate = true
    private var animator: ValueAnimator
    private var current = 0f

    init {
        w = resources.displayMetrics.widthPixels / 100f
        padding = 1.11f * w
        path = Path()
        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 1.11f * w
            color = ContextCompat.getColor(context, R.color.main_color)
        }

        animator = ValueAnimator.ofFloat(0f, 1f).apply { }
        animator.addUpdateListener {
            current += width / 20
            if (current > end) current = start
            invalidate()
        }
        animator.apply {
            duration = 500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

        }
        Handler(Looper.getMainLooper()).postDelayed({ animator.start() }, 3000)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreate) {
            isCreate = false
            start = -width.toFloat()
            end = width.toFloat()
            current = start
            rectF.set(padding, (height - padding) / 2f, width - padding, (height + padding) / 2f)
            path.addRoundRect(rectF, padding / 2f, padding / 2f, Path.Direction.CW)
            animator.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.clipPath(path)

        //draw bg
        paint.color = ContextCompat.getColor(context, R.color.gray_main2)
        canvas.drawLine(padding, height / 2f, width - padding, height / 2f, paint)

        //draw load
        paint.color = ContextCompat.getColor(context, R.color.main_color)
        canvas.drawLine(current + padding, height / 2f, current + width - padding, height / 2f, paint)
    }
}