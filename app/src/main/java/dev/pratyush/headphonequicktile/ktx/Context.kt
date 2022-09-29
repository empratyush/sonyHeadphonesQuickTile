package dev.pratyush.headphonequicktile.ktx

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.annotation.RequiresPermission
import dev.pratyush.headphonequicktile.uuid
import dev.pratyush.headphonequicktile.uuidAlt
import dev.pratyush.headphonequicktile.uuid_wf1000xm4

@RequiresPermission("android.permission.BLUETOOTH_CONNECT")
fun Context.getConnectedDevice(): BluetoothDevice? {
    val bluetoothManager = getSystemService(BluetoothManager::class.java)
    val connectedDevice = bluetoothManager.adapter.bondedDevices
    var headset: BluetoothDevice? = null
    for (device in connectedDevice) {
        if (!device.isConnected()) continue
        val uuids = device.uuids ?: continue
        for (u in uuids) {
            if (u.toString() == uuid.toString() || u.toString() == uuidAlt.toString() || u.toString() == uuid_wf1000xm4.toString()) {
                headset = device
                break
            }
        }
        if (headset != null) break
    }
    return headset
}

@RequiresPermission("android.permission.BLUETOOTH_CONNECT")
@Suppress("DEPRECATION")
fun Context.disableBt() {
    val bluetoothManager = getSystemService(BluetoothManager::class.java)
    bluetoothManager.adapter.disable()
}

fun Context.isBtOn(): Boolean {
    val bluetoothManager = getSystemService(BluetoothManager::class.java)
    return bluetoothManager.adapter.isEnabled
}