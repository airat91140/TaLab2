package regex;

import automata.Automata;
import automata.State;

import java.util.StringJoiner;

public class Regex {
    private final String regexp;
    private boolean isCompiled;
    private Automata fsm;
    private Match matched;

    private Regex() {
        regexp = "";
        isCompiled = false;
        fsm = null;
        matched = null;
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public String getExpression() {
        return regexp;
    }

    public Regex(String r) {
        regexp = r;
        isCompiled = false;
        matched = new Match();
    }

    public Match getMatch() {
        return matched;
    }

    public void compile() {
        fsm = new Automata(regexp);
        fsm.minimize();
        isCompiled = true;
    }

    public boolean match(String str) {
        Automata tmpFSM;
        if (isCompiled)
            tmpFSM = fsm;
        else {
            tmpFSM = new Automata(regexp);
            tmpFSM.minimize();
            tmpFSM.iterate();
        }
        tmpFSM.iterate();
        for (int i = 0; i < str.length(); ++i)
            tmpFSM.next(str.charAt(i));
        tmpFSM.getMatches().keySet().forEach(i -> matched.addMatch(i, tmpFSM.getMatches().get(i)));
        return tmpFSM.getCurrent().isFinal();
    }

    public String restore() {
        StringJoiner bld = new StringJoiner("|");
        fsm.iterate();
        if (isCompiled) {
            for (State j : fsm.getFinals())
                bld.add("(" + fsm.k_path(fsm.getCurrent().getId(), j.getId(), fsm.getStatesSize() - 1) + ")");
            return bld.toString();
        }
        return null;
    }

    public Regex intersect(Regex other) {
        Automata result;
        if (this.isCompiled) {
            if (other.isCompiled)
                result = Automata.and(this.fsm, other.fsm);
            else
                result = Automata.and(this.fsm, other.regexp);
        }
        else {
            if (other.isCompiled)
                result = Automata.and(this.regexp, other.fsm);
            else
                result = Automata.and(this.regexp, other.regexp);
        }
        Regex tmp = new Regex();
        tmp.fsm = result;
        return new Regex(tmp.restore());
    }

    public Regex inverse() {
        Automata result;
        if (isCompiled)
            result = fsm.getInverse();
        else
            result = new Automata(regexp).getInverse();
        Regex tmp = new Regex();
        tmp.fsm = result;
        return new Regex(tmp.restore());
    }

}
