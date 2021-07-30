package dev.pratyush.headphonequicktile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import java.io.IOException
import java.util.*


class TurnOffService : Service() {

    companion object {
        fun getConnectedDevice(): BluetoothDevice? {

            var headset: BluetoothDevice? = null
            val adapter = BluetoothAdapter.getDefaultAdapter()

            val devices = adapter.bondedDevices
            for (device in devices) {
                val uuids = device.uuids ?: continue
                for (u in uuids) {
                    if (u.toString() == uuid.toString() || u.toString() == uuidAlt.toString()) {
                        headset = device
                        break
                    }
                }
                if (headset != null) break
            }
            return headset
        }
    }

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

        try {
            var connectedDevice = getConnectedDevice()
            var roundCount = 0

            while (connectedDevice != null && connectedDevice.isConnected && roundCount < 20) {
                roundCount++
                turnOffBTDeviceNow(connectedDevice)
                connectedDevice = getConnectedDevice()
                Thread.sleep(1000)
            }

            if (connectedDevice == null || !connectedDevice.isConnected) {
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                mBluetoothAdapter.disable()
            }
            stopSelf()
        } catch (e: IOException) {
            e.fillInStackTrace()
        } catch (e: InterruptedException) {
            e.fillInStackTrace()
        }

    }

    private fun createNotificationChannel(): String {

        val channelId = "btTurnOffChannel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Turn off BT Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            serviceChannel.importance = NotificationManager.IMPORTANCE_LOW
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }

        return channelId
    }


    @Throws(IOException::class, InterruptedException::class)
    private fun turnOffBTDeviceNow(sonyHeadphone: BluetoothDevice) {
        if (!sonyHeadphone.isConnected) return
        val sonyHeadphoneSocket = sonyHeadphone.createRfcommSocketToServiceRecord(uuid)
        sonyHeadphoneSocket?.use { socket ->
            socket.connect()
            val outputStream = sonyHeadphoneSocket.outputStream
            val i = socket.inputStream

            outputStream.write(powerOff)

            val buffer = ByteArray(256)
            val date = Date()
            val sb = StringBuilder()

            while (Date().time - date.time < 200) {
                if (i.available() > 0) {
                    val r: Int = i.read(buffer)
                    for (j in 0 until r) {
                        sb.append(String.format(" %02x", buffer[j]))
                    }
                    break
                }
                Thread.sleep(50)
            }
            socket.close()
        }
        stopSelf()
    }


}