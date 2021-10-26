package lexer;

public class Group extends Token {
    private final int index;

    public int getIndex() {
        return index;
    }

    Group(int i) {
        super(Tag.GROUP);
        index = i;
    }
}
