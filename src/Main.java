import automata.Automata;
import lexer.Lexer;
import parser.Node;
import parser.Parser;

public class Main {
    public static void main(String[] args) throws Exception {
        String str = "abaaabb(a|b)+";
        Automata a = new Automata(str);
        return;
    }
}
