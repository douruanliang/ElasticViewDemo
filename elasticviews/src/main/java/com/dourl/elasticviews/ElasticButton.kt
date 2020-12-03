package com.dourl.elasticviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatButton

class ElasticButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? =null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    // 默认值
    var scale = 0.8f
    var duration = 500
    @Px
    var cornerRadius = 0f

    private var onClickListener : OnClickListener ? = null
    private var onFinishListener: ElasticFinishListener? = null


    init {
        withOnCreate()
        when {
            attrs != null && defStyleAttr != androidx.appcompat.R.attr.buttonStyle -> {
                getAttrs(attrs, defStyleAttr)
            }
            attrs != null -> getAttrs(attrs, -1)
        }
    }

    private fun withOnCreate() {
        this.isAllCaps = false
        super.setOnClickListener {
           elasticAnimation(this){
               setDuration(this@ElasticButton.duration)
               setScaleX(this@ElasticButton.scale)
               setScaleY(this@ElasticButton.scale)
               setOnFinishListener { invokeListeners() }
           }.doAction()
        }
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {

        var typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.ElasticButton, defStyleAttr, 0)
        if (defStyleAttr == -1) {
            typeArray = context.obtainStyledAttributes(attrs, R.styleable.ElasticButton)
        }
        try {
            setTypeArray(typeArray)
        } catch (e: Exception) {
            typeArray.recycle()
        }
    }

    /**
     * 设置属性
     */
    private fun setTypeArray(typeArray: TypedArray) {
        this.duration = typeArray.getInt(R.styleable.ElasticButton_button_duration,this.duration)
        this.scale = typeArray.getFloat(R.styleable.ElasticButton_button_scale,this.scale)
        this.cornerRadius = typeArray.getDimension(R.styleable.ElasticButton_button_cornerRadius,this.cornerRadius)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initBackground()
    }

    private fun initBackground() {
        if (this.background is ColorDrawable) {
            this.background = GradientDrawable().apply {
                cornerRadius = this@ElasticButton.cornerRadius
                setColor((background as ColorDrawable).color)
            }.mutate()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.onClickListener = l
    }

    fun setOnFinishListener (listener: ElasticFinishListener){
        this.onFinishListener = listener
    }

    fun invokeListeners(){
        this.onFinishListener?.onFinished()
        this.onClickListener?.onClick(this)
    }

    /**
     * 要习惯去使用这种思想
     */
    class Builder(context: Context){
        private val elasticButton = ElasticButton(context)

        fun setScale(value:Float) = apply { this.elasticButton.scale = value }
        fun setDuration(value:Int) = apply { this.elasticButton.duration = value }
        fun setCornerRadius(@Px value: Float) = apply { this.elasticButton.cornerRadius = value }

        fun build() = this.elasticButton
        @JvmSynthetic
        fun setOnClickListener(block:() -> Unit) = apply{
            setOnClickListener (OnClickListener { block() })
        }
        fun setOnClickListener(listener: OnClickListener) = apply{
            this.elasticButton.setOnClickListener(listener)
        }

        @JvmSynthetic
        fun setOnFinishListener(block: () -> Unit) = apply {
            setOnFinishListener(object : ElasticFinishListener {
                override fun onFinished() {
                    block
                }
            })
        }

        fun setOnFinishListener(value:ElasticFinishListener) = apply {
            this.elasticButton.setOnFinishListener(value)
        }
    }
}


