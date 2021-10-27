package lexer;

import java.util.Objects;

public class Token {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token)) return false;
        return tag == token.tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    private final int tag;
    public Token(int t) {tag = t;}
    public int getTag() {return tag;}
}
