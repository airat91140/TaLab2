package parser;

import lexer.Token;

public class Literal extends Node {
    public final Node child;
    public Literal(Token t) {
        super(t);
        child = null;
    }

    public Literal(Token t, Node child) {
        super(t);
        this.child = child;
    }
}
