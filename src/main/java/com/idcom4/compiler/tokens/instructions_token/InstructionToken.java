package com.idcom4.compiler.tokens.instructions_token;

import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.Token;
import com.idcom4.compiler.tokens.instructions_token.custom_instructions.CustomInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.DirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.ValueToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.StringUtils;

public abstract class InstructionToken extends Token {

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                DirectInstructionToken.Parser::TryParse,
                CustomInstructionToken.Parser::TryParse
        };

        public static ITokenParser.ParsingResult<InstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((InstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }

        public record ParsedParam(ValueToken param, int newIndex) {}

        public static ParsedParam TryParseParamValue(ITokenParser<?> paramParser, String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);
            ITokenParser.ParsingResult<?> result = paramParser.TryParse(sourceCode, i);
            return new ParsedParam((ValueToken) result.token(), result.newIndex());
        }

        public static ParsedParam TryParseParamValue(ITokenParser<?>[] paramParsers, String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            ITokenParser.ParsingResult<?> result = null;
            CompilationException exception = null;

            for (ITokenParser<?> parser : paramParsers) {
                try {
                    result = parser.TryParse(sourceCode, i);
                } catch (TokenParsingException | DuplicateIdentifierException | MissingClosureException e) {
                        exception = e;
                }
            }

            if (result == null) {
                if (paramParsers.length == 1 && exception != null) {
                    throw exception;
                }
                else
                    throw new TokenParsingException("unexpected token, at index " + i + ": " + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First);
            }

            return new ParsedParam((ValueToken) result.token(), result.newIndex());
        }
    }

}
