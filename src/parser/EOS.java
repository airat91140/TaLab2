package parser;

import lexer.Tag;
import lexer.Token;

public class EOS extends Node{
    EOS() {
        super(new Token(Tag.EOS));
    }
}
