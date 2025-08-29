package com.idcom4.compiler.tokens.instructions_token.direct_instructions;

import com.idcom4.compiler.IdASMCompiler;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.value_tokens.direct_values.DirectValueToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DuplicateIdentifierException;
import com.idcom4.exceptions.MissingClosureException;
import com.idcom4.exceptions.TokenParsingException;

public class MoveInstructionToken extends TwoParamsDirectInstructionToken {

    public static final String name = IdASMCompiler.EKeywords.MOVE.value;
    public static final short opcode = (short) 0x20;

    public static class Parser {

        public static ParsingResult<MoveInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            TwoParamsDirectInstructionToken.Parser.Parsed2Params parsed2Params =
                    TwoParamsDirectInstructionToken.Parser.TryParse(name, sourceCode, index);


            return new ParsingResult<>(new MoveInstructionToken(parsed2Params.param0(), parsed2Params.param1()), parsed2Params.newIndex());
        }
    }

    public MoveInstructionToken(DirectValueToken param0, DirectValueToken param1) {
        super(opcode, param0, param1);
    }
}
