package test;

import org.junit.Assert;
import org.junit.Test;
import regex.Regex;

public class IntersectTest {
    @Test
    public void testIntersect() {
        Regex r1 = new Regex("a");
        Regex r2 = new Regex("a|b");
        Assert.assertTrue(r1.intersect(r2).match("a"));
        Assert.assertFalse(r1.intersect(r2).match("b"));


        Regex r3 = new Regex("ab+");
        Regex r4 = new Regex("ab");
        r3.compile();
        r4.compile();
        Assert.assertTrue(r3.intersect(r4).match("ab"));
    }
}
