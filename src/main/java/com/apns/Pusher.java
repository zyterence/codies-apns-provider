package com.apns;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Pusher {

    private ExecutorService executor;
    private Client client;

    public enum Mode {
        /**
         * Use Connection to the APNs Production environments.
         */
        PRODUCTION,
        /**
         * Use Connection to the APNs Development environments.
         */
        DEVELOPMENT
    }

    public Pusher(String keyFilePath, String teamId, String keyId, Mode mode) {
        executor = Executors.newFixedThreadPool(120);
        client = new Client(keyFilePath, teamId, keyId, mode);
    }

    public ArrayList<Result> push(ArrayList<String> deviceTokens, Payload payload, String topic, String priority) {

        List<Future<Result>> futureList = new ArrayList<>();

        for (String deviceToken : deviceTokens) {
            String token = Util.sanitizeTokenString(deviceToken);
            futureList.add(executor.submit(() ->
                    client.push(payload, token, topic, priority))
            );
        }

        ArrayList<Result> results = new ArrayList<>();

        for (Future<Result> future : futureList) {
            try {
                Result result = future.get();
                results.add(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public Result pushSingle(String deviceToken, Payload payload, String topic, String priority) {

        Result result = null;
        String token = Util.sanitizeTokenString(deviceToken);

        Future<Result> future = executor.submit(() ->
                client.push(payload, token, topic, priority)
        );

        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }
}
