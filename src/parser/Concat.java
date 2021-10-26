package parser;

import lexer.Tag;
import lexer.Token;

public class Concat extends Binary {
    public Concat(Node left, Node right) {
        super(new Token(Tag.CONCAT), left, right);
    }
}
