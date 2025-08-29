package com.idcom4.compiler.tokens;

import com.idcom4.exceptions.CompilationException;
import com.idcom4.exceptions.DuplicateIdentifierException;
import com.idcom4.exceptions.MissingClosureException;
import com.idcom4.exceptions.TokenParsingException;

public interface ITokenParser<T extends Token> {
    record ParsingResult<T> (T token, int newIndex) {}

    ParsingResult<T> TryParse(String sourceCode, int index) throws CompilationException;

    static <X extends Token> ITokenParser<X> Cast(ITokenParser parser) {
        return (String sourceCode, int index) -> {
            ParsingResult result = parser.TryParse(sourceCode, index);

            return new ParsingResult<>((X)result.token, result.newIndex);
        };
    }
}
