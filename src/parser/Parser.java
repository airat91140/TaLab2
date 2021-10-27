package parser;

import lexer.*;

import java.util.TreeMap;
/*
literal -> char || escape || ^ || (Node) || \n || (n:Node) || Literal{x,y} || Literal+
concat -> concat | literal || literal
or -> or | concat || concat
Node -> or
 */
public class Parser {
    private Lexer lexer;
    private Token look;
    private TreeMap<Integer, Node> table;
    private Node root;

    public TreeMap<Integer, Node> getTable() {
        return table;
    }

    public Node getRoot() {
        return root;
    }

    public Parser(Lexer l) {
        lexer = l;
        move();
        table = new TreeMap<>();
        root = null;
    }

    void move() {
        look = lexer.scan();
    }

    void match(int t) throws Exception {
        if (look.getTag() == t)
            move();
        else
            throw new Exception("Syntax Error");
    }

    public void program() throws Exception {
        root = node();
    }

    Node node() throws Exception {
        return or();
    }

    Node or() throws Exception {
        Node x = concat();
        while (!lexer.isEOS && look.getTag() == '|' && look.getTag() != ')') {
            move();
            x = new Or(x, concat());
        }
        return x;
    }

    Node concat() throws Exception {
        Node x = literal();
        while (!lexer.isEOS && (look.getTag() == Tag.CONCAT || look.getTag() != Tag.OR && look.getTag() != ')')) {
            if (look.getTag() == Tag.CONCAT)
                move();
            x = new Concat(x, literal());
        }
        return x;
    }

    Node positive(Node x) throws Exception {
        match('+');
        Node y = new Positive(x);
        if (lexer.isEOS)
            return y;
        if (look.getTag() == Tag.POSITIVE) {
            y = positive(y);
        }
        else if (look.getTag() == Tag.REPEAT) {
            y = repeat(y);
        }
        return y;
    }

    Node repeat(Node x) throws Exception {
        lexer.Repeat tmp = (lexer.Repeat) look;
        move();
        Node y = new Repeat(tmp, x);
        if (lexer.isEOS)
            return y;
        if (look.getTag() == Tag.POSITIVE) {
            y = positive(y);
        }
        else if (look.getTag() == Tag.REPEAT) {
            y = repeat(y);
        }
        return y;
    }

    Node literal() throws Exception {
        Node x = null;
        switch (look.getTag()) {
            case Tag.CAPTURE -> {
                int index = ((Capture) look).getIndex();
                move();
                x = node();
                match(')');
                table.put(index, x);
            }
            case '(' -> {
                move();
                x = node();
                match(')');
            }
            case Tag.GROUP -> {
                x = table.get(((Group) look).getIndex());
                move();
            }
            default -> {
                x = new Literal(look);
                move();
            }
        }
        if (lexer.isEOS)
            return x;
        if (look.getTag() == Tag.POSITIVE)
            x = positive(x);
        else if (look.getTag() == Tag.REPEAT)
            x = repeat(x);
        return x;
    }
}
