package parser;

import lexer.Tag;
import lexer.Token;

public class Capture extends Node {
    private Node child;
    Capture(Node child, Token t) {
        super(t);
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    @Override
    public Node clone() {
        return new Capture(child.clone(), getOp());
    }

    @Override
    public void inverse() {
        child.inverse();
    }
}
