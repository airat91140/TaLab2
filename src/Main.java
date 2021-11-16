import regex.Regex;

public class Main {
    public static void main(String[] args) {
        String str = "a(1:aa)";
        Regex rx = new Regex(str), rr;
        rx.compile();
        System.out.println(rx.match("aaa"));
        System.out.println(rx.getMatch().get(1));

    }
}
//TODO check whats wrong with restoring repeated string