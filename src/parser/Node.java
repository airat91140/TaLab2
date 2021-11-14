package parser;

import lexer.Token;

public abstract class Node {
    private final Token op;
    Node (Token t){
        op = t;
    }

    public Token getOp() {
        return op;
    }

    public abstract Node clone();

    public abstract void inverse();
}
