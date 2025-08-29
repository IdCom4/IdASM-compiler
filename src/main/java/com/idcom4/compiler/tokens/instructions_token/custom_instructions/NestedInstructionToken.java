package com.idcom4.compiler.tokens.instructions_token.custom_instructions;


import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ITokenParser.ParsingResult;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.exceptions.*;
import com.idcom4.utils.StringUtils;

import java.util.List;

public abstract class NestedInstructionToken extends CustomInstructionToken {

    public static class Parser {

        private static final ITokenParser<?>[] parsers = new ITokenParser[]{
                IfInstructionToken.Parser::TryParse,
                LoopInstructionToken.Parser::TryParse,
        };

        public static ParsingResult<NestedInstructionToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            for (ITokenParser<?> parser : parsers) {
                try {
                    ITokenParser.ParsingResult<?> result = parser.TryParse(sourceCode, i);
                    return new ITokenParser.ParsingResult<>((NestedInstructionToken) result.token(), result.newIndex());
                } catch (DiscardTokenException ignored) {}
            }

            throw new DiscardTokenException("invalid token at index: " + i + " (" + StringUtils.SliceBetweenEmptySpaces(sourceCode, i).First + ")");
        }
    }

    protected final List<InstructionToken> subInstructions;

    public NestedInstructionToken(List<InstructionToken> subInstructions) {
        this.subInstructions = subInstructions;
    }

    public List<InstructionToken> GetSubInstructions() {
        return this.subInstructions;
    }

}
