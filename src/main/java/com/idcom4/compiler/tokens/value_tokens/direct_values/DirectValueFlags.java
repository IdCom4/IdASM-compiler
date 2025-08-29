package com.idcom4.compiler.tokens.value_tokens.direct_values;

public record DirectValueFlags(boolean isImmediate, boolean isPointer, boolean isMemAddress, boolean isStaticAddress) {
    public static DirectValueFlags Immediate() { return new DirectValueFlags(true, false, false, false); }
    public static DirectValueFlags Pointer() { return new DirectValueFlags(false, true, false, false); }
    public static DirectValueFlags MemAddress() { return new DirectValueFlags(false, false, true, false); }
    public static DirectValueFlags StaticAddress() { return new DirectValueFlags(false, false, false, true); }
    public static DirectValueFlags Or(DirectValueFlags first, DirectValueFlags second) {
        return new DirectValueFlags(
                first.isImmediate || second.isImmediate,
                first.isPointer || second.isPointer,
                first.isStaticAddress || second.isStaticAddress,
                first.isMemAddress || second.isMemAddress);
    }
}
