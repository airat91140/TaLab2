package parser;

import lexer.Tag;
import lexer.Token;

public class Capture extends Node {
    private final Node child;
    Capture(Node child, Token t) {
        super(t);
        this.child = child;
    }

    public Node getChild() {
        return child;
    }
}
