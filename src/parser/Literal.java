package parser;

import lexer.Token;

import java.util.HashSet;

public class Literal extends Node {
    private HashSet<Integer> matches;
    public Literal(Token t) {
        super(t);
        matches = new HashSet<>();
    }

    public void AddMatch(int index) {
        matches.add(index);
    }

    public HashSet<Integer> getMatches() {
        return matches;
    }

    @Override
    public Node clone() {
        Literal res = new Literal(getOp());
        res.matches.addAll(this.matches);
        return res;
    }

    @Override
    public void inverse() {
    }
}
