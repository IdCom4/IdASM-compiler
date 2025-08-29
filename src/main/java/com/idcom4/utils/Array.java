package com.idcom4.utils;

public class Array {

    public static int[] flatten(int[][] array) {
        int totalLength = 0;
        for (int[] sub : array) {
            totalLength += sub.length;
        }

        int[] result = new int[totalLength];
        int pos = 0;
        for (int[] sub : array) {
            System.arraycopy(sub, 0, result, pos, sub.length);
            pos += sub.length;
        }

        return result;
    }

    public static short[] flatten(short[][] array) {
        int totalLength = 0;
        for (short[] sub : array) {
            totalLength += sub.length;
        }

        short[] result = new short[totalLength];
        int pos = 0;
        for (short[] sub : array) {
            System.arraycopy(sub, 0, result, pos, sub.length);
            pos += sub.length;
        }

        return result;
    }

    public static byte[] flatten(byte[][] array) {
        int totalLength = 0;
        for (byte[] sub : array) {
            totalLength += sub.length;
        }

        byte[] result = new byte[totalLength];
        int pos = 0;
        for (byte[] sub : array) {
            System.arraycopy(sub, 0, result, pos, sub.length);
            pos += sub.length;
        }

        return result;
    }
}
