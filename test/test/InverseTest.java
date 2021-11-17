package test;

import org.junit.Assert;
import org.junit.Test;
import regex.Regex;

public class InverseTest {
    @Test
    public void testInverse() {
        Regex r1 = new Regex("abc");
        Assert.assertTrue(r1.inverse().match("cba"));
        Assert.assertFalse(r1.inverse().match("abc"));

        Regex r2 = new Regex("ab(de|gd)c");
        r2.compile();
        Assert.assertTrue(r2.inverse().match("cdgba"));
        Assert.assertTrue(r2.inverse().match("cedba"));
        Assert.assertFalse(r2.inverse().match("abdec"));
    }
}
