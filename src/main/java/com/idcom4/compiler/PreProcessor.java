package com.idcom4.compiler;

import com.idcom4.exceptions.PreProcessorException;
import com.idcom4.utils.Pair;
import com.idcom4.utils.Scanner;

import java.util.ArrayList;
import java.util.List;

public class PreProcessor {

    private final static char CONSTANT_DELIMITER = '$';
    private final static char CONSTANT_SPLIT = '=';

    public static String PreProcess(String sourceCode) throws PreProcessorException {
        List<Pair<String, String>> constants = new ArrayList<>();

        Scanner<Character> sourceCodeScan = new Scanner<>(sourceCode);


        while (!sourceCodeScan.IsEmpty()) {
            // skip to
            while (!sourceCodeScan.IsEmpty() && isEmptySpace(sourceCodeScan.Peek())) {
                sourceCodeScan.Consume();
            }

            if (sourceCodeScan.IsEmpty())
                break;

            if (sourceCodeScan.Consume() != CONSTANT_DELIMITER)
                break;

            // get constant identifier
            StringBuilder idBuilder = new StringBuilder(100);

            while (!sourceCodeScan.IsEmpty() && !isEmptySpace(sourceCodeScan.Peek()) && sourceCodeScan.Peek() != CONSTANT_SPLIT) {
                idBuilder.append(sourceCodeScan.Consume());
            }

            // error handling
            if (sourceCodeScan.IsEmpty())
                throw new PreProcessorException("end of file while parsing constant identifier, at index: " + sourceCodeScan.GetCursor());
            if (Character.isDigit(idBuilder.charAt(0)))
                throw new PreProcessorException("constant identifier cannot start with a digit, at index: " + (sourceCodeScan.GetCursor() - idBuilder.length()));
            if (sourceCodeScan.Consume() != CONSTANT_SPLIT)
                throw new PreProcessorException("missing " + CONSTANT_SPLIT + " at index: " + (sourceCodeScan.GetCursor() - 1) + " (" + sourceCode.charAt(sourceCodeScan.GetCursor() - 1) + ")");

            // get constant identifier
            StringBuilder valueBuilder = new StringBuilder();
            while (!sourceCodeScan.IsEmpty() && !isEmptySpace(sourceCodeScan.Peek())) {
                valueBuilder.append(sourceCodeScan.Consume());
            }
        }



        return null;
    }

    private static boolean isEmptySpace(char c) {
        return Character.isWhitespace(c) || c == '\n';
    }

}
