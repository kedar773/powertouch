package com.powertouch.app

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isFloatingEnabled: Boolean
        get() = prefs.getBoolean(KEY_FLOATING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_FLOATING_ENABLED, value).apply()

    var isDrawerEnabled: Boolean
        get() = prefs.getBoolean(KEY_DRAWER_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_DRAWER_ENABLED, value).apply()

    var floatingSizeDp: Int
        get() = prefs.getInt(KEY_FLOATING_SIZE, 56)
        set(value) = prefs.edit().putInt(KEY_FLOATING_SIZE, value).apply()

    var floatingInactiveOpacity: Float
        get() = prefs.getFloat(KEY_FLOATING_INACTIVE_OPACITY, 0.30f)
        set(value) = prefs.edit().putFloat(KEY_FLOATING_INACTIVE_OPACITY, value).apply()

    var floatingActiveOpacity: Float
        get() = prefs.getFloat(KEY_FLOATING_ACTIVE_OPACITY, 0.90f)
        set(value) = prefs.edit().putFloat(KEY_FLOATING_ACTIVE_OPACITY, value).apply()

    var floatingPosX: Int
        get() = prefs.getInt(KEY_FLOATING_POS_X, 0)
        set(value) = prefs.edit().putInt(KEY_FLOATING_POS_X, value).apply()

    var floatingPosY: Int
        get() = prefs.getInt(KEY_FLOATING_POS_Y, 400)
        set(value) = prefs.edit().putInt(KEY_FLOATING_POS_Y, value).apply()

    var drawerEdgeSide: String
        get() = prefs.getString(KEY_DRAWER_EDGE_SIDE, "RIGHT") ?: "RIGHT"
        set(value) = prefs.edit().putString(KEY_DRAWER_EDGE_SIDE, value).apply()

    var drawerInactiveOpacity: Float
        get() = prefs.getFloat(KEY_DRAWER_INACTIVE_OPACITY, 0.30f)
        set(value) = prefs.edit().putFloat(KEY_DRAWER_INACTIVE_OPACITY, value).apply()

    var drawerPosY: Int
        get() = prefs.getInt(KEY_DRAWER_POS_Y, 500)
        set(value) = prefs.edit().putInt(KEY_DRAWER_POS_Y, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    companion object {
        private const val PREFS_NAME = "power_touch_prefs"
        private const val KEY_FLOATING_ENABLED = "floating_enabled"
        private const val KEY_DRAWER_ENABLED = "drawer_enabled"
        private const val KEY_FLOATING_SIZE = "floating_size"
        private const val KEY_FLOATING_INACTIVE_OPACITY = "floating_inactive_opacity"
        private const val KEY_FLOATING_ACTIVE_OPACITY = "floating_active_opacity"
        private const val KEY_FLOATING_POS_X = "floating_pos_x"
        private const val KEY_FLOATING_POS_Y = "floating_pos_y"
        private const val KEY_DRAWER_EDGE_SIDE = "drawer_edge_side"
        private const val KEY_DRAWER_INACTIVE_OPACITY = "drawer_inactive_opacity"
        private const val KEY_DRAWER_POS_Y = "drawer_pos_y"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }
}
