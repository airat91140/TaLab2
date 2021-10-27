package automata;

import lexer.Tag;
import parser.*;

import java.util.ArrayList;
import java.util.HashSet;

public class Automata {
    private Node root;
    private ArrayList<State> states;
    public Automata(Node root) {
        states = new ArrayList<>();
        this.root = root;
    }
    private boolean nullable(Node node) {
        if (node instanceof Binary) {
            if (node instanceof Concat)
                return nullable(((Concat)node).left) && nullable(((Concat)node).right);
            else if (node instanceof Or)
                return nullable(((Or)node).left) || nullable(((Or)node).right);
        }
        else if (node instanceof Literal) {
            return ((Literal)node).getOp().getTag() == Tag.EMPTY;
        }
        else if (node instanceof Repeat) {
            return nullable(((Repeat)node).left);
        }
        else if (node instanceof Positive) {
            return nullable(((Positive)node).left);
        }
        return false;
    }

    private HashSet<State> firstpos(Node node) {

    }

    private HashSet<State> lastpos(Node node) {

    }

    private HashSet<State> followpos(Node node) {

    }
}
