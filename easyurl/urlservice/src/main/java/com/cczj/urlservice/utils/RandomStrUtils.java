package com.cczj.urlservice.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStrUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final int CHARACTER_COUNT = CHARACTERS.length();
    private static final int DESIRED_LENGTH = 7;

    public static String generateRandomString() {
        char[] randomString = new char[DESIRED_LENGTH];
        for (int i = 0; i < DESIRED_LENGTH; i++) {
            randomString[i] = CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTER_COUNT));
        }
        return new String(randomString);
    }


}
