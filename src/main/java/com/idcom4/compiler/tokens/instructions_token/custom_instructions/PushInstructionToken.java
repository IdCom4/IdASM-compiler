package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.AddressValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.*;

public class PushInstructionToken extends SingleParamCustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.PUSH.value;

    public static class Parser {

        public static ParsingResult<PushInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {

            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(ITokenParser.Cast(DirectValueToken.Parser::TryParse), name, sourceCode, index);

            if (parsedParam.param() instanceof DirectValueToken token)
                return new ParsingResult<>(new PushInstructionToken(token), parsedParam.newIndex());

            throw new InvalidAddressException(name + "'s parameter must be a direct value to push to the stack");
        }
    }

    private final MoveInstructionToken instruction;

    public PushInstructionToken(DirectValueToken src) {
        super(src);
        StaticAddressValueToken stackAddress = new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.STACK);
        this.instruction = new MoveInstructionToken(src, stackAddress);
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
