package com.powertouch.app.overlay

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.powertouch.app.PowerApplication
import com.powertouch.app.R
import com.powertouch.app.service.PowerAccessibilityService
import com.powertouch.app.util.HapticHelper
import kotlin.math.abs

class FloatingOverlayManager(private val context: Context) {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val prefs = PowerApplication.instance.preferences

    private var floatingView: View? = null
    private var windowParams: WindowManager.LayoutParams? = null

    private val handler = Handler(Looper.getMainLooper())
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longPressTimeout = 500L
    private val fadeDelayMs = 3000L

    private var isDragging = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isLongPressed = false

    private val longPressRunnable = Runnable {
        isLongPressed = true
        HapticHelper.vibrateLongPress(context)
        PowerAccessibilityService.showPowerMenu()
    }

    private val fadeRunnable = Runnable {
        floatingView?.animate()
            ?.alpha(prefs.floatingInactiveOpacity)
            ?.setDuration(300)
            ?.start()
    }

    fun show() {
        if (floatingView != null) return
        if (!prefs.isFloatingEnabled) return

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_floating_button, null)
        floatingView = view

        val sizePx = dpToPx(prefs.floatingSizeDp)
        val iconView = view.findViewById<ImageView>(R.id.btnFloatingPower)
        val layoutParams = iconView.layoutParams
        layoutParams.width = sizePx
        layoutParams.height = sizePx
        iconView.layoutParams = layoutParams

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.floatingPosX
            y = prefs.floatingPosY
        }
        windowParams = params

        view.alpha = prefs.floatingActiveOpacity
        setupTouchListener(view)

        try {
            windowManager.addView(view, params)
            resetFadeTimer()
        } catch (e: Exception) {
            e.printStackTrace()
            floatingView = null
        }
    }

    fun hide() {
        handler.removeCallbacks(longPressRunnable)
        handler.removeCallbacks(fadeRunnable)
        floatingView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            floatingView = null
        }
    }

    fun updateSettings() {
        if (!prefs.isFloatingEnabled) {
            hide()
            return
        }

        if (floatingView == null) {
            show()
            return
        }

        floatingView?.let { view ->
            val sizePx = dpToPx(prefs.floatingSizeDp)
            val iconView = view.findViewById<ImageView>(R.id.btnFloatingPower)
            val iconParams = iconView.layoutParams
            iconParams.width = sizePx
            iconParams.height = sizePx
            iconView.layoutParams = iconParams

            view.alpha = prefs.floatingActiveOpacity
            resetFadeTimer()
        }
    }

    private fun setupTouchListener(view: View) {
        view.setOnTouchListener { _, event ->
            val params = windowParams ?: return@setOnTouchListener false

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.removeCallbacks(fadeRunnable)
                    view.animate().alpha(prefs.floatingActiveOpacity).setDuration(150).start()

                    isDragging = false
                    isLongPressed = false
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    handler.postDelayed(longPressRunnable, longPressTimeout)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    if (!isDragging && (abs(deltaX) > touchSlop || abs(deltaY) > touchSlop)) {
                        isDragging = true
                        handler.removeCallbacks(longPressRunnable)
                    }

                    if (isDragging && !isLongPressed) {
                        params.x = initialX + deltaX
                        params.y = initialY + deltaY
                        try {
                            windowManager.updateViewLayout(view, params)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)

                    if (!isDragging && !isLongPressed) {
                        // Single tap detected -> Lock screen
                        HapticHelper.vibrateTap(context)
                        PowerAccessibilityService.lockScreen()
                    } else if (isDragging) {
                        snapToNearestEdge(view, params)
                    }

                    resetFadeTimer()
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                    resetFadeTimer()
                    true
                }

                else -> false
            }
        }
    }

    private fun snapToNearestEdge(view: View, params: WindowManager.LayoutParams) {
        val screenSize = getScreenSize()
        val screenWidth = screenSize.x
        val viewWidth = view.width

        val targetX = if (params.x + viewWidth / 2 < screenWidth / 2) {
            0
        } else {
            screenWidth - viewWidth
        }

        val animator = ValueAnimator.ofInt(params.x, targetX)
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { va ->
            params.x = va.animatedValue as Int
            try {
                windowManager.updateViewLayout(view, params)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        animator.start()

        // Persist new position
        prefs.floatingPosX = targetX
        prefs.floatingPosY = params.y
    }

    private fun resetFadeTimer() {
        handler.removeCallbacks(fadeRunnable)
        handler.postDelayed(fadeRunnable, fadeDelayMs)
    }

    private fun getScreenSize(): Point {
        val size = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            size.x = bounds.width()
            size.y = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getSize(size)
        }
        return size
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
