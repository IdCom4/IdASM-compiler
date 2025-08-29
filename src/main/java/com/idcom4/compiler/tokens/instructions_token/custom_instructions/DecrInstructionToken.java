package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.AddInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SubInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.AddressValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.IntLiteralValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.InvalidAddressException;
import com.idcom4.exceptions.UnknownLabelException;

public class DecrInstructionToken extends SingleParamCustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.DECR.value;

    public static class Parser {

        public static ParsingResult<DecrInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(ITokenParser.Cast(AddressValueToken.Parser::TryParse), name, sourceCode, index);

            if (parsedParam.param() instanceof AddressValueToken addressToken)
                return new ParsingResult<>(new DecrInstructionToken(addressToken), parsedParam.newIndex());

            throw new InvalidAddressException(name + "'s parameter must be an address of which value to decrement");
        }
    }

    private final SubInstructionToken sub;
    private final MoveInstructionToken move;

    private final int addresses;

    public DecrInstructionToken(AddressValueToken param) {
        super(param);

        this.sub = new SubInstructionToken(param, new IntLiteralValueToken((short)1));
        this.move = new MoveInstructionToken(new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.ACC0), param);

        this.addresses = this.sub.GetAmountOfAddresses() + this.move.GetAmountOfAddresses();
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.addresses;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addresses * Context.INSTANCE.byteEncoding];

        int byteIndex = 0;

        byte[] subBytes = this.sub.Generate();
        System.arraycopy(subBytes, 0, bytes, byteIndex, subBytes.length);

        byteIndex += subBytes.length;

        byte[] moveBytes = this.move.Generate();
        System.arraycopy(moveBytes, 0, bytes, byteIndex, moveBytes.length);


        return bytes;
    }

}
