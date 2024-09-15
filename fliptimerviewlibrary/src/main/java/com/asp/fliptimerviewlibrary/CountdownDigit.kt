package com.asp.fliptimerviewlibrary

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.github.fliptimerviewlibrary.databinding.ViewCountdownClockDigitBinding

class CountDownDigit : FrameLayout {

    var binding: ViewCountdownClockDigitBinding =
        ViewCountdownClockDigitBinding.inflate(LayoutInflater.from(context), this, true)

    private var animationDuration = 600L

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr)

    init {

        binding.frontUpperText.measure(0, 0)
        binding.frontLowerText.measure(0, 0)
        binding.backUpperText.measure(0, 0)
        binding.backLowerText.measure(0, 0)
    }

    fun setNewText(newText: String) {
        binding.frontUpper.clearAnimation()
        binding.frontLower.clearAnimation()
        binding.frontUpperText.text = newText
        binding.frontLowerText.text = newText
        binding.backUpperText.text = newText
        binding.backLowerText.text = newText
    }

    fun animateTextChange(newText: String) {
        if (binding.backUpperText.text == newText) {
            return
        }

        binding.frontUpper.clearAnimation()
        binding.frontLower.clearAnimation()

        binding.backUpperText.text = newText
        binding.frontUpper.pivotY = binding.frontUpper.bottom.toFloat()
        binding.frontLower.pivotY = binding.frontUpper.top.toFloat()
        binding.frontUpper.pivotX = (binding.frontUpper.right - ((binding.frontUpper.right - binding.frontUpper.left) / 2)).toFloat()
        binding.frontLower.pivotX = (binding.frontUpper.right - ((binding.frontUpper.right - binding.frontUpper.left) / 2)).toFloat()

        binding.frontUpper.animate().setDuration(getHalfOfAnimationDuration()).rotationX(-90f).setInterpolator(AccelerateInterpolator())
            .withEndAction {
                binding.frontUpperText.text = binding.backUpperText.text
                binding.frontUpper.rotationX = 0f
                binding.frontLower.rotationX = 90f
                binding.frontLowerText.text = binding.backUpperText.text
                binding.frontLower.animate().setDuration(getHalfOfAnimationDuration()).rotationX(0f).setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            binding.backLowerText.text = binding.frontLowerText.text
                        }.start()
            }.start()
    }

    fun setAnimationDuration(duration: Long) {
        this.animationDuration = duration
    }

    private fun getHalfOfAnimationDuration(): Long {
        return animationDuration / 2
    }


    fun setTypeFace(typeFace: Typeface){

        binding.frontUpperText.typeface = typeFace
        binding.frontLowerText.typeface = typeFace
        binding.backUpperText.typeface = typeFace
        binding.backLowerText.typeface = typeFace

    }
}
