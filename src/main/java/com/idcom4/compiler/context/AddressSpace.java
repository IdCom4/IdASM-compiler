package com.idcom4.compiler.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AddressSpace {
    private final List<Integer> relativesAddresses = new ArrayList<>();
    private final HashMap<String, Short> shifts = new HashMap<>();
    private short shiftsAmount = 0;
    private short currentAddress;

    public AddressSpace(short startAddress) {
        this.currentAddress = startAddress;
    }

    public void RegisterRelativeAddress(int address) {
        this.relativesAddresses.add(address);
    }

    public List<Integer> GetRelativesAddresses() {
        return this.relativesAddresses;
    }

    public String AddShift(short value) {
        String uid = UUID.randomUUID().toString();
        shifts.put(uid, value);

        shiftsAmount += value;
        return uid;
    }

    public Short ClaimBackShift(String uuid) {
        Short value = shifts.remove(uuid);
        shiftsAmount -= value;

        return value;
    }

    public int RemainingShifts() {
        return shifts.size();
    }

    public short GetCurrentAddress() {
        return (short)(currentAddress + shiftsAmount);
    }

    public void IncreaseAddressCount(short addressIncrease, String from) {
        currentAddress += addressIncrease;
    }
}
