package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.DirectInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.IntLiteralValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.LabelValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.List;

public class RetInstructionToken extends CustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.RET.value;

    public static class Parser {

        public static ParsingResult<RetInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            Pair<String, Integer> result = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String nameSlice = result.First;
            int i = result.Second;

            if (!nameSlice.equalsIgnoreCase(name))
                throw new DiscardTokenException("unexpected instruction name: " + name);

            InstructionToken.Parser.ParsedParam parsedParam = null;

            try {
                parsedParam = InstructionToken.Parser.TryParseParamValue(ITokenParser.Cast(DirectValueToken.Parser::TryParse), sourceCode, i);
            } catch (CompilationException ignored) {}

            if (parsedParam == null)
                return new ParsingResult<>(new RetInstructionToken(null), i);

            return new ParsingResult<>(new RetInstructionToken((DirectValueToken) parsedParam.param()), parsedParam.newIndex());
        }
    }

    private final DirectValueToken returnValue;

    private final PopInstructionToken[] popStackBackToRegisters;
    private final PopInstructionToken popReturnAddressToRegister;
    private PushInstructionToken pushReturnValueToStack = null;
    private MoveInstructionToken moveReturnValueToRFlags = null;
    private final GotoInstructionToken goBackFromFunction;

    private final int addressesAmount;

    public RetInstructionToken(DirectValueToken returnValue) {
        this.returnValue = returnValue;

        StaticAddressValueToken flagsRegister = new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.FLAGS);

        if (this.returnValue != null)
            this.moveReturnValueToRFlags = new MoveInstructionToken(this.returnValue, flagsRegister);

        // put back all data from stack to registers
        this.popStackBackToRegisters = this.CreatePopBackToRegisters();

        // pop return address to register R3
        this.popReturnAddressToRegister = new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R3));

        // put return value to stack;
        if (this.returnValue != null)
            this.pushReturnValueToStack = new PushInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.FLAGS));

        // go back from function
        this.goBackFromFunction = new GotoInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R3));

        this.addressesAmount = this.ComputeAddressesAmount(moveReturnValueToRFlags, popStackBackToRegisters, popReturnAddressToRegister, pushReturnValueToStack, goBackFromFunction);
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.addressesAmount;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addressesAmount * Context.INSTANCE.byteEncoding];

        int bytesIndex = 0;

        if (this.returnValue != null) {
            // move return value to flags register
            byte[] moveReturnValueBytes = this.moveReturnValueToRFlags.Generate();
            System.arraycopy(moveReturnValueBytes, 0, bytes, bytesIndex, moveReturnValueBytes.length);
            bytesIndex += moveReturnValueBytes.length;
        }

        // pop stack to registers
        for (PopInstructionToken pop : this.popStackBackToRegisters) {
            byte[] popBytes = pop.Generate();
            System.arraycopy(popBytes, 0, bytes, bytesIndex, popBytes.length);
            bytesIndex += popBytes.length;
        }

        // put return address to r3
        byte[] popReturnAddressBytes = this.popReturnAddressToRegister.Generate();
        System.arraycopy(popReturnAddressBytes, 0, bytes, bytesIndex, popReturnAddressBytes.length);
        bytesIndex += popReturnAddressBytes.length;

        if (this.returnValue != null) {
            // push return value
            byte[] pushReturnValueBytes = this.pushReturnValueToStack.Generate();
            System.arraycopy(pushReturnValueBytes, 0, bytes, bytesIndex, pushReturnValueBytes.length);
            bytesIndex += pushReturnValueBytes.length;
        }

        // go back from function
        byte[] goBackBytes = this.goBackFromFunction.Generate();
        System.arraycopy(goBackBytes, 0, bytes, bytesIndex, goBackBytes.length);


        return bytes;
    }

    private int ComputeAddressesAmount(MoveInstructionToken moveReturnValueToRFlags, PopInstructionToken[] popStackBackToRegisters, PopInstructionToken popReturnAddressToRegister, PushInstructionToken pushReturnValueToStack, GotoInstructionToken goBackFromFunction) {
        int popToRegistersAmount = 0;

        for (PopInstructionToken pop : popStackBackToRegisters)
            popToRegistersAmount += pop.GetAmountOfAddresses();


        return  (this.returnValue == null ? 0 : moveReturnValueToRFlags.GetAmountOfAddresses()) +
                popToRegistersAmount +
                popReturnAddressToRegister.GetAmountOfAddresses() +
                (this.returnValue == null ? 0 : pushReturnValueToStack.GetAmountOfAddresses()) +
                goBackFromFunction.GetAmountOfAddresses();
    }

    private PopInstructionToken[] CreatePopBackToRegisters() {
        return new PopInstructionToken[] {

                // special registers
                new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.SPTR)),
                new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.ACC1)),
                new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.ACC0)),

                // general purpose registers
                new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R2)),
                new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R1)),
                new PopInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R0)),

        };
    }

}
