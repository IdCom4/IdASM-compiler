package com.idcom4.compiler.tokens.instructions_token.custom_instructions;


import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.utils.StringUtils;

public abstract class CustomInstructionToken extends InstructionToken {

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                SingleParamCustomInstructionToken.Parser::TryParse,
                TwoParamsCustomInstructionToken.Parser::TryParse,
                CallInstructionToken.Parser::TryParse,
                ExitInstructionToken.Parser::TryParse,
                NestedInstructionToken.Parser::TryParse,
                BlockingGetcInstructionToken.Parser::TryParse,
                RetInstructionToken.Parser::TryParse
        };

        public static ITokenParser.ParsingResult<CustomInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((CustomInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

}
