package lexer;

public class Lexer {
    public boolean isEOS;
    private String regex;
    private int pos;
    private char peek(int i) {
        return regex.charAt(i);
    }
    public Lexer(String regex) {
        this.regex = regex;
        pos = 0;
        isEOS = false;
    }
    public Token scan() {
        try {
            if (pos >= regex.length()) isEOS = true;
            else switch (peek(pos)) {
                case '{':
                    int tmp = pos + 1;
                    while (Character.isDigit(peek(tmp)))
                        ++tmp;
                    if (peek(tmp) != ',' || tmp == pos + 1)
                        throw new Exception();
                    int start = Integer.parseInt(regex.substring(pos + 1, tmp));
                    pos = tmp + 1;
                    int end;
                    if (peek(tmp + 1) == '}')
                        end = -1;
                    else {
                        if(!Character.isDigit(peek(++tmp)))
                            throw new Exception();
                        while (Character.isDigit(peek(tmp))) {
                            ++tmp;
                        }
                        end = Integer.parseInt(regex.substring(pos, tmp));
                        pos = tmp;
                    }
                    if (peek(pos++) != '}')
                        throw new Exception();
                    return new Repeat(start, end);

                case '(':
                    if (Character.isDigit(peek(pos + 1)) && peek(pos + 2) == ':') {
                        pos = pos + 3;
                        int index = Character.getNumericValue(peek(pos - 2));
                        return new Capture(index);
                    }
                    else return new Token(peek(pos++));

                case '#':
                    ++pos;
                    return new Escape(peek(pos++));

                case '\\':
                    ++pos;
                    return new Group(Character.getNumericValue(peek(pos++)));

                default:
                    if (peek(pos) >= ' ')
                        return new Token(peek(pos++));
                    else throw new Exception();
            }
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Illegal char at " + pos);
        }
        return null;
    }
}
