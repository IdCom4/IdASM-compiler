package com.idcom4.compiler.tokens.instructions_token.direct_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DuplicateIdentifierException;
import com.idcom4.exceptions.MissingClosureException;
import com.idcom4.exceptions.TokenParsingException;

public class IntrInstructionToken extends SingleParamDirectInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.INTR.value;
    public static final short opcode = (short) 0x21;

    public static class Parser {

        public static ParsingResult<IntrInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            SingleParamDirectInstructionToken.Parser.ParsedParam parsedParam =
                    SingleParamDirectInstructionToken.Parser.TryParse(name, sourceCode, index);

            return new ParsingResult<>(new IntrInstructionToken(parsedParam.param()), parsedParam.newIndex());
        }
    }

    public IntrInstructionToken(DirectValueToken param) {
        super(opcode, param);
    }
}
