package com.idcom4.compiler.tokens.value_tokens.direct_values;

import com.idcom4.compiler.IdASMCompiler.EStaticAddresses;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.LabelToken;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.exceptions.UnknownLabelException;

import java.util.regex.Pattern;

public class StaticAddressValueToken extends AddressValueToken {

    public static class Parser {

        private final static Pattern staticAddressPattern = Pattern.compile("^([a-zA-Z_]+\\w*)");

        public static ParsingResult<StaticAddressValueToken> TryParse(String sourceCode, int index) throws TokenParsingException {

            try {

                // parse label
                ParsingResult<LabelToken> parsedLabel = LabelToken.Parser.TryParse(sourceCode, index);
                LabelToken labelToken = parsedLabel.token();
                index = parsedLabel.newIndex();

                if (!labelToken.IsStaticAddress())
                    throw new DiscardTokenException("invalid static address");

                EStaticAddresses address = EStaticAddresses.fromString(labelToken.GetLabel());

                return new ParsingResult<>(new StaticAddressValueToken(address), index);
            } catch (TokenParsingException ignored) {
                throw new DiscardTokenException("invalid static address");
            }
        }
    }

    private final short value;

    public StaticAddressValueToken(EStaticAddresses address) {
        super(DirectValueFlags.StaticAddress());
        this.value = address.value;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException {
        return this.ValueToBytes(this.value);
    }
}
