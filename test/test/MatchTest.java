package test;

import org.junit.Assert;
import org.junit.Test;
import regex.Regex;

public class MatchTest {
    @Test
    public void testTruth() {
        Regex r1 = new Regex("^");
        r1.compile();
        Assert.assertTrue(r1.match(""));

        Regex r2 = new Regex("abdknf");
        r2.compile();
        Assert.assertTrue(r2.match("abdknf"));

        Regex r3 = new Regex("a.v.f.g.y.j");
        r3.compile();
        Assert.assertTrue(r3.match("avfgyj"));
    }
}
