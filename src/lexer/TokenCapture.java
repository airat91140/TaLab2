package lexer;

public class TokenCapture extends Token { // (n:r)
    private final int index; // n

    public int getIndex() {
        return index;
    }

    public TokenCapture(int index) {
        super(Tag.CAPTURE);
        this.index = index;
    }
}
