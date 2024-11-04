package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import kotlinx.coroutines.flow.*

class GattFlowEvents(private val events: MutableSharedFlow<GattFlowTransmission>) : BluetoothGattCallback() {

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        val result = events.tryEmit(ConnectionChanged(gatt, status, newState))
        when (newState) {
            STATE_CONNECTED -> println("DEVICE IS CONNECTED!")
            STATE_DISCONNECTED -> println("DEVICE HAS DISCONNECTED!")
        }
        println("Emit result: $result with subscription count of: ${events.subscriptionCount.value}")
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        events.tryEmit(ServicesDiscovered(gatt, status))
    }

    override fun onServiceChanged(gatt: BluetoothGatt) {
        super.onServiceChanged(gatt)
        events.tryEmit(ServiceChanged(gatt))
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)
        events.tryEmit(CharacteristicRead(gatt, characteristic, status))
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorRead(gatt, descriptor, status)
        events.tryEmit(DescriptorRead(gatt, descriptor, status))
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicChanged(gatt, characteristic)
        events.tryEmit(CharacteristicChanged(gatt, characteristic))
    }

    override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status)
        events.tryEmit(PhysicalLayerUpdate(gatt, txPhy, rxPhy, status))
    }

    override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyRead(gatt, txPhy, rxPhy, status)
        events.tryEmit(PhysicalLayerRead(gatt, txPhy, rxPhy, status))
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        events.tryEmit(CharacteristicWrite(gatt, characteristic, status))
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)
        events.tryEmit(DescriptorWrite(gatt, descriptor, status))
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
        super.onReliableWriteCompleted(gatt, status)
        events.tryEmit(ReliableWrite(gatt, status))
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
        events.tryEmit(RemoteRssiRead(gatt, rssi, status))
    }

    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
        events.tryEmit(MtuChanged(gatt, mtu, status))
    }
}