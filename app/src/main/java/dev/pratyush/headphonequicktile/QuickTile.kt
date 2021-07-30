package dev.pratyush.headphonequicktile

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat

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
        TurnOffService.getConnectedDevice().let { device ->
            when {
                device != null && device.isConnected -> {
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
        ContextCompat.startForegroundService(
            this,
            Intent(this, TurnOffService::class.java)
        )
    }

    private fun isBtOn(): Boolean {
        return BluetoothAdapter.getDefaultAdapter()?.isEnabled ?: false
    }

}