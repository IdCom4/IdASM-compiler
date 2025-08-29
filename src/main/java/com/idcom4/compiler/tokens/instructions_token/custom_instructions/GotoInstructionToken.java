package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.SingleParamDirectInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.*;

public class GotoInstructionToken extends SingleParamCustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.GOTO.value;

    public static class Parser {

        public static ParsingResult<GotoInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(name, sourceCode, index);

            return new ParsingResult<>(new GotoInstructionToken(parsedParam.param()), parsedParam.newIndex());
        }
    }

    private final MoveInstructionToken instruction;

    public GotoInstructionToken(DirectValueToken param) {
        super(param);
        StaticAddressValueToken param1 = new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.EXPTR);
        this.instruction = new MoveInstructionToken(param, param1);
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
