package ir.snapp.box.interview.task.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class RippleView(context: Context?) : View(context) {

    private lateinit var paint: Paint

    init {
        init()
    }

    private fun init() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.parseColor("#00246b")
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(width/2f, height/2f, height/2f, paint)
    }

}