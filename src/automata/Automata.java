package automata;

import lexer.TokenEscape;
import lexer.Lexer;
import lexer.Tag;
import lexer.TokenGroup;
import lexer.TokenRepeat;
import parser.*;

import java.util.*;
import java.util.stream.Collectors;

public class Automata {
    private ArrayList<State> states;
    private IdentityHashMap<Literal, Set<Literal>> followPosTable;
    private Parser parser;
    private State start;

    public String toString() {
        StringBuilder builder = new StringBuilder("Start: ");
        builder.append(start.id).append('\n');
        for (State st : states) {
            builder.append(st.id).append(" -> ");
            for (int tr : st.getTransitions().keySet()) {
                builder.append(st.nextState(tr).id).append("(").append((char)tr).append(") ");
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public Automata(String regex) throws Exception {
        states = new ArrayList<>(); // states posed by their id
        followPosTable = new IdentityHashMap<>();
        parser = new Parser(new Lexer(regex));
        parser.program();
        followpos(parser.getRoot());
        build();
    }

    private Set<State> getGroupById(State st, ArrayList<Set<State>> split) {
        for (Set<State> set : split) {
            if (set.stream().anyMatch(state -> state.equals(st)))
                return set;
        }
        return null;
    }

    private boolean isInOneGroup(State first, State second, ArrayList<Set<State>> split) {
        return getGroupById(first, split) == getGroupById(second, split);
    }

    private ArrayList<Set<State>> getNewSplit(ArrayList<Set<State>> prevSplit) {
        ArrayList<Set<State>> newSplit = new ArrayList<>();
        for (Set<State> set : prevSplit) {
            for (State st : set) {
                Set<State> tmp = set.stream().filter(another -> {
                    for (char c = 'a'; c <= 'b'; ++c) {
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

    Set<Literal> getUnion(int current, int c) {
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
        states.add(new State(lastId, firstpos(parser.getRoot(), null)));
        while (!unMarkedIds.isEmpty()) {
            int current = unMarkedIds.iterator().next();
            unMarkedIds.remove(current);
            for (char c = 'a'; c <= 'b'; ++c) {
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
        } else if (node instanceof Group) {
            return nullable(parser.getTable().get(((TokenGroup) node.getOp()).getIndex()));
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
        } else if (node instanceof Group) {
            return firstpos(parser.getTable().get(((TokenGroup) node.getOp()).getIndex()), set);
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
            lastpos(((Repeat) node).left, set);
        } else if (node instanceof Positive) {
            lastpos(((Positive) node).left, set);
        } else if (node instanceof Capture) {
            lastpos(((Capture) node).getChild(), set);
        } else if (node instanceof Group) {
            return lastpos(parser.getTable().get(((TokenGroup) node.getOp()).getIndex()), set);
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
        } else if (node instanceof Or) {
            followpos(((Or) node).left);
            followpos(((Or) node).right);
        }
    }

    private Set<Literal> followpos(Literal literal) {
        return followPosTable.get(literal);
    }
}
