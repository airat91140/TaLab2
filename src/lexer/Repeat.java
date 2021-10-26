package lexer;

public class Repeat extends Token{
    private final int start, end;
    public Repeat(int start, int end) {
        super(Tag.REPEAT);
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
