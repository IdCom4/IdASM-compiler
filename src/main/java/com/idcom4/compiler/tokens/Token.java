package com.idcom4.compiler.tokens;

import com.idcom4.compiler.context.Context;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.UnknownLabelException;

import java.util.Arrays;

public abstract class Token {

    public abstract int GetAmountOfAddresses() throws CustomValueException;

    public abstract byte[] Generate() throws UnknownLabelException, CustomValueException;

    protected byte[] ValueToBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >>> 24);
        bytes[1] = (byte) (value >>> 16);
        bytes[2] = (byte) (value >>> 8);
        bytes[3] = (byte) value;


        return Arrays.copyOfRange(bytes, 4 - Context.INSTANCE.byteEncoding, 4);
    }
}
