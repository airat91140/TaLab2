package parser;

import lexer.Tag;
import lexer.Token;

public class Binary  extends Node{
    public final Node left, right;
    public Binary(Token t, Node left, Node right) {
        super(t);
        this.left = left;
        this.right = right;
    }
}
