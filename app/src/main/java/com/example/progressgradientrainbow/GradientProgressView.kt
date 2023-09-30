package com.example.progressgradientrainbow


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GradientProgressView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint: Paint = Paint()
    private val path: Path = Path()
    private var myProgress: Float = 0f
    private var animationJob: Job? = null
    private val animationDuration: Long = 3000
    private val cornerRadius: Float = 0f

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }

    fun startInfiniteProgressAnimation() {
        animationJob?.cancel()
        animationJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                animateProgress(1f, animationDuration)
                myProgress = 0f
            }
        }
    }

    private suspend fun animateProgress(targetProgress: Float, durationMillis: Long) {
        val steps = 60
        val delay = durationMillis / steps

        for (i in 0 until steps) {
            myProgress = lerp(myProgress, targetProgress, (i + 1).toFloat() / steps)
            invalidate()
            delay(delay)
        }
    }

    private fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + fraction * (end - start)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val colors = intArrayOf(
            Color.MAGENTA, Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.BLUE
        )

        val left = 0f
        val top = 0f
        val right = width.toFloat() * myProgress
        val bottom = height.toFloat()

        path.reset()
        path.moveTo(left + cornerRadius, top)
        path.lineTo(right - cornerRadius, top)
        path.arcTo(
            RectF(right - 2 * cornerRadius, top, right, top + 2 * cornerRadius),
            -90f, 90f
        )
        path.lineTo(right, bottom - cornerRadius)
        path.arcTo(
            RectF(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom),
            0f, 90f
        )
        path.lineTo(left + cornerRadius, bottom)
        path.arcTo(
            RectF(left, bottom - 2 * cornerRadius, left + 2 * cornerRadius, bottom),
            90f, 90f
        )
        path.lineTo(left, top + cornerRadius)
        path.arcTo(
            RectF(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius),
            180f, 90f
        )
        path.close()

        val shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            colors, null, Shader.TileMode.CLAMP
        )

        paint.shader = shader
        canvas?.drawPath(path, paint)
    }
}