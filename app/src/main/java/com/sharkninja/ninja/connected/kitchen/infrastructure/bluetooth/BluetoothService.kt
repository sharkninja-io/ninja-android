package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

import android.bluetooth.*
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGatt.STATE_DISCONNECTED
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.*
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import com.sharkninja.cloudcore.WifiNetwork
import com.sharkninja.grillcore.BTManager
import com.sharkninja.grillcore.BTManager.BTCommand
import com.sharkninja.grillcore.BTManager.Companion.btAppRequest
import com.sharkninja.grillcore.BTManager.Companion.getJoinableGrills
import com.sharkninja.grillcore.BTManager.Companion.sendBTPayload
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

const val BASE_BLE_SERVICE_UUID = "00000000-0000-1000-8000-00805f9b34fb"
const val GRILL_SERVICE_UUID_UNENCRYPTED = "0000a00b-0000-1000-8000-00805f9b34fb"
const val GRILL_SERVICE_UUID_ENCRYPTED = "0000fcbb-0000-1000-8000-00805f9b34fb"
const val WRITE_COMMAND_CHARACTERISTIC_UUID = "0000b002-0000-1000-8000-00805f9b34fb"
const val NOTIFY_CHARACTERISTIC_UUID = "0000b004-0000-1000-8000-00805f9b34fb"
const val NOTIFY_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"

const val REMOTE_DEVICE_DISCOVERY_UUID = "0000180f-0000-1000-8000-00805f9b34fb"

const val MAX_MTU_SIZE = 517 // includes ATT Header

const val NINJA_PROTOTYPE_MAC_ADDRESS = "66:55:44:77:88:99"

val CHARACTERISTIC_UUIDS = listOf(UUID.fromString(WRITE_COMMAND_CHARACTERISTIC_UUID), UUID.fromString(NOTIFY_CHARACTERISTIC_UUID))

class BluetoothService(context: Context) {

    private val btManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val btAdapter: BluetoothAdapter? = btManager.adapter

    private val ctx = context
    private var bleScanner: BluetoothLeScanner? = null
    private var scanning = false
    private val handler = Handler()

    private val SCAN_DURATION: Long = 15000

    private val chron = Chronometer(ctx)

    private var serviceList = arrayListOf<BLEService>()
    private var characteristicList = arrayListOf<BLECharacteristic>()
    private var descriptorList = arrayListOf<BLEDescriptor>()

    private var btGatt: BluetoothGatt? = null
    private var grillCommunicationService: BluetoothGattService? = null
    private var grillNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private var grillCommandCharacteristic: BluetoothGattCharacteristic? = null
    private var grillNotifyDescriptor: BluetoothGattDescriptor? = null

    val wifiNetworkList = mutableListOf<BTLEWifiNetwork>()

    private val _grillCoreWifiNetworks = MutableStateFlow<List<WifiNetwork>>(listOf())
    val grillCoreWifiNetworks = _grillCoreWifiNetworks.asStateFlow()

    private val _ninjaDeviceFound = MutableSharedFlow<List<BTManager.BTJoinableGrill>>(extraBufferCapacity = 1)
    val ninjaDeviceFound = _ninjaDeviceFound.asSharedFlow()

    private val _wifiConnectedStatus = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val wifiConnectedStatus = _wifiConnectedStatus.asSharedFlow()

    private val _deviceDSN = MutableStateFlow<String?>(null)
    val deviceDSN = _deviceDSN.asStateFlow()

    private val _deviceToken = MutableSharedFlow<String?>(extraBufferCapacity = 1)
    val deviceToken = _deviceToken.asSharedFlow()
    private var randomToken: String = ""

    private val _flowEvents = MutableSharedFlow<GattFlowTransmission>(
        replay = 1,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val flowEvents = _flowEvents.asSharedFlow()

    private val gattFlowCallback = GattFlowEvents(_flowEvents)

    private var currentDeviceId = ""

    private val commandQueue = ConcurrentLinkedQueue<GattFlowTransmission>()
    private var pendingCommand: GattFlowTransmission? = null

    private var canProcessBTAdvertisements = true

    @Synchronized
    private fun queueCommand(command: GattFlowTransmission) {
        println("QUEUE: Command QUEUED")
        commandQueue.add(command)
        println("QUEUE: ADDED - Queue size: ${commandQueue.size}")
//        pendingCommand ?: processNextCommand()
        if (pendingCommand == null) {
            processNextCommand()
        }
    }

    @Synchronized
    private fun processNextCommand() {
        val command = commandQueue.poll()
        println("QUEUE: POLLED - Queue size: ${commandQueue.size}")
        pendingCommand = command

        when (command) {
            is ConnectionChanged -> {
                println("QUEUE: STATUS - ${command.status}")
                if (command.status == GATT_SUCCESS && command.newState == STATE_CONNECTED) {
                    btGatt = command.gatt
                    setupDevice()
                    println("btcore: CONNECTION STATUS: CONNECTED")
                }

                if (command.status == 133 && command.newState == STATE_DISCONNECTED) {
                    connectToDevice(
                        if (currentDeviceId.isNotEmpty()) currentDeviceId else btGatt?.device?.address!!
                    )
                }

                if (command.newState == STATE_DISCONNECTED) {
                    deviceDisconnectionCleanup()
                    println("btcore: CONNECTION STATUS: DISCONNECTED")
                }
                if (pendingCommand == command) onCommandComplete() else println("QUEUE: COMMANDS DO NOT MATCH!!!")
            }
            is ServicesDiscovered -> {
                println("QUEUE: STATUS - ${command.status}")
                if (command.status == GATT_SUCCESS) prepareDeviceForCommunication()
                if (pendingCommand == command) onCommandComplete() else println("QUEUE: COMMANDS DO NOT MATCH!!!")
            }
            is CharacteristicChanged -> {
                parseDataPacket(command.characteristic?.value)
                println("CONNECTION STATUS: SEND RESPONSE FIRED")
                btGatt?.device?.address?.let { macAddress ->
                    sendBTDeviceResponse(BTCommand.Send, macAddress, command.characteristic?.value)
                }
                if (pendingCommand == command) onCommandComplete() else println("QUEUE: COMMANDS DO NOT MATCH!!!")
            }
            is DescriptorWrite -> {
                println("QUEUE: STATUS - ${command.status}")
                btGatt?.device?.address?.let { macAddress ->
                    println("CONNECTION STATUS: CONNECTION RESPONSE FIRED")
                    sendBTDeviceResponse(BTCommand.Connect, macAddress, null)
                }
                if (pendingCommand == command) onCommandComplete() else println("QUEUE: COMMANDS DO NOT MATCH!!!")
            }
            is MtuChanged -> {
                println("QUEUE: STATUS - ${command.status}")
                if (command.status == GATT_SUCCESS) {
                    println("Payload successfully set to ${command.mtu}")
                    println("PREP: Enable OS and device BLE notifications...")
                    enableWifiResultNotification()
                    enableWifiResultDescriptor()
                }
                if (pendingCommand == command) onCommandComplete() else println("QUEUE: COMMANDS DO NOT MATCH!!!")
            }
            is ServiceChanged,
            is CharacteristicRead,
            is DescriptorRead,
            is PhysicalLayerUpdate,
            is PhysicalLayerRead,
            is CharacteristicWrite,
            is ReliableWrite,
            is RemoteRssiRead -> if (pendingCommand == command) onCommandComplete() else println("QUEUE: COMMANDS DO NOT MATCH!!!")
        }
    }

    @Synchronized
    private fun onCommandComplete() {
        println("QUEUE: Command COMPLETED")
        pendingCommand = null
        if (commandQueue.isNotEmpty()) processNextCommand()
    }


    private suspend fun listenForEvents() {
        flowEvents.collect {
//            println("service stuff - subscription #1: $it")
            when (it) {
                is ConnectionChanged,
                is ServicesDiscovered,
                is ServiceChanged,
                is CharacteristicRead,
                is DescriptorRead,
                is CharacteristicChanged,
                is PhysicalLayerUpdate,
                is PhysicalLayerRead,
                is CharacteristicWrite,
                is DescriptorWrite,
                is ReliableWrite,
                is RemoteRssiRead,
                is MtuChanged -> queueCommand(it)
            }
        }
    }

    private fun setupDevice() {
        chron.base = SystemClock.elapsedRealtime()
        println("timer started: ${chron.base}")
        chron.start()
        btGatt?.discoverServices()
    }

    private fun deviceDisconnectionCleanup() {
        // what else?
        canProcessBTAdvertisements = true
        chron.stop()
        println("timer stopped: ${SystemClock.elapsedRealtime() - chron.base}")
        println("Device has disconnected!")
    }

    private fun prepareDeviceForCommunication() {
        println("PREP: Setting up service and characteristic objects...")
        grillCommunicationService = btGatt?.getService(UUID.fromString(GRILL_SERVICE_UUID_UNENCRYPTED))
            ?: btGatt?.getService(UUID.fromString(GRILL_SERVICE_UUID_ENCRYPTED))

        grillCommunicationService?.characteristics?.filter { CHARACTERISTIC_UUIDS.contains(it?.uuid) }?.forEach {
                if (it.uuid == UUID.fromString(WRITE_COMMAND_CHARACTERISTIC_UUID)) {
                    grillCommandCharacteristic = it
                } else {
                    if (it.containsProperty(PROPERTY_INDICATE)) {
                        if (it.uuid == UUID.fromString(NOTIFY_CHARACTERISTIC_UUID)) {
                            grillNotifyCharacteristic = it
                            grillNotifyDescriptor = it.getDescriptor(UUID.fromString(NOTIFY_DESCRIPTOR_UUID))
                        }
                    }
                }
        }

        println("PREP: Increase maximum payload size...")
        btGatt?.requestMtu(MAX_MTU_SIZE)
    }

    private fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    private fun parseDataPacket(dataPacket: ByteArray?) {
        println("Networks: NULL PACKET")
        dataPacket?.let {
            var data = it
            btGatt?.device?.address?.let { mac ->
                data = BTManager.decryptData(mac, data)
            }

            if (data.isNotEmpty()) {
                when (data[0].toInt()) {
                    BTLEDataType.WIFI_SCAN_RESULT.ordinal -> {
                        val network = data.toBTLEWifiNetwork()
                        println("Networks: $network")

                        if (network.index == 0) {
                            wifiNetworkList.clear()
                        }

                        wifiNetworkList.add(network)

                        // can't be sure the contents of this network packet will make sense...
                        network.index?.let {
                            network.totalCount?.let {
                                if (network.index <= network.totalCount - 1) {
                                    convertBTNetworksToGrillCoreNetworks()
                                }
                            }
                        }
                    }
                    BTLEDataType.WIFI_JOIN_STATUS.ordinal -> {
                        val result = data.toBTLEWifiJoinStatus()
                        println("Wifi connection status: $result")
                        notifyOnConnection(result)
                    }
                    BTLEDataType.GRILL_INFO_RESPONSE.ordinal -> {
                        val response = data.toBTLEGrillInfoResponse()
                        println("Grill Info Response: $response")
                        storeDeviceSerial(response)
                    }
                    else -> { }
                }
            } else {
                println("Networks: GARBAGE PACKET")
            }
        }
    }

    private fun convertBTNetworksToGrillCoreNetworks() {
        val grillCoreWifi = mutableListOf<WifiNetwork>()
        wifiNetworkList.forEach {
            grillCoreWifi.add(WifiNetwork(
                it.bars,
                "",
                it.channel,
                if (it.security!! > 0) "yes" else null,
                it.rssi,
                it.ssid,
                "",
                ""
            ))
        }

        _grillCoreWifiNetworks.tryEmit(grillCoreWifi.distinctBy { it.ssid })
    }

    private fun notifyOnConnection(connection: BTLEWifiJoinStatus) {
        connection.result?.let {
            if (it == BTLEWifiConnectionStatus.CONNECTED.ordinal) {
                println("Networks: JOIN NETWORK NOTIFIED CALLED!!!!")
                _deviceToken.tryEmit(randomToken)
            }
            _wifiConnectedStatus.tryEmit(it)
        }
    }

    private fun storeDeviceSerial(grillInfo: BTLEGrillInfoResponse) {
        grillInfo.dsn?.let { _deviceDSN.tryEmit(it) }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            println("Found: ${result?.device?.name} at ${result?.device?.address}")
            println("${result?.scanRecord}")

            if (canProcessBTAdvertisements) {
                result?.scanRecord?.bytes?.let { bytes ->
                    result.device?.address?.let { mac ->
                        println("btcore: ACTIVE SCAN - I'M FIRING ON PROCESSED PACKETS!!!!")
                        BTManager.processAdvertisementData(mac, bytes, result.rssi)
                    }
                }
            }
//            _ninjaDeviceFound.tryEmit(result?.device?.address)

            // NOT NEEDED AS OF FIRMWARE UPDATE VER. 170
//            result?.scanRecord?.bytes?.let {
//                val serviceUUID = StringBuilder()
//                serviceUUID.append(BASE_BLE_SERVICE_UUID.substring(0, 4))
//                serviceUUID.append(byteArrayOf(it[6], it[5]).toHex())
//                serviceUUID.append(BASE_BLE_SERVICE_UUID.substring(8))
//
//                if (serviceUUID.toString() == GRILL_SERVICE_UUID) {
//                    _ninjaDeviceFound.tryEmit(result?.device?.address)
//                }
//            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            println("Failed to find devices!!!")
        }
    }

//    fun scanForBleDevices() {
//        if (!scanning) {
//            handler.postDelayed({
//                scanning = false
//                bleScanner?.stopScan(scanCallback)
////                _ninjaDeviceFound.tryEmit("ERROR")
////                connectToDevice("66:55:44:77:88:99") // Ninja Prototype
////                connectToDevice("F4:4D:2D:C6:98:C0") // Ionic
////                connectToDevice("A4:C1:38:A7:0E:F3") // Govee Button Monitor
//            }, SCAN_DURATION)
//            scanning = true
//
//            val settings = ScanSettings.Builder()
//                .setScanMode(SCAN_MODE_LOW_LATENCY)
//                .setMatchMode(MATCH_MODE_AGGRESSIVE)
//                .setCallbackType(CALLBACK_TYPE_ALL_MATCHES).build()
//            val filterUnencrypted = ScanFilter.Builder().setServiceUuid(
//                ParcelUuid(UUID.fromString(
//                GRILL_SERVICE_UUID_UNENCRYPTED))
//            ).build()
//            val filterEncrypted = ScanFilter.Builder().setServiceUuid(
//                ParcelUuid(UUID.fromString(
//                    GRILL_SERVICE_UUID_ENCRYPTED))
//            ).build()
//
//            // works in API 33 setAdvertisingDataTypeWithData(ScanRecord.DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE, byteArrayOf(11, -96), byteArrayOf(0x0F, 0x0F))
////                ParcelUuid(UUID.fromString(
////                    GRILL_SERVICE_UUID)), ParcelUuid(UUID.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"))).build()
//            bleScanner?.startScan(listOf<ScanFilter>(filterUnencrypted, filterEncrypted), settings, scanCallback)
//        } else {
//            scanning = false
//            bleScanner?.stopScan(scanCallback)
//        }
//    }

    // Bluetooth Command & Encryption Integration

    private suspend fun listenForBTPayloads() {
        btAppRequest.collect {
            receiveBTDeviceData(it.cmd, it.id, it.data)
        }
    }

    fun passiveBleScan() {
        val settings = ScanSettings.Builder()
            .setScanMode(SCAN_MODE_LOW_LATENCY)
            .setMatchMode(MATCH_MODE_AGGRESSIVE)
            .setCallbackType(CALLBACK_TYPE_ALL_MATCHES).build()
        val filterUnencrypted = ScanFilter.Builder().setServiceUuid(
            ParcelUuid(UUID.fromString(
                GRILL_SERVICE_UUID_UNENCRYPTED))
        ).build()
        val filterEncrypted = ScanFilter.Builder().setServiceUuid(
            ParcelUuid(UUID.fromString(
                GRILL_SERVICE_UUID_ENCRYPTED))
        ).build()
        try {
            if(bleScanner == null) {
                bleScanner = btAdapter?.bluetoothLeScanner
            }
            Log.d(BluetoothService::class.java.simpleName, "passiveBleScan: STARTING BLE SCAN: $bleScanner")
            bleScanner?.startScan(listOf<ScanFilter>(filterUnencrypted, filterEncrypted), settings, passiveScanCallback)
        } catch (error: Throwable) {
            Log.e(BluetoothService::class.java.simpleName, "passiveBleScan: ${error.message}")
        }
    }

    fun stopPassiveBleScan() {
        try {
            Log.d(BluetoothService::class.java.simpleName, "stopPassiveBleScan: STOP PASSIVE BLE SCAN")
            bleScanner?.stopScan(passiveScanCallback)
        } catch (error: Throwable) {
            Log.e(BluetoothService::class.java.simpleName, "stopPassiveBleScan: Error Stopping Scan ${error.message}")
        }
//        bleScanner?.flushPendingScanResults(passiveScanCallback)
    }

    fun setBTAvailable(isAvailable: Boolean) {
        BTManager.setBTAvailable(isAvailable)
    }

    private val passiveScanCallback: ScanCallback

    init {
        passiveScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                println("PASSIVE SCAN - Found: ${result?.device?.name} at ${result?.device?.address}")
                println("${result?.scanRecord}")
                println("${result?.scanRecord?.serviceUuids}")

                if (canProcessBTAdvertisements) {
                    result?.scanRecord?.bytes?.let { bytes ->
                        result.device?.address?.let { mac ->
                            println("btcore: PASSIVE SCAN - I'M FIRING ON PROCESSED PACKETS!!!!")
                            BTManager.processAdvertisementData(mac, bytes, result.rssi)
                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            listenForEvents()
        }

        CoroutineScope(Dispatchers.IO).launch {
            listenForBTPayloads()
        }
    }

    private fun receiveBTDeviceData(commandType: BTCommand, deviceId: String, data: ByteArray) {
        println("SEND_ARRAY: $data")

        when (commandType) {
            BTCommand.Connect -> {
                currentDeviceId = deviceId
                connectToDevice(deviceId)
            }
            BTCommand.Disconnect -> {
                println("CONNECTION STATUS: GRILLCORE FIRED DISCONNECT")
                disconnectDevice()
            }
            BTCommand.Send -> {
                println("CONNECTION STATUS: GRILLCORE FIRED SEND")
                writeBTCommand(data)
            }
            BTCommand.StartScan -> { passiveBleScan() }
            BTCommand.StopScan -> {
                stopPassiveBleScan()
                sendBTDeviceResponse(BTCommand.StopScan, deviceId, null)
            }
            else -> { }
        }
    }

    private fun sendBTDeviceResponse(commandType: BTCommand, deviceId: String, data: ByteArray?) {
        val payload =
            BTManager.BTPayload(cmd = commandType, id = deviceId, data = data ?: byteArrayOf())

        CoroutineScope(Dispatchers.IO).launch {
            println("CONNECTION STATUS: Sending BT Response: $payload")
            delay(350)
            sendBTPayload(payload)
        }
    }

    fun getBTJoinableGrills() {
        _ninjaDeviceFound.tryEmit(getJoinableGrills())
    }

    private fun writeBTCommand(data: ByteArray) {
        grillCommandCharacteristic?.writeType = WRITE_TYPE_DEFAULT
        grillCommandCharacteristic?.value = data
        val result = btGatt?.writeCharacteristic(grillCommandCharacteristic)
        println("GrillCore BT Command Sent: $result")
    }

    fun connectToDevice(address: String): Boolean {
        canProcessBTAdvertisements = false
        btAdapter?.let {
            val device = it.getRemoteDevice(address)
            btGatt = device.connectGatt(ctx, false, gattFlowCallback, TRANSPORT_LE)
            return true
        }
        return false
    }

    fun disconnectDevice() {
        canProcessBTAdvertisements = true
        btGatt?.disconnect()
        btGatt?.close()
        btGatt?.device?.address?.let { macAddress ->
            sendBTDeviceResponse(BTCommand.Disconnect, macAddress, null)
        }
    }

    fun sendScanRequestCommand() {
        grillCommandCharacteristic?.writeType = WRITE_TYPE_DEFAULT
        var data = byteArrayOf(3, 2)
        btGatt?.device?.address?.let { mac ->
            data = BTManager.encryptData(mac, data)
        }
        grillCommandCharacteristic?.value = data

        val success = btGatt?.writeCharacteristic(grillCommandCharacteristic)
        println("Request Command Sent: $success")
    }

    fun sendScanResultCommand() {
        grillCommandCharacteristic?.writeType = WRITE_TYPE_DEFAULT
        var data = byteArrayOf(4, 2)
        btGatt?.device?.address?.let { mac ->
            //data = BTManager.encryptData(mac, data)
        }
        grillCommandCharacteristic?.value = data
        val success = btGatt?.writeCharacteristic(grillCommandCharacteristic)
        println("Result Command Sent: $success")
    }

    fun enableWifiResultNotification() {
        val success = btGatt?.setCharacteristicNotification(grillNotifyCharacteristic, true)
        println("Characteristic Enabled: $success")
    }

    fun sendGrillInfoRequestCommand() {
        grillCommandCharacteristic?.writeType = WRITE_TYPE_DEFAULT
        var data = byteArrayOf(11, 2)
        btGatt?.device?.address?.let { mac ->
            data = BTManager.encryptData(mac, data)
        }
        grillCommandCharacteristic?.value = data
        val success = btGatt?.writeCharacteristic(grillCommandCharacteristic)
        println("Grill Info Command Sent: $success")
    }

    fun enableWifiResultDescriptor() {
        grillNotifyDescriptor?.value = ENABLE_INDICATION_VALUE // is it ENABLE_INDICATION_VALUE? ENABLE_NOTIFICATION_VALUE
        val success = btGatt?.writeDescriptor(grillNotifyDescriptor)
        println("Descriptor Enabled: $success")
    }

    fun readWifiScanResult() {
        val success = btGatt?.readCharacteristic(grillNotifyCharacteristic)
        println("Wifi Result Read: $success")
    }

    fun sendWifiJoinRequest(wifiNetwork: WifiNetwork) {
        grillCommandCharacteristic?.writeType = WRITE_TYPE_DEFAULT

        randomToken = randomizeToken(8)
        val tokenBytes = randomToken.toByteArray(Charsets.UTF_8)
        val ssid = wifiNetwork.ssid ?: ""
        val ssidBytes = ssid.toByteArray(Charsets.UTF_8)
        val pwd = wifiNetwork.password ?: ""
        val pwdBytes = pwd.toByteArray(Charsets.UTF_8)

        var data = byteArrayOf(
            6,
            2,
            randomToken.length.toByte(),
            ssid.length.toByte(),
            pwd.length.toByte())
            .plus(tokenBytes)
            .plus(ssidBytes)
            .plus(pwdBytes)

        btGatt?.device?.address?.let { mac ->
            data = BTManager.encryptData(mac, data)
        }
        grillCommandCharacteristic?.value = data

        val success = btGatt?.writeCharacteristic(grillCommandCharacteristic)
        println("Join Wifi Request: $success")
    }

    private fun randomizeToken(length: Int): String {
        val seed = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length).map { seed.random() }.joinToString("")
    }

    fun passDeviceToCore() {
        // ???
    }

    fun fetchDeviceFromCore() {
        // ???
    }
}

data class BLEService(val uuid: String, val type: Int, val characteristics: ArrayList<BLECharacteristic>)

data class BLECharacteristic(val uuid: String, val writeType: Int, val descriptors: ArrayList<BLEDescriptor>)

data class BLEDescriptor(val uuid: String)