package automata;

import java.util.HashMap;

public class State {
    private HashMap<Character, Integer> transitions;
    int id;

    public State(int id) {
        this.id = id;
        transitions = new HashMap<>();
    }

    public void addTransition(char symbol, State state) {
        transitions.put(symbol, state.id);
    }

    public void addTransition(char symbol, int id) {
        transitions.put(symbol, id);
    }
}
