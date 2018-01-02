package com.apns;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import okio.BufferedSink;
import static com.apns.Pusher.Mode;
import static com.apns.Result.Reason;

public class Client {

    private JWT jwt;
    private final String gateway;
    private final String keyFilePath;
    private final String teamId;
    private final String keyId;
    private static final MediaType mediaType = MediaType.parse("application/json");
    private OkHttpClient client;
    private OkHttpClient.Builder clientBuilder;

    public Client(String keyFilePath, String teamId, String keyId, Mode mode) {

        if (mode == Mode.PRODUCTION) {
            gateway = Util.ENDPOINT_PRODUCTION;
        } else {
            gateway = Util.ENDPOINT_SANDBOX;
        }

        this.keyFilePath = keyFilePath;
        this.teamId = teamId;
        this.keyId = keyId;
        this.jwt = JWT.getJWT(keyFilePath, teamId, keyId);

        clientBuilder = getBuilder();
        client = clientBuilder.build();
    }

    private static OkHttpClient.Builder getBuilder() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        builder.connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES));
        return builder;
    }

    Result push(Payload payload, String deviceToken, String topic, String priority) {

        RequestBody requestBody =new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(payload.getPayload().getBytes(Util.UTF_8));
            }
        };

        if (jwt.getToken() == null || System.currentTimeMillis() - jwt.getTimestamp() > 55 * 60 * 1000) {
                this.jwt = JWT.getJWT(keyFilePath, teamId, keyId);
        }

        Headers headers = buildHeader(payload.size()+"", jwt.getToken(), topic, priority);

        Request request = new Request.Builder()
                .url(gateway+deviceToken)
                .post(requestBody)
                .headers(headers)
                .build();

        okhttp3.Response response = null;
        try {
            response = client.newCall(request).execute();
            Result result = parseResponse(response);
            result.setDeviceToken(deviceToken);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    private Headers buildHeader(String contentLength, String jwt, String topic, String priority) {

        return new Headers.Builder()
                .add("Content-Length", contentLength)
                .add("apns-topic", topic)
                .add("apns-priority", priority)
                .add("authorization", "bearer "+jwt)
                .build();
    }

    private Result parseResponse(Response response) throws IOException {

        Result result = new Result();
        result.setResponseCode(response.code());
        if (response.code() != 200 && response.body() != null) {
            final String reason = JSON.parseObject(response.body().string(), ApnsData.class).getReason();
            switch (Reason.valueOf(reason)) {
                case BadDeviceToken:
                    result.setReason(Reason.BadDeviceToken);
                    break;
                default:
                    result.setReason(Reason.Other);
                    break;
            }
        }
        return result;
    }
}
