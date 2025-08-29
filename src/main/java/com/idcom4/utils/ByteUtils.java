package com.idcom4.utils;

import com.idcom4.compiler.context.Context;

import java.util.List;

public class ByteUtils {

    public static String BytesToXmemString(byte[] bytes, int byteEncoding) {
        StringBuilder xmem = new StringBuilder();

        xmem.append("$byte_encoding=").append(String.format("%02d", byteEncoding), 0, 2).append("\n\n\n");

        List<Integer> relativeAddresses = Context.INSTANCE.addressSpace.GetRelativesAddresses();
        if (!relativeAddresses.isEmpty()) {
            xmem.append("@relative=[");
            for (int i = 0; i < relativeAddresses.size(); i++) {
                xmem.append(Integer.toHexString(relativeAddresses.get(i)));
                if (i < relativeAddresses.size() - 1)
                    xmem.append(", ");
            }
            xmem.append("]\n\n");
        }

        for (int i = 0; i < bytes.length; i += byteEncoding) {
            int val = 0;
            for (int x = 0; x < byteEncoding; x++) {
                val = ((val << 8) | (bytes[i + x] & 0xFF));
            }

            val = val & 0xFFFF;
            xmem.append(String.format("%04x", val).substring(0, 4));
            if (i != 0 && (i + byteEncoding) % (byteEncoding * 3) == 0)
                xmem.append("\n");
            else
                xmem.append(" ");
        }

        return xmem.toString();
    }

    public static void PrintBytes(byte[] bytes, int byteEncoding, boolean withAddresses) {
        for (int i = 0; i < bytes.length; i += byteEncoding) {

            int val = 0;
            for (int x = 0; x < byteEncoding; x++) {
                val = ((val << 8) | (bytes[i + x] & 0xFF));
            }

            val = val & 0xFFFF;

            if (withAddresses)
                System.out.print("[" + String.format("%04x", (i / 2) + 25).substring(0, 4) + "] ");
            System.out.print(String.format("%04x", val).substring(0, 4));

            if (i != 0 && (i + byteEncoding) % (byteEncoding * 3) == 0)
                System.out.println();
            else
                System.out.print(" ");
        }
    }
}
