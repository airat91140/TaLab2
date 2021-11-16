import regex.Regex;

public class Main {
    public static void main(String[] args) {
        String str = "b{2,}";
        Regex rx = new Regex(str), rr;
        rx.compile();
        System.out.println(rx.match("b"));
        System.out.println(rx.match("bb"));
        System.out.println(rx.match("bbb"));
        System.out.println(rx.match("bbbb"));
        System.out.println(rx.restore());

    }
}
