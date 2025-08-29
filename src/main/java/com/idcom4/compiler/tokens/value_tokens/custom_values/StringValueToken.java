package com.idcom4.compiler.tokens.value_tokens.custom_values;

import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValueToken extends CustomValueToken {

    public static class Parser {

        private final static Pattern stringPattern = Pattern.compile("^\"((?:[^\"\\\\]|\\\\[\"\\\\/bfnrt])*)\"");

        public static ParsingResult<StringValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            Matcher matcher = stringPattern.matcher(sourceCode.substring(i));

            if (!matcher.find())
                throw new TokenParsingException("invalid string, at index" + i);


            StringValueToken token = new StringValueToken(StringUtils.Unescape(matcher.group(1)));

            return new ParsingResult<>(token, i + matcher.group(0).length());
        }
    }

    private final String value;

    public StringValueToken(String value) {
        this.value = value;
    }

    public String GetValue() {
        return value;
    }

    @Override
    public int GetAmountOfAddresses() throws CustomValueException {
        throw new CustomValueException("cannot get custom values amount of addresses, they must be processed by other tokens");
    }

    @Override
    public byte[] Generate() throws CustomValueException {
        throw new CustomValueException("cannot generate custom values bytes, they must be processed by other tokens");
    }
}
