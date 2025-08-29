package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.LabelToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.DirectInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.IntLiteralValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.LabelValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CallInstructionToken extends CustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.CALL.value;

    public static class Parser {

        private final static char PARAM_OPEN = IdASMCompiler.EControlChars.OPEN_PARENTHESIS.value.charAt(0);
        private final static char PARAM_SEPARATOR = IdASMCompiler.EControlChars.COMMA.value.charAt(0);
        private final static char PARAM_CLOSE = IdASMCompiler.EControlChars.CLOSE_PARENTHESIS.value.charAt(0);

        public static ParsingResult<CallInstructionToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            Pair<String, Integer> instrNameResult = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String instrName = instrNameResult.First;
            int i = instrNameResult.Second;

            // parse name
            if (!instrName.equalsIgnoreCase(name)) {
                throw new DiscardTokenException("unexpected instruction name: " + instrName);
            }

            // parse label
            LabelToken labelToken;
            try {
                ParsingResult<LabelToken> parsedLabel = LabelToken.Parser.TryParse(sourceCode, i);
                labelToken = parsedLabel.token();
                i = parsedLabel.newIndex();
            } catch (TokenParsingException e) {
                throw new UnknownLabelException("expected function name, at index " + i, e);
            }

            // parse params
            Pair<List<DirectValueToken>, Integer> result = TryParseParams(sourceCode, i);
            List<DirectValueToken> params = result.First;
            i = result.Second;

            return new ParsingResult<>(new CallInstructionToken(labelToken, params), i);
        }

        private static Pair<List<DirectValueToken>, Integer> TryParseParams(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            if (i >= sourceCode.length() || sourceCode.charAt(i++) != PARAM_OPEN)
                throw new TokenParsingException("at index " + --i + ": '" + PARAM_OPEN + "' expected");

            // parse sub instructions
            List<DirectValueToken> params = new ArrayList<>();

            int endIndex;
            while ((endIndex = TryParseChar(sourceCode, i, PARAM_CLOSE)) < 0) {
                // try to parse coma
                int j = i;
                if (!params.isEmpty() && (i = TryParseChar(sourceCode, j, PARAM_SEPARATOR)) < 0)
                    throw new TokenParsingException("at index " + j + ": '" + PARAM_SEPARATOR + "' expected");
                try {
                    ITokenParser.ParsingResult<DirectValueToken> result = DirectValueToken.Parser.TryParse(sourceCode, i);
                    params.add(result.token());
                    i = result.newIndex();
                } catch (DiscardTokenException ignored) {}
            }

            i = endIndex;
            if (i >= sourceCode.length() || sourceCode.charAt(i - 1) != PARAM_CLOSE)
                throw new TokenParsingException("at index " + i + ": '" + PARAM_CLOSE + "' expected");

            return new Pair<>(params, i);
        }

        private static int TryParseChar(String sourceCode, int index, char c) {
            index = StringUtils.SkipEmptySpace(sourceCode, index);
            return sourceCode.charAt(index) == c ? index + 1 : -1;
        }
    }

    private final LabelToken labelToken;
    private final List<DirectValueToken> params;

    private final MoveInstructionToken moveReturnAddressToStack;
    private final MoveInstructionToken[] moveRegistersToStack;
    private final MoveInstructionToken[] moveParamsToRegisters;
    private final GotoInstructionToken gotoFunction;

    private final int addressesAmount;

    public CallInstructionToken(LabelToken label, List<DirectValueToken> params) {
        this.labelToken = label;
        this.params = params;

        // put return address to stack
        // below >>

        // put all registers to stack
        this.moveRegistersToStack = this.CreateMoveRegistersToStack();

        // store all parameters to registers then to stack
        this.moveParamsToRegisters = this.CreateMoveParamsToRegisters(params);

        // goto function
        this.gotoFunction = new GotoInstructionToken(new LabelValueToken(labelToken.GetLabel()));

        // compute total address amount + 1 additional instruction,
        // to take into account "moveReturnAddressToStack" that hasn't yet been initialized
        this.addressesAmount =
                this.ComputeAddressesAmount(moveRegistersToStack, moveParamsToRegisters, gotoFunction) + DirectInstructionToken.ADDRESS_AMOUNT;

        // put return address to stack
        this.moveReturnAddressToStack =
                new MoveInstructionToken(
                    new IntLiteralValueToken((short)(Context.INSTANCE.addressSpace.GetCurrentAddress() + DirectInstructionToken.ADDRESS_AMOUNT)),
                    new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.STACK)
                );
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.addressesAmount;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addressesAmount * Context.INSTANCE.byteEncoding];

        int bytesIndex = 0;

        // put return address to stack
        byte[] moveBytes = this.moveReturnAddressToStack.Generate();
        System.arraycopy(moveBytes, 0, bytes, bytesIndex, moveBytes.length);
        bytesIndex += moveBytes.length;


        // put all registers to stack
        for (MoveInstructionToken move : this.moveRegistersToStack) {
            byte[] jumpBytes = move.Generate();
            System.arraycopy(jumpBytes, 0, bytes, bytesIndex, jumpBytes.length);
            bytesIndex += jumpBytes.length;
        }

        // store all parameters to registers then to stack
        for (MoveInstructionToken move : this.moveParamsToRegisters) {
            byte[] jumpBytes = move.Generate();
            System.arraycopy(jumpBytes, 0, bytes, bytesIndex, jumpBytes.length);
            bytesIndex += jumpBytes.length;
        }

        // goto function
        byte[] gotoBytes = this.gotoFunction.Generate();
        System.arraycopy(gotoBytes, 0, bytes, bytesIndex, gotoBytes.length);

        return bytes;
    }

    private int ComputeAddressesAmount(MoveInstructionToken[] moveRegistersToStack, MoveInstructionToken[] moveParamsToRegisters, GotoInstructionToken gotoFunction) {
        int moveToStackAmount = 0;
        int moveToRegistersAmount = 0;

        for (MoveInstructionToken move : moveRegistersToStack)
            moveToStackAmount += move.GetAmountOfAddresses();

        for (MoveInstructionToken move : moveParamsToRegisters)
            moveToRegistersAmount += move.GetAmountOfAddresses();

        return moveToStackAmount + moveToRegistersAmount + gotoFunction.GetAmountOfAddresses();
    }

    private MoveInstructionToken[] CreateMoveRegistersToStack() {

        StaticAddressValueToken StackAddress = new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.STACK);

        return new MoveInstructionToken[] {
                // general purpose registers
                new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R0), StackAddress),
                new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R1), StackAddress),
                new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R2), StackAddress),

                // no need to save R3, as it will be overwritten at return anyway

                // special registers
                new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.ACC0), StackAddress),
                new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.ACC1), StackAddress),
                new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.SPTR), StackAddress),
        };
    }

    private MoveInstructionToken[] CreateMoveParamsToRegisters(List<DirectValueToken> params) {

        MoveInstructionToken[] moves = new MoveInstructionToken[params.size()];

        for (int i = 0; i < params.size(); i++) {
            if (i == 0)
                moves[i] = new MoveInstructionToken(params.get(i), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R0));
            else if (i == 1)
                moves[i] = new MoveInstructionToken(params.get(i), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R1));
            else if (i == 2)
                moves[i] = new MoveInstructionToken(params.get(i), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R1));
            else if (i == 3)
                moves[i] = new MoveInstructionToken(params.get(i), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R3));
            else
                moves[i] = new MoveInstructionToken(params.get(i), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.STACK));
        }

        return moves;
    }


}
