package com.asp.fliptimerviewlibrary

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.github.fliptimerviewlibrary.R
import com.github.fliptimerviewlibrary.databinding.ViewSimpleClockBinding
import java.util.concurrent.TimeUnit

class CountDownClock : LinearLayout {
    private lateinit var binding: ViewSimpleClockBinding
    private var countDownTimer: CountDownTimer? = null
    private var countdownListener: CountdownCallBack? = null
    private var countdownTickInterval = 1000

    private var almostFinishedCallbackTimeInSeconds: Int = 5

    private var resetSymbol: String = "8"

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        binding = ViewSimpleClockBinding.inflate(LayoutInflater.from(context), this, true)

        attrs?.let {
            val typedArray =
                context?.obtainStyledAttributes(attrs, R.styleable.CountDownClock, defStyleAttr, 0)
            val resetSymbol = typedArray?.getString(R.styleable.CountDownClock_resetSymbol)
            if (resetSymbol != null) {
                setResetSymbol(resetSymbol)
            }

            val digitTopDrawable =
                typedArray?.getDrawable(R.styleable.CountDownClock_digitTopDrawable)
            setDigitTopDrawable(digitTopDrawable)
            val digitBottomDrawable =
                typedArray?.getDrawable(R.styleable.CountDownClock_digitBottomDrawable)
            setDigitBottomDrawable(digitBottomDrawable)
            val digitDividerColor =
                typedArray?.getColor(R.styleable.CountDownClock_digitDividerColor, 0)
            setDigitDividerColor(digitDividerColor ?: 0)
            val digitSplitterColor =
                typedArray?.getColor(R.styleable.CountDownClock_digitSplitterColor, 0)
            setDigitSplitterColor(digitSplitterColor ?: 0)

            val digitTextColor = typedArray?.getColor(R.styleable.CountDownClock_digitTextColor, 0)
            setDigitTextColor(digitTextColor ?: 0)

            val digitTextSize =
                typedArray?.getDimension(R.styleable.CountDownClock_digitTextSize, 0f)
            setDigitTextSize(digitTextSize ?: 0f)
            setSplitterDigitTextSize(digitTextSize ?: 0f)

            val digitPadding = typedArray?.getDimension(R.styleable.CountDownClock_digitPadding, 0f)
            setDigitPadding(digitPadding?.toInt() ?: 0)

            val splitterPadding =
                typedArray?.getDimension(R.styleable.CountDownClock_splitterPadding, 0f)
            setSplitterPadding(splitterPadding?.toInt() ?: 0)

            val halfDigitHeight =
                typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_halfDigitHeight, 0)
            val digitWidth =
                typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_digitWidth, 0)
            setHalfDigitHeightAndDigitWidth(halfDigitHeight ?: 0, digitWidth ?: 0)

            val animationDuration =
                typedArray?.getInt(R.styleable.CountDownClock_animationDuration, 0)
            setAnimationDuration(animationDuration ?: 600)

            val almostFinishedCallbackTimeInSeconds = typedArray?.getInt(
                R.styleable.CountDownClock_almostFinishedCallbackTimeInSeconds, 5
            )
            setAlmostFinishedCallbackTimeInSeconds(almostFinishedCallbackTimeInSeconds ?: 5)

            val countdownTickInterval =
                typedArray?.getInt(R.styleable.CountDownClock_countdownTickInterval, 1000)
            this.countdownTickInterval = countdownTickInterval ?: 1000

            invalidate()
            typedArray?.recycle()
        }
    }

    ////////////////
    // Public methods
    ////////////////

    private var milliLeft: Long = 0

    fun startCountDown(timeToNextEvent: Long) {
        countDownTimer?.cancel()
        var hasCalledAlmostFinished = false
        countDownTimer = object : CountDownTimer(timeToNextEvent, countdownTickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                milliLeft = millisUntilFinished
                if (millisUntilFinished / 1000 <= almostFinishedCallbackTimeInSeconds && !hasCalledAlmostFinished) {
                    hasCalledAlmostFinished = true
                    countdownListener?.countdownAboutToFinish()
                }
                setCountDownTime(millisUntilFinished)
            }

            override fun onFinish() {
                hasCalledAlmostFinished = false
                countdownListener?.countdownFinished()
            }
        }
        countDownTimer?.start()
    }

    fun resetCountdownTimer() {
        countDownTimer?.cancel()
        binding.firstDigitDays.setNewText(resetSymbol)
        binding.secondDigitDays.setNewText(resetSymbol)
        binding.firstDigitHours.setNewText(resetSymbol)
        binding.secondDigitHours.setNewText(resetSymbol)
        binding.firstDigitMinute.setNewText(resetSymbol)
        binding.secondDigitMinute.setNewText(resetSymbol)
        binding.firstDigitSecond.setNewText(resetSymbol)
        binding.secondDigitSecond.setNewText(resetSymbol)
    }

    ////////////////
    // Private methods
    ////////////////

    private fun setCountDownTime(timeToStart: Long) {

        val days = TimeUnit.MILLISECONDS.toDays(timeToStart)
        val hours = TimeUnit.MILLISECONDS.toHours(timeToStart - TimeUnit.DAYS.toMillis(days))
        val minutes = TimeUnit.MILLISECONDS.toMinutes(
            timeToStart - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours))
        )
        val seconds = TimeUnit.MILLISECONDS.toSeconds(
            timeToStart - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(
                minutes
            ))
        )

        val daysString = days.toString()
        val hoursString = hours.toString()
        val minutesString = minutes.toString()
        val secondsString = seconds.toString()


        when (daysString.length) {
            2 -> {
                binding.firstDigitDays.animateTextChange((daysString[0].toString()))
                binding.secondDigitDays.animateTextChange((daysString[1].toString()))
            }

            1 -> {
                binding.firstDigitDays.animateTextChange(("0"))
                binding.secondDigitDays.animateTextChange((daysString[0].toString()))
            }

            else -> {
                binding.firstDigitDays.animateTextChange(("3"))
                binding.secondDigitDays.animateTextChange(("0"))
            }
        }

        when (hoursString.length) {
            2 -> {
                binding.firstDigitHours.animateTextChange((hoursString[0].toString()))
                binding.secondDigitHours.animateTextChange((hoursString[1].toString()))
            }

            1 -> {
                binding.firstDigitHours.animateTextChange(("0"))
                binding.secondDigitHours.animateTextChange((hoursString[0].toString()))
            }

            else -> {
                binding.firstDigitHours.animateTextChange(("1"))
                binding.secondDigitHours.animateTextChange(("1"))
            }
        }

        when (minutesString.length) {
            2 -> {
                binding.firstDigitMinute.animateTextChange((minutesString[0].toString()))
                binding.secondDigitMinute.animateTextChange((minutesString[1].toString()))
            }

            1 -> {
                binding.firstDigitMinute.animateTextChange(("0"))
                binding.secondDigitMinute.animateTextChange((minutesString[0].toString()))
            }

            else -> {
                binding.firstDigitMinute.animateTextChange(("5"))
                binding.secondDigitMinute.animateTextChange(("9"))
            }
        }
        when (secondsString.length) {
            2 -> {
                binding.firstDigitSecond.animateTextChange((secondsString[0].toString()))
                binding.secondDigitSecond.animateTextChange((secondsString[1].toString()))
            }

            1 -> {
                binding.firstDigitSecond.animateTextChange(("0"))
                binding.secondDigitSecond.animateTextChange((secondsString[0].toString()))
            }

            else -> {
                binding.firstDigitSecond.animateTextChange((secondsString[secondsString.length - 2].toString()))
                binding.secondDigitSecond.animateTextChange((secondsString[secondsString.length - 1].toString()))
            }
        }
    }

    private fun setResetSymbol(resetSymbol: String?) {
        resetSymbol?.let {
            if (it.isNotEmpty()) {
                this.resetSymbol = resetSymbol
            } else {
                this.resetSymbol = ""
            }
        } ?: kotlin.run {
            this.resetSymbol = ""
        }
    }

    private fun setDigitTopDrawable(digitTopDrawable: Drawable?) {
        if (digitTopDrawable != null) {
            binding.firstDigitDays.binding.frontUpper.background = digitTopDrawable
            binding.firstDigitDays.binding.backUpper.background = digitTopDrawable
            binding.secondDigitDays.binding.frontUpper.background = digitTopDrawable
            binding.secondDigitDays.binding.backUpper.background = digitTopDrawable
            binding.firstDigitHours.binding.frontUpper.background = digitTopDrawable
            binding.firstDigitHours.binding.backUpper.background = digitTopDrawable
            binding.secondDigitHours.binding.frontUpper.background = digitTopDrawable
            binding.secondDigitHours.binding.backUpper.background = digitTopDrawable
            binding.firstDigitMinute.binding.frontUpper.background = digitTopDrawable
            binding.firstDigitMinute.binding.backUpper.background = digitTopDrawable
            binding.secondDigitMinute.binding.frontUpper.background = digitTopDrawable
            binding.secondDigitMinute.binding.backUpper.background = digitTopDrawable
            binding.firstDigitSecond.binding.frontUpper.background = digitTopDrawable
            binding.firstDigitSecond.binding.backUpper.background = digitTopDrawable
            binding.secondDigitSecond.binding.frontUpper.background = digitTopDrawable
            binding.secondDigitSecond.binding.backUpper.background = digitTopDrawable
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitBottomDrawable(digitBottomDrawable: Drawable?) {
        if (digitBottomDrawable != null) {
            binding.firstDigitDays.binding.frontLower.background = digitBottomDrawable
            binding.firstDigitDays.binding.backLower.background = digitBottomDrawable
            binding.secondDigitDays.binding.frontLower.background = digitBottomDrawable
            binding.secondDigitDays.binding.backLower.background = digitBottomDrawable
            binding.firstDigitHours.binding.frontLower.background = digitBottomDrawable
            binding.firstDigitHours.binding.backLower.background = digitBottomDrawable
            binding.secondDigitHours.binding.frontLower.background = digitBottomDrawable
            binding.secondDigitHours.binding.backLower.background = digitBottomDrawable
            binding.firstDigitMinute.binding.frontLower.background = digitBottomDrawable
            binding.firstDigitMinute.binding.backLower.background = digitBottomDrawable
            binding.secondDigitMinute.binding.frontLower.background = digitBottomDrawable
            binding.secondDigitMinute.binding.backLower.background = digitBottomDrawable
            binding.firstDigitSecond.binding.frontLower.background = digitBottomDrawable
            binding.firstDigitSecond.binding.backLower.background = digitBottomDrawable
            binding.secondDigitSecond.binding.frontLower.background = digitBottomDrawable
            binding.secondDigitSecond.binding.backLower.background = digitBottomDrawable
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitDividerColor(digitDividerColor: Int) {
        var dividerColor = digitDividerColor
        if (dividerColor == 0) {
            dividerColor = ContextCompat.getColor(context, R.color.transparent)
        }

        binding.firstDigitDays.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.secondDigitDays.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.firstDigitHours.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.secondDigitHours.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.firstDigitMinute.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.secondDigitMinute.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.firstDigitSecond.binding.digitDivider.setBackgroundColor(dividerColor)
        binding.secondDigitSecond.binding.digitDivider.setBackgroundColor(dividerColor)
    }

    private fun setDigitSplitterColor(digitsSplitterColor: Int) {
        if (digitsSplitterColor != 0) {
            //  digitsSplitter.setTextColor(digitsSplitterColor)
        } else {
            // digitsSplitter.setTextColor(ContextCompat.getColor(context, R.color.transparent))
        }
    }

    private fun setSplitterDigitTextSize(digitsTextSize: Float) {
        //digitsSplitter.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    private fun setDigitPadding(digitPadding: Int) {

        binding.firstDigitDays.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.secondDigitDays.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.firstDigitHours.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.secondDigitHours.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.firstDigitMinute.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.secondDigitMinute.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.firstDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.secondDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
    }

    private fun setSplitterPadding(splitterPadding: Int) {
        //digitsSplitter.setPadding(splitterPadding, 0, splitterPadding, 0)
    }

    private fun setDigitTextColor(digitsTextColor: Int) {
        var textColor = digitsTextColor
        if (textColor == 0) {
            textColor = ContextCompat.getColor(context, R.color.transparent)
        }

        binding.firstDigitDays.binding.frontUpperText.setTextColor(textColor)
        binding.firstDigitDays.binding.backUpperText.setTextColor(textColor)
        binding.firstDigitHours.binding.frontUpperText.setTextColor(textColor)
        binding.firstDigitHours.binding.backUpperText.setTextColor(textColor)
        binding.secondDigitDays.binding.frontUpperText.setTextColor(textColor)
        binding.secondDigitDays.binding.backUpperText.setTextColor(textColor)
        binding.secondDigitHours.binding.frontUpperText.setTextColor(textColor)
        binding.secondDigitHours.binding.backUpperText.setTextColor(textColor)
        binding.firstDigitMinute.binding.frontUpperText.setTextColor(textColor)
        binding.firstDigitMinute.binding.backUpperText.setTextColor(textColor)
        binding.secondDigitMinute.binding.frontUpperText.setTextColor(textColor)
        binding.secondDigitMinute.binding.backUpperText.setTextColor(textColor)
        binding.firstDigitSecond.binding.frontUpperText.setTextColor(textColor)
        binding.firstDigitSecond.binding.backUpperText.setTextColor(textColor)
        binding.secondDigitSecond.binding.frontUpperText.setTextColor(textColor)
        binding.secondDigitSecond.binding.backUpperText.setTextColor(textColor)
        binding.firstDigitDays.binding.frontLowerText.setTextColor(textColor)
        binding.firstDigitDays.binding.backLowerText.setTextColor(textColor)
        binding.firstDigitHours.binding.frontLowerText.setTextColor(textColor)
        binding.firstDigitHours.binding.backLowerText.setTextColor(textColor)
        binding.secondDigitDays.binding.frontLowerText.setTextColor(textColor)
        binding.secondDigitDays.binding.backLowerText.setTextColor(textColor)
        binding.secondDigitHours.binding.frontLowerText.setTextColor(textColor)
        binding.secondDigitHours.binding.backLowerText.setTextColor(textColor)
        binding.firstDigitMinute.binding.frontLowerText.setTextColor(textColor)
        binding.firstDigitMinute.binding.backLowerText.setTextColor(textColor)
        binding.secondDigitMinute.binding.frontLowerText.setTextColor(textColor)
        binding.secondDigitMinute.binding.backLowerText.setTextColor(textColor)
        binding.firstDigitSecond.binding.frontLowerText.setTextColor(textColor)
        binding.firstDigitSecond.binding.backLowerText.setTextColor(textColor)
        binding.secondDigitSecond.binding.frontLowerText.setTextColor(textColor)
        binding.secondDigitSecond.binding.backLowerText.setTextColor(textColor)
    }

    private fun setDigitTextSize(digitsTextSize: Float) {

        binding.firstDigitDays.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitDays.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitDays.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitDays.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitHours.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitHours.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitHours.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitHours.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitMinute.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitMinute.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitMinute.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitMinute.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitSecond.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitSecond.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitSecond.binding.frontUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitSecond.binding.backUpperText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitDays.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitDays.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitDays.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitDays.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitHours.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitHours.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitHours.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitHours.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitMinute.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitMinute.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitMinute.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitMinute.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitSecond.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.firstDigitSecond.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitSecond.binding.frontLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
        binding.secondDigitSecond.binding.backLowerText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, digitsTextSize
        )
    }

    private fun setHalfDigitHeightAndDigitWidth(halfDigitHeight: Int, digitWidth: Int) {
        setHeightAndWidthToView(
            binding.firstDigitDays.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitDays.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitDays.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitDays.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitHours.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitHours.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitHours.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitHours.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitMinute.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitMinute.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitMinute.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitMinute.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitSecond.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitSecond.binding.backUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitSecond.binding.frontUpper, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitSecond.binding.backUpper, halfDigitHeight, digitWidth
        )

        // Lower
        setHeightAndWidthToView(
            binding.firstDigitDays.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitDays.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitDays.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitDays.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitHours.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitHours.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitHours.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitHours.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitMinute.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitMinute.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitMinute.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitMinute.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitSecond.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.firstDigitSecond.binding.backLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitSecond.binding.frontLower, halfDigitHeight, digitWidth
        )
        setHeightAndWidthToView(
            binding.secondDigitSecond.binding.backLower, halfDigitHeight, digitWidth
        )

        // Dividers
        binding.firstDigitDays.binding.digitDivider.layoutParams.width = digitWidth
        binding.secondDigitDays.binding.digitDivider.layoutParams.width = digitWidth
        binding.firstDigitHours.binding.digitDivider.layoutParams.width = digitWidth
        binding.secondDigitHours.binding.digitDivider.layoutParams.width = digitWidth
        binding.firstDigitMinute.binding.digitDivider.layoutParams.width = digitWidth
        binding.secondDigitMinute.binding.digitDivider.layoutParams.width = digitWidth
        binding.firstDigitSecond.binding.digitDivider.layoutParams.width = digitWidth
        binding.secondDigitSecond.binding.digitDivider.layoutParams.width = digitWidth
    }

    private fun setHeightAndWidthToView(view: View, halfDigitHeight: Int, digitWidth: Int) {
        val firstDigitMinuteFrontUpperLayoutParams = view.layoutParams
        firstDigitMinuteFrontUpperLayoutParams.height = halfDigitHeight
        firstDigitMinuteFrontUpperLayoutParams.width = digitWidth
        binding.firstDigitDays.binding.frontUpper.layoutParams =
            firstDigitMinuteFrontUpperLayoutParams
    }

    private fun setAnimationDuration(animationDuration: Int) {
        binding.firstDigitDays.setAnimationDuration(animationDuration.toLong())
        binding.secondDigitDays.setAnimationDuration(animationDuration.toLong())
        binding.firstDigitHours.setAnimationDuration(animationDuration.toLong())
        binding.secondDigitHours.setAnimationDuration(animationDuration.toLong())
        binding.firstDigitMinute.setAnimationDuration(animationDuration.toLong())
        binding.secondDigitMinute.setAnimationDuration(animationDuration.toLong())
        binding.firstDigitSecond.setAnimationDuration(animationDuration.toLong())
        binding.secondDigitSecond.setAnimationDuration(animationDuration.toLong())
    }

    private fun setAlmostFinishedCallbackTimeInSeconds(almostFinishedCallbackTimeInSeconds: Int) {
        this.almostFinishedCallbackTimeInSeconds = almostFinishedCallbackTimeInSeconds
    }

    private fun setTransparentBackgroundColor() {
        val transparent = ContextCompat.getColor(context, R.color.transparent)
        binding.firstDigitDays.binding.frontLower.setBackgroundColor(transparent)
        binding.firstDigitDays.binding.backLower.setBackgroundColor(transparent)
        binding.secondDigitDays.binding.frontLower.setBackgroundColor(transparent)
        binding.secondDigitDays.binding.backLower.setBackgroundColor(transparent)
        binding.firstDigitHours.binding.frontLower.setBackgroundColor(transparent)
        binding.firstDigitHours.binding.backLower.setBackgroundColor(transparent)
        binding.secondDigitHours.binding.frontLower.setBackgroundColor(transparent)
        binding.secondDigitHours.binding.backLower.setBackgroundColor(transparent)
        binding.firstDigitMinute.binding.frontLower.setBackgroundColor(transparent)
        binding.firstDigitMinute.binding.backLower.setBackgroundColor(transparent)
        binding.secondDigitMinute.binding.frontLower.setBackgroundColor(transparent)
        binding.secondDigitMinute.binding.backLower.setBackgroundColor(transparent)
        binding.firstDigitSecond.binding.frontLower.setBackgroundColor(transparent)
        binding.firstDigitSecond.binding.backLower.setBackgroundColor(transparent)
        binding.secondDigitSecond.binding.frontLower.setBackgroundColor(transparent)
        binding.secondDigitSecond.binding.backLower.setBackgroundColor(transparent)
    }

    ////////////////
    // Listeners
    ////////////////

    fun setCountdownListener(countdownListener: CountdownCallBack) {
        this.countdownListener = countdownListener
    }

    interface CountdownCallBack {
        fun countdownAboutToFinish()
        fun countdownFinished()
    }


    fun pauseCountDownTimer() {
        countDownTimer?.cancel()
    }

    fun resumeCountDownTimer() {
        startCountDown(milliLeft)
    }


    fun setCustomTypeface(typeface: Typeface) {
        binding.firstDigitDays.setTypeFace(typeface)
        binding.firstDigitDays.setTypeFace(typeface)
        binding.secondDigitDays.setTypeFace(typeface)
        binding.secondDigitDays.setTypeFace(typeface)
        binding.firstDigitHours.setTypeFace(typeface)
        binding.firstDigitHours.setTypeFace(typeface)
        binding.secondDigitHours.setTypeFace(typeface)
        binding.secondDigitHours.setTypeFace(typeface)
        binding.firstDigitMinute.setTypeFace(typeface)
        binding.firstDigitMinute.setTypeFace(typeface)
        binding.secondDigitMinute.setTypeFace(typeface)
        binding.secondDigitMinute.setTypeFace(typeface)
        binding.firstDigitSecond.setTypeFace(typeface)
        binding.firstDigitSecond.setTypeFace(typeface)
        binding.secondDigitSecond.setTypeFace(typeface)
        binding.secondDigitSecond.setTypeFace(typeface)

    }
}
