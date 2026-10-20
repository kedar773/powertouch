package com.powertouch.app

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.powertouch.app.databinding.ActivityMainBinding
import com.powertouch.app.service.PowerAccessibilityService
import com.powertouch.app.util.HapticHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val prefs by lazy { PowerApplication.instance.preferences }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStates()
        if (isAccessibilityServiceEnabled() && Settings.canDrawOverlays(this)) {
            PowerAccessibilityService.refreshOverlays()
        }
    }

    private fun initViews() {
        // Floating button switch and sliders
        binding.switchFloating.isChecked = prefs.isFloatingEnabled
        binding.layoutFloatingOptions.visibility =
            if (prefs.isFloatingEnabled) View.VISIBLE else View.GONE

        binding.sliderSize.value = prefs.floatingSizeDp.toFloat()
        binding.tvSizeLabel.text = "Icon Size: ${prefs.floatingSizeDp} dp"

        val inactivePct = (prefs.floatingInactiveOpacity * 100).toInt()
        binding.sliderInactiveOpacity.value = inactivePct.toFloat()
        binding.tvInactiveOpacityLabel.text =
            "Idle Opacity: $inactivePct% (OLED burn-in prevention)"

        val activePct = (prefs.floatingActiveOpacity * 100).toInt()
        binding.sliderActiveOpacity.value = activePct.toFloat()
        binding.tvActiveOpacityLabel.text = "Active Opacity: $activePct%"

        // Drawer tile switch and sliders
        binding.switchDrawer.isChecked = prefs.isDrawerEnabled
        binding.layoutDrawerOptions.visibility =
            if (prefs.isDrawerEnabled) View.VISIBLE else View.GONE

        if (prefs.drawerEdgeSide == "RIGHT") {
            binding.rbRightEdge.isChecked = true
        } else {
            binding.rbLeftEdge.isChecked = true
        }

        val drawerInactivePct = (prefs.drawerInactiveOpacity * 100).toInt()
        binding.sliderDrawerInactiveOpacity.value = drawerInactivePct.toFloat()
        binding.tvDrawerInactiveOpacityLabel.text = "Handle Idle Opacity: $drawerInactivePct%"

        // Feedback switch
        binding.switchVibration.isChecked = prefs.isVibrationEnabled
    }

    private fun setupListeners() {
        // Accessibility Permission
        binding.btnEnableAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // Overlay Permission
        binding.btnEnableOverlay.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        // Battery Optimization
        binding.btnBatteryOptimization.setOnClickListener {
            requestBatteryOptimizationExemption()
        }

        // Floating switch
        binding.switchFloating.setOnCheckedChangeListener { _, isChecked ->
            prefs.isFloatingEnabled = isChecked
            binding.layoutFloatingOptions.visibility = if (isChecked) View.VISIBLE else View.GONE
            PowerAccessibilityService.refreshOverlays()
        }

        // Floating Size Slider
        binding.sliderSize.addOnChangeListener { _, value, _ ->
            val size = value.toInt()
            prefs.floatingSizeDp = size
            binding.tvSizeLabel.text = "Icon Size: $size dp"
            PowerAccessibilityService.refreshOverlays()
        }

        // Floating Inactive Opacity Slider
        binding.sliderInactiveOpacity.addOnChangeListener { _, value, _ ->
            val opacity = value / 100f
            prefs.floatingInactiveOpacity = opacity
            binding.tvInactiveOpacityLabel.text =
                "Idle Opacity: ${value.toInt()}% (OLED burn-in prevention)"
            PowerAccessibilityService.refreshOverlays()
        }

        // Floating Active Opacity Slider
        binding.sliderActiveOpacity.addOnChangeListener { _, value, _ ->
            val opacity = value / 100f
            prefs.floatingActiveOpacity = opacity
            binding.tvActiveOpacityLabel.text = "Active Opacity: ${value.toInt()}%"
            PowerAccessibilityService.refreshOverlays()
        }

        // Drawer switch
        binding.switchDrawer.setOnCheckedChangeListener { _, isChecked ->
            prefs.isDrawerEnabled = isChecked
            binding.layoutDrawerOptions.visibility = if (isChecked) View.VISIBLE else View.GONE
            PowerAccessibilityService.refreshOverlays()
        }

        // Drawer Edge Selector
        binding.rgEdgeSide.setOnCheckedChangeListener { _, checkedId ->
            prefs.drawerEdgeSide = if (checkedId == R.id.rbRightEdge) "RIGHT" else "LEFT"
            PowerAccessibilityService.refreshOverlays()
        }

        // Drawer Inactive Opacity Slider
        binding.sliderDrawerInactiveOpacity.addOnChangeListener { _, value, _ ->
            val opacity = value / 100f
            prefs.drawerInactiveOpacity = opacity
            binding.tvDrawerInactiveOpacityLabel.text = "Handle Idle Opacity: ${value.toInt()}%"
            PowerAccessibilityService.refreshOverlays()
        }

        // Vibration Switch
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.isVibrationEnabled = isChecked
            if (isChecked) {
                HapticHelper.vibrateTap(this)
            }
        }

        // Test buttons
        binding.btnTestLock.setOnClickListener {
            if (PowerAccessibilityService.isRunning()) {
                HapticHelper.vibrateTap(this)
                PowerAccessibilityService.lockScreen()
            } else {
                Toast.makeText(
                    this,
                    "Please enable PowerTouch Accessibility Service first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnTestPowerMenu.setOnClickListener {
            if (PowerAccessibilityService.isRunning()) {
                HapticHelper.vibrateLongPress(this)
                PowerAccessibilityService.showPowerMenu()
            } else {
                Toast.makeText(
                    this,
                    "Please enable PowerTouch Accessibility Service first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updatePermissionStates() {
        val isA11yEnabled = isAccessibilityServiceEnabled()
        if (isA11yEnabled) {
            binding.ivAccessibilityStatus.setImageResource(R.drawable.ic_check_circle)
            binding.tvAccessibilityStatus.text = "Active (Locks screen & Power menu enabled)"
            binding.tvAccessibilityStatus.setTextColor(ContextCompat.getColor(this, R.color.success))
            binding.btnEnableAccessibility.text = "Settings"
        } else {
            binding.ivAccessibilityStatus.setImageResource(R.drawable.ic_warning)
            binding.tvAccessibilityStatus.text = "Disabled (Locks screen & Power menu)"
            binding.tvAccessibilityStatus.setTextColor(ContextCompat.getColor(this, R.color.error))
            binding.btnEnableAccessibility.text = "Enable"
        }

        val isOverlayGranted = Settings.canDrawOverlays(this)
        if (isOverlayGranted) {
            binding.ivOverlayStatus.setImageResource(R.drawable.ic_check_circle)
            binding.tvOverlayStatus.text = "Granted (Draws over apps)"
            binding.tvOverlayStatus.setTextColor(ContextCompat.getColor(this, R.color.success))
            binding.btnEnableOverlay.text = "Settings"
        } else {
            binding.ivOverlayStatus.setImageResource(R.drawable.ic_warning)
            binding.tvOverlayStatus.text = "Not Granted (Shows floating button)"
            binding.tvOverlayStatus.setTextColor(ContextCompat.getColor(this, R.color.error))
            binding.btnEnableOverlay.text = "Grant"
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        if (PowerAccessibilityService.isRunning()) return true
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.id.contains(packageName)) {
                return true
            }
        }
        return false
    }

    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationExemption() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "PowerTouch is already exempted from battery saver", Toast.LENGTH_SHORT).show()
        }
    }
}
