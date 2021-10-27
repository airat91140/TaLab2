package lexer;

public class Lexer {
    public boolean isEOS; // shows end of string
    private String regex;
    private int pos; // current looking position

    public Lexer(String regex) {
        this.regex = regex;
        pos = 0;
        isEOS = false;
    }

    void Error() {
        throw new IllegalArgumentException("Illegal char near " + pos);
    }

    private char peek(int i) { // method for reducing code size
        try {
            return regex.charAt(i);
        } catch (Exception ex) {
            Error();
        }
        return 0;
    }

    public Token scan() {
        if (pos >= regex.length()) isEOS = true;
        else switch (peek(pos)) {
            case '{': // probably repeat tag. // if we would have the EOS during changing of pos, that means we have syntax error
                int tmp = pos + 1;
                while (Character.isDigit(peek(tmp)))  //scanning the whole number
                    ++tmp;
                if (peek(tmp) != ',' || tmp == pos + 1) // we reached any symbol, so we need to check if it has ',' and string is not like {,y}
                    Error();
                int start = Integer.parseInt(regex.substring(pos + 1, tmp)); // pos + 1 points on first digit, tmp on ','
                pos = tmp + 1;
                int end;
                if (peek(pos) == '}') // there is no y
                    end = -1;
                else {
                    if (!Character.isDigit(peek(++tmp))) // non-digit and not '}' symbol
                        Error();
                    while (Character.isDigit(peek(tmp))) { // scanning the whole number
                        ++tmp;
                    }
                    end = Integer.parseInt(regex.substring(pos, tmp)); // pos point on first digit, tmp on the first non-digit
                    pos = tmp;
                }
                if (peek(pos++) != '}') // repeat tag must be closed by '}'
                    Error();
                return new Repeat(start, end);

            case '(':
                tmp = pos + 1;
                while (Character.isDigit(peek(tmp)))  //scanning the whole number
                    ++tmp;
                if (peek(tmp) == ':' && tmp != pos + 1) {// we reached any symbol, so we need to check if it has ':' and string is not like (:r)
                    int index = Integer.parseInt(regex.substring(pos + 1, tmp)); // pos + 1 points on first digit, tmp on ':'
                    pos = tmp + 1;
                    return new Capture(index);
                } else return new Token(peek(pos++)); // usual '('

            case '#': // escape symbol
                ++pos; // just skip it and send the escaping symbol value
                return new Escape(peek(pos++));

            case '\\': // extracting numeric group
                tmp = pos + 1;
                if (!Character.isDigit(peek(tmp)))
                    Error();
                while (Character.isDigit(peek(tmp))) // scanning num until it meets another symbol
                    ++tmp;
                int index = Integer.parseInt(regex.substring(pos + 1, tmp));
                pos = tmp;
                return new Group(index);

            default:
                if (peek(pos) >= ' ') // sending only writable symbols
                    return new Token(peek(pos++));
                else Error();
        }
        return null;
    }
}
