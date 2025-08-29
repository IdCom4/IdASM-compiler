package com.idcom4.compiler.tokens;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.NotAKeywordException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.exceptions.UnknownStaticAddressException;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelToken extends Token {

    public static class Parser {

        public final static Pattern labelPattern = Pattern.compile("^([a-zA-Z_]+\\w*)");

        public static ITokenParser.ParsingResult<LabelToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            Pair<String, Integer> labelResult = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String label = labelResult.First;
            int i = labelResult.Second;

            Matcher match = labelPattern.matcher(label);
            if (!match.find())
                throw new TokenParsingException("expected label, at index " + (i - label.length()) + ": " + label);

            int newIndex = i - (label.length() - match.group(0).length());
            return new ITokenParser.ParsingResult<>(new LabelToken(match.group(0)), newIndex);
        }
    }

    private final String label;

    public LabelToken(String label) {
        this.label = label;
    }

    public String GetLabel() {
        return this.label;
    }

    @Override
    public int GetAmountOfAddresses() throws CustomValueException {
        throw new CustomValueException("cannot get custom values amount of addresses, they must be processed by other tokens");
    }

    @Override
    public byte[] Generate() throws CustomValueException {
        throw new CustomValueException("cannot generate custom values bytes, they must be processed by other tokens");
    }

    public boolean IsReservedTerm() {
        return IsKeyword() || IsStaticAddress();
    }

    public boolean IsKeyword() {
        try {
            IdASMCompiler.EKeywords.fromString(this.label);
        } catch (NotAKeywordException e) {
            return false;
        }

        return true;
    }

    public boolean IsStaticAddress() {
        try {
            IdASMCompiler.EStaticAddresses.fromString(this.label);
        } catch (UnknownStaticAddressException e) {
            return false;
        }

        return true;
    }
}
