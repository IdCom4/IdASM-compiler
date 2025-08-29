package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.BodyTokenParser;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken.Parser.ParsedParam;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.*;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.custom_values.CmpValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.LabelValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.List;
import java.util.UUID;

public class IfInstructionToken extends NestedInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.IF.value;

    public static class Parser {

        public static ParsingResult<IfInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {

            Pair<String, Integer> instrNameResult = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String instrName = instrNameResult.First;
            int i = instrNameResult.Second;

            // parse name
            if (!instrName.equalsIgnoreCase(name))
                throw new DiscardTokenException("unexpected instruction name: " + instrName);

            // parse params
            ParsedParam parsedParam0 = InstructionToken.Parser.TryParseParamValue(ITokenParser.Cast(DirectValueToken.Parser::TryParse), sourceCode, i);
            ParsedParam parsedCmpOperator = InstructionToken.Parser.TryParseParamValue(ITokenParser.Cast(CmpValueToken.Parser::TryParse), sourceCode, parsedParam0.newIndex());
            ParsedParam parsedParam1 = InstructionToken.Parser.TryParseParamValue(ITokenParser.Cast(DirectValueToken.Parser::TryParse), sourceCode, parsedCmpOperator.newIndex());

            i = parsedParam1.newIndex();

            int addressesShift = ComputeAddressShift((CmpValueToken) parsedCmpOperator.param());

            // parse sub instructions
            Pair<List<InstructionToken>, Integer> result = BodyTokenParser.ParseBody(name, sourceCode, i, addressesShift);
            List<InstructionToken> subInstructions = result.First;
            i = result.Second;

            return new ParsingResult<>(
                    new IfInstructionToken(
                            (DirectValueToken) parsedParam0.param(),
                            (CmpValueToken) parsedCmpOperator.param(),
                            (DirectValueToken) parsedParam1.param(),
                            subInstructions
                    ),
                    i
            );
        }

        private static int ComputeAddressShift(CmpValueToken cmpToken) {
            int instructions = 2;
            if (cmpToken.GetOperator().equals(CmpValueToken.ECmpOperators.EQ))
                instructions += 1;

            return instructions * DirectInstructionToken.ADDRESS_AMOUNT;
        }
    }

    private final String exitConditionHiddenLabel;
    private final DirectValueToken param0;
    private final CmpValueToken cmpOperator;
    private final DirectValueToken param1;

    private MoveInstructionToken moveInstruction = null;
    private JmpInstructionToken[] jumpInstructions = null;

    private int addressesAmount;

    public IfInstructionToken(DirectValueToken param0, CmpValueToken cmpOperator, DirectValueToken param1, List<InstructionToken> subInstructions) throws DuplicateIdentifierException {
        super(subInstructions);

        // generate hidden label
        String hiddenLabel;
        do {
            hiddenLabel = UUID.randomUUID().toString();
        } while (Context.INSTANCE.labels.IsAlreadyRegistered(hiddenLabel));
        this.exitConditionHiddenLabel = hiddenLabel;

        this.param0 = param0;
        this.cmpOperator = cmpOperator;
        this.param1 = param1;

        try {
            Pair<MoveInstructionToken, JmpInstructionToken[]> controlInstructions = this.CreateIfControlInstructions(hiddenLabel, cmpOperator, param0, param1);
            this.moveInstruction = controlInstructions.First;
            this.jumpInstructions = controlInstructions.Second;
        } catch (InvalidOperatorException e) {
            System.err.println("[ERR] compiler error");
            e.printStackTrace();
            System.exit(1);
        }

        this.addressesAmount = this.ComputeAddressesAmount(subInstructions, moveInstruction, jumpInstructions);

        Context.INSTANCE.labels.Register(exitConditionHiddenLabel);
    }


    @Override
    public int GetAmountOfAddresses() {
        return this.addressesAmount;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addressesAmount * Context.INSTANCE.byteEncoding];

        int bytesIndex = 0;

        // move
        byte[] moveBytes = this.moveInstruction.Generate();
        System.arraycopy(moveBytes, 0, bytes, bytesIndex, moveBytes.length);
        bytesIndex += moveBytes.length;

        // jump
        for (JmpInstructionToken jump : this.jumpInstructions) {
            byte[] jumpBytes = jump.Generate();
            System.arraycopy(jumpBytes, 0, bytes, bytesIndex, jumpBytes.length);
            bytesIndex += jumpBytes.length;
        }

        // sub instructions
        for (InstructionToken instruction : subInstructions) {
            byte[] instrBytes = instruction.Generate();
            System.arraycopy(instrBytes, 0, bytes, bytesIndex, instrBytes.length);
            bytesIndex += instrBytes.length;
        }

        return bytes;
    }

    private Pair<MoveInstructionToken, JmpInstructionToken[]> CreateIfControlInstructions(String label, CmpValueToken cmpToken, DirectValueToken param0, DirectValueToken param1) throws InvalidOperatorException {

        MoveInstructionToken move = new MoveInstructionToken(new LabelValueToken(label), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.R3));
        JmpInstructionToken[] jumps = this.CreateJmpInstructions(cmpToken, param0, param1);

        return new Pair<>(move, jumps);
    }

    private JmpInstructionToken[] CreateJmpInstructions(CmpValueToken cmpToken, DirectValueToken param0, DirectValueToken param1) throws InvalidOperatorException {
        JmpInstructionToken[] jumps = new JmpInstructionToken[cmpToken.GetOperator().equals(CmpValueToken.ECmpOperators.EQ) ? 2 : 1];

        if (cmpToken.GetOperator().equals(CmpValueToken.ECmpOperators.EQ)) {
            jumps[0] = new JmplInstructionToken(param0, param1);
            jumps[1] = new JmpgInstructionToken(param0, param1);
        }
        else {
            jumps[0] = switch (cmpToken.GetOperator()) {
                case LESS -> new JmpgeInstructionToken(param0, param1);
                case LESS_EQ -> new JmpgInstructionToken(param0, param1);
                case GREATER_EQ -> new JmplInstructionToken(param0, param1);
                case GREATER -> new JmpleInstructionToken(param0, param1);
                default -> throw new InvalidOperatorException(name + " instruction: " + cmpToken.GetOperator().value + " cmp operator is not handled");
            };
        }
        return jumps;
    }

    private int ComputeAddressesAmount(List<InstructionToken> subInstructions, MoveInstructionToken moveInstruction, JmpInstructionToken[] jumpInstructions) {
        int subInstructionsAddressAmount = subInstructions.stream().map(this::ErrCheck).reduce(0, Integer::sum);
        int moveInstructionAddressAmount = moveInstruction.GetAmountOfAddresses();
        int jumpInstructionsAddressAmount = 0;

        for (JmpInstructionToken jump : jumpInstructions)
            jumpInstructionsAddressAmount += jump.GetAmountOfAddresses();

        return subInstructionsAddressAmount + moveInstructionAddressAmount + jumpInstructionsAddressAmount;
    }

    private int ErrCheck(InstructionToken token) {
        try {
            return token.GetAmountOfAddresses();
        } catch (Exception e) {
            System.err.println("[ERR] compiler error");
            e.printStackTrace();
            System.exit(1);
        }

        return 0;
    }


}
