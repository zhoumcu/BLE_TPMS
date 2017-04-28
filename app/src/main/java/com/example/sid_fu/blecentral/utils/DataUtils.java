package com.example.sid_fu.blecentral.utils;

import com.example.sid_fu.blecentral.ParsedAd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by sid-fu on 2016/5/9.
 */
public class DataUtils {
    public static final int BLE_GAP_AD_TYPE_ADVDATA                              = 0x00; /**< Flags for discoverability. */
    public static final int BLE_GAP_AD_TYPE_FLAGS                              = 0x01; /**< Flags for discoverability. */
    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_MORE_AVAILABLE  = 0x02;/**< Partial list of 16 bit service UUIDs. */
    public static final int BLE_GAP_AD_TYPE_16BIT_SERVICE_UUID_COMPLETE        = 0x03; /**< Complete list of 16 bit service UUIDs. */
    public static final int BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_MORE_AVAILABLE  = 0x04; /**< Partial list of 32 bit service UUIDs. */
    public static final int BLE_GAP_AD_TYPE_32BIT_SERVICE_UUID_COMPLETE        = 0x05; /**< Complete list of 32 bit service UUIDs. */
    public static final int BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_MORE_AVAILABLE = 0x06; /**< Partial list of 128 bit service UUIDs. */
    public static final int BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE       = 0x07; /**< Complete list of 128 bit service UUIDs. */
    public static final int BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME                   = 0x08; /**< Short local device name. */
    public static final int BLE_GAP_AD_TYPE_COMPLETE_LOCAL_NAME                = 0x09; /**< Complete local device name. */
    public static final int BLE_GAP_AD_TYPE_TX_POWER_LEVEL                     = 0x0A; /**< Transmit power level. */
    public static final int BLE_GAP_AD_TYPE_CLASS_OF_DEVICE                    = 0x0D; /**< Class of device. */
    public static final int BLE_GAP_AD_TYPE_SIMPLE_PAIRING_HASH_C              = 0x0E; /**< Simple Pairing Hash C. */
    public static final int BLE_GAP_AD_TYPE_SIMPLE_PAIRING_RANDOMIZER_R        = 0x0F; /**< Simple Pairing Randomizer R. */
    public static final int BLE_GAP_AD_TYPE_SECURITY_MANAGER_TK_VALUE          = 0x10; /**< Security Manager TK Value. */
    public static final int BLE_GAP_AD_TYPE_SECURITY_MANAGER_OOB_FLAGS         = 0x11; /**< Security Manager Out Of Band Flags. */
    public static final int BLE_GAP_AD_TYPE_SLAVE_CONNECTION_INTERVAL_RANGE    = 0x12; /**< Slave Connection Interval Range. */
    public static final int BLE_GAP_AD_TYPE_SLAVE_DATA    = 0x13; /**< Slave Connection Interval Range. */
    public static final int BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_16BIT      = 0x14; /**< List of 16-bit Service Solicitation UUIDs. */
    public static final int BLE_GAP_AD_TYPE_SOLICITED_SERVICE_UUIDS_128BIT     = 0x15; /**< List of 128-bit Service Solicitation UUIDs. */
    public static final int BLE_GAP_AD_TYPE_SERVICE_DATA                       = 0x16; /**< Service Data. */
    public static final int BLE_GAP_AD_TYPE_PUBLIC_TARGET_ADDRESS              = 0x17; /**< Public Target Address. */
    public static final int BLE_GAP_AD_TYPE_RANDOM_TARGET_ADDRESS              = 0x18; /**< Random Target Address. */
    public static final int BLE_GAP_AD_TYPE_APPEARANCE                         = 0x19; /**< Appearance. */
    public static final int BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA         = 0xFF; /**< Manufacturer Specific Data. */
    private static String toHexUtil(int n){
        String rt="";
        switch(n){
            case 10:rt+="A";break;
            case 11:rt+="B";break;
            case 12:rt+="C";break;
            case 13:rt+="D";break;
            case 14:rt+="E";break;
            case 15:rt+="F";break;
            default:
                rt+=n;
        }
        return rt;
    }

    public static String toHex(int n){
        StringBuilder sb=new StringBuilder();
        if(n/16==0){
            return toHexUtil(n);
        }else{
            String t=toHex(n/16);
            int nn=n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseAscii(String str){
        StringBuilder sb=new StringBuilder();
        byte[] bs=str.getBytes();
        for(int i=0;i<bs.length;i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

    public static  String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String btyesToAsciiString(byte[] bArray)
    {
        return parseAscii(bytesToHexString(bArray));
    }

    public static ParsedAd parseData(byte[] adv_data) {
        if (adv_data.length == 0) return null;
        ParsedAd parsedAd = new ParsedAd();
        ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;
            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01: // Flags
                    parsedAd.flags = buffer.get();
                    length--;
                    break;
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                case 0x14: // List of 16-bit Service Solicitation UUIDs
                    /*while (length >= 2) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }*/
                    break;
                case 0x13:
                    byte sb1[] = new byte[length];
                    buffer.get(sb1, 0, length);
                    length = 0;
                    //parsedAd.datas = bytesToHexString(sb1);
                    parsedAd.datas = sb1;
                    break;
                case 0x04: // Partial list of 32 bit service UUIDs
                case 0x05: // Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));
                        length -= 4;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                case 0x15: // List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        parsedAd.uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;
                case 0x08: // Short local device name
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    parsedAd.localName = new String(sb).trim();
                    break;
                case (byte) 0xFF: // Manufacturer Specific Data
                    parsedAd.manufacturer = buffer.getShort();
                    length -= 2;
                    break;
                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return parsedAd;
    }
    //解析广播数据
    public static byte[] adv_report_parse(byte type, byte[] adv_data)
    {
        int index = 0;
        int length;
        byte[] type_data;

        length = adv_data.length;
        while (index < length)
        {
            int   field_length = adv_data[index];
            byte  field_type   = adv_data[index+1];

            if (field_type == type)
            {
                type_data = new byte[field_length-1];
                int i;
                for(i = 0;i < field_length-1;i++)
                {
                    type_data[i] = adv_data[index+2+i];
                }
                return type_data;
            }
            index += field_length+1;
        }
        return null;
    }
}
