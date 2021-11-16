package parser;

import lexer.Tag;
import lexer.Token;

public abstract class Binary extends Node{
    public Node left;
    public Node right;
    public Binary(Token t, Node left, Node right) {
        super(t);
        this.left = left;
        this.right = right;
    }
}
