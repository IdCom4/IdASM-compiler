package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.AddInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.*;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.InvalidAddressException;
import com.idcom4.exceptions.UnknownLabelException;

public class IncrInstructionToken extends SingleParamCustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.INCR.value;

    public static class Parser {

        public static ParsingResult<IncrInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {

            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(ITokenParser.Cast(AddressValueToken.Parser::TryParse), name, sourceCode, index);

            if (parsedParam.param() instanceof AddressValueToken addressToken)
                return new ParsingResult<>(new IncrInstructionToken(addressToken), parsedParam.newIndex());

            throw new InvalidAddressException(name + "'s parameter must be an address of which value to increment");
        }
    }

    private final AddInstructionToken add;
    private final MoveInstructionToken move;

    private final int addresses;

    public IncrInstructionToken(AddressValueToken param) {
        super(param);

        if (param.GetFlags().isImmediate()) {
            System.err.println("[ERR] compiler error, " + name);
            System.exit(1);
        }

        this.add = new AddInstructionToken(param, new IntLiteralValueToken((short)1));
        this.move = new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.ACC0), param);

        this.addresses = this.add.GetAmountOfAddresses() + this.move.GetAmountOfAddresses();
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.addresses;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addresses * Context.INSTANCE.byteEncoding];

        int byteIndex = 0;

        byte[] addBytes = this.add.Generate();
        System.arraycopy(addBytes, 0, bytes, byteIndex, addBytes.length);

        byteIndex += addBytes.length;

        byte[] moveBytes = this.move.Generate();
        System.arraycopy(moveBytes, 0, bytes, byteIndex, moveBytes.length);


        return bytes;
    }

}
