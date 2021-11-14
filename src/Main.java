import regex.Regex;

public class Main {
    public static void main(String[] args) {
        String str = "a(a|b)";
        Regex rx = new Regex(str), rr;
        rx.compile();
        System.out.println(rx.match("a"));
        System.out.println(rx.restore());

    }
}
