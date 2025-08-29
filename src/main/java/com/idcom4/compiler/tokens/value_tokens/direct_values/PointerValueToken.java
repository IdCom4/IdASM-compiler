package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.exceptions.UnknownLabelException;
import com.idcom4.utils.StringUtils;

public class PointerValueToken extends AddressValueToken {

    public static class Parser {

        private final static char START_DELIMITER = '*';

        public static ParsingResult<PointerValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            if (i >= sourceCode.length() || sourceCode.charAt(i++) != START_DELIMITER)
                throw new DiscardTokenException("expected " + START_DELIMITER + " at index: " + --i + " (" + sourceCode.charAt(Math.min(i, sourceCode.length() - 1)) + ")");

            ParsingResult<DirectValueToken> recursiveResult;
            try {
                recursiveResult = DirectValueToken.Parser.TryParse(sourceCode, i);
            } catch (TokenParsingException e) {
                throw new TokenParsingException("invalid pointer value", e);
            }

            if (recursiveResult.token() instanceof MemAddressValueToken)
                throw new TokenParsingException("a pointer value is already processed as an address, at index " + i + ": " + sourceCode.substring(i, recursiveResult.newIndex()));

            PointerValueToken token = new PointerValueToken(recursiveResult.token());

            return new ParsingResult<>(token, recursiveResult.newIndex());
        }
    }

    private final DirectValueToken value;

    public PointerValueToken(DirectValueToken value) {
        super(DirectValueFlags.Pointer());
        this.value = value;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        return value.Generate();
    }
}
