package parser;

import lexer.Tag;
import lexer.Token;

public class Positive extends Node {
    public final Node left;
    Positive(Node node) {
        super(new Token(Tag.POSITIVE));
        left = node;
    }
}
