import regex.Regex;

public class Main {
    public static void main(String[] args) {
        Regex r1 = new Regex("ab");
        r1.compile();
        System.out.println(r1.inverse().match("ba"));
    }
}