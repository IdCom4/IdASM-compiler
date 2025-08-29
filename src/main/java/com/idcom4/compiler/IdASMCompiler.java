package com.idcom4.compiler;

import com.idcom4.compiler.context.Context;
import com.idcom4.compiler.tokens.ITokenParser;
import com.idcom4.compiler.tokens.ProgramToken;
import com.idcom4.exceptions.*;

public class IdASMCompiler {
    public enum EControlChars {
        OPEN_PARENTHESIS("("),
        CLOSE_PARENTHESIS(")"),
        CMP_EQUAL("=="),
        CMP_LESS_EQ("<="),
        CMP_GREATER_EQ(">="),
        CMP_LESS("<"),
        CMP_GREATER(">"),
        OPEN_BRACKET("{"),
        CLOSE_BRACKET("}"),
        COMMA(","),
        WILDCARD("*"),
        OPEN_SQUARE_BRACKET("["),
        CLOSE_SQUARE_BRACKET("]");

        public final String value;

        EControlChars(String value) {
            this.value = value;
        }

        public static EControlChars fromString(String str) throws NotAControlCharException {
            for (EControlChars ctrlChar : EControlChars.values()) {
                if (ctrlChar.value.equalsIgnoreCase(str)) return ctrlChar;
            }

            throw new NotAControlCharException(str);
        }
    }

    public enum EStaticAddresses {
        R0("R0", (short) 0x0),
        R1("R1", (short) 0x1),
        R2("R2", (short) 0x2),
        R3("R3", (short) 0x3),
        ACC0("ACC0", (short) 0x4),
        ACC1("ACC1", (short) 0x5),
        FLAGS("FLAGS", (short) 0x6),
        SPTR("SPTR", (short) 0x7),
        EXPTR("EXPTR", (short) 0x8),
        MEMEXT("MEMEXT", (short) 0x9),
        INTRC("INTRC", (short) 0xa),
        STACK("STACK", (short) 0xb),
        IN("IN", (short) 0xc),
        OUT("OUT", (short) 0xd),
        MEM("MEM", (short) 0x19);

        public final String name;
        public final short value;

        EStaticAddresses(String name, short value) {
            this.name = name;
            this.value = value;
        }

        public static EStaticAddresses fromString(String str) throws UnknownStaticAddressException {
            for (EStaticAddresses addr : EStaticAddresses.values()) {
                if (addr.name.equalsIgnoreCase(str)) return addr;
            }

            throw new UnknownStaticAddressException(str);
        }
    }

    public enum EKeywords {
        ADD("ADD"),
        SUB("SUB"),
        DIV("DIV"),
        MUL("MUL"),
        MOD("MOD"),
        AND("AND"),
        OR("OR"),
        XOR("XOR"),
        LSHFT("LSHFT"),
        RSHFT("RSHFT"),
        JMPE("JMPE"),
        JMPL("JMPL"),
        JMPLE("JMPLE"),
        JMPG("JMPG"),
        JMPGE("JMPGE"),
        INCR("INCR"),
        DECR("DECR"),
        IF("IF"),
        LOOP("LOOP"),
        DONE("DONE"),
        MOVE("MOVE"),
        INTR("INTR"),
        GOTO("GOTO"),
        PUSH("PUSH"),
        POP("POP"),
        CALL("CALL"),
        RET("RET"),
        EXIT("EXIT"),
        PRINT("PRINT"),
        GETC("GETC"),
        BGETC("BGETC"),
        FUNC("FUNC");

        public final String value;

        EKeywords(String value) {
            this.value = value;
        }

        public static EKeywords fromString(String str) throws NotAKeywordException {
            for (EKeywords keyword : EKeywords.values()) {
                if (keyword.value.equalsIgnoreCase(str)) return keyword;
            }

            throw new NotAKeywordException(str);
        }
    }

    public IdASMCompiler() {}

    public byte[] Compile(String sourceCode) throws CompilationException {
        sourceCode = this.RemoveComments(sourceCode);

        ITokenParser.ParsingResult<ProgramToken> result = ProgramToken.Parser.TryParse(sourceCode, 0);

        if (Context.INSTANCE.addressSpace.RemainingShifts() > 0) {
            System.err.println("[ERR] remaining memory shifts: " + Context.INSTANCE.addressSpace.RemainingShifts());
            System.exit(1);

        }
        return result.token().Generate();
    }

    private String RemoveComments(String sourceCode) {
        return sourceCode.replaceAll("#[^\\r\\n]*(\\R|$)", "$1");
    }


}
