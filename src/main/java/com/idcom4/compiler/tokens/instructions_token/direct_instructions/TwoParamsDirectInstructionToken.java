package com.idcom4.compiler.tokens.instructions_token.direct_instructions;


import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken.Parser.ParsedParam;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

public abstract class TwoParamsDirectInstructionToken extends DirectInstructionToken {

    public static class Parser {

        public record Parsed2Params(DirectValueToken param0, DirectValueToken param1, int newIndex) {}

        public static Parsed2Params TryParse(String instructionName, String sourceCode, int index) throws CompilationException {
            Pair<String, Integer> result = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String name = result.First;
            int i = result.Second;

            if (!name.equalsIgnoreCase(instructionName))
                throw new DiscardTokenException("unexpected instruction name: " + name);

            ParsedParam parsedParam0 = InstructionToken.Parser.TryParseParamValue(ITokenParser.Cast(DirectValueToken.Parser::TryParse), sourceCode, i);
            ParsedParam parsedParam1 = InstructionToken.Parser.TryParseParamValue(ITokenParser.Cast(DirectValueToken.Parser::TryParse), sourceCode, parsedParam0.newIndex());

            return new Parsed2Params((DirectValueToken) parsedParam0.param(), (DirectValueToken) parsedParam1.param(), parsedParam1.newIndex());
        }

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                AddInstructionToken.Parser::TryParse,
                SubInstructionToken.Parser::TryParse,
                DivInstructionToken.Parser::TryParse,
                MulInstructionToken.Parser::TryParse,
                ModInstructionToken.Parser::TryParse,
                AndInstructionToken.Parser::TryParse,
                OrInstructionToken.Parser::TryParse,
                XorInstructionToken.Parser::TryParse,
                LshftInstructionToken.Parser::TryParse,
                RshftInstructionToken.Parser::TryParse,
                JmpeInstructionToken.Parser::TryParse,
                JmplInstructionToken.Parser::TryParse,
                JmpleInstructionToken.Parser::TryParse,
                JmpgInstructionToken.Parser::TryParse,
                JmpgeInstructionToken.Parser::TryParse,
                MoveInstructionToken.Parser::TryParse,
        };

        public static ITokenParser.ParsingResult<TwoParamsDirectInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((TwoParamsDirectInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    private final short instructionOpCode;
    private final DirectValueToken param0;
    private final DirectValueToken param1;

    public TwoParamsDirectInstructionToken(short instructionOpCode, DirectValueToken param0, DirectValueToken param1) {
        this.instructionOpCode = instructionOpCode;
        this.param0 = param0;
        this.param1 = param1;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.GetAmountOfAddresses() * Context.INSTANCE.byteEncoding];

        short opcode = ApplyOpCodeMasks(instructionOpCode, param0, param1);

        byte[] opcodeBytes = ValueToBytes(opcode);
        byte[] param0Bytes = this.param0.Generate();
        byte[] param1Bytes = this.param1.Generate();

        System.arraycopy(opcodeBytes, 0, bytes, 0, opcodeBytes.length);
        System.arraycopy(param0Bytes, 0, bytes, opcodeBytes.length, param0Bytes.length);
        System.arraycopy(param1Bytes, 0, bytes, param0Bytes.length + param1Bytes.length, param1Bytes.length);

        return bytes;
    }


}
