package com.idcom4.utils;

public class StringUtils {

    public static boolean isEmptySpace(char c) {
        return Character.isWhitespace(c) || c == '\n';
    }

    public static int SkipEmptySpace(String str, int index) {
        while (index < str.length() && StringUtils.isEmptySpace(str.charAt(index))) {
            index++;
        }

        return index;
    }

    public static int SkipUntilEmptySpace(String str, int index) {
        while (index < str.length() && !StringUtils.isEmptySpace(str.charAt(index))) {
            index++;
        }

        return index;
    }

    public static int SkipUntil(String str, char[] delimiters, int index) {
        while (index < str.length()) {
            for (char delimiter : delimiters) {
                if (str.charAt(index) == delimiter) return index;
            }

            index++;
        }

        return index;
    }

    public static int SkipUntil(String str, char delimiter, int index) {
        return SkipUntil(str, new char[] { delimiter }, index);
    }

    public static Pair<String, Integer> SliceBetweenEmptySpaces(String str, int index) {
        index = StringUtils.SkipEmptySpace(str, index);
        int end = StringUtils.SkipUntilEmptySpace(str, index);

        return new Pair<>(str.substring(index, end), end);
    }

    public static String Unescape(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case 'n': result.append('\n'); break;
                    case 't': result.append('\t'); break;
                    case 'r': result.append('\r'); break;
                    case 'b': result.append('\b'); break;
                    case 'f': result.append('\f'); break;
                    case '\\': result.append('\\'); break;
                    case '"': result.append('\"'); break;
                    case '\'': result.append('\''); break;
                    default: result.append(next); break;
                }
                i++; // skip next char
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
