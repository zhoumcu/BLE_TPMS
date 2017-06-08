/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sid_fu.blecentral.model;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static final String ACTION_RETURN_OK = "send_msg_to_ble_return_ok";
    public static final String ACTION_SEND_OK = "send_msg_ok";
    public static final String SCAN_FOR_RESULT = "scan_for_result";
    public static final String ACTION_NAME_RSSI = "action_name_rssi";
    public static final String ACTION_DISCONNECT_SCAN = "aciton_disconnect_scan";
    public static final String ACTION_STOP_SCAN = "ACTION_STOP_SCAN";
    public static final String ACTION_START_SCAN = "ACTION_START_SCAN";
    public static final String ACTION_REFREASH_DEVICE = "ACTION_REFREASH_DEVICE";
    public static String ACTION_CHANGE_RESULT = "action_change_result";
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_NOTIFY =
            //UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
            UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SERVICE =
            //UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
            UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public BluetoothGattCharacteristic mNotifyCharacteristic;
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_CHAR =
            //UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
            UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    public static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR1 = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR2 = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR3 = "0000fff3-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR4 = "0000fff4-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR5 = "0000fff5-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR6 = "0000fff6-0000-1000-8000-00805f9b34fb";
    public static String UUID_CHAR7 = "0000fff7-0000-1000-8000-00805f9b34fb";
    public static String UUID_HERATRATE = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String UUID_TEMPERATURE = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String UUID_0XFFA6 = "0000ffa6-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
