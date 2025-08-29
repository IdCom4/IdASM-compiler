package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.custom_values.CmpValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.AddressValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.IntLiteralValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.*;

import java.util.List;

public class BlockingGetcInstructionToken extends SingleParamCustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.BGETC.value;

    public static class Parser {

        public static ParsingResult<BlockingGetcInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {

            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(ITokenParser.Cast(AddressValueToken.Parser::TryParse), name, sourceCode, index);

            if (parsedParam.param() instanceof AddressValueToken addressToken)
                return new ParsingResult<>(new BlockingGetcInstructionToken(addressToken), parsedParam.newIndex());

            throw new InvalidAddressException(name + "'s parameter must be an address where to store the popped value");
        }
    }

    private final MoveInstructionToken setDest;
    private final LoopInstructionToken loop;

    private final int addresses;

    public BlockingGetcInstructionToken(AddressValueToken dest) throws DuplicateIdentifierException, CustomValueException {
        super(dest);
        IntLiteralValueToken noInput = new IntLiteralValueToken(GetcInstructionToken.NO_INPUT);
        this.setDest = new MoveInstructionToken(noInput, dest);

        // apply address shift before initiating inner loop instructions
        CmpValueToken equal = new CmpValueToken(CmpValueToken.ECmpOperators.EQ);
        String shiftId = Context.INSTANCE.addressSpace.AddShift(LoopInstructionToken.Parser.ComputeAddressShift(equal));

        List<InstructionToken> subInstructions = List.of(new GetcInstructionToken(dest));

        // remove address shift
        Context.INSTANCE.addressSpace.ClaimBackShift(shiftId);

        this.loop = new LoopInstructionToken(
                dest, equal, noInput,
                subInstructions
        );

        this.addresses = setDest.GetAmountOfAddresses() + this.loop.GetAmountOfAddresses();
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.addresses;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addresses * Context.INSTANCE.byteEncoding];

        byte[] setBytes = this.setDest.Generate();
        byte[] loopBytes = this.loop.Generate();

        System.arraycopy(setBytes, 0, bytes, 0, setBytes.length);
        System.arraycopy(loopBytes, 0, bytes, setBytes.length, loopBytes.length);

        return bytes;
    }


}
