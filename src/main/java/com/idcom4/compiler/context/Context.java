package com.idcom4.compiler.context;

public class Context {

    public static Context INSTANCE;

    public final AddressSpace addressSpace;
    public final Labels labels;


    public final int byteEncoding = 2;

    private Context(AddressSpace addressSpace) {
        this.addressSpace = addressSpace;
        this.labels = new Labels(addressSpace);
    }

    public static void InitInstance(AddressSpace addressSpace) {
        INSTANCE = new Context(addressSpace);
    }
}
