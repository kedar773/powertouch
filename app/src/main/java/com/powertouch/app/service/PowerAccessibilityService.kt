package com.powertouch.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import com.powertouch.app.PowerApplication
import com.powertouch.app.overlay.EdgeDrawerOverlayManager
import com.powertouch.app.overlay.FloatingOverlayManager

class PowerAccessibilityService : AccessibilityService() {

    private var floatingManager: FloatingOverlayManager? = null
    private var drawerManager: EdgeDrawerOverlayManager? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        initOverlays()
    }

    fun initOverlays() {
        if (!Settings.canDrawOverlays(this)) return

        if (floatingManager == null) {
            floatingManager = FloatingOverlayManager(this)
        }
        if (drawerManager == null) {
            drawerManager = EdgeDrawerOverlayManager(this)
        }

        val prefs = PowerApplication.instance.preferences
        if (prefs.isFloatingEnabled) {
            floatingManager?.show()
        } else {
            floatingManager?.hide()
        }

        if (prefs.isDrawerEnabled) {
            drawerManager?.show()
        } else {
            drawerManager?.hide()
        }
    }

    fun updateOverlaySettings() {
        if (!Settings.canDrawOverlays(this)) {
            floatingManager?.hide()
            drawerManager?.hide()
            return
        }

        if (floatingManager == null || drawerManager == null) {
            initOverlays()
            return
        }

        val prefs = PowerApplication.instance.preferences
        if (prefs.isFloatingEnabled) {
            floatingManager?.updateSettings()
        } else {
            floatingManager?.hide()
        }

        if (prefs.isDrawerEnabled) {
            drawerManager?.updateSettings()
        } else {
            drawerManager?.hide()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No need to inspect window content
    }

    override fun onInterrupt() {
        // Handle interruption
    }

    override fun onDestroy() {
        floatingManager?.hide()
        drawerManager?.hide()
        floatingManager = null
        drawerManager = null
        instance = null
        super.onDestroy()
    }

    companion object {
        var instance: PowerAccessibilityService? = null
            private set

        fun isRunning(): Boolean = instance != null

        fun lockScreen(): Boolean {
            val service = instance ?: return false
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            } else {
                false
            }
        }

        fun showPowerMenu(): Boolean {
            val service = instance ?: return false
            return service.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        }

        fun refreshOverlays() {
            instance?.updateOverlaySettings()
        }
    }
}
