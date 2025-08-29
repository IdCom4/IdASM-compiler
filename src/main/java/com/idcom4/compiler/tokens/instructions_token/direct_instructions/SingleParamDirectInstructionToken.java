package com.idcom4.compiler.tokens.instructions_token.direct_instructions;


import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

public abstract class SingleParamDirectInstructionToken extends DirectInstructionToken {

    public static class Parser {

        public record ParsedParam(DirectValueToken param, int newIndex) {}

        public static ParsedParam TryParse(String instructionName, String sourceCode, int index) throws CompilationException {
            return TryParse(ITokenParser.Cast(DirectValueToken.Parser::TryParse), instructionName, sourceCode, index);
        }

        public static ParsedParam TryParse(ITokenParser<?> parser, String instructionName, String sourceCode, int index) throws CompilationException {
            Pair<String, Integer> result = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String name = result.First;
            int i = result.Second;

            if (!name.equalsIgnoreCase(instructionName))
                throw new DiscardTokenException("unexpected instruction name: " + name);


            InstructionToken.Parser.ParsedParam parsedParam = InstructionToken.Parser.TryParseParamValue(parser,sourceCode, i);

            return new ParsedParam((DirectValueToken) parsedParam.param(), parsedParam.newIndex());
        }

        public static ITokenParser.ParsingResult<SingleParamDirectInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            try {
                ITokenParser.ParsingResult<IntrInstructionToken> result = IntrInstructionToken.Parser.TryParse(sourceCode, i);
                return new ITokenParser.ParsingResult<>(result.token(), result.newIndex());
            } catch (DiscardTokenException ignored) {}

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    protected final short instructionOpCode;
    protected final DirectValueToken param;

    public SingleParamDirectInstructionToken(short instructionOpCode, DirectValueToken param) {
        this.instructionOpCode = instructionOpCode;
        this.param = param;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.GetAmountOfAddresses() * Context.INSTANCE.byteEncoding];

        short opcode = ApplyOpCodeMasks(instructionOpCode, param, null);

        byte[] opcodeBytes = ValueToBytes(opcode);
        byte[] param0Bytes = this.param.Generate();
        byte[] param1Bytes = ValueToBytes((short) 0);

        System.arraycopy(opcodeBytes, 0, bytes, 0, opcodeBytes.length);
        System.arraycopy(param0Bytes, 0, bytes, opcodeBytes.length, param0Bytes.length);
        System.arraycopy(param1Bytes, 0, bytes, param0Bytes.length + param1Bytes.length, param1Bytes.length);

        return bytes;
    }

}
