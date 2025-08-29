package com.idcom4.compiler.tokens;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.instructions_token.custom_instructions.RetInstructionToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.List;

public class FunctionToken extends Token {

    public static final String name = IdASMCompiler.EKeywords.FUNC.value;

    public static class Parser {

        public static ITokenParser.ParsingResult<FunctionToken> TryParse(String sourceCode, int index) throws CompilationException {
            Pair<String, Integer> funcSliceResult = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String funcSlice = funcSliceResult.First;
            int i = funcSliceResult.Second;

            // parse func keyword
            if (!funcSlice.equalsIgnoreCase(name))
                throw new DiscardTokenException("expected " +  name + " keyword: " + funcSlice);

            // parse label
            LabelToken labelToken;
            try {
                ITokenParser.ParsingResult<LabelToken> parsedLabel = LabelToken.Parser.TryParse(sourceCode, i);
                labelToken = parsedLabel.token();
                i = parsedLabel.newIndex();
            } catch (TokenParsingException e) {
                throw new UnknownLabelException("expected function name, at index " + i, e);
            }

            if (labelToken.IsReservedTerm())
                throw new ReservedKeywordException("invalid function name at index " + (i - labelToken.GetLabel().length()) + ", " + labelToken.GetLabel() + " is a reserved keyword");

            // parse sub instructions
            Pair<List<InstructionToken>, Integer> result = BodyTokenParser.ParseBody(labelToken.GetLabel(), sourceCode, i, 0);
            List<InstructionToken> subInstructions = result.First;
            i = result.Second;

            if (!HasUnconditionalReturnInstruction(subInstructions))
                throw new NoGuaranteedReturnException("the function " + labelToken.GetLabel() + " has no guaranteed return");
            return new ITokenParser.ParsingResult<>(new FunctionToken(labelToken, subInstructions), i);
        }

        private static boolean HasUnconditionalReturnInstruction(List<InstructionToken> instructions) {
            for (InstructionToken instruction : instructions) {
                if (instruction instanceof RetInstructionToken)
                    return true;
            }

            return false;
        }
    }

    private final List<InstructionToken> subInstructions;

    private final int addressesAmount;

    public FunctionToken(LabelToken name, List<InstructionToken> subInstructions) throws DuplicateIdentifierException, CustomValueException {
        this.subInstructions = subInstructions;

        this.addressesAmount = this.ComputeAddressesAmount(subInstructions);

        // register label
        Context.INSTANCE.labels.Register(name.GetLabel(), (short)(Context.INSTANCE.addressSpace.GetCurrentAddress() - this.addressesAmount));
    }


    @Override
    public int GetAmountOfAddresses() {
        return this.addressesAmount;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addressesAmount * Context.INSTANCE.byteEncoding];

        int bytesIndex = 0;

        // sub instructions
        for (InstructionToken instruction : subInstructions) {
            byte[] instrBytes = instruction.Generate();
            System.arraycopy(instrBytes, 0, bytes, bytesIndex, instrBytes.length);
            bytesIndex += instrBytes.length;
        }

        return bytes;
    }

    private int ComputeAddressesAmount(List<InstructionToken> subInstructions) throws CustomValueException {
        return subInstructions.stream().map(this::ErrCheck).reduce(0, Integer::sum);
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
