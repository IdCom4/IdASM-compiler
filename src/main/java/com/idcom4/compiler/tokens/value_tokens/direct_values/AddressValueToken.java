package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;

public abstract class AddressValueToken extends DirectValueToken {


    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                MemAddressValueToken.Parser::TryParse,
                PointerValueToken.Parser::TryParse,
                StaticAddressValueToken.Parser::TryParse
        };

        public static ParsingResult<DirectValueToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ParsingResult<>((DirectValueToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new TokenParsingException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    public AddressValueToken(DirectValueFlags flags) {
        super(flags);
    }

}
