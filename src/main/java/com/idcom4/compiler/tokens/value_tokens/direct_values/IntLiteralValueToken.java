package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntLiteralValueToken extends ImmediateValueToken {

    public static class Parser {

        private final static Pattern binaryPattern = Pattern.compile("^0b([0-1]+)");
        private final static Pattern hexadecimalPattern = Pattern.compile("^0x([0-9a-zA-Z]+)");
        private final static Pattern decimalPattern = Pattern.compile("^(-?[0-9]+)");

        public static ParsingResult<IntLiteralValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            String slice = sourceCode.substring(i);
            IntLiteralValueToken token = null;

            Matcher matcher;

            // binary
            if ((matcher = binaryPattern.matcher(slice)).find()) {
                token = new IntLiteralValueToken((short)Integer.parseInt(matcher.group(1), 2));
            }
            // hexadecimal
            else if ((matcher = hexadecimalPattern.matcher(slice)).find()) {
                token = new IntLiteralValueToken((short)Integer.parseInt(matcher.group(1), 16));
            }
            // decimal
            else if ((matcher = decimalPattern.matcher(slice)).find()) {
                token = new IntLiteralValueToken((short)Integer.parseInt(matcher.group(1),10));
            }

            if (token == null)
                throw new DiscardTokenException("invalid int literal token at: " + i + " (" + sourceCode.charAt(i) + ")");

            return new ParsingResult<>(token, i + matcher.group(0).length());
        }
    }

    private final short value;

    public IntLiteralValueToken(short value) {
        super();
        this.value = value;
    }

    @Override
    public byte[] Generate() {
        return  this.ValueToBytes(this.value);
    }
}
