package lexer;

public class Capture extends Token { // (n:r)
    private final int index; // n

    public int getIndex() {
        return index;
    }

    public Capture(int index) {
        super(Tag.CAPTURE);
        this.index = index;
    }
}
