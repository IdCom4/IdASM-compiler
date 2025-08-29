package com.idcom4.compiler.tokens.value_tokens.custom_values;

import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.InvalidOperatorException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CmpValueToken extends CustomValueToken {

    public enum ECmpOperators {
        LESS("<"),
        LESS_EQ("<="),
        EQ("=="),
        GREATER_EQ(">="),
        GREATER(">");

        public final String value;

        ECmpOperators(String value) {
            this.value = value;
        }

        public static ECmpOperators fromString(String str) throws InvalidOperatorException {
            for (ECmpOperators operator : ECmpOperators.values()) {
                if (operator.value.equals(str)) return operator;
            }

            throw new InvalidOperatorException(str);
        }
    }

    public static class Parser {

        private static final List<ECmpOperators> sortedOperators = Arrays.stream(ECmpOperators.values()).sorted((_s1, _s2) -> _s2.value.length() - _s1.value.length()).toList();

        public static ParsingResult<CmpValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            Pair<String, Integer> result = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            int i = result.Second;
            String slice = result.First;

            ECmpOperators operator = sortedOperators.stream().filter((cmp) -> slice.startsWith(cmp.value)).findFirst().orElse(null);
            if (operator == null)
                throw new InvalidOperatorException(slice);

            CmpValueToken token = new CmpValueToken(operator);

            return new ParsingResult<>(token, i - (slice.length() - operator.value.length()));
        }
    }

    private final ECmpOperators operator;

    public CmpValueToken(ECmpOperators operator) {
        this.operator = operator;
    }

    public ECmpOperators GetOperator() {
        return this.operator;
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
