package lexer;

import lexer.Tag;
import lexer.Token;

public class TokenEscape extends Token { // #
    private final int val; // value after #
    public TokenEscape(char ch) {
        super(Tag.ESCAPE);
        val = ch;
    }

    public int getVal() {
        return val;
    }
}
