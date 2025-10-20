package com.example.playlistmaker.player.presentation.fragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.playlistmaker.R
import androidx.core.content.withStyledAttributes

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var isPlaying = false
    private var playIcon: Drawable? = null
    private var pauseIcon: Drawable? = null
    private val iconRect = RectF()

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
        ) {
            playIcon = getDrawable(R.styleable.PlaybackButtonView_playIcon)
            pauseIcon = getDrawable(R.styleable.PlaybackButtonView_pauseIcon)
        }
        isClickable = true
        isFocusable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = minOf(w, h) * 1f
        val left = (w - size) / 2f
        val top = (h - size) / 2f
        val right = left + size
        val bottom = top + size

        iconRect.set(left, top, right, bottom)
        playIcon?.bounds = Rect(
            iconRect.left.toInt(),
            iconRect.top.toInt(),
            iconRect.right.toInt(),
            iconRect.bottom.toInt()
        )
        pauseIcon?.bounds = Rect(
            iconRect.left.toInt(),
            iconRect.top.toInt(),
            iconRect.right.toInt(),
            iconRect.bottom.toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val icon = if (isPlaying) pauseIcon else playIcon
        icon?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)

        return when (event.action) {
            MotionEvent.ACTION_UP -> {
                performClick()
                true
            }

            else -> super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setPlaying(isPlaying: Boolean) {
        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying
            invalidate()
        }
    }
}
