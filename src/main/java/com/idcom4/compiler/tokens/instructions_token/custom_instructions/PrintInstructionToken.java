package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.DirectInstructionToken;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.MoveInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.ValueToken;
import com.idcom4.compiler.tokens.value_tokens.custom_values.StringValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.CharValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.StaticAddressValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.UnknownLabelException;

import java.util.Arrays;

public class PrintInstructionToken extends SingleParamCustomInstructionToken {

    public static final char PRINT_SIG = (char) -1;

    public static final String name = IdASMCompiler.EKeywords.PRINT.value;

    public static class Parser {

        public static ParsingResult<PrintInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            SingleParamCustomInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamCustomInstructionToken.Parser.TryParse(new ITokenParser[] {
                            DirectValueToken.Parser::TryParse,
                            StringValueToken.Parser::TryParse
                    }, name, sourceCode, index);

            ValueToken token = parsedParam.param();

            if (token instanceof StringValueToken stringToken)
                return new ParsingResult<>(new PrintInstructionToken(stringToken), parsedParam.newIndex());
            if (token instanceof DirectValueToken directValueToken)
                return new ParsingResult<>(new PrintInstructionToken(directValueToken), parsedParam.newIndex());

            throw new CompilationException("invalid value for " + name + " instruction");
        }
    }

    private final MoveInstructionToken[] instructions;
    private final int addressesAmount;

    public PrintInstructionToken(DirectValueToken param) {
        super(param);
        this.instructions = this.CreateSingleValueInstructions(param);
        this.addressesAmount = Arrays.stream(this.instructions).map(DirectInstructionToken::GetAmountOfAddresses).reduce(0, Integer::sum);
    }

    public PrintInstructionToken(StringValueToken param) {
        super(param);
        this.instructions = this.CreateStringInstructions(param.GetValue());
        this.addressesAmount = Arrays.stream(this.instructions).map(DirectInstructionToken::GetAmountOfAddresses).reduce(0, Integer::sum);
    }

    @Override
    public int GetAmountOfAddresses() {
        return this.addressesAmount;
    }

    @Override
    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addressesAmount * Context.INSTANCE.byteEncoding];

        int bytesIndex = 0;

        for (MoveInstructionToken instruction : instructions) {
            byte[] instrBytes = instruction.Generate();
            System.arraycopy(instrBytes, 0, bytes, bytesIndex, instrBytes.length);
            bytesIndex += instrBytes.length;
        }

        return bytes;
    }

    private MoveInstructionToken[] CreateSingleValueInstructions(DirectValueToken token)  {

        MoveInstructionToken[] instructions = new MoveInstructionToken[2];

        instructions[0] = new MoveInstructionToken(token, new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.OUT));

        instructions[1] = new MoveInstructionToken(new CharValueToken(PRINT_SIG), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.OUT));

        return instructions;
    }

    private MoveInstructionToken[] CreateStringInstructions(String value)  {

        MoveInstructionToken[] instructions = new MoveInstructionToken[value.length() + 1];

        for (int i = 0; i < value.length(); i++) {
            instructions[i] = new MoveInstructionToken(new CharValueToken(value.charAt(i)), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.OUT));
        }

        instructions[value.length()] = new MoveInstructionToken(new CharValueToken(PRINT_SIG), new StaticAddressValueToken(IdASMCompiler.EStaticAddresses.OUT));

        return instructions;
    }


}
