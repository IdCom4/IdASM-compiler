package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;

public class CharValueToken extends ImmediateValueToken {

    public static class Parser {

        private final static char OPEN_DELIMITER = '\'';
        private final static char CLOSE_DELIMITER = '\'';

        public static ParsingResult<CharValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            if (i >= sourceCode.length() || sourceCode.charAt(i++) != OPEN_DELIMITER)
                throw new DiscardTokenException("expected " + OPEN_DELIMITER + " at index: " + --i + " (" + sourceCode.charAt(Math.min(i, sourceCode.length() - 1)) + ")");


            char value = sourceCode.charAt(i++);
            if (value == '\\') {
                String parsedEscapedChar = StringUtils.Unescape(sourceCode.substring(i - 1, i + 1));
                if (parsedEscapedChar.length() > 1)
                    throw new TokenParsingException("expected " + CLOSE_DELIMITER + " at index: " + --i + " (" + sourceCode.charAt(Math.min(i, sourceCode.length() - 1)) + ")");

                value = parsedEscapedChar.charAt(0);
                i++;
            }
            CharValueToken token = new CharValueToken(value);

            if (i >= sourceCode.length() || sourceCode.charAt(i++) != CLOSE_DELIMITER)
                throw new TokenParsingException("expected " + CLOSE_DELIMITER + " at index: " + --i + " (" + sourceCode.charAt(Math.min(i, sourceCode.length() - 1)) + ")");


            return new ParsingResult<>(token, i);
        }
    }

    private final short value;

    public CharValueToken(char c) {
        super();
        this.value = (short) c;
    }

    @Override
    public byte[] Generate() {
        return this.ValueToBytes(this.value);
    }
}
