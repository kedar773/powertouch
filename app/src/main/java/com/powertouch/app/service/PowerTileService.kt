package com.powertouch.app.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.powertouch.app.MainActivity
import com.powertouch.app.util.HapticHelper

@RequiresApi(Build.VERSION_CODES.N)
class PowerTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        tile.state = if (PowerAccessibilityService.isRunning()) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (PowerAccessibilityService.isRunning()) {
            HapticHelper.vibrateTap(this)
            PowerAccessibilityService.lockScreen()
        } else {
            Toast.makeText(
                this,
                "Please enable PowerTouch Accessibility Service first",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivityAndCollapse(intent)
        }
    }
}
