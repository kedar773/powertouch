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
import android.widget.ImageButton
import android.widget.LinearLayout
import com.powertouch.app.PowerApplication
import com.powertouch.app.R
import com.powertouch.app.service.PowerAccessibilityService
import com.powertouch.app.util.HapticHelper
import kotlin.math.abs

class EdgeDrawerOverlayManager(private val context: Context) {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val prefs = PowerApplication.instance.preferences

    private var rootView: View? = null
    private var windowParams: WindowManager.LayoutParams? = null

    private val handler = Handler(Looper.getMainLooper())
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longPressTimeout = 500L
    private val autoRetractDelay = 3500L

    private var isDraggingVertical = false
    private var isExpanded = false
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isLongPressed = false

    private val longPressRunnable = Runnable {
        isLongPressed = true
        HapticHelper.vibrateLongPress(context)
        PowerAccessibilityService.showPowerMenu()
    }

    private val retractRunnable = Runnable {
        retractPanel()
    }

    private val fadeRunnable = Runnable {
        if (!isExpanded) {
            rootView?.findViewById<View>(R.id.edgeHandle)?.animate()
                ?.alpha(prefs.drawerInactiveOpacity)
                ?.setDuration(300)
                ?.start()
        }
    }

    fun show() {
        if (rootView != null) return
        if (!prefs.isDrawerEnabled) return

        val isRightSide = prefs.drawerEdgeSide == "RIGHT"
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_edge_drawer, null)
        rootView = view

        val edgeHandle = view.findViewById<View>(R.id.edgeHandle)
        val panelExpanded = view.findViewById<LinearLayout>(R.id.panelExpanded)
        val btnDrawerLock = view.findViewById<ImageButton>(R.id.btnDrawerLock)

        // Adjust layout orientation/ordering if on Left edge
        if (!isRightSide) {
            // Swap child order so handle is at left and panel opens to the right
            val linear = view as LinearLayout
            linear.removeView(edgeHandle)
            linear.removeView(panelExpanded)
            linear.addView(edgeHandle)
            linear.addView(panelExpanded)
            edgeHandle.setBackgroundResource(R.drawable.bg_drawer_handle_left)
        } else {
            edgeHandle.setBackgroundResource(R.drawable.bg_drawer_handle)
        }

        panelExpanded.visibility = View.GONE
        isExpanded = false

        val gravity = if (isRightSide) {
            Gravity.TOP or Gravity.END
        } else {
            Gravity.TOP or Gravity.START
        }

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
            this.gravity = gravity
            x = 0
            y = prefs.drawerPosY
        }
        windowParams = params

        setupHandleTouchListener(edgeHandle, panelExpanded)

        // Panel button click: tap to lock, hold for power menu
        btnDrawerLock.setOnClickListener {
            HapticHelper.vibrateTap(context)
            PowerAccessibilityService.lockScreen()
            retractPanel()
        }
        btnDrawerLock.setOnLongClickListener {
            HapticHelper.vibrateLongPress(context)
            PowerAccessibilityService.showPowerMenu()
            retractPanel()
            true
        }

        try {
            windowManager.addView(view, params)
            resetFadeTimer()
        } catch (e: Exception) {
            e.printStackTrace()
            rootView = null
        }
    }

    fun hide() {
        handler.removeCallbacks(longPressRunnable)
        handler.removeCallbacks(retractRunnable)
        handler.removeCallbacks(fadeRunnable)
        rootView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            rootView = null
        }
    }

    fun updateSettings() {
        hide()
        if (prefs.isDrawerEnabled) {
            show()
        }
    }

    private fun setupHandleTouchListener(handle: View, panelExpanded: LinearLayout) {
        handle.setOnTouchListener { _, event ->
            val params = windowParams ?: return@setOnTouchListener false
            val isRightSide = prefs.drawerEdgeSide == "RIGHT"

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handler.removeCallbacks(fadeRunnable)
                    handler.removeCallbacks(retractRunnable)
                    handle.animate().alpha(1.0f).setDuration(150).start()

                    isDraggingVertical = false
                    isLongPressed = false
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    handler.postDelayed(longPressRunnable, longPressTimeout)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    // Check horizontal slide inward gesture to draw out panel
                    val isSlideInward = if (isRightSide) {
                        deltaX < -dpToPx(24) // swiped left from right edge
                    } else {
                        deltaX > dpToPx(24) // swiped right from left edge
                    }

                    if (isSlideInward && !isExpanded) {
                        handler.removeCallbacks(longPressRunnable)
                        expandPanel()
                        return@setOnTouchListener true
                    }

                    if (!isDraggingVertical && abs(deltaY) > touchSlop) {
                        isDraggingVertical = true
                        handler.removeCallbacks(longPressRunnable)
                    }

                    if (isDraggingVertical && !isLongPressed) {
                        params.y = initialY + deltaY
                        try {
                            windowManager.updateViewLayout(rootView, params)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)

                    if (!isDraggingVertical && !isLongPressed) {
                        if (!isExpanded) {
                            // Tapping handle either expands or directly locks
                            // Slide gesture draws it out; a quick tap locks screen immediately
                            HapticHelper.vibrateTap(context)
                            PowerAccessibilityService.lockScreen()
                        }
                    } else if (isDraggingVertical) {
                        // Persist vertical position on edge
                        prefs.drawerPosY = params.y
                    }

                    if (isExpanded) {
                        scheduleRetract()
                    } else {
                        resetFadeTimer()
                    }
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (isExpanded) scheduleRetract() else resetFadeTimer()
                    true
                }

                else -> false
            }
        }
    }

    private fun expandPanel() {
        val view = rootView ?: return
        val panelExpanded = view.findViewById<LinearLayout>(R.id.panelExpanded) ?: return
        if (isExpanded) return

        isExpanded = true
        HapticHelper.vibrateTap(context)

        panelExpanded.visibility = View.VISIBLE
        panelExpanded.alpha = 0f
        panelExpanded.scaleX = 0.8f
        panelExpanded.scaleY = 0.8f
        panelExpanded.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(220)
            .setInterpolator(DecelerateInterpolator())
            .start()

        scheduleRetract()
    }

    private fun retractPanel() {
        val view = rootView ?: return
        val panelExpanded = view.findViewById<LinearLayout>(R.id.panelExpanded) ?: return
        if (!isExpanded) return

        panelExpanded.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(180)
            .withEndAction {
                panelExpanded.visibility = View.GONE
                isExpanded = false
                resetFadeTimer()
            }
            .start()
    }

    private fun scheduleRetract() {
        handler.removeCallbacks(retractRunnable)
        handler.postDelayed(retractRunnable, autoRetractDelay)
    }

    private fun resetFadeTimer() {
        handler.removeCallbacks(fadeRunnable)
        handler.postDelayed(fadeRunnable, 2500L)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
