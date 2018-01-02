package com.apns;

import java.util.HashMap;
import com.alibaba.fastjson.JSON;

public class Payload {

    private final String payload;

    private Payload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public int size() {
        return payload.getBytes(Util.UTF_8).length;
    }

    public static class Builder {

        private final HashMap<String, Object> root, aps, alert;

        public Builder() {
            root = new HashMap<>();
            aps = new HashMap<>();
            alert = new HashMap<>();
        }

        public Builder mutableContent(boolean mutable) {
            if (mutable) {
                aps.put("mutable-content", 1);
            } else {
                aps.remove("mutable-content");
            }

            return this;
        }

        public Builder mutableContent() {
            return this.mutableContent(true);
        }

        public Builder contentAvailable(boolean contentAvailable) {
            if (contentAvailable) {
                aps.put("content-available", 1);
            } else {
                aps.remove("content-available");
            }

            return this;
        }

        public Builder contentAvailable() {
            return this.contentAvailable(true);
        }

        public Builder alertBody(String body) {
            alert.put("body", body);
            return this;
        }

        public Builder alertTitle(String title) {
            alert.put("title", title);
            return this;
        }

        public Builder sound(String sound) {

            if (sound != null) {
                aps.put("sound", sound);
            } else {
                aps.remove("sound");
            }

            return this;
        }

        public Builder category(String category) {

            if (category != null) {
                aps.put("category", category);
            } else {
                aps.remove("category");
            }
            return this;
        }

        public Builder badge(int badge) {
            aps.put("badge", badge);
            return this;
        }

        public Builder customField(String key, Object value) {
            root.put(key, value);
            return this;
        }

        public Payload build() {

            final String payload;
            root.put("aps", aps);
            aps.put("alert", alert);
            payload = JSON.toJSONString(root);

            return new Payload(payload);
        }
    }

}
