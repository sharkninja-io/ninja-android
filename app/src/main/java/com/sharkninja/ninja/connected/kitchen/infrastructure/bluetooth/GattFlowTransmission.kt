package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

sealed class GattFlowTransmission

data class ConnectionChanged(val gatt: BluetoothGatt?,
                             val status: Int,
                             val newState: Int) : GattFlowTransmission()

data class ServicesDiscovered(val gatt: BluetoothGatt?,
                              val status: Int) : GattFlowTransmission()

data class ServiceChanged(val gatt: BluetoothGatt?) : GattFlowTransmission()

data class CharacteristicRead(val gatt: BluetoothGatt?,
                              val characteristic: BluetoothGattCharacteristic?,
                              val status: Int) : GattFlowTransmission()

data class DescriptorRead(val gatt: BluetoothGatt?,
                          val descriptor: BluetoothGattDescriptor?,
                          val status: Int) : GattFlowTransmission()

data class CharacteristicChanged(val gatt: BluetoothGatt?,
                                 val characteristic: BluetoothGattCharacteristic?) : GattFlowTransmission()

data class PhysicalLayerUpdate(val gatt: BluetoothGatt?,
                               val txPhy: Int,
                               val rxPhy: Int,
                               val status: Int) : GattFlowTransmission()

data class PhysicalLayerRead(val gatt: BluetoothGatt?,
                             val txPhy: Int,
                             val rxPhy: Int,
                             val status: Int) : GattFlowTransmission()

data class CharacteristicWrite(val gatt: BluetoothGatt?,
                               val characteristic: BluetoothGattCharacteristic?,
                               val status: Int) : GattFlowTransmission()

data class DescriptorWrite(val gatt: BluetoothGatt?,
                           val descriptor: BluetoothGattDescriptor?,
                           val status: Int) : GattFlowTransmission()

data class ReliableWrite(val gatt: BluetoothGatt?,
                         val status: Int) : GattFlowTransmission()

data class RemoteRssiRead(val gatt: BluetoothGatt?,
                          val rssi: Int,
                          val status: Int) : GattFlowTransmission()

data class MtuChanged(val gatt: BluetoothGatt?,
                      val mtu: Int,
                      val status: Int) : GattFlowTransmission()