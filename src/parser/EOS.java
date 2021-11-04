package parser;

import lexer.Tag;
import lexer.Token;

public class EOS extends Literal {
    EOS() {
        super(new Token(Tag.EOS));
    }
}
