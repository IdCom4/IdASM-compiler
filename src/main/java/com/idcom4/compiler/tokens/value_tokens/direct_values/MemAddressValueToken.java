package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.exceptions.UnknownLabelException;
import com.idcom4.utils.StringUtils;

public class MemAddressValueToken extends AddressValueToken {

    public static class Parser {

        private final static char OPEN_DELIMITER = '[';
        private final static char CLOSE_DELIMITER = ']';

        public static ParsingResult<MemAddressValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            if (i >= sourceCode.length() || sourceCode.charAt(i++) != OPEN_DELIMITER)
                throw new DiscardTokenException("expected " + OPEN_DELIMITER + " at index: " + --i + " (" + sourceCode.charAt(Math.min(i, sourceCode.length() - 1)) + ")");

            ParsingResult<DirectValueToken> recursiveResult;
            try {
                recursiveResult = ImmediateValueToken.Parser.TryParse(sourceCode, i);
            } catch (TokenParsingException e) {
                throw new TokenParsingException("invalid address value", e);
            }

            i = StringUtils.SkipEmptySpace(sourceCode, recursiveResult.newIndex());
            if (i >= sourceCode.length() || sourceCode.charAt(i++) != CLOSE_DELIMITER)
                throw new TokenParsingException("expected " + CLOSE_DELIMITER + " at index: " + --i + " (" + sourceCode.charAt(Math.min(i, sourceCode.length() - 1)) + ")");

            MemAddressValueToken token = new MemAddressValueToken(recursiveResult.token());

            return new ParsingResult<>(token, i);
        }
    }

    private final DirectValueToken value;

    public MemAddressValueToken(DirectValueToken value) {
        super(
                value instanceof PointerValueToken
                    ? new DirectValueFlags(false, true, true, false)
                    : DirectValueFlags.MemAddress()
        );

        this.value = value;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        return value.Generate();
    }
}
