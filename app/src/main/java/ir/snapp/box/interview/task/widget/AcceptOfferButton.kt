package ir.snapp.box.interview.task.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView


class AcceptOfferButton(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    private val rippleView: RippleView = RippleView(context)
    private val animators = mutableListOf<Animator>()
    private val animatorSet = AnimatorSet()
    private val track = LinearLayout(context)
    private val lParams = LayoutParams(0, MATCH_PARENT)
    private var isTrackAdded = false
    private lateinit var intervalAnimator: ValueAnimator

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    init {
        Handler(Looper.getMainLooper()).postDelayed({


        }, 3000)
    }

    fun startInterval() {
        isTrackAdded = if (!isTrackAdded) {
            addView(track, 0)
            true
        } else {
            removeView(track)
            false
        }
        intervalAnimator = ObjectAnimator.ofInt(0, width)
        intervalAnimator.duration = 33000
        intervalAnimator.repeatCount = 0
        intervalAnimator.addUpdateListener {
            lParams.width = it.animatedValue as Int
            track.layoutParams = lParams
            track.setBackgroundColor(Color.parseColor("#00246b"))
        }
        intervalAnimator.start()
    }

    fun stopInterval() {
        intervalAnimator.cancel()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                Log.d("TAG", "onTouchEvent: ${System.currentTimeMillis()}")
                addView(rippleView, 0)
                val xScaleAnimator = ObjectAnimator.ofFloat(rippleView, "scaleX", 0f, 7f)
                xScaleAnimator.repeatCount = 0
                xScaleAnimator.repeatMode = ObjectAnimator.REVERSE
                xScaleAnimator.duration = 3500
                animators.add(xScaleAnimator)
                val yScaleAnimator = ObjectAnimator.ofFloat(rippleView, "scaleY", 0f, 7f)
                yScaleAnimator.repeatCount = 0
                yScaleAnimator.repeatMode = ObjectAnimator.REVERSE
                yScaleAnimator.duration = 3500
                animators.add(yScaleAnimator)
                animatorSet.playTogether(animators)
                animatorSet.start()
            }
            MotionEvent.ACTION_UP -> {
                animators.clear()
                removeView(rippleView)
                Log.d("TAG", "onTouchEvent: ${System.currentTimeMillis()}")
                animatorSet.end()
            }
        }
        return true
    }
}