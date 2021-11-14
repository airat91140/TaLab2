package regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Match implements Iterator<String> {
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

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public String next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
