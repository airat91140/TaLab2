package lexer;

public class Capture extends Token {
    private final int index;

    public int getIndex() {
        return index;
    }

    public Capture(int index) {
        super(Tag.CAPTURE);
        this.index = index;
    }
}
