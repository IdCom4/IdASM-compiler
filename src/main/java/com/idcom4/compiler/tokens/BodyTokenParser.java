package com.idcom4.compiler.tokens;

import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.instructions_token.InstructionToken;
import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DiscardTokenException;
import com.idcom4.exceptions.EmptyScopeException;
import com.idcom4.exceptions.TokenParsingException;
import com.idcom4.utils.Pair;
import com.idcom4.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BodyTokenParser {

    private static final char OPEN_DELIMITER = '{';
    private static final char CLOSING_DELIMITER = '}';

    public static Pair<List<InstructionToken>, Integer> ParseBody(String parentName, String sourceCode, int index, int addressOffset) throws CompilationException {
        // expect '{'
        int i = StringUtils.SkipEmptySpace(sourceCode, index);

        if (i >= sourceCode.length() || sourceCode.charAt(i++) != OPEN_DELIMITER)
            throw new TokenParsingException("at index " + --i + ": '" + OPEN_DELIMITER + "' expected");

        String shiftId = Context.INSTANCE.addressSpace.AddShift((short)addressOffset);

        // parse sub instructions
        List<InstructionToken> subInstructions = new ArrayList<>();

        int endIndex;
        while ((endIndex = TryParseEnd(sourceCode, i)) < 0) {
            try {
                ITokenParser.ParsingResult<InstructionToken> result = InstructionToken.Parser.TryParse(sourceCode, i);
                subInstructions.add(result.token());

                i = result.newIndex();
            } catch (DiscardTokenException e) {
                throw new CompilationException(e.getMessage(), e.getCause());
                // throw new CompilationException("invalid token inside " + parentName + " body", e);
            }
        }

        i = endIndex;

        if (subInstructions.isEmpty())
            throw new EmptyScopeException(parentName + " has an empty body");

        // expect '}'
        i = StringUtils.SkipEmptySpace(sourceCode, i);
        if (i >= sourceCode.length() || sourceCode.charAt(i++) != CLOSING_DELIMITER)
            throw new TokenParsingException("at index " + --i + ": '" + CLOSING_DELIMITER + "' expected");

        Context.INSTANCE.addressSpace.ClaimBackShift(shiftId);


        return new Pair<>(subInstructions, i);
    }

    private static int TryParseEnd(String sourceCode, int index) {
        index = StringUtils.SkipEmptySpace(sourceCode, index);
        return sourceCode.charAt(index) == CLOSING_DELIMITER ? index : -1;
    }

}
