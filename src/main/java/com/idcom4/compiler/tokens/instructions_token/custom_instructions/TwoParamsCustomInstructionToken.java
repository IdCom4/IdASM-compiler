package com.idcom4.compiler.tokens.instructions_token.custom_instructions;


import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken.Parser.ParsedParam;
import com.idcom4.compiler.tokens.value_tokens.ValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

public abstract class TwoParamsCustomInstructionToken extends CustomInstructionToken {

    public static class Parser {

        public record Parsed2Params(ValueToken param0, ValueToken param1, int newIndex) {}

        public static Parsed2Params TryParse(String instructionName, ITokenParser<?>[] param0Parsers, ITokenParser<?>[] param1Parsers, String sourceCode, int index) throws CompilationException {
            Pair<String, Integer> result = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String name = result.First;
            int i = result.Second;

            if (!name.equalsIgnoreCase(instructionName))
                throw new DiscardTokenException("unexpected instruction name: " + name);

            ParsedParam parsedParam0 = InstructionToken.Parser.TryParseParamValue(param0Parsers, sourceCode, i);
            ParsedParam parsedParam1 = InstructionToken.Parser.TryParseParamValue(param1Parsers, sourceCode, parsedParam0.newIndex());

            return new Parsed2Params(parsedParam0.param(), parsedParam1.param(), parsedParam1.newIndex());
        }

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
        };

        public static ITokenParser.ParsingResult<TwoParamsCustomInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((TwoParamsCustomInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    private final short instructionOpCode;
    private final ValueToken param0;
    private final ValueToken param1;

    public TwoParamsCustomInstructionToken(short instructionOpCode, ValueToken param0, ValueToken param1) {
        this.instructionOpCode = instructionOpCode;
        this.param0 = param0;
        this.param1 = param1;
    }

    @Override
    public int GetAmountOfAddresses() {
        return 3;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.GetAmountOfAddresses() * Context.INSTANCE.byteEncoding];


        byte[] opCodeBytes = ValueToBytes(this.instructionOpCode);
        byte[] param0Bytes = this.param0.Generate();
        byte[] param1Bytes = this.param1.Generate();

        System.arraycopy(opCodeBytes, 0, bytes, 0, opCodeBytes.length);
        System.arraycopy(param0Bytes, 0, bytes, opCodeBytes.length, param0Bytes.length);
        System.arraycopy(param1Bytes, 0, bytes, opCodeBytes.length + param0Bytes.length + 2, param1Bytes.length);

        return bytes;
    }
}
