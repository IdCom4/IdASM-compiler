package com.idcom4.compiler.tokens.instructions_token.custom_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.direct_instructions.IntrInstructionToken;
import com.idcom4.compiler.tokens.value_tokens.direct_values.IntLiteralValueToken;
import com.idcom4.exceptions.CustomValueException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.exceptions.UnknownLabelException;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

public class  ExitInstructionToken extends CustomInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.EXIT.value;

    public static class Parser {

        public static ParsingResult<ExitInstructionToken> TryParse(String sourceCode, int index) throws TokenParsingException {
            Pair<String, Integer> instrNameResult = StringUtils.SliceBetweenEmptySpaces(sourceCode, index);
            String instrName = instrNameResult.First;
            int i = instrNameResult.Second;

            // parse name
            if (!instrName.equalsIgnoreCase(name)) {
                throw new DiscardTokenException("unexpected instruction name: " + instrName);
            }

            return new ParsingResult<>(new ExitInstructionToken(), i);
        }
    }

    private final IntrInstructionToken instruction;

    public ExitInstructionToken() {
        this.instruction = new IntrInstructionToken(new IntLiteralValueToken((short)1));
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
