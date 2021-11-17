package regex;

import java.util.HashMap;

public class Match {
    private HashMap<Integer, String> matched;

    void addMatch(int k, String v) {
        matched.put(k, v);
    }

    public String get(int index) {
        return matched.get(index);
    }

    public Match() {
        matched = new HashMap<>();
    }
}
