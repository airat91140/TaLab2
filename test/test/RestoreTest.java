package test;

import org.junit.Assert;
import org.junit.Test;
import regex.Regex;

public class RestoreTest {
    @Test
    public void testRestore() {
        Regex r1 = new Regex("abd|e");
        r1.compile();
        Assert.assertTrue(r1.match("abd"));
        Assert.assertTrue(r1.match("e"));
        Assert.assertFalse(r1.match("abde"));
        Assert.assertTrue(new Regex(r1.restore()).match("abd"));
        Assert.assertTrue(new Regex(r1.restore()).match("e"));
        Assert.assertFalse(new Regex(r1.restore()).match("abde"));

        Regex r2 = new Regex("ab+d");
        r2.compile();
        Assert.assertTrue(r2.match("abd"));
        Assert.assertTrue(r2.match("abbbd"));
        Assert.assertFalse(r2.match("ad"));
        Assert.assertTrue(new Regex(r2.restore()).match("abd"));
        Assert.assertTrue(new Regex(r2.restore()).match("abbbd"));
        Assert.assertFalse(new Regex(r2.restore()).match("ad"));

        Regex r3 = new Regex("a(bdc){1,2}");
        r3.compile();
        Assert.assertTrue(r3.match("abdc"));
        Assert.assertTrue(r3.match("abdcbdc"));
        Assert.assertFalse(r3.match("a"));
        Assert.assertTrue(new Regex(r3.restore()).match("abdc"));
        Assert.assertTrue(new Regex(r3.restore()).match("abdcbdc"));
        Assert.assertFalse(new Regex(r3.restore()).match("a"));
    }
}
