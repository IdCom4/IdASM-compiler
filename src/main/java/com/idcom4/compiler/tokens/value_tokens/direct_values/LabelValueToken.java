package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.LabelToken;
import com.idcom4.exceptions.ReservedKeywordException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.exceptions.UnknownLabelException;

public class LabelValueToken extends ImmediateValueToken {

    public static class Parser {

        public static ParsingResult<LabelValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            // parse label
            ParsingResult<LabelToken> parsedLabel = LabelToken.Parser.TryParse(sourceCode, index);
            LabelToken labelToken = parsedLabel.token();
            index = parsedLabel.newIndex();

            if (labelToken.IsReservedTerm())
                throw new ReservedKeywordException("at index " + (index - labelToken.GetLabel().length()) + ": " + labelToken.GetLabel());

            return new ParsingResult<>(new LabelValueToken(labelToken.GetLabel()), index);
        }
    }

    private final String label;

    public LabelValueToken(String label) {
        super();
        this.label = label;
        Context.INSTANCE.addressSpace.RegisterRelativeAddress(Context.INSTANCE.addressSpace.GetCurrentAddress());
    }

    @Override
    public byte[] Generate() throws UnknownLabelException {
        return this.ValueToBytes(Context.INSTANCE.labels.GetAddress(this.label));
    }
}
