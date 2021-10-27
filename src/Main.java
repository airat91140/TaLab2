import lexer.Lexer;
import parser.Node;
import parser.Parser;

public class Main {
    public static void main(String[] args) throws Exception {
        String str = "(34:56)\\34^34";
        Lexer l = new Lexer(str);
        Parser p = new Parser(l);
        p.program();
        Node n = p.getRoot();
    }
}
