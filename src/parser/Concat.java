package parser;

import lexer.Tag;
import lexer.Token;

public class Concat extends Binary {
    public Concat(Node left, Node right) {
        super(new Token(Tag.CONCAT), left, right);
    }

    @Override
    public Node clone() {
        return new Concat(left.clone(), right.clone());
    }
}
