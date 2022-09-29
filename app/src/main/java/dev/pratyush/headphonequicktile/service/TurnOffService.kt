package dev.pratyush.headphonequicktile.service

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dev.pratyush.headphonequicktile.R
import dev.pratyush.headphonequicktile.ktx.disableBt
import dev.pratyush.headphonequicktile.ktx.getConnectedDevice
import dev.pratyush.headphonequicktile.ktx.isConnected
import dev.pratyush.headphonequicktile.ktx.turnOff
import java.io.IOException

class TurnOffService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this, createNotificationChannel())
            .setAllowSystemGeneratedContextualActions(false)
            .setSmallIcon(IconCompat.createWithResource(this, R.drawable.ic_headset))
            .setContentTitle("Turning off WXM BT device")
            .build()

        startForeground(1, notification)

        tryTurningOffDevice()
        return START_NOT_STICKY
    }

    private fun tryTurningOffDevice() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }
        var connectedDevice = getConnectedDevice()
        var roundCount = 0
        var shouldTry = true
        while (shouldTry) {
            shouldTry = connectedDevice != null && connectedDevice.isConnected() && roundCount < 20
            roundCount++
            try {
                connectedDevice?.turnOff()
            } catch (ignored: IOException) {
            }
            connectedDevice = getConnectedDevice()
        }

        if (connectedDevice == null || !connectedDevice.isConnected()) {
            disableBt()
        }
        stopSelf()
    }

    private fun createNotificationChannel(): String {
        val channelId = "btTurnOffChannel"

        val serviceChannel =
            NotificationChannelCompat.Builder(channelId, NotificationManager.IMPORTANCE_LOW)
                .setName("Turn off BT Notification").build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(serviceChannel)
        return channelId
    }
}