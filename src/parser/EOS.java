package parser;

import lexer.Tag;
import lexer.Token;

public class EOS extends Literal {
    public EOS() {
        super(new Token(Tag.EOS));
    }

    @Override
    public Node clone() {
        return new EOS();
    }
}
