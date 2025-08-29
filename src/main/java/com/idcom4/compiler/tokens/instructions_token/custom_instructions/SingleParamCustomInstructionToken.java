package com.idcom4.compiler.tokens.instructions_token.custom_instructions;


import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.value_tokens.ValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

public abstract class SingleParamCustomInstructionToken extends CustomInstructionToken {

    public static class Parser {

        public record ParsedParam(ValueToken param, int newIndex) {}

        public static ParsedParam TryParse(ITokenParser<?>[] parsers, String instructionName, String sourceCode, int index) throws CompilationException {
            Pair<String, Integer> result = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String name = result.First;
            int i = result.Second;

            if (!name.equalsIgnoreCase(instructionName))
                throw new DiscardTokenException("unexpected instruction name: " + name);

            InstructionToken.Parser.ParsedParam parsedParam = InstructionToken.Parser.TryParseParamValue(parsers, sourceCode, i);

            return new ParsedParam(parsedParam.param(), parsedParam.newIndex());
        }

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                GotoInstructionToken.Parser::TryParse,
                PushInstructionToken.Parser::TryParse,
                PopInstructionToken.Parser::TryParse,
                PrintInstructionToken.Parser::TryParse,
                IncrInstructionToken.Parser::TryParse,
                DecrInstructionToken.Parser::TryParse,
                GetcInstructionToken.Parser::TryParse
        };

        public static ITokenParser.ParsingResult<SingleParamCustomInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((SingleParamCustomInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    protected final ValueToken param;

    public SingleParamCustomInstructionToken(ValueToken param) {
        this.param = param;
    }
}
