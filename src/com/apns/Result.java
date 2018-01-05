package com.apns;

public class Result extends Object {

    public enum Reason {
        BadDeviceToken {
            @Override
            public String toString() {
                return "BadDeviceToken";
            }
        },
        Other {
            @Override
            public String toString() {
                return "Other";
            }
        }
    }

    private String deviceToken;
    private int responseCode;
    private Reason reason;

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Reason getReason() {
        return reason;
    }
}
