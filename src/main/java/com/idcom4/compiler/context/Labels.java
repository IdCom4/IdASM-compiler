package com.idcom4.compiler.context;

import com.idcom4.exceptions.DuplicateIdentifierException;
import com.idcom4.exceptions.UnknownLabelException;

import java.util.HashMap;

public class Labels {
    private final AddressSpace addressSpace;
    private final HashMap<String, Short> labels = new HashMap<>();

    public Labels(AddressSpace addressSpace) {
        this.addressSpace = addressSpace;
    }

    public void Register(String label) {
        labels.put(label, this.addressSpace.GetCurrentAddress());
    }

    public void Register(String label, short address) throws DuplicateIdentifierException {
        if (IsAlreadyRegistered(label)) throw new DuplicateIdentifierException(label);
        labels.put(label, address);
    }

    public boolean IsAlreadyRegistered(String label) {
        return labels.containsKey(label);
    }

    public short GetAddress(String label) throws UnknownLabelException {
        Short address = labels.get(label);
        if (address == null) throw new UnknownLabelException(label);

        return address;
    }
}
