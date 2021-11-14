package parser;

import lexer.Tag;
import lexer.Token;

public class Group extends Node {
    public Group(Token t) {
        super(t);
    }

    @Override
    public Node clone() {
        return new Group(getOp());
    }

    @Override
    public void inverse() {}
}
