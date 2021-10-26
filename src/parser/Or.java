package parser;

import lexer.Tag;
import lexer.Token;

public class Or extends Binary{
    public Or(Node left, Node right) {
        super(new Token(Tag.OR), left, right);
    }
}
