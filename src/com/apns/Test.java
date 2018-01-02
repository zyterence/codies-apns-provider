package com.apns;

import java.util.ArrayList;
import static com.apns.Pusher.Mode;

public class Test {

    static final String teamId = "";
    static final String keyId = "";
    static final String keyFilePath = "";
    static final String defaultTopic = "";
    static final String defaultPriority = "";  // "10" or "5"
    static final String TEST_TOKEN = "";
    static final String TEST_WRONG_TOKEN = "";

    public static void main(String[] args)  {

        long start = System.currentTimeMillis();
        // 1
        Payload payload = new Payload.Builder().alertTitle("Hello").alertBody("World").badge(23).build();
        // 2
        Pusher pusher = new Pusher(keyFilePath, teamId, keyId, Mode.DEVELOPMENT);

        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Time for initial: " + time + " millisecond");

        // Test for pushing single Notification with bad token
        testSingleToken(pusher, TEST_WRONG_TOKEN, payload, false);
        // Test for pushing single Notification with good token
        testSingleToken(pusher, TEST_TOKEN, payload, true);

        // Test for pushing multiple Notification
        // testMultipulToken(pusher, payload);
    }

    public static void testSingleToken(Pusher pusher, String token, Payload payload, Boolean tokenIsvalid) {

        long start = System.currentTimeMillis();
        // 3
        Result result = pusher.pushSingle(token, payload, defaultTopic, defaultPriority);
        long end = System.currentTimeMillis();
        final long time = end - start;
        if (tokenIsvalid) {
            System.out.println("Time for single token test: " + time + " millisecond");
        } else {
            System.out.println("Time for bad token test: " + time + " millisecond");
        }
        System.out.println(result.getDeviceToken());
        System.out.println(result.getResponseCode());
        System.out.println(result.getReason());
    }

    public static void testMultipulToken(Pusher pusher, Payload payload) {

        ArrayList<String> tokens = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            tokens.add(TEST_WRONG_TOKEN);
        }

        for (int i = 0; i < 1000; i++) {
            tokens.add(TEST_TOKEN);
        }

        long start = System.currentTimeMillis();
        // 3
        ArrayList<Result> results = pusher.push(tokens, payload, defaultTopic, defaultPriority);
        long end = System.currentTimeMillis();
        final long time = end - start;
        System.out.println("Time for test with " + tokens.size() + " tokens test: " + time + " millisecond");

        System.out.println("result size: " + results.size());
        for (int i = 0; i < results.size(); i++) {
            Result result = results.get(i);
            if (result.getResponseCode()!=200) {
                System.out.println(i + 1);
                System.out.println(result.getDeviceToken() + "  " + result.getResponseCode() + "  " + result.getReason());
            }
        }
    }
}
