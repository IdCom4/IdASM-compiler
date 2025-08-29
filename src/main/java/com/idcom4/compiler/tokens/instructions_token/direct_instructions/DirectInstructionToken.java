package com.idcom4.compiler.tokens.instructions_token.direct_instructions;


import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.utils.StringUtils;

public abstract class DirectInstructionToken extends InstructionToken {

    public static final short ADDRESS_AMOUNT = 3;

    public enum EOpCodeMasks {
        V0_IMMEDIATE((short) 0x8000),
        V1_IMMEDIATE((short) 0x4000),
        V0_POINTER((short) 0x2000),
        V1_POINTER((short) 0x1000),
        V0_MEMADDR((short) 0x800),
        V1_MEMADDR((short) 0x400);

        public final short value;

        EOpCodeMasks(short value) {
            this.value = value;
        }
    }

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                SingleParamDirectInstructionToken.Parser::TryParse,
                TwoParamsDirectInstructionToken.Parser::TryParse,
        };

        public static ITokenParser.ParsingResult<DirectInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((DirectInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    protected DirectInstructionToken() {
        Context.INSTANCE.addressSpace.IncreaseAddressCount(ADDRESS_AMOUNT, this.getClass().getSimpleName());
    }

    @Override
    public int GetAmountOfAddresses() {
        return ADDRESS_AMOUNT;
    }

    public static short ApplyOpCodeMasks(short opcode, DirectValueToken param0, DirectValueToken param1) {

        if (param0 != null) {
            if (param0.GetFlags().isImmediate())
                opcode = (short)(opcode | DirectInstructionToken.EOpCodeMasks.V0_IMMEDIATE.value);
            if (param0.GetFlags().isPointer())
                opcode |= DirectInstructionToken.EOpCodeMasks.V0_POINTER.value;
            if (param0.GetFlags().isMemAddress())
                opcode |= DirectInstructionToken.EOpCodeMasks.V0_MEMADDR.value;
        }

        if (param1 != null) {
            if (param1.GetFlags().isImmediate())
                opcode |= DirectInstructionToken.EOpCodeMasks.V1_IMMEDIATE.value;
            if (param1.GetFlags().isPointer())
                opcode |= DirectInstructionToken.EOpCodeMasks.V1_POINTER.value;
            if (param1.GetFlags().isMemAddress())
                opcode |= DirectInstructionToken.EOpCodeMasks.V1_MEMADDR.value;
        }

        return opcode;
    }

}
