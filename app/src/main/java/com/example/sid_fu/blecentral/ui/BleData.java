package com.example.sid_fu.blecentral.ui;

/**
 * Created by Administrator on 2016/4/14.
 */
public class BleData {
    public int temp = 0;
    public float press = 0;
    public double sensorFailure = 0;
    public double sensorLow = 0;
    public double leakage = 0;
    public double leakageQucik = 0;
    private int status;
    private float voltage;
    private String deviceAddress;
    private int viewPosition;
    private boolean exception;
    private boolean noReceviceData;
    private String data;
    private String stringPress;
    private String errorState;
    private boolean error;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    private int rssi = 0;

    public double getSensorLow() {
        return sensorLow;
    }

    public void setSensorLow(double sensorLow) {
        this.sensorLow = sensorLow;
    }

    public double getLeakageQucik() {
        return leakageQucik;
    }

    public void setLeakageQucik(double leakageQucik) {
        this.leakageQucik = leakageQucik;
    }

    public float getPress() {
        return press;
    }

    public void setPress(float press) {
        this.press = press;
    }

    public double getLeakage() {
        return leakage;
    }

    public void setLeakage(double leakage) {
        this.leakage = leakage;
    }

    public double getSensorFailure() {
        return sensorFailure;
    }

    public void setSensorFailure(double sensorFailure) {
        this.sensorFailure = sensorFailure;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setViewPosition(int viewPosition) {
        this.viewPosition = viewPosition;
    }

    public int getViewPosition() {
        return this.viewPosition;
    }

    public boolean isException() {
        return this.exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }


    public void setNoReceviceData(boolean noReceviceData) {
        this.noReceviceData = noReceviceData;
    }

    public boolean isNoReceviceData() {
        return noReceviceData;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setStringPress(String stringPress) {
        this.stringPress = stringPress;
    }

    public String getStringPress() {
        return stringPress;
    }

    public void setErrorState(String errorState) {
        this.errorState = errorState;
    }

    public String getErrorState() {
        return errorState;
    }

    public void setIsError(boolean isError) {
        this.error = isError;
    }

    public boolean isError() {
        return error;
    }
}
