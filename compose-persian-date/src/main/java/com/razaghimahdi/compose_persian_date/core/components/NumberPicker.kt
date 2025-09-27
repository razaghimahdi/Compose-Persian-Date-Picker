package com.razaghimahdi.compose_persian_date.core.components

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.text.Spanned
import android.text.TextUtils
import android.text.method.NumberKeyListener
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.razaghimahdi.compose_persian_date.R
import java.text.DecimalFormatSymbols
import java.util.Formatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A widget that enables the user to select a number from a predefined range.
 */
class NumberPicker @JvmOverloads constructor(
    /**
     * The context of this widget.
     */
    private val mContext: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(mContext, attrs) {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(*[VERTICAL, HORIZONTAL])
    annotation class Orientation

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(*[ASCENDING, DESCENDING])
    annotation class Order

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(*[LEFT, CENTER, RIGHT])
    annotation class Align

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(*[SIDE_LINES, UNDERLINE])
    annotation class DividerType

    /**
     * Use a custom NumberPicker formatting callback to use two-digit minutes
     * strings like "01". Keeping a static formatter etc. is the most efficient
     * way to do this; it avoids creating temporary objects on every call to
     * format().
     */
    private class TwoDigitFormatter internal constructor() : Formatter {
        val mBuilder = StringBuilder()
        var mZeroDigit = 0.toChar()
        var mFmt: java.util.Formatter? = null
        val mArgs = arrayOfNulls<Any>(1)

        init {
            val locale = Locale.getDefault()
            init(locale)
        }

        private fun init(locale: Locale) {
            mFmt = createFormatter(locale)
            mZeroDigit = getZeroDigit(locale)
        }

        override fun format(value: Int): String {
            val currentLocale = Locale.getDefault()
            if (mZeroDigit != getZeroDigit(currentLocale)) {
                init(currentLocale)
            }
            mArgs[0] = value
            mBuilder.delete(0, mBuilder.length)
            mFmt!!.format("%02d", *mArgs)
            return mFmt.toString()
        }

        private fun createFormatter(locale: Locale): java.util.Formatter {
            return Formatter(mBuilder, locale)
        }

        companion object {
            private fun getZeroDigit(locale: Locale): Char {
                // return LocaleData.get(locale).zeroDigit;
                return DecimalFormatSymbols(locale).zeroDigit
            }
        }
    }

    /**
     * The text for showing the current value.
     */
    private val mSelectedText: EditText

    /**
     * The center X position of the selected text.
     */
    private var mSelectedTextCenterX = 0f

    /**
     * The center Y position of the selected text.
     */
    private var mSelectedTextCenterY = 0f

    /**
     * The min height of this widget.
     */
    private var mMinHeight = 0

    /**
     * The max height of this widget.
     */
    private var mMaxHeight = 0

    /**
     * The max width of this widget.
     */
    private var mMinWidth = 0

    /**
     * The max width of this widget.
     */
    private var mMaxWidth = 0

    /**
     * Flag whether to compute the max width.
     */
    private val mComputeMaxWidth: Boolean

    /**
     * The align of the selected text.
     */
    var selectedTextAlign = DEFAULT_TEXT_ALIGN

    /**
     * The color of the selected text.
     */
    private var mSelectedTextColor = DEFAULT_TEXT_COLOR

    /**
     * The size of the selected text.
     */
    private var mSelectedTextSize = DEFAULT_TEXT_SIZE

    /**
     * Flag whether the selected text should strikethroughed.
     */
    var selectedTextStrikeThru: Boolean = false

    /**
     * Flag whether the selected text should underlined.
     */
    var selectedTextUnderline: Boolean = false

    /**
     * The typeface of the selected text.
     */
    private var mSelectedTypeface: Typeface?

    /**
     * The align of the text.
     */
    var textAlign = DEFAULT_TEXT_ALIGN

    /**
     * The color of the text.
     */
    private var mTextColor = DEFAULT_TEXT_COLOR

    /**
     * The size of the text.
     */
    private var mTextSize = DEFAULT_TEXT_SIZE

    /**
     * Flag whether the text should strikethroughed.
     */
    var textStrikeThru: Boolean = false

    /**
     * Flag whether the text should underlined.
     */
    var textUnderline: Boolean = false

    /**
     * The typeface of the text.
     */
    private var mTypeface: Typeface?

    /**
     * The width of the gap between text elements if the selector wheel.
     */
    private var mSelectorTextGapWidth = 0

    /**
     * The height of the gap between text elements if the selector wheel.
     */
    private var mSelectorTextGapHeight = 0
    /**
     * Gets the values to be displayed instead of string values.
     *
     * @return The displayed values.
     */
    /**
     * The values to be displayed instead the indices.
     */
    var displayedValues2: Array<String>? = null

    /**
     * Lower value of the range of numbers allowed for the NumberPicker
     */
    private var mMinValue = DEFAULT_MIN_VALUE

    /**
     * Upper value of the range of numbers allowed for the NumberPicker
     */
    private var mMaxValue = DEFAULT_MAX_VALUE

    /**
     * Current value of this NumberPicker
     */
    private var mValue: Int = 0

    /**
     * Listener to be notified upon current value click.
     */
    private var mOnClickListener: OnClickListener? = null

    /**
     * Listener to be notified upon current value change.
     */
    private var mOnValueChangeListener: OnValueChangeListener? = null

    /**
     * Listener to be notified upon scroll state change.
     */
    private var mOnScrollListener: OnScrollListener? = null

    /**
     * Formatter for for displaying the current value.
     */
    private var mFormatter: Formatter?

    /**
     * The speed for updating the value form long press.
     */
    private var mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL

    /**
     * Cache for the string representation of selector indices.
     */
    private val mSelectorIndexToStringCache = SparseArray<String?>()

    /**
     * The number of items show in the selector wheel.
     */
    private var mWheelItemCount = DEFAULT_WHEEL_ITEM_COUNT

    /**
     * The real number of items show in the selector wheel.
     */
    private var mRealWheelItemCount = DEFAULT_WHEEL_ITEM_COUNT

    /**
     * The index of the middle selector item.
     */
    private var mWheelMiddleItemIndex = mWheelItemCount / 2

    /**
     * The selector indices whose value are show by the selector.
     */
    private var selectorIndices = IntArray(mWheelItemCount)

    /**
     * The [Paint] for drawing the selector.
     */
    private val mSelectorWheelPaint: Paint

    /**
     * The size of a selector element (text + gap).
     */
    private var mSelectorElementSize = 0

    /**
     * The initial offset of the scroll selector.
     */
    private var mInitialScrollOffset = Int.MIN_VALUE

    /**
     * The current offset of the scroll selector.
     */
    private var mCurrentScrollOffset = 0

    /**
     * The [Scroller] responsible for flinging the selector.
     */
    private val mFlingScroller: Scroller

    /**
     * The [Scroller] responsible for adjusting the selector.
     */
    private val mAdjustScroller: Scroller

    /**
     * The previous X coordinate while scrolling the selector.
     */
    private var mPreviousScrollerX = 0

    /**
     * The previous Y coordinate while scrolling the selector.
     */
    private var mPreviousScrollerY = 0

    /**
     * Handle to the reusable command for setting the input text selection.
     */
    private var mSetSelectionCommand: SetSelectionCommand? = null

    /**
     * Handle to the reusable command for changing the current value from long press by one.
     */
    private var mChangeCurrentByOneFromLongPressCommand: ChangeCurrentByOneFromLongPressCommand? = null

    /**
     * The X position of the last down event.
     */
    private var mLastDownEventX = 0f

    /**
     * The Y position of the last down event.
     */
    private var mLastDownEventY = 0f

    /**
     * The X position of the last down or move event.
     */
    private var mLastDownOrMoveEventX = 0f

    /**
     * The Y position of the last down or move event.
     */
    private var mLastDownOrMoveEventY = 0f

    /**
     * Determines speed during touch scrolling.
     */
    private var mVelocityTracker: VelocityTracker? = null

    /**
     * @see ViewConfiguration.getScaledTouchSlop
     */
    private val mTouchSlop: Int

    /**
     * @see ViewConfiguration.getScaledMinimumFlingVelocity
     */
    private val mMinimumFlingVelocity: Int

    /**
     * @see ViewConfiguration.getScaledMaximumFlingVelocity
     */
    private var mMaximumFlingVelocity: Int

    /**
     * Flag whether the selector should wrap around.
     */
    private var mWrapSelectorWheel: Boolean = false

    /**
     * User choice on whether the selector wheel should be wrapped.
     */
    private var mWrapSelectorWheelPreferred = true

    /**
     * Divider for showing item to be selected while scrolling
     */
    private var mDividerDrawable: Drawable? = null

    /**
     * The color of the divider.
     */
    private var mDividerColor = DEFAULT_DIVIDER_COLOR
    var dividerColor: Int
        get() = mDividerColor
        set(color) {
            mDividerColor = color
            mDividerDrawable = ColorDrawable(color)
        }

    /**
     * The distance between the two dividers.
     */
    private var mDividerDistance: Int

    /**
     * The thickness of the divider.
     */
    private val mDividerLength: Int

    /**
     * The thickness of the divider.
     */
    private var mDividerThickness: Int

    /**
     * The top of the top divider.
     */
    private var mTopDividerTop = 0

    /**
     * The bottom of the bottom divider.
     */
    private var mBottomDividerBottom = 0

    /**
     * The left of the top divider.
     */
    private var mLeftDividerLeft = 0

    /**
     * The right of the right divider.
     */
    private var mRightDividerRight = 0

    /**
     * The type of the divider.
     */
    private var mDividerType: Int

    /**
     * The current scroll state of the number picker.
     */
    private var mScrollState = OnScrollListener.SCROLL_STATE_IDLE

    /**
     * The keycode of the last handled DPAD down event.
     */
    private var mLastHandledDownDpadKeyCode = -1

    /**
     * Flag whether the selector wheel should hidden until the picker has focus.
     */
    private val mHideWheelUntilFocused: Boolean

    /**
     * The orientation of this widget.
     */
    private var mOrientation: Int
    /**
     * Should sort numbers in ascending or descending order.
     *
     * @param order Pass [.ASCENDING] or [.ASCENDING].
     * Default value is [.DESCENDING].
     */
    /**
     * The order of this widget.
     */
    var order: Int

    /**
     * Flag whether the fading edge should enabled.
     */
    var isFadingEdgeEnabled = true

    /**
     * The strength of fading edge while drawing the selector.
     */
    var fadingEdgeStrength = DEFAULT_FADING_EDGE_STRENGTH

    /**
     * Flag whether the scroller should enabled.
     */
    var isScrollerEnabled = true

    /**
     * The line spacing multiplier of the text.
     */
    var lineSpacingMultiplier = DEFAULT_LINE_SPACING_MULTIPLIER

    /**
     * The coefficient to adjust (divide) the max fling velocity.
     */
    private var mMaxFlingVelocityCoefficient = DEFAULT_MAX_FLING_VELOCITY_COEFFICIENT

    /**
     * Flag whether the accessibility description enabled.
     */
    var isAccessibilityDescriptionEnabled = true

    /**
     * The number formatter for current locale.
     */
    //  private NumberFormat mNumberFormatter;
    /**
     * The view configuration of this widget.
     */
    private val mViewConfiguration: ViewConfiguration


    var wheelItemCount: Int
        get() = mWheelItemCount
        set(count) {
            if (count < 1) {
                throw IllegalArgumentException("Wheel item count must be >= 1")
            }
            mRealWheelItemCount = count
            mWheelItemCount = max(count.toDouble(), DEFAULT_WHEEL_ITEM_COUNT.toDouble()).toInt()
            mWheelMiddleItemIndex = mWheelItemCount / 2
            selectorIndices = IntArray(mWheelItemCount)
        }
    var formatter: Formatter?
        get() = mFormatter
        /**
         * Set the formatter to be used for formatting the current value.
         *
         *
         * Note: If you have provided alternative values for the values this
         * formatter is never invoked.
         *
         *
         * @param formatter The formatter object. If formatter is `null`,
         * [String.valueOf] will be used.
         * @see .setDisplayedValues
         */
        set(formatter) {
            if (formatter === mFormatter) {
                return
            }
            mFormatter = formatter
            initializeSelectorWheelIndices()
            updateInputTextView()
        }
    var selectedTextColor: Int
        get() = mSelectedTextColor
        set(color) {
            mSelectedTextColor = color
            mSelectedText.setTextColor(mSelectedTextColor)
        }
    var selectedTextSize: Float
        get() = mSelectedTextSize
        set(textSize) {
            mSelectedTextSize = textSize
            mSelectedText.textSize = pxToSp(mSelectedTextSize)
        }
    var textColor: Int
        get() = mTextColor
        set(color) {
            mTextColor = color
            mSelectorWheelPaint.setColor(mTextColor)
        }
    var textSize: Float
        get() = spToPx(mTextSize)
        set(textSize) {
            mTextSize = textSize
            mSelectorWheelPaint.textSize = mTextSize
        }
    var typeface: Typeface?
        get() = mTypeface
        set(typeface) {
            mTypeface = typeface
            if (mTypeface != null) {
                mSelectedText.setTypeface(mTypeface)
                setSelectedTypeface(mSelectedTypeface)
            } else {
                mSelectedText.setTypeface(Typeface.MONOSPACE)
            }
        }
    var maxFlingVelocityCoefficient: Int
        get() = mMaxFlingVelocityCoefficient
        set(coefficient) {
            mMaxFlingVelocityCoefficient = coefficient
            mMaximumFlingVelocity = (mViewConfiguration.scaledMaximumFlingVelocity
                    / mMaxFlingVelocityCoefficient)
        }


    /**
     * Interface to listen for changes of the current value.
     */
    interface OnValueChangeListener {
        /**
         * Called upon a change of the current value.
         *
         * @param picker The NumberPicker associated with this listener.
         * @param oldVal The previous value.
         * @param newVal The new value.
         */
        fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int)
    }

    /**
     * The amount of space between items.
     */
    private var mItemSpacing = 0

    /**
     * Interface to listen for the picker scroll state.
     */
    interface OnScrollListener {
        @IntDef(*[SCROLL_STATE_IDLE, SCROLL_STATE_TOUCH_SCROLL, SCROLL_STATE_FLING])
        @Retention(AnnotationRetention.SOURCE)
        annotation class ScrollState

        /**
         * Callback invoked while the number picker scroll state has changed.
         *
         * @param view        The view whose scroll state is being reported.
         * @param scrollState The current scroll state. One of
         * [.SCROLL_STATE_IDLE],
         * [.SCROLL_STATE_TOUCH_SCROLL] or
         * [.SCROLL_STATE_IDLE].
         */
        fun onScrollStateChange(view: NumberPicker?, @ScrollState scrollState: Int)

        companion object {
            /**
             * The view is not scrolling.
             */
            const val SCROLL_STATE_IDLE = 0

            /**
             * The user is scrolling using touch, and his finger is still on the screen.
             */
            const val SCROLL_STATE_TOUCH_SCROLL = 1

            /**
             * The user had previously been scrolling using touch and performed a fling.
             */
            const val SCROLL_STATE_FLING = 2
        }
    }

    /**
     * Interface used to format current value into a string for presentation.
     */
    interface Formatter {
        /**
         * Formats a string representation of the current value.
         *
         * @param value The currently selected value.
         * @return A formatted string representation.
         */
        fun format(value: Int): String
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val msrdWdth = measuredWidth
        val msrdHght = measuredHeight

        // Input text centered horizontally.
        val inptTxtMsrdWdth = mSelectedText.measuredWidth
        val inptTxtMsrdHght = mSelectedText.measuredHeight
        val inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / 2
        val inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2
        val inptTxtRight = inptTxtLeft + inptTxtMsrdWdth
        val inptTxtBottom = inptTxtTop + inptTxtMsrdHght
        mSelectedText.layout(inptTxtLeft, inptTxtTop, inptTxtRight, inptTxtBottom)
        mSelectedTextCenterX = mSelectedText.x + mSelectedText.measuredWidth / 2f - 2f
        mSelectedTextCenterY = mSelectedText.y + mSelectedText.measuredHeight / 2f - 5f
        if (changed) {
            // need to do all this when we know our size
            initializeSelectorWheel()
            initializeFadingEdges()
            val dividerDistance = 2 * mDividerThickness + mDividerDistance
            if (isHorizontalMode) {
                mLeftDividerLeft = (width - mDividerDistance) / 2 - mDividerThickness
                mRightDividerRight = mLeftDividerLeft + dividerDistance
                mBottomDividerBottom = height
            } else {
                mTopDividerTop = (height - mDividerDistance) / 2 - mDividerThickness
                mBottomDividerBottom = mTopDividerTop + dividerDistance
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try greedily to fit the max width and height.
        val newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec, mMaxWidth)
        val newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec, mMaxHeight)
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
        // Flag if we are measured with width or height less than the respective min.
        val widthSize = resolveSizeAndStateRespectingMinSize(
            mMinWidth, measuredWidth,
            widthMeasureSpec
        )
        val heightSize = resolveSizeAndStateRespectingMinSize(
            mMinHeight, measuredHeight,
            heightMeasureSpec
        )
        setMeasuredDimension(widthSize, heightSize)
    }

    /**
     * Move to the final position of a scroller. Ensures to force finish the scroller
     * and if it is not at its final position a scroll of the selector wheel is
     * performed to fast forward to the final position.
     *
     * @param scroller The scroller to whose final position to get.
     * @return True of the a move was performed, i.e. the scroller was not in final position.
     */
    private fun moveToFinalScrollerPosition(scroller: Scroller): Boolean {
        scroller.forceFinished(true)
        if (isHorizontalMode) {
            var amountToScroll = scroller.finalX - scroller.currX
            val futureScrollOffset = (mCurrentScrollOffset + amountToScroll) % mSelectorElementSize
            var overshootAdjustment = mInitialScrollOffset - futureScrollOffset
            if (overshootAdjustment != 0) {
                if (abs(overshootAdjustment.toDouble()) > mSelectorElementSize / 2) {
                    if (overshootAdjustment > 0) {
                        overshootAdjustment -= mSelectorElementSize
                    } else {
                        overshootAdjustment += mSelectorElementSize
                    }
                }
                amountToScroll += overshootAdjustment
                scrollBy(amountToScroll, 0)
                return true
            }
        } else {
            var amountToScroll = scroller.finalY - scroller.currY
            val futureScrollOffset = (mCurrentScrollOffset + amountToScroll) % mSelectorElementSize
            var overshootAdjustment = mInitialScrollOffset - futureScrollOffset
            if (overshootAdjustment != 0) {
                if (abs(overshootAdjustment.toDouble()) > mSelectorElementSize / 2) {
                    if (overshootAdjustment > 0) {
                        overshootAdjustment -= mSelectorElementSize
                    } else {
                        overshootAdjustment += mSelectorElementSize
                    }
                }
                amountToScroll += overshootAdjustment
                scrollBy(0, amountToScroll)
                return true
            }
        }
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        val action = event.action and MotionEvent.ACTION_MASK
        if (action != MotionEvent.ACTION_DOWN) {
            return false
        }
        removeAllCallbacks()
        // Make sure we support flinging inside scrollables.
        parent.requestDisallowInterceptTouchEvent(true)
        if (isHorizontalMode) {
            mLastDownEventX = event.x
            mLastDownOrMoveEventX = mLastDownEventX
            if (!mFlingScroller.isFinished) {
                mFlingScroller.forceFinished(true)
                mAdjustScroller.forceFinished(true)
                onScrollerFinished(mFlingScroller)
                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
            } else if (!mAdjustScroller.isFinished) {
                mFlingScroller.forceFinished(true)
                mAdjustScroller.forceFinished(true)
                onScrollerFinished(mAdjustScroller)
            } else if (mLastDownEventX >= mLeftDividerLeft
                && mLastDownEventX <= mRightDividerRight
            ) {
                if (mOnClickListener != null) {
                    mOnClickListener!!.onClick(this)
                }
            } else if (mLastDownEventX < mLeftDividerLeft) {
                postChangeCurrentByOneFromLongPress(false)
            } else if (mLastDownEventX > mRightDividerRight) {
                postChangeCurrentByOneFromLongPress(true)
            }
        } else {
            mLastDownEventY = event.y
            mLastDownOrMoveEventY = mLastDownEventY
            if (!mFlingScroller.isFinished) {
                mFlingScroller.forceFinished(true)
                mAdjustScroller.forceFinished(true)
                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
            } else if (!mAdjustScroller.isFinished) {
                mFlingScroller.forceFinished(true)
                mAdjustScroller.forceFinished(true)
            } else if (mLastDownEventY >= mTopDividerTop
                && mLastDownEventY <= mBottomDividerBottom
            ) {
                if (mOnClickListener != null) {
                    mOnClickListener!!.onClick(this)
                }
            } else if (mLastDownEventY < mTopDividerTop) {
                postChangeCurrentByOneFromLongPress(false)
            } else if (mLastDownEventY > mBottomDividerBottom) {
                postChangeCurrentByOneFromLongPress(true)
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        if (!isScrollerEnabled) {
            return false
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        val action = event.action and MotionEvent.ACTION_MASK
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (isHorizontalMode) {
                    val currentMoveX = event.x
                    if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        val deltaDownX = abs((currentMoveX - mLastDownEventX).toDouble()).toInt()
                        if (deltaDownX > mTouchSlop) {
                            removeAllCallbacks()
                            onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        }
                    } else {
                        val deltaMoveX = (currentMoveX - mLastDownOrMoveEventX).toInt()
                        scrollBy(deltaMoveX, 0)
                        invalidate()
                    }
                    mLastDownOrMoveEventX = currentMoveX
                } else {
                    val currentMoveY = event.y
                    if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        val deltaDownY = abs((currentMoveY - mLastDownEventY).toDouble()).toInt()
                        if (deltaDownY > mTouchSlop) {
                            removeAllCallbacks()
                            onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        }
                    } else {
                        val deltaMoveY = (currentMoveY - mLastDownOrMoveEventY).toInt()
                        scrollBy(0, deltaMoveY)
                        invalidate()
                    }
                    mLastDownOrMoveEventY = currentMoveY
                }
            }

            MotionEvent.ACTION_UP -> {
                removeChangeCurrentByOneFromLongPress()
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
                if (isHorizontalMode) {
                    val initialVelocity = velocityTracker.xVelocity.toInt()
                    if (abs(initialVelocity.toDouble()) > mMinimumFlingVelocity) {
                        fling(initialVelocity)
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING)
                    } else {
                        val eventX = event.x.toInt()
                        val deltaMoveX = abs((eventX - mLastDownEventX).toDouble()).toInt()
                        if (deltaMoveX <= mTouchSlop) {
                            val selectorIndexOffset = (eventX / mSelectorElementSize
                                    - mWheelMiddleItemIndex)
                            if (selectorIndexOffset > 0) {
                                changeValueByOne(true)
                            } else if (selectorIndexOffset < 0) {
                                changeValueByOne(false)
                            } else {
                                ensureScrollWheelAdjusted()
                            }
                        } else {
                            ensureScrollWheelAdjusted()
                        }
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
                    }
                } else {
                    val initialVelocity = velocityTracker.yVelocity.toInt()
                    if (abs(initialVelocity.toDouble()) > mMinimumFlingVelocity) {
                        fling(initialVelocity)
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING)
                    } else {
                        val eventY = event.y.toInt()
                        val deltaMoveY = abs((eventY - mLastDownEventY).toDouble()).toInt()
                        if (deltaMoveY <= mTouchSlop) {
                            val selectorIndexOffset = (eventY / mSelectorElementSize
                                    - mWheelMiddleItemIndex)
                            if (selectorIndexOffset > 0) {
                                changeValueByOne(true)
                            } else if (selectorIndexOffset < 0) {
                                changeValueByOne(false)
                            } else {
                                ensureScrollWheelAdjusted()
                            }
                        } else {
                            ensureScrollWheelAdjusted()
                        }
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
                    }
                }
                mVelocityTracker!!.recycle()
                mVelocityTracker = null
            }
        }
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action and MotionEvent.ACTION_MASK
        when (action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> removeAllCallbacks()
        }
        return super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> removeAllCallbacks()
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP -> when (event.action) {
                KeyEvent.ACTION_DOWN -> if (mWrapSelectorWheel || (if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) value < maxValue else value > minValue)) {
                    requestFocus()
                    mLastHandledDownDpadKeyCode = keyCode
                    removeAllCallbacks()
                    if (mFlingScroller.isFinished) {
                        changeValueByOne(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
                    }
                    return true
                }

                KeyEvent.ACTION_UP -> if (mLastHandledDownDpadKeyCode == keyCode) {
                    mLastHandledDownDpadKeyCode = -1
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTrackballEvent(event: MotionEvent): Boolean {
        val action = event.action and MotionEvent.ACTION_MASK
        when (action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> removeAllCallbacks()
        }
        return super.dispatchTrackballEvent(event)
    }

    override fun computeScroll() {
        if (!isScrollerEnabled) {
            return
        }
        var scroller = mFlingScroller
        if (scroller.isFinished) {
            scroller = mAdjustScroller
            if (scroller.isFinished) {
                return
            }
        }
        scroller.computeScrollOffset()
        if (isHorizontalMode) {
            val currentScrollerX = scroller.currX
            if (mPreviousScrollerX == 0) {
                mPreviousScrollerX = scroller.startX
            }
            scrollBy(currentScrollerX - mPreviousScrollerX, 0)
            mPreviousScrollerX = currentScrollerX
        } else {
            val currentScrollerY = scroller.currY
            if (mPreviousScrollerY == 0) {
                mPreviousScrollerY = scroller.startY
            }
            scrollBy(0, currentScrollerY - mPreviousScrollerY)
            mPreviousScrollerY = currentScrollerY
        }
        if (scroller.isFinished) {
            onScrollerFinished(scroller)
        } else {
            postInvalidate()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mSelectedText.setEnabled(enabled)
    }

    override fun scrollBy(x: Int, y: Int) {
        if (!isScrollerEnabled) {
            return
        }
        val selectorIndices = selectorIndices
        val startScrollOffset = mCurrentScrollOffset
        val gap = maxTextSize.toInt()
        if (isHorizontalMode) {
            if (isAscendingOrder) {
                if (!mWrapSelectorWheel && x > 0 && selectorIndices[mWheelMiddleItemIndex] <= mMinValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
                if (!mWrapSelectorWheel && x < 0 && selectorIndices[mWheelMiddleItemIndex] >= mMaxValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
            } else {
                if (!mWrapSelectorWheel && x > 0 && selectorIndices[mWheelMiddleItemIndex] >= mMaxValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
                if (!mWrapSelectorWheel && x < 0 && selectorIndices[mWheelMiddleItemIndex] <= mMinValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
            }
            mCurrentScrollOffset += x
        } else {
            if (isAscendingOrder) {
                if (!mWrapSelectorWheel && y > 0 && selectorIndices[mWheelMiddleItemIndex] <= mMinValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
                if (!mWrapSelectorWheel && y < 0 && selectorIndices[mWheelMiddleItemIndex] >= mMaxValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
            } else {
                if (!mWrapSelectorWheel && y > 0 && selectorIndices[mWheelMiddleItemIndex] >= mMaxValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
                if (!mWrapSelectorWheel && y < 0 && selectorIndices[mWheelMiddleItemIndex] <= mMinValue) {
                    mCurrentScrollOffset = mInitialScrollOffset
                    return
                }
            }
            mCurrentScrollOffset += y
        }
        while (mCurrentScrollOffset - mInitialScrollOffset > gap) {
            mCurrentScrollOffset -= mSelectorElementSize
            if (isAscendingOrder) {
                decrementSelectorIndices(selectorIndices)
            } else {
                incrementSelectorIndices(selectorIndices)
            }
            setValueInternal(selectorIndices[mWheelMiddleItemIndex], true)
            if (!mWrapSelectorWheel && selectorIndices[mWheelMiddleItemIndex] < mMinValue) {
                mCurrentScrollOffset = mInitialScrollOffset
            }
        }
        while (mCurrentScrollOffset - mInitialScrollOffset < -gap) {
            mCurrentScrollOffset += mSelectorElementSize
            if (isAscendingOrder) {
                incrementSelectorIndices(selectorIndices)
            } else {
                decrementSelectorIndices(selectorIndices)
            }
            setValueInternal(selectorIndices[mWheelMiddleItemIndex], true)
            if (!mWrapSelectorWheel && selectorIndices[mWheelMiddleItemIndex] > mMaxValue) {
                mCurrentScrollOffset = mInitialScrollOffset
            }
        }
        if (startScrollOffset != mCurrentScrollOffset) {
            if (isHorizontalMode) {
                onScrollChanged(mCurrentScrollOffset, 0, startScrollOffset, 0)
            } else {
                onScrollChanged(0, mCurrentScrollOffset, 0, startScrollOffset)
            }
        }
    }

    private fun computeScrollOffset(isHorizontalMode: Boolean): Int {
        return if (isHorizontalMode) mCurrentScrollOffset else 0
    }

    private fun computeScrollRange(isHorizontalMode: Boolean): Int {
        return if (isHorizontalMode) (mMaxValue - mMinValue + 1) * mSelectorElementSize else 0
    }

    private fun computeScrollExtent(isHorizontalMode: Boolean): Int {
        return if (isHorizontalMode) width else height
    }

    override fun computeHorizontalScrollOffset(): Int {
        return computeScrollOffset(isHorizontalMode)
    }

    override fun computeHorizontalScrollRange(): Int {
        return computeScrollRange(isHorizontalMode)
    }

    override fun computeHorizontalScrollExtent(): Int {
        return computeScrollExtent(isHorizontalMode)
    }

    override fun computeVerticalScrollOffset(): Int {
        return computeScrollOffset(!isHorizontalMode)
    }

    override fun computeVerticalScrollRange(): Int {
        return computeScrollRange(!isHorizontalMode)
    }

    override fun computeVerticalScrollExtent(): Int {
        return computeScrollExtent(isHorizontalMode)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //mNumberFormatter = NumberFormat.getInstance();
    }

    /**
     * Set listener to be notified on click of the current value.
     *
     * @param onClickListener The listener.
     */
    override fun setOnClickListener(onClickListener: OnClickListener?) {
        mOnClickListener = onClickListener
    }

    /**
     * Sets the listener to be notified on change of the current value.
     *
     * @param onValueChangedListener The listener.
     */
    fun setOnValueChangedListener(onValueChangedListener: OnValueChangeListener?) {
        mOnValueChangeListener = onValueChangedListener
    }

    /**
     * Set listener to be notified for scroll state changes.
     *
     * @param onScrollListener The listener.
     */
    fun setOnScrollListener(onScrollListener: OnScrollListener?) {
        mOnScrollListener = onScrollListener
    }

    private val maxTextSize: Float
        private get() = max(mTextSize.toDouble(), mSelectedTextSize.toDouble()).toFloat()

    private fun getPaintCenterY(fontMetrics: Paint.FontMetrics?): Float {
        return if (fontMetrics == null) {
            0f
        } else (abs((fontMetrics.top + fontMetrics.bottom).toDouble()) / 2).toFloat()
    }

    /**
     * Computes the max width if no such specified as an attribute.
     */
    private fun tryComputeMaxWidth() {
        if (!mComputeMaxWidth) {
            return
        }
        mSelectorWheelPaint.textSize = maxTextSize
        var maxTextWidth = 0
        if (displayedValues2 == null) {
            var maxDigitWidth = 0f
            for (i in 0..9) {
                val digitWidth = mSelectorWheelPaint.measureText(formatNumber(i))
                if (digitWidth > maxDigitWidth) {
                    maxDigitWidth = digitWidth
                }
            }
            var numberOfDigits = 0
            var current = mMaxValue
            while (current > 0) {
                numberOfDigits++
                current = current / 10
            }
            maxTextWidth = (numberOfDigits * maxDigitWidth).toInt()
        } else {
            for (displayedValue in displayedValues2!!) {
                val textWidth = mSelectorWheelPaint.measureText(displayedValue)
                if (textWidth > maxTextWidth) {
                    maxTextWidth = textWidth.toInt()
                }
            }
        }
        maxTextWidth += mSelectedText.getPaddingLeft() + mSelectedText.getPaddingRight()
        if (mMaxWidth != maxTextWidth) {
            mMaxWidth = max(maxTextWidth.toDouble(), mMinWidth.toDouble()).toInt()
            invalidate()
        }
    }

    var wrapSelectorWheel: Boolean
        /**
         * Gets whether the selector wheel wraps when reaching the min/max value.
         *
         * @return True if the selector wheel wraps.
         * @see .getMinValue
         * @see .getMaxValue
         */
        get() = mWrapSelectorWheel
        /**
         * Sets whether the selector wheel shown during flinging/scrolling should
         * wrap around the [NumberPicker.getMinValue] and
         * [NumberPicker.getMaxValue] values.
         *
         *
         * By default if the range (max - min) is more than the number of items shown
         * on the selector wheel the selector wheel wrapping is enabled.
         *
         *
         *
         * **Note:** If the number of items, i.e. the range (
         * [.getMaxValue] - [.getMinValue]) is less than
         * the number of items shown on the selector wheel, the selector wheel will
         * not wrap. Hence, in such a case calling this method is a NOP.
         *
         *
         * @param wrapSelectorWheel Whether to wrap.
         */
        set(wrapSelectorWheel) {
            mWrapSelectorWheelPreferred = wrapSelectorWheel
            updateWrapSelectorWheel()
        }

    /**
     * Whether or not the selector wheel should be wrapped is determined by user choice and whether
     * the choice is allowed. The former comes from [.setWrapSelectorWheel], the
     * latter is calculated based on min & max value set vs selector's visual length. Therefore,
     * this method should be called any time any of the 3 values (i.e. user choice, min and max
     * value) gets updated.
     */
    private fun updateWrapSelectorWheel() {
        mWrapSelectorWheel = isWrappingAllowed && mWrapSelectorWheelPreferred
    }

    private val isWrappingAllowed: Boolean
        private get() = mMaxValue - mMinValue >= selectorIndices.size - 1

    /**
     * Sets the speed at which the numbers be incremented and decremented when
     * the up and down buttons are long pressed respectively.
     *
     *
     * The default value is 300 ms.
     *
     *
     * @param intervalMillis The speed (in milliseconds) at which the numbers
     * will be incremented and decremented.
     */
    fun setOnLongPressUpdateInterval(intervalMillis: Long) {
        mLongPressUpdateInterval = intervalMillis
    }

    var value: Int
        /**
         * Returns the value of the picker.
         *
         * @return The value.
         */
        get() = mValue
        /**
         * Set the current value for the number picker.
         *
         *
         * If the argument is less than the [NumberPicker.getMinValue] and
         * [NumberPicker.getWrapSelectorWheel] is `false` the
         * current value is set to the [NumberPicker.getMinValue] value.
         *
         *
         *
         * If the argument is less than the [NumberPicker.getMinValue] and
         * [NumberPicker.getWrapSelectorWheel] is `true` the
         * current value is set to the [NumberPicker.getMaxValue] value.
         *
         *
         *
         * If the argument is less than the [NumberPicker.getMaxValue] and
         * [NumberPicker.getWrapSelectorWheel] is `false` the
         * current value is set to the [NumberPicker.getMaxValue] value.
         *
         *
         *
         * If the argument is less than the [NumberPicker.getMaxValue] and
         * [NumberPicker.getWrapSelectorWheel] is `true` the
         * current value is set to the [NumberPicker.getMinValue] value.
         *
         *
         * @param value The current value.
         * @see .setWrapSelectorWheel
         * @see .setMinValue
         * @see .setMaxValue
         */
        set(value) {
            setValueInternal(value, false)
        }
    var minValue: Int
        /**
         * Returns the min value of the picker.
         *
         * @return The min value
         */
        get() = mMinValue
        /**
         * Sets the min value of the picker.
         *
         * @param minValue The min value inclusive.
         *
         * **Note:** The length of the displayed values array
         * set via [.setDisplayedValues] must be equal to the
         * range of selectable numbers which is equal to
         * [.getMaxValue] - [.getMinValue] + 1.
         */
        set(minValue) {
//        if (minValue < 0) {
//            throw new IllegalArgumentException("minValue must be >= 0");
//        }
            mMinValue = minValue
            if (mMinValue > mValue) {
                mValue = mMinValue
            }
            updateWrapSelectorWheel()
            initializeSelectorWheelIndices()
            updateInputTextView()
            tryComputeMaxWidth()
            invalidate()
        }
    var maxValue: Int
        /**
         * Returns the max value of the picker.
         *
         * @return The max value.
         */
        get() = mMaxValue
        /**
         * Sets the max value of the picker.
         *
         * @param maxValue The max value inclusive.
         *
         * **Note:** The length of the displayed values array
         * set via [.setDisplayedValues] must be equal to the
         * range of selectable numbers which is equal to
         * [.getMaxValue] - [.getMinValue] + 1.
         */
        set(maxValue) {
            require(!(maxValue < 0)) { "maxValue must be >= 0" }
            mMaxValue = maxValue
            if (mMaxValue < mValue) {
                mValue = mMaxValue
            }
            updateWrapSelectorWheel()
            initializeSelectorWheelIndices()
            updateInputTextView()
            tryComputeMaxWidth()
            invalidate()
        }

    /**
     * Sets the values to be displayed.
     *
     * @param displayedValues The displayed values.
     *
     * **Note:** The length of the displayed values array
     * must be equal to the range of selectable numbers which is equal to
     * [.getMaxValue] - [.getMinValue] + 1.
     */
    fun setDisplayedValues(displayedValues: Array<String>?) {
        if (this.displayedValues2 == displayedValues) {
            return
        }
        this.displayedValues2 = displayedValues
        if (this.displayedValues2 != null) {
            // Allow text entry rather than strictly numeric entry.
            mSelectedText.setRawInputType(
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            )
        } else {
            mSelectedText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        }
        updateInputTextView()
        initializeSelectorWheelIndices()
        tryComputeMaxWidth()
    }

    private fun getFadingEdgeStrength(isHorizontalMode: Boolean): Float {
        return if (isHorizontalMode && isFadingEdgeEnabled) fadingEdgeStrength else 0f
    }

    override fun getTopFadingEdgeStrength(): Float {
        return getFadingEdgeStrength(!isHorizontalMode)
    }

    override fun getBottomFadingEdgeStrength(): Float {
        return getFadingEdgeStrength(!isHorizontalMode)
    }

    override fun getLeftFadingEdgeStrength(): Float {
        return getFadingEdgeStrength(isHorizontalMode)
    }

    override fun getRightFadingEdgeStrength(): Float {
        return getFadingEdgeStrength(isHorizontalMode)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeAllCallbacks()
    }

    @CallSuper
    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mDividerDrawable != null && mDividerDrawable!!.isStateful
            && mDividerDrawable!!.setState(drawableState)
        ) {
            invalidateDrawable(mDividerDrawable!!)
        }
    }

    @CallSuper
    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (mDividerDrawable != null) {
            mDividerDrawable!!.jumpToCurrentState()
        }
    }

    override fun onDraw(canvas: Canvas) {
        // save canvas
        canvas.save()
        val showSelectorWheel = !mHideWheelUntilFocused || hasFocus()
        var x: Float
        var y: Float
        if (isHorizontalMode) {
            x = mCurrentScrollOffset.toFloat()
            y = (mSelectedText.getBaseline() + mSelectedText.top).toFloat()
            if (mRealWheelItemCount < DEFAULT_WHEEL_ITEM_COUNT) {
                canvas.clipRect(mLeftDividerLeft, 0, mRightDividerRight, bottom)
            }
        } else {
            x = (right - left) / 2f
            y = mCurrentScrollOffset.toFloat()
            if (mRealWheelItemCount < DEFAULT_WHEEL_ITEM_COUNT) {
                canvas.clipRect(0, mTopDividerTop, right, mBottomDividerBottom)
            }
        }

        // draw the selector wheel
        val selectorIndices = selectorIndices
        for (i in selectorIndices.indices) {
            if (i == mWheelMiddleItemIndex) {
                mSelectorWheelPaint.textAlign = Paint.Align.entries.toTypedArray()[selectedTextAlign];
                mSelectorWheelPaint.textSize = mSelectedTextSize
                mSelectorWheelPaint.setColor(mSelectedTextColor)
                mSelectorWheelPaint.isStrikeThruText = selectedTextStrikeThru
                mSelectorWheelPaint.isUnderlineText = selectedTextUnderline
                mSelectorWheelPaint.setTypeface(mSelectedTypeface)
            } else {
                mSelectorWheelPaint.textAlign = Paint.Align.entries.toTypedArray()[textAlign];
                mSelectorWheelPaint.textSize = mTextSize
                mSelectorWheelPaint.setColor(mTextColor)
                mSelectorWheelPaint.isStrikeThruText = textStrikeThru
                mSelectorWheelPaint.isUnderlineText = textUnderline
                mSelectorWheelPaint.setTypeface(mTypeface)
            }
            val selectorIndex = selectorIndices[if (isAscendingOrder) i else selectorIndices.size - i - 1]
            val scrollSelectorValue = mSelectorIndexToStringCache.get(selectorIndex) ?: continue
            // Do not draw the middle item if input is visible since the input
            // is shown only if the wheel is static and it covers the middle
            // item. Otherwise, if the user starts editing the text via the
            // IME he may see a dimmed version of the old value intermixed
            // with the new one.
            if (showSelectorWheel && i != mWheelMiddleItemIndex || i == mWheelMiddleItemIndex && mSelectedText.visibility != VISIBLE) {
                var textY = y
                if (!isHorizontalMode) {
                    textY += getPaintCenterY(mSelectorWheelPaint.getFontMetrics())
                }
                var xOffset = 0
                var yOffset = 0
                if (i != mWheelMiddleItemIndex && mItemSpacing != 0) {
                    if (isHorizontalMode) {
                        xOffset = if (i > mWheelMiddleItemIndex) {
                            mItemSpacing
                        } else {
                            -mItemSpacing
                        }
                    } else {
                        yOffset = if (i > mWheelMiddleItemIndex) {
                            mItemSpacing
                        } else {
                            -mItemSpacing
                        }
                    }
                }
                drawText(scrollSelectorValue, x + xOffset, textY + yOffset, mSelectorWheelPaint, canvas)
            }
            if (isHorizontalMode) {
                x += mSelectorElementSize.toFloat()
            } else {
                y += mSelectorElementSize.toFloat()
            }
        }

        // restore canvas
        canvas.restore()

        // draw the dividers
        if (showSelectorWheel && mDividerDrawable != null) {
            if (isHorizontalMode) drawHorizontalDividers(canvas) else drawVerticalDividers(canvas)
        }
    }

    private fun drawHorizontalDividers(canvas: Canvas) {
        when (mDividerType) {
            SIDE_LINES -> {
                val top: Int
                val bottom: Int
                if (mDividerLength > 0 && mDividerLength <= mMaxHeight) {
                    top = (mMaxHeight - mDividerLength) / 2
                    bottom = top + mDividerLength
                } else {
                    top = 0
                    bottom = getBottom()
                }
                // draw the left divider
                val leftOfLeftDivider = mLeftDividerLeft
                val rightOfLeftDivider = leftOfLeftDivider + mDividerThickness
                mDividerDrawable!!.setBounds(leftOfLeftDivider, top, rightOfLeftDivider, bottom)
                mDividerDrawable!!.draw(canvas)
                // draw the right divider
                val rightOfRightDivider = mRightDividerRight
                val leftOfRightDivider = rightOfRightDivider - mDividerThickness
                mDividerDrawable!!.setBounds(leftOfRightDivider, top, rightOfRightDivider, bottom)
                mDividerDrawable!!.draw(canvas)
            }

            UNDERLINE -> {
                val left: Int
                val right: Int
                if (mDividerLength > 0 && mDividerLength <= mMaxWidth) {
                    left = (mMaxWidth - mDividerLength) / 2
                    right = left + mDividerLength
                } else {
                    left = mLeftDividerLeft
                    right = mRightDividerRight
                }
                val bottomOfUnderlineDivider = mBottomDividerBottom
                val topOfUnderlineDivider = bottomOfUnderlineDivider - mDividerThickness
                mDividerDrawable!!.setBounds(
                    left,
                    topOfUnderlineDivider,
                    right,
                    bottomOfUnderlineDivider
                )
                mDividerDrawable!!.draw(canvas)
            }
        }
    }

    private fun drawVerticalDividers(canvas: Canvas) {
        val left: Int
        val right: Int
        if (mDividerLength > 0 && mDividerLength <= mMaxWidth) {
            left = (mMaxWidth - mDividerLength) / 2
            right = left + mDividerLength
        } else {
            left = 0
            right = getRight()
        }
        when (mDividerType) {
            SIDE_LINES -> {
                // draw the top divider
                val topOfTopDivider = mTopDividerTop
                val bottomOfTopDivider = topOfTopDivider + mDividerThickness
                mDividerDrawable!!.setBounds(left, topOfTopDivider, right, bottomOfTopDivider)
                mDividerDrawable!!.draw(canvas)
                // draw the bottom divider
                val bottomOfBottomDivider = mBottomDividerBottom
                val topOfBottomDivider = bottomOfBottomDivider - mDividerThickness
                mDividerDrawable!!.setBounds(
                    left,
                    topOfBottomDivider,
                    right,
                    bottomOfBottomDivider
                )
                mDividerDrawable!!.draw(canvas)
            }

            UNDERLINE -> {
                val bottomOfUnderlineDivider = mBottomDividerBottom
                val topOfUnderlineDivider = bottomOfUnderlineDivider - mDividerThickness
                mDividerDrawable!!.setBounds(
                    left,
                    topOfUnderlineDivider,
                    right,
                    bottomOfUnderlineDivider
                )
                mDividerDrawable!!.draw(canvas)
            }
        }
    }

    private fun drawText(text: String, x: Float, y: Float, paint: Paint, canvas: Canvas) {
        var y = y
        if (text.contains("\n")) {
            val lines = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val height = (abs((paint.descent() + paint.ascent()).toDouble()) * lineSpacingMultiplier).toFloat()
            val diff = (lines.size - 1) * height / 2
            y -= diff
            for (line in lines) {
                canvas.drawText(line, x, y, paint)
                y += height
            }
        } else {
            canvas.drawText(text, x, y, paint)
        }
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.setClassName(NumberPicker::class.java.getName())
        event.setScrollable(isScrollerEnabled)
        val scroll = (mMinValue + mValue) * mSelectorElementSize
        val maxScroll = (mMaxValue - mMinValue) * mSelectorElementSize
        if (isHorizontalMode) {
            event.setScrollX(scroll)
            event.setMaxScrollX(maxScroll)
        } else {
            event.setScrollY(scroll)
            event.setMaxScrollY(maxScroll)
        }
    }

    /**
     * Makes a measure spec that tries greedily to use the max value.
     *
     * @param measureSpec The measure spec.
     * @param maxSize     The max value for the size.
     * @return A measure spec greedily imposing the max size.
     */
    private fun makeMeasureSpec(measureSpec: Int, maxSize: Int): Int {
        if (maxSize == SIZE_UNSPECIFIED) {
            return measureSpec
        }
        val size = MeasureSpec.getSize(measureSpec)
        val mode = MeasureSpec.getMode(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> measureSpec
            MeasureSpec.AT_MOST -> MeasureSpec.makeMeasureSpec(min(size.toDouble(), maxSize.toDouble()).toInt(), MeasureSpec.EXACTLY)
            MeasureSpec.UNSPECIFIED -> MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.EXACTLY)
            else -> throw IllegalArgumentException("Unknown measure mode: $mode")
        }
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec. Tries to respect the min size, unless a different size
     * is imposed by the constraints.
     *
     * @param minSize      The minimal desired size.
     * @param measuredSize The currently measured size.
     * @param measureSpec  The current measure spec.
     * @return The resolved size and state.
     */
    private fun resolveSizeAndStateRespectingMinSize(
        minSize: Int, measuredSize: Int,
        measureSpec: Int
    ): Int {
        return if (minSize != SIZE_UNSPECIFIED) {
            val desiredWidth = max(minSize.toDouble(), measuredSize.toDouble()).toInt()
            resolveSizeAndState(desiredWidth, measureSpec, 0)
        } else {
            measuredSize
        }
    }

    /**
     * Resets the selector indices and clear the cached string representation of
     * these indices.
     */
    private fun initializeSelectorWheelIndices() {
        mSelectorIndexToStringCache.clear()
        val selectorIndices = selectorIndices
        val current = value
        for (i in selectorIndices.indices) {
            var selectorIndex = current + (i - mWheelMiddleItemIndex)
            if (mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex)
            }
            selectorIndices[i] = selectorIndex
            ensureCachedScrollSelectorValue(selectorIndices[i])
        }
    }

    /**
     * Sets the current value of this NumberPicker.
     *
     * @param current      The new value of the NumberPicker.
     * @param notifyChange Whether to notify if the current value changed.
     */
    private fun setValueInternal(current: Int, notifyChange: Boolean) {
        var current = current
        if (mValue == current) {
            return
        }
        // Wrap around the values if we go past the start or end
        if (mWrapSelectorWheel) {
            current = getWrappedSelectorIndex(current)
        } else {
            current = max(current.toDouble(), mMinValue.toDouble()).toInt()
            current = min(current.toDouble(), mMaxValue.toDouble()).toInt()
        }
        val previous = mValue
        mValue = current
        // If we're flinging, we'll update the text view at the end when it becomes visible
        if (mScrollState != OnScrollListener.SCROLL_STATE_FLING) {
            updateInputTextView()
        }
        if (notifyChange) {
            notifyChange(previous, current)
        }
        initializeSelectorWheelIndices()
        updateAccessibilityDescription()
        invalidate()
    }

    /**
     * Updates the accessibility values of the view,
     * to the currently selected value
     */
    private fun updateAccessibilityDescription() {
        if (!isAccessibilityDescriptionEnabled) {
            return
        }
        setContentDescription(value.toString())
    }

    /**
     * Changes the current value by one which is increment or
     * decrement based on the passes argument.
     * decrement the current value.
     *
     * @param increment True to increment, false to decrement.
     */
    private fun changeValueByOne(increment: Boolean) {
        if (!moveToFinalScrollerPosition(mFlingScroller)) {
            moveToFinalScrollerPosition(mAdjustScroller)
        }
        smoothScroll(increment, 1)
    }

    /**
     * Starts a smooth scroll to wheel position.
     *
     * @param position The wheel position to scroll to.
     */
    fun smoothScrollToPosition(position: Int) {
        val currentPosition = selectorIndices[mWheelMiddleItemIndex]
        if (currentPosition == position) {
            return
        }
        smoothScroll(position > currentPosition, abs((position - currentPosition).toDouble()).toInt())
    }

    /**
     * Starts a smooth scroll
     *
     * @param increment True to increment, false to decrement.
     * @param steps     The steps to scroll.
     */
    fun smoothScroll(increment: Boolean, steps: Int) {
        val diffSteps = (if (increment) -mSelectorElementSize else mSelectorElementSize) * steps
        if (isHorizontalMode) {
            mPreviousScrollerX = 0
            mFlingScroller.startScroll(0, 0, diffSteps, 0, SNAP_SCROLL_DURATION)
        } else {
            mPreviousScrollerY = 0
            mFlingScroller.startScroll(0, 0, 0, diffSteps, SNAP_SCROLL_DURATION)
        }
        invalidate()
    }

    private fun initializeSelectorWheel() {
        initializeSelectorWheelIndices()
        val selectorIndices = selectorIndices
        val totalTextSize = ((selectorIndices.size - 1) * mTextSize + mSelectedTextSize).toInt()
        val textGapCount = selectorIndices.size.toFloat()
        if (isHorizontalMode) {
            val totalTextGapWidth = (right - left - totalTextSize).toFloat()
            mSelectorTextGapWidth = (totalTextGapWidth / textGapCount).toInt()
            mSelectorElementSize = maxTextSize.toInt() + mSelectorTextGapWidth
            mInitialScrollOffset = (mSelectedTextCenterX - mSelectorElementSize * mWheelMiddleItemIndex).toInt()
        } else {
            val totalTextGapHeight = (bottom - top - totalTextSize).toFloat()
            mSelectorTextGapHeight = (totalTextGapHeight / textGapCount).toInt()
            mSelectorElementSize = maxTextSize.toInt() + mSelectorTextGapHeight
            mInitialScrollOffset = (mSelectedTextCenterY - mSelectorElementSize * mWheelMiddleItemIndex).toInt()
        }
        mCurrentScrollOffset = mInitialScrollOffset
        updateInputTextView()
    }

    private fun initializeFadingEdges() {
        if (isHorizontalMode) {
            isHorizontalFadingEdgeEnabled = true
            isVerticalFadingEdgeEnabled = false
            setFadingEdgeLength((right - left - mTextSize.toInt()) / 2)
        } else {
            isHorizontalFadingEdgeEnabled = false
            isVerticalFadingEdgeEnabled = true
            setFadingEdgeLength((bottom - top - mTextSize.toInt()) / 2)
        }
    }

    /**
     * Callback invoked upon completion of a given `scroller`.
     */
    private fun onScrollerFinished(scroller: Scroller) {
        if (scroller == mFlingScroller) {
            ensureScrollWheelAdjusted()
            updateInputTextView()
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
        } else if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            updateInputTextView()
        }
    }

    /**
     * Handles transition to a given `scrollState`
     */
    private fun onScrollStateChange(scrollState: Int) {
        if (mScrollState == scrollState) {
            return
        }
        mScrollState = scrollState
        if (mOnScrollListener != null) {
            mOnScrollListener!!.onScrollStateChange(this, scrollState)
        }
    }

    /**
     * Flings the selector with the given `velocity`.
     */
    private fun fling(velocity: Int) {
        if (isHorizontalMode) {
            mPreviousScrollerX = 0
            if (velocity > 0) {
                mFlingScroller.fling(0, 0, velocity, 0, 0, Int.MAX_VALUE, 0, 0)
            } else {
                mFlingScroller.fling(Int.MAX_VALUE, 0, velocity, 0, 0, Int.MAX_VALUE, 0, 0)
            }
        } else {
            mPreviousScrollerY = 0
            if (velocity > 0) {
                mFlingScroller.fling(0, 0, 0, velocity, 0, 0, 0, Int.MAX_VALUE)
            } else {
                mFlingScroller.fling(0, Int.MAX_VALUE, 0, velocity, 0, 0, 0, Int.MAX_VALUE)
            }
        }
        invalidate()
    }

    /**
     * @return The wrapped index `selectorIndex` value.
     */
    private fun getWrappedSelectorIndex(selectorIndex: Int): Int {
        if (selectorIndex > mMaxValue) {
            return mMinValue + (selectorIndex - mMaxValue) % (mMaxValue - mMinValue) - 1
        } else if (selectorIndex < mMinValue) {
            return mMaxValue - (mMinValue - selectorIndex) % (mMaxValue - mMinValue) + 1
        }
        return selectorIndex
    }

    /**
     * Increments the `selectorIndices` whose string representations
     * will be displayed in the selector.
     */
    private fun incrementSelectorIndices(selectorIndices: IntArray) {
        for (i in 0 until selectorIndices.size - 1) {
            selectorIndices[i] = selectorIndices[i + 1]
        }
        var nextScrollSelectorIndex = selectorIndices[selectorIndices.size - 2] + 1
        if (mWrapSelectorWheel && nextScrollSelectorIndex > mMaxValue) {
            nextScrollSelectorIndex = mMinValue
        }
        selectorIndices[selectorIndices.size - 1] = nextScrollSelectorIndex
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex)
    }

    /**
     * Decrements the `selectorIndices` whose string representations
     * will be displayed in the selector.
     */
    private fun decrementSelectorIndices(selectorIndices: IntArray) {
        for (i in selectorIndices.size - 1 downTo 1) {
            selectorIndices[i] = selectorIndices[i - 1]
        }
        var nextScrollSelectorIndex = selectorIndices[1] - 1
        if (mWrapSelectorWheel && nextScrollSelectorIndex < mMinValue) {
            nextScrollSelectorIndex = mMaxValue
        }
        selectorIndices[0] = nextScrollSelectorIndex
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex)
    }

    /**
     * Ensures we have a cached string representation of the given `
     * selectorIndex` to avoid multiple instantiations of the same string.
     */
    private fun ensureCachedScrollSelectorValue(selectorIndex: Int) {
        val cache = mSelectorIndexToStringCache
        var scrollSelectorValue = cache[selectorIndex]
        if (scrollSelectorValue != null) {
            return
        }
        scrollSelectorValue = if (selectorIndex < mMinValue || selectorIndex > mMaxValue) {
            ""
        } else {
            if (displayedValues2 != null) {
                val displayedValueIndex = selectorIndex - mMinValue
                if (displayedValueIndex >= displayedValues2!!.size) {
                    cache.remove(selectorIndex)
                    return
                }
                displayedValues2!![displayedValueIndex]
            } else {
                formatNumber(selectorIndex)
            }
        }
        cache.put(selectorIndex, scrollSelectorValue)
    }

    private fun formatNumber(value: Int): String {
        return if (mFormatter != null) mFormatter!!.format(value) else formatNumberWithLocale(value)
    }

    /**
     * Updates the view of this NumberPicker. If displayValues were specified in
     * the string corresponding to the index specified by the current value will
     * be returned. Otherwise, the formatter specified in [.setFormatter]
     * will be used to format the number.
     */
    private fun updateInputTextView() {
        /*
         * If we don't have displayed values then use the current number else
         * find the correct value in the displayed values for the current
         * number.
         */
        val text = if (displayedValues2 == null) formatNumber(mValue) else displayedValues2!![mValue - mMinValue]
        if (TextUtils.isEmpty(text)) {
            return
        }
        val beforeText: CharSequence = mSelectedText.getText()
        if (text == beforeText.toString()) {
            return
        }
        mSelectedText.setText(text)
    }

    /**
     * Notifies the listener, if registered, of a change of the value of this
     * NumberPicker.
     */
    private fun notifyChange(previous: Int, current: Int) {
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener!!.onValueChange(this, previous, current)
        }
    }
    /**
     * Posts a command for changing the current value by one.
     *
     * @param increment Whether to increment or decrement the value.
     */
    /**
     * Posts a command for changing the current value by one.
     *
     * @param increment Whether to increment or decrement the value.
     */
    private fun postChangeCurrentByOneFromLongPress(increment: Boolean, delayMillis: Long = ViewConfiguration.getLongPressTimeout().toLong()) {
        if (mChangeCurrentByOneFromLongPressCommand == null) {
            mChangeCurrentByOneFromLongPressCommand = ChangeCurrentByOneFromLongPressCommand()
        } else {
            removeCallbacks(mChangeCurrentByOneFromLongPressCommand)
        }
        mChangeCurrentByOneFromLongPressCommand!!.setStep(increment)
        postDelayed(mChangeCurrentByOneFromLongPressCommand, delayMillis)
    }

    /**
     * Removes the command for changing the current value by one.
     */
    private fun removeChangeCurrentByOneFromLongPress() {
        if (mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(mChangeCurrentByOneFromLongPressCommand)
        }
    }

    /**
     * Removes all pending callback from the message queue.
     */
    private fun removeAllCallbacks() {
        if (mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(mChangeCurrentByOneFromLongPressCommand)
        }
        if (mSetSelectionCommand != null) {
            mSetSelectionCommand!!.cancel()
        }
    }

    /**
     * @return The selected index given its displayed `value`.
     */
    private fun getSelectedPos(value: String): Int {
        var value = value
        if (displayedValues2 == null) {
            try {
                return value.toInt()
            } catch (e: NumberFormatException) {
                // Ignore as if it's not a number we don't care
            }
        } else {
            for (i in displayedValues2!!.indices) {
                // Don't force the user to type in jan when ja will do
                value = value.lowercase(Locale.getDefault())
                if (displayedValues2!![i].lowercase(Locale.getDefault()).startsWith(value)) {
                    return mMinValue + i
                }
            }

            /*
             * The user might have typed in a number into the month field i.e.
             * 10 instead of OCT so support that too.
             */try {
                return value.toInt()
            } catch (e: NumberFormatException) {
                // Ignore as if it's not a number we don't care
            }
        }
        return mMinValue
    }

    /**
     * Posts a [SetSelectionCommand] from the given
     * `selectionStart` to `selectionEnd`.
     */
    private fun postSetSelectionCommand(selectionStart: Int, selectionEnd: Int) {
        if (mSetSelectionCommand == null) {
            mSetSelectionCommand = SetSelectionCommand(mSelectedText)
        } else {
            mSetSelectionCommand!!.post(selectionStart, selectionEnd)
        }
    }
    /**
     * Create a new number picker
     *
     * @param mContext  the application environment.
     * @param attrs    a collection of attributes.
     * @param defStyle The default style to apply to this view.
     */
    /**
     * Create a new number picker.
     *
     * @param mContext The application environment.
     * @param attrs   A collection of attributes.
     */
    /**
     * Create a new number picker.
     *
     * @param context The application environment.
     */
    init {
        //  mNumberFormatter = NumberFormat.getInstance();
        val attributes = mContext.obtainStyledAttributes(
            attrs,
            R.styleable.NumberPicker, defStyle, 0
        )
        val selectionDivider = attributes.getDrawable(
            R.styleable.NumberPicker_np_divider
        )
        if (selectionDivider != null) {
            selectionDivider.callback = this
            if (selectionDivider.isStateful) {
                selectionDivider.setState(drawableState)
            }
            mDividerDrawable = selectionDivider
        } else {
            mDividerColor = attributes.getColor(
                R.styleable.NumberPicker_np_dividerColor,
                mDividerColor
            )
            dividerColor = mDividerColor
        }
        val displayMetrics = resources.displayMetrics
        val defDividerDistance = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            UNSCALED_DEFAULT_DIVIDER_DISTANCE.toFloat(), displayMetrics
        ).toInt()
        val defDividerThickness = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            UNSCALED_DEFAULT_DIVIDER_THICKNESS.toFloat(), displayMetrics
        ).toInt()
        mDividerDistance = attributes.getDimensionPixelSize(
            R.styleable.NumberPicker_np_dividerDistance, defDividerDistance
        )
        mDividerLength = attributes.getDimensionPixelSize(
            R.styleable.NumberPicker_np_dividerLength, 0
        )
        mDividerThickness = attributes.getDimensionPixelSize(
            R.styleable.NumberPicker_np_dividerThickness, defDividerThickness
        )
        mDividerType = attributes.getInt(R.styleable.NumberPicker_np_dividerType, SIDE_LINES)
        order = attributes.getInt(R.styleable.NumberPicker_np_order, ASCENDING)
        mOrientation = attributes.getInt(R.styleable.NumberPicker_np_orientation, VERTICAL)
        val width = attributes.getDimensionPixelSize(
            R.styleable.NumberPicker_np_width,
            SIZE_UNSPECIFIED
        ).toFloat()
        val height = attributes.getDimensionPixelSize(
            R.styleable.NumberPicker_np_height,
            SIZE_UNSPECIFIED
        ).toFloat()
        setWidthAndHeight()
        mComputeMaxWidth = true
        mValue = attributes.getInt(R.styleable.NumberPicker_np_value, mValue)
        mMaxValue = attributes.getInt(R.styleable.NumberPicker_np_max, mMaxValue)
        mMinValue = attributes.getInt(R.styleable.NumberPicker_np_min, mMinValue)
        selectedTextAlign = attributes.getInt(
            R.styleable.NumberPicker_np_selectedTextAlign,
            selectedTextAlign
        )
        mSelectedTextColor = attributes.getColor(
            R.styleable.NumberPicker_np_selectedTextColor,
            mSelectedTextColor
        )
        mSelectedTextSize = attributes.getDimension(
            R.styleable.NumberPicker_np_selectedTextSize,
            spToPx(mSelectedTextSize)
        )
        selectedTextStrikeThru = attributes.getBoolean(
            R.styleable.NumberPicker_np_selectedTextStrikeThru, selectedTextStrikeThru
        )
        selectedTextUnderline = attributes.getBoolean(
            R.styleable.NumberPicker_np_selectedTextUnderline, selectedTextUnderline
        )
        mSelectedTypeface = Typeface.create(
            attributes.getString(
                R.styleable.NumberPicker_np_selectedTypeface
            ), Typeface.NORMAL
        )
        textAlign = attributes.getInt(R.styleable.NumberPicker_np_textAlign, textAlign)
        mTextColor = attributes.getColor(R.styleable.NumberPicker_np_textColor, mTextColor)
        mTextSize = attributes.getDimension(
            R.styleable.NumberPicker_np_textSize,
            spToPx(mTextSize)
        )
        textStrikeThru = attributes.getBoolean(
            R.styleable.NumberPicker_np_textStrikeThru, textStrikeThru
        )
        textUnderline = attributes.getBoolean(
            R.styleable.NumberPicker_np_textUnderline, textUnderline
        )
        mTypeface = Typeface.create(
            attributes.getString(R.styleable.NumberPicker_np_typeface),
            Typeface.NORMAL
        )
        mFormatter = stringToFormatter(attributes.getString(R.styleable.NumberPicker_np_formatter))
        isFadingEdgeEnabled = attributes.getBoolean(
            R.styleable.NumberPicker_np_fadingEdgeEnabled,
            isFadingEdgeEnabled
        )
        fadingEdgeStrength = attributes.getFloat(
            R.styleable.NumberPicker_np_fadingEdgeStrength,
            fadingEdgeStrength
        )
        isScrollerEnabled = attributes.getBoolean(
            R.styleable.NumberPicker_np_scrollerEnabled,
            isScrollerEnabled
        )
        mWheelItemCount = attributes.getInt(
            R.styleable.NumberPicker_np_wheelItemCount,
            mWheelItemCount
        )
        lineSpacingMultiplier = attributes.getFloat(
            R.styleable.NumberPicker_np_lineSpacingMultiplier, lineSpacingMultiplier
        )
        mMaxFlingVelocityCoefficient = attributes.getInt(
            R.styleable.NumberPicker_np_maxFlingVelocityCoefficient,
            mMaxFlingVelocityCoefficient
        )
        mHideWheelUntilFocused = attributes.getBoolean(
            R.styleable.NumberPicker_np_hideWheelUntilFocused, false
        )
        isAccessibilityDescriptionEnabled = attributes.getBoolean(
            R.styleable.NumberPicker_np_accessibilityDescriptionEnabled, true
        )
        mItemSpacing = attributes.getDimensionPixelSize(
            R.styleable.NumberPicker_np_itemSpacing, 0
        )
        // By default LinearLayout that we extend is not drawn. This is
        // its draw() method is not called but dispatchDraw() is called
        // directly (see ViewGroup.drawChild()). However, this class uses
        // the fading edge effect implemented by View and we need our
        // draw() method to be called. Therefore, we declare we will draw.
        setWillNotDraw(false)
        val inflater = mContext.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        inflater.inflate(R.layout.number_picker_material, this, true)

        // input text
        mSelectedText = findViewById(R.id.np__numberpicker_input)
        mSelectedText.setEnabled(false)
        mSelectedText.isFocusable = false
        mSelectedText.setImeOptions(EditorInfo.IME_ACTION_NONE)

        // create the selector wheel paint
        val paint = Paint()
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        mSelectorWheelPaint = paint
        selectedTextColor = mSelectedTextColor
        textColor = mTextColor
        textSize = mTextSize
        selectedTextSize = mSelectedTextSize
        typeface = mTypeface
        setSelectedTypeface(mSelectedTypeface)
        formatter = mFormatter
        updateInputTextView()
        value = mValue
        maxValue = mMaxValue
        minValue = mMinValue
        wheelItemCount = mWheelItemCount
        mWrapSelectorWheel = attributes.getBoolean(
            R.styleable.NumberPicker_np_wrapSelectorWheel,
            mWrapSelectorWheel
        )
        wrapSelectorWheel = mWrapSelectorWheel
        if (width != SIZE_UNSPECIFIED.toFloat() && height != SIZE_UNSPECIFIED.toFloat()) {
            scaleX = width / mMinWidth
            scaleY = height / mMaxHeight
        } else if (width != SIZE_UNSPECIFIED.toFloat()) {
            val scale = width / mMinWidth
            scaleX = scale
            scaleY = scale
        } else if (height != SIZE_UNSPECIFIED.toFloat()) {
            val scale = height / mMaxHeight
            scaleX = scale
            scaleY = scale
        }

        // initialize constants
        mViewConfiguration = ViewConfiguration.get(mContext)
        mTouchSlop = mViewConfiguration.scaledTouchSlop
        mMinimumFlingVelocity = mViewConfiguration.scaledMinimumFlingVelocity
        mMaximumFlingVelocity = (mViewConfiguration.scaledMaximumFlingVelocity
                / mMaxFlingVelocityCoefficient)

        // create the fling and adjust scrollers
        mFlingScroller = Scroller(mContext, null, true)
        mAdjustScroller = Scroller(mContext, DecelerateInterpolator(2.5f))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // If not explicitly specified this view is important for accessibility.
            if (importantForAccessibility == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
                setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Should be focusable by default, as the text view whose visibility changes is focusable
            if (focusable == FOCUSABLE_AUTO) {
                setFocusable(FOCUSABLE)
                setFocusableInTouchMode(true)
            }
        }
        attributes.recycle()
    }

    /**
     * Filter for accepting only valid indices or prefixes of the string
     * representation of valid indices.
     */
    internal inner class InputTextFilter : NumberKeyListener() {
        // XXX This doesn't allow for range limits when controlled by a soft input method!
        override fun getInputType(): Int {
            return InputType.TYPE_CLASS_TEXT
        }

        override fun getAcceptedChars(): CharArray {
            return DIGIT_CHARACTERS
        }

        override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned,
            dstart: Int, dend: Int
        ): CharSequence {
            // We don't know what the output will be, so always cancel any
            // pending set selection command.
            if (mSetSelectionCommand != null) {
                mSetSelectionCommand!!.cancel()
            }
            return if (displayedValues2 == null) {
                var filtered = super.filter(source, start, end, dest, dstart, dend)
                if (filtered == null) {
                    filtered = source.subSequence(start, end)
                }
                val result = (dest.subSequence(0, dstart).toString() + filtered
                        + dest.subSequence(dend, dest.length))
                if ("" == result) {
                    return result
                }
                val `val` = getSelectedPos(result)

                /*
                 * Ensure the user can't type in a value greater than the max
                 * allowed. We have to allow less than min as the user might
                 * want to delete some numbers and then type a new number.
                 * And prevent multiple-"0" that exceeds the length of upper
                 * bound number.
                 */if (`val` > mMaxValue || result.length > mMaxValue.toString().length) {
                    ""
                } else {
                    filtered
                }
            } else {
                val filtered: CharSequence = source.subSequence(start, end).toString()
                if (TextUtils.isEmpty(filtered)) {
                    return ""
                }
                val result = (dest.subSequence(0, dstart).toString() + filtered
                        + dest.subSequence(dend, dest.length))
                val str = result.toString().lowercase(Locale.getDefault())
                for (`val`: String in displayedValues2!!) {
                    val valLowerCase = `val`.lowercase(Locale.getDefault())
                    if (valLowerCase.startsWith(str)) {
                        postSetSelectionCommand(result.length, `val`.length)
                        return `val`.subSequence(dstart, `val`.length)
                    }
                }
                ""
            }
        }
    }

    /**
     * Ensures that the scroll wheel is adjusted i.e. there is no offset and the
     * middle element is in the middle of the widget.
     */
    private fun ensureScrollWheelAdjusted() {
        // adjust to the closest value
        var delta = mInitialScrollOffset - mCurrentScrollOffset
        if (delta == 0) {
            return
        }
        if (abs(delta.toDouble()) > mSelectorElementSize / 2) {
            delta += if (delta > 0) -mSelectorElementSize else mSelectorElementSize
        }
        if (isHorizontalMode) {
            mPreviousScrollerX = 0
            mAdjustScroller.startScroll(0, 0, delta, 0, SELECTOR_ADJUSTMENT_DURATION_MILLIS)
        } else {
            mPreviousScrollerY = 0
            mAdjustScroller.startScroll(0, 0, 0, delta, SELECTOR_ADJUSTMENT_DURATION_MILLIS)
        }
        invalidate()
    }

    /**
     * Command for setting the input text selection.
     */
    private class SetSelectionCommand internal constructor(private val mInputText: EditText) : Runnable {
        private var mSelectionStart = 0
        private var mSelectionEnd = 0

        /**
         * Whether this runnable is currently posted.
         */
        private var mPosted = false
        fun post(selectionStart: Int, selectionEnd: Int) {
            mSelectionStart = selectionStart
            mSelectionEnd = selectionEnd
            if (!mPosted) {
                mInputText.post(this)
                mPosted = true
            }
        }

        fun cancel() {
            if (mPosted) {
                mInputText.removeCallbacks(this)
                mPosted = false
            }
        }

        override fun run() {
            mPosted = false
            mInputText.setSelection(mSelectionStart, mSelectionEnd)
        }
    }

    /**
     * Command for changing the current value from a long press by one.
     */
    internal inner class ChangeCurrentByOneFromLongPressCommand : Runnable {
        private var mIncrement = false
        fun setStep(increment: Boolean) {
            mIncrement = increment
        }

        override fun run() {
            changeValueByOne(mIncrement)
            postDelayed(this, mLongPressUpdateInterval)
        }
    }

    private fun formatNumberWithLocale(value: Int): String {
        return value.toString()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    private fun pxToDp(px: Float): Float {
        return px / resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp,
            resources.displayMetrics
        )
    }

    private fun pxToSp(px: Float): Float {
        return px / resources.displayMetrics.scaledDensity
    }

    private fun stringToFormatter(formatter: String?): Formatter? {
        return if (TextUtils.isEmpty(formatter)) {
            null
        } else object : Formatter {
            override fun format(i: Int): String {
                return String.format(Locale.getDefault(), (formatter)!!, i)
            }
        }
    }

    private fun setWidthAndHeight() {
        if (isHorizontalMode) {
            mMinHeight = SIZE_UNSPECIFIED
            mMaxHeight = dpToPx(DEFAULT_MIN_WIDTH.toFloat()).toInt()
            mMinWidth = dpToPx(DEFAULT_MAX_HEIGHT.toFloat()).toInt()
            mMaxWidth = SIZE_UNSPECIFIED
        } else {
            mMinHeight = SIZE_UNSPECIFIED
            mMaxHeight = dpToPx(DEFAULT_MAX_HEIGHT.toFloat()).toInt()
            mMinWidth = dpToPx(DEFAULT_MIN_WIDTH.toFloat()).toInt()
            mMaxWidth = SIZE_UNSPECIFIED
        }
    }

    fun setDividerColorResource(@ColorRes colorId: Int) {
        dividerColor = ContextCompat.getColor(mContext, colorId)
    }

    fun setDividerDistance(distance: Int) {
        mDividerDistance = distance
    }

    fun setDividerDistanceResource(@DimenRes dimenId: Int) {
        setDividerDistance(resources.getDimensionPixelSize(dimenId))
    }

    fun setDividerType(@DividerType dividerType: Int) {
        mDividerType = dividerType
        invalidate()
    }

    fun setDividerThickness(thickness: Int) {
        mDividerThickness = thickness
    }

    fun setDividerThicknessResource(@DimenRes dimenId: Int) {
        setDividerThickness(resources.getDimensionPixelSize(dimenId))
    }

    override fun setOrientation(@Orientation orientation: Int) {
        mOrientation = orientation
        setWidthAndHeight()
        requestLayout()
    }

    fun setFormatter(formatter: String?) {
        if (TextUtils.isEmpty(formatter)) {
            return
        }
        this.formatter = stringToFormatter(formatter)
    }

    fun setFormatter(@StringRes stringId: Int) {
        setFormatter(resources.getString(stringId))
    }

    fun setSelectedTextColorResource(@ColorRes colorId: Int) {
        selectedTextColor = ContextCompat.getColor(mContext, colorId)
    }

    fun setSelectedTextSize(@DimenRes dimenId: Int) {
        selectedTextSize = resources.getDimension(dimenId)
    }

    fun setSelectedTypeface(typeface: Typeface?) {
        mSelectedTypeface = typeface
        if (mSelectedTypeface != null) {
            mSelectorWheelPaint.setTypeface(mSelectedTypeface)
        } else if (mTypeface != null) {
            mSelectorWheelPaint.setTypeface(mTypeface)
        } else {
            mSelectorWheelPaint.setTypeface(Typeface.MONOSPACE)
        }
    }

    fun setSelectedTypeface(string: String?, style: Int) {
        if (TextUtils.isEmpty(string)) {
            return
        }
        setSelectedTypeface(Typeface.create(string, style))
    }

    fun setSelectedTypeface(string: String?) {
        setSelectedTypeface(string, Typeface.NORMAL)
    }

    fun setSelectedTypeface(@StringRes stringId: Int, style: Int) {
        setSelectedTypeface(resources.getString(stringId), style)
    }

    fun setSelectedTypeface(@StringRes stringId: Int) {
        setSelectedTypeface(stringId, Typeface.NORMAL)
    }

    fun setTextColorResource(@ColorRes colorId: Int) {
        textColor = ContextCompat.getColor(mContext, colorId)
    }

    fun setTextSize(@DimenRes dimenId: Int) {
        textSize = resources.getDimension(dimenId)
    }

    fun setTypeface(string: String?, style: Int) {
        if (TextUtils.isEmpty(string)) {
            return
        }
        typeface = Typeface.create(string, style)
    }

    fun setTypeface(string: String?) {
        setTypeface(string, Typeface.NORMAL)
    }

    fun setTypeface(@StringRes stringId: Int, style: Int) {
        setTypeface(resources.getString(stringId), style)
    }

    fun setTypeface(@StringRes stringId: Int) {
        setTypeface(stringId, Typeface.NORMAL)
    }

    fun setItemSpacing(itemSpacing: Int) {
        mItemSpacing = itemSpacing
    }

    val isHorizontalMode: Boolean
        get() = orientation == HORIZONTAL
    val isAscendingOrder: Boolean
        get() = order == ASCENDING

    val dividerDistance: Float
        get() = pxToDp(mDividerDistance.toFloat())
    val dividerThickness: Float
        get() = pxToDp(mDividerThickness.toFloat())

    override fun getOrientation(): Int {
        return mOrientation
    }

    companion object {
        const val VERTICAL = LinearLayout.VERTICAL
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val ASCENDING = 0
        const val DESCENDING = 1
        const val RIGHT = 0
        const val CENTER = 1
        const val LEFT = 2
        const val SIDE_LINES = 0
        const val UNDERLINE = 1

        /**
         * The default update interval during long press.
         */
        private const val DEFAULT_LONG_PRESS_UPDATE_INTERVAL: Long = 300

        /**
         * The default coefficient to adjust (divide) the max fling velocity.
         */
        private const val DEFAULT_MAX_FLING_VELOCITY_COEFFICIENT = 8

        /**
         * The the duration for adjusting the selector wheel.
         */
        private const val SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800

        /**
         * The duration of scrolling while snapping to a given position.
         */
        private const val SNAP_SCROLL_DURATION = 300

        /**
         * The default strength of fading edge while drawing the selector.
         */
        private const val DEFAULT_FADING_EDGE_STRENGTH = 0.9f

        /**
         * The default unscaled height of the divider.
         */
        private const val UNSCALED_DEFAULT_DIVIDER_THICKNESS = 2

        /**
         * The default unscaled distance between the dividers.
         */
        private const val UNSCALED_DEFAULT_DIVIDER_DISTANCE = 48

        /**
         * Constant for unspecified size.
         */
        private const val SIZE_UNSPECIFIED = -1

        /**
         * The default color of divider.
         */
        private const val DEFAULT_DIVIDER_COLOR = -0x1000000

        /**
         * The default max value of this widget.
         */
        private const val DEFAULT_MAX_VALUE = 100

        /**
         * The default min value of this widget.
         */
        private const val DEFAULT_MIN_VALUE = 1

        /**
         * The default wheel item count of this widget.
         */
        private const val DEFAULT_WHEEL_ITEM_COUNT = 3

        /**
         * The default max height of this widget.
         */
        private const val DEFAULT_MAX_HEIGHT = 180

        /**
         * The default min width of this widget.
         */
        private const val DEFAULT_MIN_WIDTH = 64

        /**
         * The default align of text.
         */
        private val DEFAULT_TEXT_ALIGN = CENTER

        /**
         * The default color of text.
         */
        private const val DEFAULT_TEXT_COLOR = -0x1000000

        /**
         * The default size of text.
         */
        private const val DEFAULT_TEXT_SIZE = 25f

        /**
         * The default line spacing multiplier of text.
         */
        private const val DEFAULT_LINE_SPACING_MULTIPLIER = 1f
        private val sTwoDigitFormatter = TwoDigitFormatter()
        val twoDigitFormatter: Formatter
            get() = sTwoDigitFormatter

        /**
         * Utility to reconcile a desired size and state, with constraints imposed
         * by a MeasureSpec.  Will take the desired size, unless a different size
         * is imposed by the constraints.  The returned value is a compound integer,
         * with the resolved size in the [.MEASURED_SIZE_MASK] bits and
         * optionally the bit [.MEASURED_STATE_TOO_SMALL] set if the resulting
         * size is smaller than the size the view wants to be.
         *
         * @param size        How big the view wants to be
         * @param measureSpec Constraints imposed by the parent
         * @return Size information bit mask as defined by
         * [.MEASURED_SIZE_MASK] and [.MEASURED_STATE_TOO_SMALL].
         */
        fun resolveSizeAndState(size: Int, measureSpec: Int, childMeasuredState: Int): Int {
            var result = size
            val specMode = MeasureSpec.getMode(measureSpec)
            val specSize = MeasureSpec.getSize(measureSpec)
            when (specMode) {
                MeasureSpec.UNSPECIFIED -> result = size
                MeasureSpec.AT_MOST -> result = if (specSize < size) {
                    specSize or MEASURED_STATE_TOO_SMALL
                } else {
                    size
                }

                MeasureSpec.EXACTLY -> result = specSize
            }
            return result or (childMeasuredState and MEASURED_STATE_MASK)
        }

        /**
         * The numbers accepted by the input text's [Filter]
         */
        private val DIGIT_CHARACTERS = charArrayOf( // Latin digits are the common case
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  // Arabic-Indic
            '\u0660', '\u0661', '\u0662', '\u0663', '\u0664',
            '\u0665', '\u0666', '\u0667', '\u0668', '\u0669',  // Extended Arabic-Indic
            '\u06f0', '\u06f1', '\u06f2', '\u06f3', '\u06f4',
            '\u06f5', '\u06f6', '\u06f7', '\u06f8', '\u06f9',  // Hindi and Marathi (Devanagari script)
            '\u0966', '\u0967', '\u0968', '\u0969', '\u096a',
            '\u096b', '\u096c', '\u096d', '\u096e', '\u096f',  // Bengali
            '\u09e6', '\u09e7', '\u09e8', '\u09e9', '\u09ea',
            '\u09eb', '\u09ec', '\u09ed', '\u09ee', '\u09ef',  // Kannada
            '\u0ce6', '\u0ce7', '\u0ce8', '\u0ce9', '\u0cea',
            '\u0ceb', '\u0cec', '\u0ced', '\u0cee', '\u0cef',  // Negative
            '-'
        )
    }
}
