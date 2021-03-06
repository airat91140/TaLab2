package automata;

import lexer.*;
import parser.*;

import java.util.*;
import java.util.stream.Collectors;

public class Automata {
    private final static char alphStart = ' ';
    private final static char alphEnd = '~';
    private ArrayList<State> states;
    private IdentityHashMap<Literal, Set<Literal>> followPosTable;
    private Parser parser;
    private State start;
    private State current;
    private Node root;
    private HashMap<Integer, String> matches;
    private HashMap<Triple, String> cashedKPath;

    private class Triple {
        int i, j, k;
        Triple(int i, int j, int k) {
            this.i = i;
            this.j = j;
            this.k = k;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Triple)) return false;
            Triple triple = (Triple) o;
            return i == triple.i && j == triple.j && k == triple.k;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j, k);
        }
    }

    private boolean containsCash(int i, int j, int k) {
        return cashedKPath.containsKey(new Triple(i, j, k));
    }

    private String getCash(int i, int j, int k) {
        return cashedKPath.get(new Triple(i, j, k));
    }

    public Automata(String regex) {
        states = new ArrayList<>(); // states posed by their id
        followPosTable = new IdentityHashMap<>();
        parser = new Parser(new Lexer(regex));
        parser.program();
        root = parser.getRoot();
        matches = new HashMap<>();
        cashedKPath = new HashMap<>();
        for (Integer i : parser.getAllInddexes())
            matches.put(i, "");
        followpos(root);
        build();
        start = states.get(0);
    }

    private Automata(Node node) {
        states = new ArrayList<>(); // states posed by their id
        followPosTable = new IdentityHashMap<>();
        cashedKPath = new HashMap<>();
        parser = null;
        root = node;
        followpos(node);
        build();
        start = states.get(0);
    }

    public static Automata and(Automata a1, Automata a2) {
        return a1.and(a2);
    }

    public static Automata and(String a1, Automata a2) {
        Automata first = new Automata(a1);
        first.minimize();
        return first.and(a2);
    }

    public static Automata and(Automata a1, String a2) {
        return a1.and(a2);
    }

    public static Automata and(String a1, String a2) {
        Automata first = new Automata(a1);
        first.minimize();
        return first.and(a2);
    }

    public State getCurrent() {
        return current;
    }

    public int getStatesSize() {
        return states.size();
    }

    public List<State> getFinals() {
        return states.stream().filter(State::isFinal).collect(Collectors.toList());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Start: ");
        builder.append(start.id).append('\n');
        for (State st : states) {
            builder.append(st.id).append(" -> ");
            for (int tr : st.getTransitions().keySet()) {
                builder.append(st.nextState(tr).id).append("(").append((char) tr).append(") ");
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public Automata getInverse() {
        return new Automata(parser.inverse());
    }

    private Set<State> getGroupById(State st, ArrayList<Set<State>> split) {
        for (Set<State> set : split) {
            if (set.stream().anyMatch(state -> state.equals(st)))
                return set;
        }
        return null;
    }

    public HashMap<Integer, String> getMatches() {
        return matches;
    }

    private boolean isInOneGroup(State first, State second, ArrayList<Set<State>> split) {
        return getGroupById(first, split) == getGroupById(second, split);
    }

    private ArrayList<Set<State>> getNewSplit(ArrayList<Set<State>> prevSplit) {
        ArrayList<Set<State>> newSplit = new ArrayList<>();
        for (Set<State> set : prevSplit) {
            for (State st : set) {
                Set<State> tmp = set.stream().filter(another -> {
                    for (char c = alphStart; c <= alphEnd; ++c) {
                        if (!isInOneGroup(st.nextState(c), another.nextState(c), prevSplit))
                            return false;
                    }
                    return true;
                }).collect(Collectors.toSet());
                if (!newSplit.contains(tmp))
                    newSplit.add(tmp);
            }
        }
        return newSplit;
    }

    private void makeStates(ArrayList<Set<State>> split) {
        ArrayList<State> result = new ArrayList<>();
        int lastId = 0;
        for (Set<State> set : split) {
            result.add(new State(lastId++, set.stream().flatMap(st -> st.getInternal().stream()).collect(Collectors.toSet())));
            if (set.stream().anyMatch(state -> state.id == 0))
                start = result.get(lastId - 1);
        }
        for (int i = 0; i < result.size(); ++i) {
            Map<Integer, State> currentTransitions = split.get(i).iterator().next().getTransitions();
            int finalI = i;
            currentTransitions.keySet().forEach(c -> result.get(finalI)
                    .addTransition(c, result.get(split.indexOf(getGroupById(currentTransitions.get(c), split)))));
        }
        states = result;
    }

    public void minimize() {
        ArrayList<Set<State>> result = new ArrayList<>(), tmpSplit;
        result.add(Collections.newSetFromMap(new IdentityHashMap<>()));
        result.add(Collections.newSetFromMap(new IdentityHashMap<>()));
        for (State st : states) {
            if (st.isFinal())
                result.get(1).add(st);
            else
                result.get(0).add(st);
        }
        tmpSplit = getNewSplit(result);
        while (!tmpSplit.equals(result)) {
            result = tmpSplit;
            tmpSplit = getNewSplit(result);
        }
        makeStates(result);
    }

    private Set<Literal> getUnion(int current, int c) {
        Set<Literal> result = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Literal lit : states.get(current).getInternal()) {
            if (lit.getOp().getTag() == c) {
                result.addAll(followpos(lit));
            } else if (lit.getOp() instanceof TokenEscape) {
                if (((TokenEscape) lit.getOp()).getVal() == c) {
                    result.addAll(followpos(lit));
                }
            }
        }
        return result;
    }

    private void build() {
        int lastId = 0;
        HashSet<Integer> unMarkedIds = new HashSet<>();
        unMarkedIds.add(lastId);
        states.add(new State(lastId, firstpos(root, null)));
        while (!unMarkedIds.isEmpty()) {
            int current = unMarkedIds.iterator().next();
            unMarkedIds.remove(current);
            for (char c = alphStart; c <= alphEnd; ++c) {
                State u = new State(lastId + 1, getUnion(current, c));
                if (!states.contains(u)) {
                    unMarkedIds.add(++lastId);
                    states.add(u);
                } else {
                    u = states.get(states.indexOf(u));
                }
                states.get(current).addTransition(c, u);
            }
        }
    }

    private void put(Literal lit, Set<Literal> follow) {
        if (!followPosTable.containsKey(lit))
            followPosTable.put(lit, Collections.newSetFromMap(new IdentityHashMap<>()));
        followPosTable.get(lit).addAll(follow);
    }

    private String putBracketsIfNes(String expr) {
        if (expr.contains("|"))
            return "(" + expr + ")";
        return expr;
    }

    private boolean nullable(Node node) {
        if (node instanceof Binary) {
            if (node instanceof Concat)
                return nullable(((Concat) node).left) && nullable(((Concat) node).right);
            else if (node instanceof Or)
                return nullable(((Or) node).left) || nullable(((Or) node).right);
        } else if (node instanceof Literal) {
            return ((Literal) node).getOp().getTag() == Tag.EMPTY;
        } else if (node instanceof Repeat) {
            if (((TokenRepeat) node.getOp()).getStart() == 0)
                return true;
            else
                return nullable(((Repeat) node).left);
        } else if (node instanceof Positive) {
            return nullable(((Positive) node).left);
        } else if (node instanceof Capture) {
            return nullable(((Capture) node).getChild());
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
                    firstpos(((Concat) node).right, set);
                } else firstpos(((Concat) node).left, set);
            else if (node instanceof Or) {
                firstpos(((Or) node).left, set);
                firstpos(((Or) node).right, set);
            }
        } else if (node instanceof Literal) {
            if (((Literal) node).getOp().getTag() != Tag.EMPTY)
                set.add((Literal) node);
        } else if (node instanceof Repeat) {
            firstpos(((Repeat) node).left, set);
        } else if (node instanceof Positive) {
            firstpos(((Positive) node).left, set);
        } else if (node instanceof Capture) {
            firstpos(((Capture) node).getChild(), set);
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
                    lastpos(((Concat) node).right, set);
                } else lastpos(((Concat) node).right, set);
            else if (node instanceof Or) {
                lastpos(((Or) node).left, set);
                lastpos(((Or) node).right, set);
            }
        } else if (node instanceof Literal) {
            if (((Literal) node).getOp().getTag() != Tag.EMPTY)
                set.add((Literal) node);
        } else if (node instanceof Repeat) {
            set.addAll(((Repeat) node).lastpos);
        } else if (node instanceof Positive) {
            lastpos(((Positive) node).left, set);
        } else if (node instanceof Capture) {
            lastpos(((Capture) node).getChild(), set);
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
            Node prev = ((Repeat) node).left;
            Node next = ((Repeat) node).left.clone();
            for (int i = 0; i < ((TokenRepeat) node.getOp()).getStart() - 1; ++i) { //from zero to start
                followpos(next);
                Set<Literal> lastSet = lastpos(prev, null);
                Set<Literal> firstSet = firstpos(next, null);
                for (Literal it : lastSet)
                    put(it, firstSet);
                prev = next;
                next = next.clone();
            }
            if (((TokenRepeat) node.getOp()).getEnd() == -1) { //no end
                Set<Literal> lastSet = lastpos(prev, null);
                ((Repeat) node).lastpos = lastSet;
                Set<Literal> firstSet = firstpos(prev, null);
                for (Literal it : lastSet)
                    put(it, firstSet);
            }
            int inc = 0;
            if (((TokenRepeat) node.getOp()).getStart() == 0)
                inc = -1;
            ((Repeat) node).lastpos = Collections.newSetFromMap(new IdentityHashMap<>());
            for (int i = 0; i < ((TokenRepeat) node.getOp()).getEnd() - ((TokenRepeat) node.getOp()).getStart() + inc; ++i) { // from start to end
                followpos(next);
                Set<Literal> lastSet = lastpos(prev, null);
                ((Repeat) node).lastpos.addAll(lastSet);
                Set<Literal> firstSet = firstpos(next, null);
                for (Literal it : lastSet)
                    put(it, firstSet);
                prev = next;
                next = next.clone();
            }
            ((Repeat) node).lastpos.addAll(lastpos(prev, null));
        } else if (node instanceof Or) {
            followpos(((Or) node).left);
            followpos(((Or) node).right);
        } else if (node instanceof Capture) {
            followpos(((Capture) node).getChild());
        }
    }

    private Set<Literal> followpos(Literal literal) {
        return followPosTable.get(literal);
    }

    public void iterate() {
        current = start;
    }

    public State next(char c) {
        current.appendToMap(c, matches);
        current = current.nextState(c);
        return current;
    }

    private String getPath(int i, int j, int k) {
        return containsCash(i, j, k) ? getCash(i, j, k) : k_path(i, j, k);
    }

    public String k_path(int i, int j, int k) {
        if (k == -1) {
            StringJoiner transChars = new StringJoiner("|", "(", ")");
            for (char c = alphStart; c <= alphEnd; ++c)
                if (states.get(i).nextState(c) == states.get(j))
                    transChars.add(Character.toString(c));
            if (transChars.length() == 2) {
                if (i == j)
                    return "^";
                return null;
            }
            return transChars.toString();
        } else {
            String first = getPath(i, j, k - 1);
            String second = getPath(i, k, k - 1);
            String third = getPath(k, k, k - 1);
            String fourth = getPath(k, j, k - 1);
            String res = (first == null ? "" : putBracketsIfNes(first)) +
                    (second == null || third == null || fourth == null ? "" :
                            (first == null ? "" : '|') + putBracketsIfNes(second) +
                                    (third.equals("^") ? "^" : "(" + third + "|^)+") +
                                    putBracketsIfNes(fourth));
            cashedKPath.put(new Triple(i, j, k), res.equals("") ? null : res);
            return res.equals("") ? null : res;
        }
    }

    public Automata and(Automata other) {
        Automata result = new Automata("^");
        result.states.clear();
        for (int i = 0; i < this.states.size() * other.states.size(); ++i)
            result.states.add(new State(i, new HashSet<>()));
        result.start = result.states.get(this.start.id * other.getStatesSize() + other.start.id);
        for (State st1 : this.states) {
            for (State st2 : other.states) {
                for (char c = alphStart; c <= alphEnd; ++c) {
                    if (st1.isFinal() && st2.isFinal())
                        result.states.get(st1.id * other.getStatesSize() + st2.id).internal.add(new EOS());
                    State first = st1.nextState(c);
                    State second = st2.nextState(c);
                    result.states.get(st1.id * other.states.size() + st2.id)
                            .addTransition(c, result.states.get(first.id * other.states.size() + second.id));
                }
            }
        }
        return result;
    }

    public Automata and(String other) {
        Automata another = new Automata(other);
        another.minimize();
        return this.and(another);
    }
}
