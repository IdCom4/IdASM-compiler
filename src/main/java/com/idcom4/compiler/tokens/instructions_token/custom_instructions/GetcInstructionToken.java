package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.AddressValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.InvalidAddressException;
import com.idcom4.exceptions.UnknownLabelException;

public class GetcInstructionToken extends SingleParamCustomInstructionToken {

    public static final short NO_INPUT = 0;
    public static final String name = IdASMCompiler.EKeywords.GETC.value;

    public static class Parser {

        public static ParsingResult<GetcInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {

            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(ITokenParser.Cast(AddressValueToken.Parser::TryParse), name, sourceCode, index);

            if (parsedParam.param() instanceof AddressValueToken addressToken)
                return new ParsingResult<>(new GetcInstructionToken(addressToken), parsedParam.newIndex());

            throw new InvalidAddressException(name + "'s parameter must be an address where to store the popped value");
        }
    }

    private final MoveInstructionToken instruction;

    public GetcInstructionToken(AddressValueToken dest) {
        super(dest);
        StaticAddressValueToken src = new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.IN);
        this.instruction = new MoveInstructionToken(src, dest);
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.instruction.GetAmountOfAddresses();
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        return this.instruction.Generate();
    }


}
