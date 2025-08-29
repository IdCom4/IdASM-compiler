package com.idcom4.compiler.tokens.value_tokens.custom_values;

import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.value_tokens.ValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.*;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DuplicateIdentifierException;
import com.idcom4.exceptions.MissingClosureException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;


public abstract class CustomValueToken extends ValueToken {

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                StringValueToken.Parser::TryParse,
        };

        public static ParsingResult<CustomValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ParsingResult<>((CustomValueToken) result.token(), result.newIndex());
                } catch (CompilationException ignored) {}
            }

            throw new TokenParsingException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

}
