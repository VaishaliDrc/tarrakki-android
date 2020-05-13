package com.tarrakki.speedometer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.anastr.speedviewlib.R
import com.tarrakki.speedometer.components.Section
import com.tarrakki.speedometer.components.indicators.NeedleIndicator

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
open class SpeedView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val secondSpeedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()
    private val secondSpeedometerRect = RectF()

    /**
     * change the color of the center circle.
     */
    var centerCircleColor: Int
        get() = circlePaint.color
        set(centerCircleColor) {
            circlePaint.color = centerCircleColor
            if (isAttachedToWindow)
                invalidate()
        }

    /**
     * change the width of the center circle.
     */
    var centerCircleRadius = dpTOpx(20f)
        set(centerCircleRadius) {
            field = centerCircleRadius
            if (isAttachedToWindow)
                invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    var markWidth: Float
        get() = viewSizePa / 35f
        set(markWidth) {
            markPaint.strokeWidth = markWidth
            if (isAttachedToWindow)
                invalidate()
        }

    override fun defaultGaugeValues() {}

    override fun defaultSpeedometerValues() {
        indicator = NeedleIndicator(context)
        super.backgroundCircleColor = 0
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        secondSpeedometerPaint.style = Paint.Style.STROKE
        markPaint.style = Paint.Style.STROKE
        circlePaint.color = 0xFF444444.toInt()
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0)

        circlePaint.color = a.getColor(R.styleable.SpeedView_sv_centerCircleColor, circlePaint.color)
        centerCircleRadius = a.getDimension(R.styleable.SpeedView_sv_centerCircleRadius, centerCircleRadius)
        val styleIndex = a.getInt(R.styleable.SpeedView_sv_sectionStyle, -1)
        if (styleIndex != -1)
            sections.forEach { it.style = Section.Style.values()[styleIndex] }
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateBackgroundBitmap()
    }

    private fun initDraw() {
        markPaint.color = markColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * To remove speed text
         * */
        //drawSpeedUnitText(canvas)

        drawIndicator(canvas)

        //drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        secondSpeedometerPaint.strokeWidth = 20f
        secondSpeedometerPaint.color = Color.CYAN

        val risk1 = speedometerWidth * .5f + padding + 70
        secondSpeedometerRect.set(risk1, risk1, size - risk1, size - risk1)

        c.drawArc(secondSpeedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()).toFloat(), false, secondSpeedometerPaint)

        val markH = speedometerWidth
        markPath.reset()
        markPath.moveTo(size * .5f, padding.toFloat())
        markPath.lineTo(size * .5f, markH + padding)
        markPaint.strokeWidth = markWidth

        sections.forEach {
            val risk = it.width * .5f + padding + it.padding
            speedometerRect.set(risk, risk, size - risk, size - risk)
            speedometerPaint.strokeWidth = it.width
            speedometerPaint.color = it.color
            val startAngle = (getEndDegree() - getStartDegree()) * it.startOffset + getStartDegree()
            val sweepAngle = (getEndDegree() - getStartDegree()) * it.endOffset - (startAngle - getStartDegree())

            textPaint.textAlign = Paint.Align.LEFT

            /*  // val d = getDegreeAtSpeed(ticks[i]) + 90f
              c.save()
              c.rotate(startAngle, size * .5f, size.toFloat())
              //  if (!tickRotation)
              //  c.rotate(-d, size * .5f, initTickPadding + textPaint.textSize + padding.toFloat() + tickPadding.toFloat())

              var tick: CharSequence? = null
              if (tick == null)
                  tick = "dkajkjaskjaskads"

              c.translate(0f, 20f)
              StaticLayout(tick, textPaint, size, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
                      .draw(c)

              c.restore()*/

            if (it.style == Section.Style.ROUND) {
                // here we calculate the extra length when strokeCap = ROUND.
                // A: Arc Length, the extra length that taken ny ROUND stroke in one side.
                // D: Diameter of circle.
                // round angle padding =         A       * 360 / (           D             *   PI   )
                val roundAngle = (it.width * .5f * 360 / (speedometerRect.width() * Math.PI)).toFloat()
                speedometerPaint.strokeCap = Paint.Cap.ROUND
                c.drawArc(speedometerRect, startAngle + roundAngle, sweepAngle - roundAngle * 2f, false, speedometerPaint)
            } else {
                speedometerPaint.strokeCap = Paint.Cap.BUTT
                c.drawArc(speedometerRect, startAngle, sweepAngle, false, speedometerPaint)
            }

            c.save()
            c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
            val everyDegree = (getEndDegree() - getStartDegree()) * .20f
            var i = getStartDegree().toFloat()
            while (i < getEndDegree() - everyDegree) {
                c.rotate(everyDegree, size * .5f, size * .5f)
                c.drawPath(markPath, markPaint)
                i += everyDegree
            }
            c.restore()

            if (tickNumber > 0)
                drawTicks(c)
            else
                drawDefMinMaxSpeedPosition(c)
        }
    }
}
