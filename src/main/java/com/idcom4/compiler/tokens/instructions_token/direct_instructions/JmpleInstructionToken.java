package com.idcom4.compiler.tokens.instructions_token.direct_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.TokenParsingException;

public class JmpleInstructionToken extends JmpInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.JMPLE.value;
    public static final short opcode = (short) 0x12;

    public static class Parser {

        public static ParsingResult<JmpleInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            TwoParamsDirectInstructionToken.Parser.Parsed2Params parsed2Params =
                    TwoParamsDirectInstructionToken.Parser.TryParse(name, sourceCode, index);


            return new ParsingResult<>(new JmpleInstructionToken(parsed2Params.param0(), parsed2Params.param1()), parsed2Params.newIndex());
        }
    }

    public JmpleInstructionToken(DirectValueToken param0, DirectValueToken param1) {
        super(opcode, param0, param1);
    }
}
