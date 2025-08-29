package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ImmediateValueToken extends DirectValueToken {


    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[] {
                IntLiteralValueToken.Parser::TryParse,
                CharValueToken.Parser::TryParse,
                LabelValueToken.Parser::TryParse
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

    public ImmediateValueToken() {
        super(DirectValueFlags.Immediate());
    }

}
