import automata.Automata;
import lexer.Lexer;
import parser.Node;
import parser.Parser;

public class Main {
    public static void main(String[] args) throws Exception {
        String str = "ab+(a|ba)bba";
        Automata a = new Automata(str);
        a.minimize();
        System.out.println(a);
    }
}
