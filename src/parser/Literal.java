package parser;

import lexer.Token;

public class Literal extends Node {
    public Literal(Token t) {
        super(t);
    }

    @Override
    public Node clone() {
        return new Literal(getOp());
    }

    @Override
    public void inverse() {
    }
}
