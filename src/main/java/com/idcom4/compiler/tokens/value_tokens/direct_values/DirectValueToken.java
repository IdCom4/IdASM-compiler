package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.value_tokens.ValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;


public abstract class DirectValueToken extends ValueToken {

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                ImmediateValueToken.Parser::TryParse,
                AddressValueToken.Parser::TryParse,
        };

        public static ParsingResult<DirectValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((DirectValueToken) result.token(), result.newIndex());
                } catch (CompilationException ignored) {}
            }

            throw new TokenParsingException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    protected final DirectValueFlags flags;

    public DirectValueToken(DirectValueFlags flags) {
        this.flags = flags;
    }

    public DirectValueFlags GetFlags() {
        return this.flags;
    }

    public int GetAmountOfAddresses() {
        return 1;
    }

}
