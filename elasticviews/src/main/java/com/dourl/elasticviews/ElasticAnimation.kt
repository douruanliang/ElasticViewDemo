package com.dourl.elasticviews

import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener


fun View.elasticAnimation(
    scaleX: Float,
    scaleY: Float,
    duration: Int,
    listener: ElasticFinishListener
): ElasticAnimation {
    return ElasticAnimation(this).setScaleX(scaleX).setDuration(duration).setScaleX(scaleX)
        .setScaleY(scaleY).setFinishListener(listener)
}
@JvmSynthetic
fun elasticAnimation(view: View, block: ElasticAnimation.() -> Unit): ElasticAnimation =
    ElasticAnimation(view).apply(block)

class ElasticAnimation(private val view: View) {
    @JvmField
    var scaleX = 0.7f

    @JvmField
    var scaleY = 0.7f

    @JvmField
    var duration = 400

    @JvmField
    var listener: ViewPropertyAnimatorListener? = null

    @JvmField
    var finishListener: ElasticFinishListener? = null
    private var isAnimating: Boolean = false

    fun setScaleX(scaleX: Float): ElasticAnimation = apply { this.scaleX = scaleX }
    fun setScaleY(scaleY: Float): ElasticAnimation = apply { this.scaleY = scaleY }
    fun setDuration(duration: Int): ElasticAnimation = apply { this.duration = duration }

    fun setListener(listener: ViewPropertyAnimatorListener): ElasticAnimation = apply {
        this.listener = listener
    }

    @JvmSynthetic
    fun setFinishListener(finishListener: ElasticFinishListener): ElasticAnimation = apply {
        this.finishListener = finishListener
    }

    @JvmSynthetic
    fun setOnFinishListener(block: () -> Unit): ElasticAnimation = apply {
        this.finishListener = object : ElasticFinishListener {
            override fun onFinished() {
                block()
            }
        }
    }

    fun doAction() {
        if (!this.isAnimating && this.view.scaleX == 1f) {
            val animatorCompat = ViewCompat.animate(view)
                .setDuration(this.duration.toLong())
                .scaleX(this.scaleX)
                .scaleY(this.scaleY)
                .setInterpolator(CycleInterpolator(0.5f)).apply {
                    listener?.let { setListener(it) } ?: setListener(object :
                        ViewPropertyAnimatorListener {
                        override fun onAnimationEnd(view: View?) {
                            isAnimating = false
                        }

                        override fun onAnimationCancel(view: View?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationStart(view: View?) {
                            isAnimating = true
                        }
                    })
                }

            if (this.view is ViewGroup) {
                for (index in 0 until this.view.childCount) {
                    val nextChild = this.view.getChildAt(index)
                    ViewCompat.animate(nextChild)
                        .setDuration(this.duration.toLong())
                        .scaleX(this.scaleX)
                        .scaleY(this.scaleY)
                        .setInterpolator(CycleInterpolator(0.5f))
                        .withLayer()
                        .start()
                }
            }
            animatorCompat.withLayer().start()

        }
    }

    fun isAnimating(): Boolean = this.isAnimating
}