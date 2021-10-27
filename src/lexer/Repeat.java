package lexer;

public class Repeat extends Token{ // r{x, y} if y is not assigned, it puts -1 to end
    private final int start; // x
    private final int end; // y
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
