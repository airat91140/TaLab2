package lexer;

public class TokenGroup extends Token { // \n
    private final int index; //n

    public int getIndex() {
        return index;
    }

    TokenGroup(int i) {
        super(Tag.GROUP);
        index = i;
    }
}
