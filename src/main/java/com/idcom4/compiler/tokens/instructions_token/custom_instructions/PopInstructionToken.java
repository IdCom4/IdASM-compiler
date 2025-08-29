package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.*;
import com.idcom4.exceptions.*;

import java.util.Arrays;

public class PopInstructionToken extends SingleParamCustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.POP.value;

    public static class Parser {

        public static ParsingResult<PopInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {

            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(ITokenParser.Cast(AddressValueToken.Parser::TryParse), name, sourceCode, index);

            if (parsedParam.param() instanceof AddressValueToken addressToken)
                return new ParsingResult<>(new PopInstructionToken(addressToken), parsedParam.newIndex());

            throw new InvalidAddressException(name + "'s parameter must be an address where to store the popped value");
        }
    }

    private final MoveInstructionToken instruction;

    public PopInstructionToken(AddressValueToken dest) {
        super(dest);
        StaticAddressValueToken stackAddress = new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.STACK);
        this.instruction = new MoveInstructionToken(stackAddress, dest);
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
