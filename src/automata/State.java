package automata;

import lexer.Tag;
import parser.Literal;

import java.util.*;

public class State {
    private HashMap<Integer, State> transitions; // first is symbol, second is id
    int id;
    Set<Literal> internal;

    public boolean isFinal() {
        return internal.stream().anyMatch(lit -> lit.getOp().getTag() == Tag.EOS);
    }

    public State nextState(int c) {
        return transitions.get(c);
    }

    public State(int id, Set <Literal> poses) {
        this.id = id;
        transitions = new HashMap<>();
        internal =   new HashSet<>(poses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State state)) return false;
        return internal.equals(state.internal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internal);
    }

    public HashMap<Integer, State> getTransitions() {
        return transitions;
    }

    public int getId() {
        return id;
    }

    public Set<Literal> getInternal() {
        return internal;
    }

    public void addTransition(int symbol, State state) {
        transitions.put(symbol, state);
    }
}
