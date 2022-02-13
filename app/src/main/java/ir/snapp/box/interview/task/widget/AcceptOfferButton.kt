package ir.snapp.box.interview.task.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.cardview.widget.CardView

class AcceptOfferButton(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    private val rippleView: RippleView = RippleView(context)
    private val animators = mutableListOf<Animator>()
    private val animatorSet = AnimatorSet()
    private var isRippleViewRemoved = false
    private lateinit var progressAnimation: ValueAnimator
    private var progressPaint: Paint = Paint()
    private var progressRect: Rect = Rect()
    private lateinit var progressAnimationListener: ValueAnimator.AnimatorUpdateListener
    private var isButtonPressed: Boolean = false
    private var isAnimationStopped: Boolean = false
    private val buttonOnLongClickAction: () -> Unit = {
        if (isButtonPressed) {
            if (listener != null) {
                listener?.onPerform()
            }
        }
    }

    init {
        progressPaint.style = Paint.Style.FILL
        progressPaint.color = Color.parseColor("#00246b")
    }

    var listener: LongClickListener? = null

    fun setLongClickListener(listener: LongClickListener) {
        this.listener = listener
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun startInterval() {
        progressAnimationListener = ValueAnimator.AnimatorUpdateListener {
            val width = it.animatedValue as Int
            progressRect.left = 0
            progressRect.top = 0
            progressRect.bottom = height
            if (!isAnimationStopped) {
                progressRect.right = width
                invalidate()
            } else {
                progressAnimation.removeUpdateListener(progressAnimationListener)
            }
        }
        progressAnimation = ObjectAnimator.ofInt(0, width)
        progressAnimation.duration = 30000
        progressAnimation.repeatCount = 0
        progressAnimation.addUpdateListener(progressAnimationListener)
        progressAnimation.start()
        isAnimationStopped = false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        performClick()
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                pressDown()
            }
            MotionEvent.ACTION_UP -> {
                moveUp()
            }
        }
        return true
    }


    private fun pressDown() {
        handler.postDelayed(buttonOnLongClickAction, 3000)
        isAnimationStopped = true
        isButtonPressed = true
        clearInterval()
        if (!isRippleViewRemoved) {
            removeView(rippleView)
            isRippleViewRemoved = true
        }
        addView(rippleView, 0)
        isRippleViewRemoved = false
        val xScaleAnimator = ObjectAnimator.ofFloat(rippleView, "scaleX", 0f, 7f)
        xScaleAnimator.repeatCount = 0
        xScaleAnimator.repeatMode = ObjectAnimator.REVERSE
        xScaleAnimator.duration = 1500
        animators.add(xScaleAnimator)
        val yScaleAnimator = ObjectAnimator.ofFloat(rippleView, "scaleY", 0f, 7f)
        yScaleAnimator.repeatCount = 0
        yScaleAnimator.repeatMode = ObjectAnimator.REVERSE
        yScaleAnimator.duration = 1500
        animators.add(yScaleAnimator)
        animatorSet.playTogether(animators)
        animatorSet.start()
    }

    private fun moveUp() {
        if (isButtonPressed) {
            isButtonPressed = false
            handler.removeCallbacks(buttonOnLongClickAction)
        }
        animators.clear()
        removeView(rippleView)
        isRippleViewRemoved = true
        animatorSet.end()
    }

    private fun clearInterval() {
        progressRect.left = 0
        progressRect.top = 0
        progressRect.bottom = height
        progressRect.right = 0
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(progressRect, progressPaint)
    }

    interface LongClickListener {
        fun onPerform()
    }
}