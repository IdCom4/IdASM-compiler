package com.idcom4.compiler.tokens;

import com.idcom4.compiler.context.Context;
import com.idcom4.exceptions.*;
import com.idcom4.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProgramToken extends Token {

    public static class Parser {
        public static ITokenParser.ParsingResult<ProgramToken> TryParse(String sourceCode, int index) throws CompilationException {
            int i = StringUtils.SkipEmptySpace(sourceCode, index);

            // parse sub instructions
            List<FunctionToken> functions = new ArrayList<>();

            int mem = i;
            while (i < sourceCode.length()) {
                ITokenParser.ParsingResult<FunctionToken> result = FunctionToken.Parser.TryParse(sourceCode, i);
                functions.add(result.token());
                i = StringUtils.SkipEmptySpace(sourceCode, result.newIndex());

                if (i == mem) break;
                mem = i;
            }

            return new ITokenParser.ParsingResult<>(new ProgramToken(functions), i);
        }
    }

    private final List<FunctionToken> functions;
    private final int addressesAmount;

    public ProgramToken(List<FunctionToken> functions) {
        this.functions = functions;

        this.addressesAmount = functions.stream().map(FunctionToken::GetAmountOfAddresses).reduce(0, Integer::sum);
    }

    public int GetAmountOfAddresses() {
        return this.addressesAmount;
    }

    public byte[] Generate() throws UnknownLabelException, CustomValueException {
        byte[] bytes = new byte[this.addressesAmount * Context.INSTANCE.byteEncoding];

        int byteIndex = 0;

        for (FunctionToken function : this.functions) {
            byte[] funcBytes = function.Generate();
            System.arraycopy(funcBytes, 0, bytes, byteIndex, funcBytes.length);
            byteIndex += funcBytes.length;
        }

        return bytes;
    }

}
