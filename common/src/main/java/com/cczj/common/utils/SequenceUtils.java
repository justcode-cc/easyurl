package com.cczj.common.utils;

public class SequenceUtils {


    private static final GlobalIdUtils sequence = new GlobalIdUtils(3L);

    private SequenceUtils() {
    }

    public static long getNextId() {
        return sequence.nextId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            System.out.println(getNextId());
        }
    }
}
