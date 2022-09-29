package dev.pratyush.headphonequicktile.service

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.pratyush.headphonequicktile.ktx.getConnectedDevice
import dev.pratyush.headphonequicktile.ktx.isBtOn
import dev.pratyush.headphonequicktile.ktx.isConnected
import dev.pratyush.headphonequicktile.ui.ConfigActivity

class QuickTile : TileService() {

    private val btStateChangesReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()

        val btFilter = IntentFilter()
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        btFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        btFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)
        registerReceiver(btStateChangesReceiver, btFilter)
    }

    override fun onStopListening() {
        super.onStopListening()
        unregisterReceiver(btStateChangesReceiver)
    }

    private fun updateTile() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            qsTile.label = "Configure"
            qsTile.state = Tile.STATE_INACTIVE
            return
        }
        getConnectedDevice().let { device ->
            when {
                device != null && device.isConnected() -> {
                    qsTile.label = "Turn Off " + device.name
                    qsTile.state = Tile.STATE_ACTIVE
                }
                !isBtOn() -> {
                    qsTile.label = "BT Unavailable"
                    qsTile.state = Tile.STATE_UNAVAILABLE
                }
                else -> {
                    qsTile.label = "Device not connected"
                    qsTile.state = Tile.STATE_INACTIVE
                }
            }
        }
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(
                Intent(this, ConfigActivity::class.java)
            )
            return
        }
        ContextCompat.startForegroundService(
            this,
            Intent(this, TurnOffService::class.java)
        )
    }
}