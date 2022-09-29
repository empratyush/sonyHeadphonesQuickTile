package dev.pratyush.headphonequicktile.ktx

import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import dev.pratyush.headphonequicktile.powerOff
import dev.pratyush.headphonequicktile.powerOff_wf1000xm4
import dev.pratyush.headphonequicktile.uuid
import dev.pratyush.headphonequicktile.uuidAlt
import dev.pratyush.headphonequicktile.uuid_wf1000xm4
import java.io.IOException

fun BluetoothDevice.isConnected(): Boolean {
    val method = this.javaClass.getMethod("isConnected")
    val isConnected = method.invoke(this)
    return isConnected == true
}

@RequiresPermission("android.permission.BLUETOOTH_CONNECT")
@Throws(IOException::class, InterruptedException::class)
fun BluetoothDevice.turnOff() {
    if (!isConnected()) return
    val uuids = uuids.map { it.uuid.toString().uppercase() }
    val headphoneUid =
        if (uuids.contains(uuid_wf1000xm4.toString().uppercase())) uuid_wf1000xm4
        else if (uuids.contains(uuid.toString().uppercase())) uuid
        else if (uuids.contains(uuidAlt.toString().uppercase())) uuidAlt
        else return
    val powerOffBytes = if (headphoneUid == uuid_wf1000xm4) powerOff_wf1000xm4  else powerOff
    val sonyHeadphoneSocket = createRfcommSocketToServiceRecord(headphoneUid)

    sonyHeadphoneSocket?.use { socket ->
        while (!socket.isConnected){
            socket.connect()
            Thread.sleep(500)
        }
        val outputStream = sonyHeadphoneSocket.outputStream
        outputStream.write(powerOffBytes)
        socket.close()
    }
}