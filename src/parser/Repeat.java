package parser;

import lexer.Token;

public class Repeat extends Node{
    public Node left;
    public Repeat(Token t, Node left) {
        super(t);
        this.left = left;
    }

    @Override
    public Node clone() {
        return new Repeat(getOp(), left.clone());
    }

    @Override
    public void inverse() {
        left.inverse();
    }
}
