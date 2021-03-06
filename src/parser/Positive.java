package parser;

import lexer.Tag;
import lexer.Token;

public class Positive extends Node {
    public Node left;
    Positive(Node node) {
        super(new Token(Tag.POSITIVE));
        left = node;
    }

    @Override
    public Node clone() {
        return new Positive(left.clone());
    }

    @Override
    public void inverse() {
        left.inverse();
    }
}
