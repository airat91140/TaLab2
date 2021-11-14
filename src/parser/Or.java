package parser;

import lexer.Tag;
import lexer.Token;

public class Or extends Binary{
    public Or(Node left, Node right) {
        super(new Token(Tag.OR), left, right);
    }

    @Override
    public Node clone() {
        return new Or(left.clone(), right.clone());
    }

    @Override
    public void inverse() {
        left.inverse();
        right.inverse();
    }
}
