package com.apns;

public class Result extends Object {

    private String deviceToken;
    private int responseCode;
    private String reason;

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getReason() {
        return reason;
    }
}
