package com.idcom4.compiler.tokens.instructions_token.direct_instructions;

import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.value_tokens.custom_values.CmpValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.StringUtils;

public class JmpInstructionToken extends TwoParamsDirectInstructionToken {

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                JmpeInstructionToken.Parser::TryParse,
                JmplInstructionToken.Parser::TryParse,
                JmpleInstructionToken.Parser::TryParse,
                JmpgInstructionToken.Parser::TryParse,
                JmpgeInstructionToken.Parser::TryParse,
        };

        public static ParsingResult<JmpInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((JmpInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    public JmpInstructionToken(short opcode, DirectValueToken param0, DirectValueToken param1) {
        super(opcode, param0, param1);
    }
}
