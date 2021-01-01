/*
 * Copyright (C) 2016-2020 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.platform.gui

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Scroller

/**
 * An AndroidView that implements scrolling.
 */
class AndroidDataView(
    context: Context,
    attrs: AttributeSet? = null,
) : AndroidView<DataView>(context, attrs),
    GestureDetector.OnGestureListener,
    ValueAnimator.AnimatorUpdateListener {

    private val detector = GestureDetector(context, this)
    private val scroller = Scroller(context, null, true)
    private val scrollAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener(this@AndroidDataView)
    }

    override fun onTouchEvent(event: MotionEvent?) = detector.onTouchEvent(event)
    override fun onDown(e: MotionEvent?) = true
    override fun onShowPress(e: MotionEvent?) = Unit
    override fun onSingleTapUp(e: MotionEvent?) = false
    override fun onLongPress(e: MotionEvent?) = Unit

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        dx: Float,
        dy: Float,
    ): Boolean {
        if (Math.abs(dx) > Math.abs(dy)) {
            val parent = parent
            parent?.requestDisallowInterceptTouchEvent(true)
        }
        scroller.startScroll(
            scroller.currX,
            scroller.currY,
            -dx.toInt(),
            dy.toInt(),
            0
        )
        scroller.computeScrollOffset()
        updateDataOffset()
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        scroller.fling(scroller.currX, scroller.currY, velocityX.toInt() / 2, 0, 0, 10000, 0, 0)
        invalidate()
        scrollAnimator.duration = scroller.duration.toLong()
        scrollAnimator.start()
        return false
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        if (!scroller.isFinished) {
            scroller.computeScrollOffset()
            updateDataOffset()
        } else {
            scrollAnimator.cancel()
        }
    }

    fun resetDataOffset() {
        scroller.finalX = 0
        scroller.computeScrollOffset()
        updateDataOffset()
    }

    private fun updateDataOffset() {
        var newDataOffset: Int = scroller.currX / (view.dataColumnWidth * canvas.innerDensity).toInt()
        newDataOffset = Math.max(0, newDataOffset)
        if (newDataOffset != view.dataOffset) {
            view.dataOffset = newDataOffset
            postInvalidate()
        }
    }
}
