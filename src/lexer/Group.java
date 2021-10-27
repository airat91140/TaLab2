package lexer;

public class Group extends Token { // \n
    private final int index; //n

    public int getIndex() {
        return index;
    }

    Group(int i) {
        super(Tag.GROUP);
        index = i;
    }
}
