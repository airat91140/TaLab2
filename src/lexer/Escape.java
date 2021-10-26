package lexer;

import lexer.Tag;
import lexer.Token;

public class Escape extends Token {
    private final int val;
    public Escape(char ch) {
        super(Tag.ESCAPE);
        val = ch;
    }

    public int getVal() {
        return val;
    }
}
