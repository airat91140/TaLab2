package automata;

import lexer.Tag;
import parser.*;

import java.util.*;

public class Automata {
    private Node root;
    private ArrayList<State> states;
    private IdentityHashMap<Literal, Set<Literal>> table;
    public Automata(Node root) {
        states = new ArrayList<>();
        this.root = root;
        table = new IdentityHashMap<>();
    }
    private void put(Literal lit, Set<Literal> follow) {
        if (!table.containsKey(lit))
            table.put(lit, Collections.newSetFromMap(new IdentityHashMap<>()));
        table.get(lit).addAll(follow);

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
            if (((lexer.Repeat)node.getOp()).getStart() == 0)
                return true;
            else
                return nullable(((Repeat)node).left);
        }
        else if (node instanceof Positive) {
            return nullable(((Positive)node).left);
        }
        return false;
    }

    private Set<Literal> firstpos(Node node, Set<Literal> set) {
        if (set == null) //first launch
            set = Collections.newSetFromMap(new IdentityHashMap<>());
        if (node instanceof Binary) {
            if (node instanceof Concat)
                if (nullable(((Concat) node).left)) {
                    firstpos(((Concat) node).left, set);
                    firstpos(((Concat)node).right, set);
                }
                else firstpos(((Concat) node).left, set);
            else if (node instanceof Or) {
                firstpos(((Or) node).left, set);
                firstpos(((Or) node).right, set);
            }
        }
        else if (node instanceof Literal) {
            if (((Literal)node).getOp().getTag() != Tag.EMPTY)
                set.add((Literal) node);
        }
        else if (node instanceof Repeat) {
            firstpos(((Repeat) node).left, set);
        }
        else if (node instanceof Positive) {
            firstpos(((Positive) node).left, set);
        }
        return set;
    }

    private Set<Literal> lastpos(Node node, Set<Literal> set) {
        if (set == null) //first launch
            set = Collections.newSetFromMap(new IdentityHashMap<>());
        if (node instanceof Binary) {
            if (node instanceof Concat)
                if (nullable(((Concat) node).right)) {
                    lastpos(((Concat) node).left, set);
                    lastpos(((Concat)node).right, set);
                }
                else lastpos(((Concat) node).right, set);
            else if (node instanceof Or) {
                lastpos(((Or) node).left, set);
                lastpos(((Or) node).right, set);
            }
        }
        else if (node instanceof Literal) {
            if (((Literal)node).getOp().getTag() != Tag.EMPTY)
                set.add((Literal) node);
        }
        else if (node instanceof Repeat) {
            lastpos(((Repeat) node).left, set);
        }
        else if (node instanceof Positive) {
            lastpos(((Positive) node).left, set);
        }
        return set;
    }

    private void followpos(Node node) {
        if (node instanceof Concat) {
            followpos(((Concat) node).left);
            Set<Literal> leftSet = lastpos(((Concat) node).left, null);
            Set<Literal> rightSet = firstpos(((Concat) node).right, null);
            for (Literal it : leftSet)
                put(it, rightSet);
            followpos(((Concat) node).right);
        } else if (node instanceof Positive) {
            followpos(((Positive) node).left);
            Set<Literal> lastSet = lastpos(((Positive) node).left, null);
            Set<Literal> firstSet = firstpos(((Positive) node).left, null);
            for (Literal it : lastSet)
                put(it, firstSet);
        } else if (node instanceof Repeat) {
            followpos(((Repeat) node).left);
            Set<Literal> lastSet = lastpos(((Repeat) node).left, null);
            Set<Literal> firstSet = firstpos(((Repeat) node).left, null);
            for (Literal it : lastSet)
                put(it, firstSet);
        }
    }

    private Set<Literal> followpos(Literal literal) {
        return table.get(literal);
    }
}
