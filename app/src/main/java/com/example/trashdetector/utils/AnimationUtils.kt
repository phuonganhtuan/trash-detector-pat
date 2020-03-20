package com.example.trashdetector.utils

import android.animation.*
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.animation.DecelerateInterpolator

object AnimationUtils {

    private var currentAnimator: Animator? = null
    private const val animationDuration = 100L
    private var startBounds = RectF()
    private var startScale = 0f

    fun zoomImageFromThumb(outView: View, inView: View, container: View) {
        currentAnimator?.cancel()
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        outView.getGlobalVisibleRect(startBoundsInt)
        container.getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }
        outView.alpha = 0f
        inView.visibility = View.VISIBLE
        inView.pivotX = 0f
        inView.pivotY = 0f

        currentAnimator = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                    inView,
                    View.X,
                    startBounds.left,
                    finalBounds.left
                )
            ).apply {
                with(
                    ObjectAnimator.ofFloat(
                        inView,
                        View.Y,
                        startBounds.top,
                        finalBounds.top
                    )
                )
                with(ObjectAnimator.ofFloat(inView, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(inView, View.SCALE_Y, startScale, 1f))
            }
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }
    }

    fun zoomOutView(outView: View, inView: View, container: View) {
        currentAnimator?.cancel()
        currentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(inView, View.X, startBounds.left)).apply {
                with(ObjectAnimator.ofFloat(inView, View.Y, startBounds.top))
                with(ObjectAnimator.ofFloat(inView, View.SCALE_X, startScale))
                with(ObjectAnimator.ofFloat(inView, View.SCALE_Y, startScale))
            }
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    outView.alpha = 1f
                    inView.visibility = View.GONE
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    outView.alpha = 1f
                    inView.visibility = View.GONE
                    currentAnimator = null
                }
            })
            start()
        }
    }
}
