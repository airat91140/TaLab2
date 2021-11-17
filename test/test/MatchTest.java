package test;

import org.junit.Assert;
import org.junit.Test;
import regex.Regex;
import nl.flotsam.xeger.*;

public class MatchTest {
    @Test
    public void testConcat() {
        Regex r1 = new Regex("^");
        r1.compile();
        Assert.assertTrue(r1.match(""));
        Assert.assertFalse(r1.match("a"));

        Regex r2 = new Regex("abdknf");
        r2.compile();
        Assert.assertTrue(r2.match("abdknf"));
        Assert.assertFalse(r2.match("gfgfhjk"));

        Regex r3 = new Regex("a.v.f.g.y.j");
        r3.compile();
        Assert.assertTrue(r3.match("avfgyj"));
        Assert.assertFalse(r3.match("dghnjbh"));
    }

    @Test
    public void testOr() {
        Regex r4 = new Regex("a|b");
        r4.compile();
        Assert.assertTrue(r4.match("a"));
        Assert.assertTrue(r4.match("b"));
        Assert.assertFalse(r4.match("e"));
        Assert.assertFalse(r4.match("ab"));

        Regex r5 = new Regex("aaa|bbb|ccc|ddd");
        r5.compile();
        Assert.assertFalse(r5.match("aa"));
        Assert.assertFalse(r5.match("aaabbb"));
        Assert.assertFalse(r5.match("cccc"));
        Assert.assertFalse(r5.match("d"));
        Assert.assertTrue(r5.match("aaa"));
        Assert.assertTrue(r5.match("bbb"));
        Assert.assertTrue(r5.match("ccc"));
        Assert.assertTrue(r5.match("ddd"));
    }

    @Test
    public void testCombinedAndOr() {
        Regex r6 = new Regex("ab.c(bb|d.de)a.bc");
        r6.compile();
        Assert.assertTrue(r6.match("abcbbabc"));
        Assert.assertTrue(r6.match("abcddeabc"));
        Assert.assertFalse(r6.match("abcbabc"));
        Assert.assertFalse(r6.match("abcddabc"));

        Regex r8 = new Regex("a^bd(^|ee)");
        r8.compile();
        Assert.assertTrue(r8.match("abd"));
        Assert.assertTrue(r8.match("abdee"));
        Assert.assertFalse(r8.match("abd^"));
        Assert.assertFalse(r8.match("abd^ee"));

        Regex r9 = new Regex("aa((abc|bdc|eee)bv|vdeg|(ae(gg|dd)|hh)g)m");
        r9.compile();
        Assert.assertTrue(r9.match("aaabcbvm"));
        Assert.assertTrue(r9.match("aabdcbvm"));
        Assert.assertTrue(r9.match("aaeeebvm"));
        Assert.assertTrue(r9.match("aavdegm"));
        Assert.assertTrue(r9.match("aaaegggm"));
        Assert.assertTrue(r9.match("aaaeddgm"));
        Assert.assertTrue(r9.match("aahhgm"));
        Assert.assertFalse(r9.match("aaabcbykfvm"));
        Assert.assertFalse(r9.match("aabdccfcbvm"));
        Assert.assertFalse(r9.match("aaeeedghjbvm"));
        Assert.assertFalse(r9.match("aavtfhdegm"));
        Assert.assertFalse(r9.match("aaaegggmygygy"));
        Assert.assertFalse(r9.match("ygyguaaaeddgm"));
        Assert.assertFalse(r9.match("aahyghgm"));
    }
    @Test
    public void testEscape() {
        Regex r7 = new Regex("abc#(dd#w");
        r7.compile();
        Assert.assertTrue(r7.match("abc(ddw"));
        Assert.assertFalse(r7.match("abcdd"));
    }

    @Test
    public void testPositive() {
        Regex r10 = new Regex("(bc)+");
        r10.compile();
        Assert.assertTrue(r10.match("bc"));
        Assert.assertTrue(r10.match("bcbc"));
        Assert.assertTrue(r10.match("bcbcbc"));
        Assert.assertTrue(r10.match("bcbcbcbcbcbcbcbcbcbcbc"));
        Assert.assertFalse(r10.match(""));
        Assert.assertFalse(r10.match("bcb"));
        Assert.assertFalse(r10.match("bcbcc"));
        Assert.assertFalse(r10.match("bcbcbcbcbcbcbbcbcbcbcbc"));

        Regex r11 = new Regex("(a|b)+");
        r11.compile();
        Assert.assertTrue(r11.match("a"));
        Assert.assertTrue(r11.match("b"));
        Assert.assertTrue(r11.match("ab"));
        Assert.assertTrue(r11.match("ba"));
        Assert.assertTrue(r11.match("babababbab"));
        Assert.assertFalse(r11.match(""));
        Assert.assertFalse(r11.match("^"));
        Assert.assertFalse(r11.match("adb"));
        Assert.assertFalse(r11.match("bad"));
        Assert.assertFalse(r11.match("babab|abbab"));

        Regex r12 = new Regex("(a|^)+");
        r12.compile();
        Assert.assertTrue(r12.match("a"));
        Assert.assertTrue(r12.match(""));
        Assert.assertTrue(r12.match("aa"));
        Assert.assertTrue(r12.match("aaaaa"));
        Assert.assertFalse(r12.match("b"));
        Assert.assertFalse(r12.match("a^"));
        Assert.assertFalse(r12.match("^"));
        Assert.assertFalse(r12.match("^a"));

        Regex r13 = new Regex("b+++");
        r13.compile();
        Assert.assertTrue(r11.match("b"));
        Assert.assertTrue(r11.match("bbbbb"));
        Assert.assertTrue(r11.match("bbbbbbbbbbbbbbbbb"));
        Assert.assertFalse(r11.match(""));
        Assert.assertFalse(r11.match("bbdbb"));
        Assert.assertFalse(r11.match("bbbbbbbbbbbbbbbbb+"));
    }

    @Test
    public void testRepeat() {
        Regex r14 = new Regex("b{1,2}");
        r14.compile();
        Assert.assertTrue(r14.match("b"));
        Assert.assertTrue(r14.match("bb"));
        Assert.assertFalse(r14.match(""));
        Assert.assertFalse(r14.match("bbb"));

        Regex r15 = new Regex("b{0,2}");
        r15.compile();
        Assert.assertTrue(r15.match(""));
        Assert.assertTrue(r15.match("b"));
        Assert.assertTrue(r15.match("bb"));
        Assert.assertFalse(r15.match("bbb"));

        Regex r16 = new Regex("b{5,9}");
        r16.compile();
        Assert.assertTrue(r16.match("bbbbb"));
        Assert.assertTrue(r16.match("bbbbbbbb"));
        Assert.assertFalse(r16.match("bbb"));
        Assert.assertFalse(r16.match("bbbbbbbbbbbbbbbbb"));

        Regex r17 = new Regex("(abde){1,3}");
        r17.compile();
        Assert.assertTrue(r17.match("abde"));
        Assert.assertTrue(r17.match("abdeabde"));
        Assert.assertTrue(r17.match("abdeabdeabde"));
        Assert.assertFalse(r17.match(""));
        Assert.assertFalse(r17.match("abdeabdeabdeabde"));

        Regex r18 = new Regex("c{4,}");
        r18.compile();
        Assert.assertTrue(r18.match("cccc"));
        Assert.assertTrue(r18.match("ccccccccccc"));
        Assert.assertTrue(r18.match("ccccccccccccccccccc"));
        Assert.assertTrue(r18.match("ccccccccccccccccccccccccccccccccc"));
        Assert.assertFalse(r18.match(""));
        Assert.assertFalse(r18.match("ccc"));

    }

    @Test
    public void testCombinedPositiveRepeat() {
        Regex r19 = new Regex("b+{1,2}");
        r19.compile();
        Assert.assertTrue(r19.match("bbb"));
        Assert.assertTrue(r19.match("b"));
        Assert.assertTrue(r19.match("bbbbb"));
        Assert.assertFalse(r19.match(""));

        Regex r20 = new Regex("b{1,2}+");
        r20.compile();
        Assert.assertTrue(r20.match("bbb"));
        Assert.assertTrue(r20.match("b"));
        Assert.assertTrue(r20.match("bbbbb"));
        Assert.assertFalse(r20.match(""));
    }

    @Test
    public void testMatches() {
        Regex r21 = new Regex("ab(2:anbf)");
        r21.compile();
        Assert.assertTrue(r21.match("abanbf"));
        Assert.assertEquals("anbf", r21.getMatch().get(2));
        Assert.assertNull(r21.getMatch().get(1));

        Regex r23 = new Regex("ab(2:an(1:eeg)bf)");
        r23.compile();
        Assert.assertTrue(r23.match("abaneegbf"));
        Assert.assertEquals("aneegbf", r23.getMatch().get(2));
        Assert.assertEquals("eeg", r23.getMatch().get(1));
    }

    @Test
    public void testAllTogether() {
        Regex r22 = new Regex("a.bd.fg(ab|de)##e{4,}e(1:a|b)bd+(ooo|aaa(ff|^)aaa|g+)m{3,8}(2:ghf|asd|^)");
        r22.compile();
        Assert.assertTrue(r22.match("abdfgab#eeeeeeeabddddaaaffaaammmm"));
        Assert.assertEquals("a", r22.getMatch().get(1));
        Assert.assertEquals("", r22.getMatch().get(2));
    }

    @Test
    public void testUnCompiledTruth() {
        Regex r1 = new Regex("^");
        Assert.assertTrue(r1.match(""));

        Regex r2 = new Regex("abdknf");
        Assert.assertTrue(r2.match("abdknf"));

        Regex r3 = new Regex("a.v.f.g.y.j");
        Assert.assertTrue(r3.match("avfgyj"));

        Regex r4 = new Regex("a|b");
        Assert.assertTrue(r4.match("a"));
        Assert.assertTrue(r4.match("b"));

        Regex r5 = new Regex("aaa|bbb|ccc|ddd");
        Assert.assertTrue(r5.match("aaa"));
        Assert.assertTrue(r5.match("bbb"));
        Assert.assertTrue(r5.match("ccc"));
        Assert.assertTrue(r5.match("ddd"));

        Regex r6 = new Regex("ab.c(bb|d.de)a.bc");
        Assert.assertTrue(r6.match("abcbbabc"));
        Assert.assertTrue(r6.match("abcddeabc"));

        Regex r7 = new Regex("abc#(dd#w");
        Assert.assertTrue(r7.match("abc(ddw"));

        Regex r8 = new Regex("a^bd(^|ee)");
        Assert.assertTrue(r8.match("abd"));
        Assert.assertTrue(r8.match("abdee"));

        Regex r9 = new Regex("aa((abc|bdc|eee)bv|vdeg|(ae(gg|dd)|hh)g)m");
        Assert.assertTrue(r9.match("aaabcbvm"));
        Assert.assertTrue(r9.match("aabdcbvm"));
        Assert.assertTrue(r9.match("aaeeebvm"));
        Assert.assertTrue(r9.match("aavdegm"));
        Assert.assertTrue(r9.match("aaaegggm"));
        Assert.assertTrue(r9.match("aaaeddgm"));
        Assert.assertTrue(r9.match("aahhgm"));

        Regex r10 = new Regex("(bc)+");
        Assert.assertTrue(r10.match("bc"));
        Assert.assertTrue(r10.match("bcbc"));
        Assert.assertTrue(r10.match("bcbcbc"));
        Assert.assertTrue(r10.match("bcbcbcbcbcbcbcbcbcbcbc"));

        Regex r11 = new Regex("(a|b)+");
        Assert.assertTrue(r11.match("a"));
        Assert.assertTrue(r11.match("b"));
        Assert.assertTrue(r11.match("ab"));
        Assert.assertTrue(r11.match("ba"));
        Assert.assertTrue(r11.match("babababbab"));

        Regex r12 = new Regex("(a|^)+");
        Assert.assertTrue(r12.match("a"));
        Assert.assertTrue(r12.match(""));
        Assert.assertTrue(r12.match("aa"));
        Assert.assertTrue(r12.match("aaaaa"));

        Regex r13 = new Regex("b+++");
        Assert.assertTrue(r11.match("b"));
        Assert.assertTrue(r11.match("bbbbb"));
        Assert.assertTrue(r11.match("bbbbbbbbbbbbbbbbb"));
    }

    @Test
    public void testUnCompiledFalse() {
        Regex r1 = new Regex("^");
        Assert.assertFalse(r1.match("a"));

        Regex r2 = new Regex("abdknf");
        Assert.assertFalse(r2.match("gfgfhjk"));

        Regex r3 = new Regex("a.v.f.g.y.j");
        Assert.assertFalse(r3.match("dghnjbh"));

        Regex r4 = new Regex("a|b");
        Assert.assertFalse(r4.match("e"));
        Assert.assertFalse(r4.match("ab"));

        Regex r5 = new Regex("aaa|bbb|ccc|ddd");
        Assert.assertFalse(r5.match("aa"));
        Assert.assertFalse(r5.match("aaabbb"));
        Assert.assertFalse(r5.match("cccc"));
        Assert.assertFalse(r5.match("d"));

        Regex r6 = new Regex("ab.c(bb|d.de)a.bc");
        Assert.assertFalse(r6.match("abcbabc"));
        Assert.assertFalse(r6.match("abcddabc"));

        Regex r7 = new Regex("abc#(dd#w");
        Assert.assertFalse(r7.match("abcdd"));

        Regex r8 = new Regex("a^bd(^|ee)");
        Assert.assertFalse(r8.match("abd^"));
        Assert.assertFalse(r8.match("abd^ee"));

        Regex r9 = new Regex("aa((abc|bdc|eee)bv|vdeg|(ae(gg|dd)|hh)g)m");
        Assert.assertFalse(r9.match("aaabcbykfvm"));
        Assert.assertFalse(r9.match("aabdccfcbvm"));
        Assert.assertFalse(r9.match("aaeeedghjbvm"));
        Assert.assertFalse(r9.match("aavtfhdegm"));
        Assert.assertFalse(r9.match("aaaegggmygygy"));
        Assert.assertFalse(r9.match("ygyguaaaeddgm"));
        Assert.assertFalse(r9.match("aahyghgm"));

        Regex r10 = new Regex("(bc)+");
        Assert.assertFalse(r10.match(""));
        Assert.assertFalse(r10.match("bcb"));
        Assert.assertFalse(r10.match("bcbcc"));
        Assert.assertFalse(r10.match("bcbcbcbcbcbcbbcbcbcbcbc"));

        Regex r11 = new Regex("(a|b)+");
        Assert.assertFalse(r11.match(""));
        Assert.assertFalse(r11.match("^"));
        Assert.assertFalse(r11.match("adb"));
        Assert.assertFalse(r11.match("bad"));
        Assert.assertFalse(r11.match("babab|abbab"));

        Regex r12 = new Regex("(a|^)+");
        Assert.assertFalse(r12.match("b"));
        Assert.assertFalse(r12.match("a^"));
        Assert.assertFalse(r12.match("^"));
        Assert.assertFalse(r12.match("^a"));

        Regex r13 = new Regex("b+++");
        Assert.assertFalse(r11.match(""));
        Assert.assertFalse(r11.match("bbdbb"));
        Assert.assertFalse(r11.match("bbbbbbbbbbbbbbbbb+"));
    }
}
