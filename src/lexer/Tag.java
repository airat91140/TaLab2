package lexer;

public class Tag { // class of used tags. for other symbols it is their utf8 symbol
    public static final int
            ESCAPE = '#',    EMPTY = '^',   OR = '|',        CONCAT = '.',
            POSITIVE = '+',  REPEAT = 257,  CAPTURE = 258,   GROUP = '\\', EOS = 259;
}
