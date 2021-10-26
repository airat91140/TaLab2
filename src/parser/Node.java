package parser;

import lexer.Token;

public class Node {
    private final Token op;
    Node (Token t){
        op = t;
    }

    public Token getOp() {
        return op;
    }
}
