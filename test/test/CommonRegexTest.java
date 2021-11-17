package test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import regex.Regex;

public class CommonRegexTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testLexical() {
        Regex r1 = new Regex("abd(gh");
        Assert.assertThrows(IllegalArgumentException.class, r1::compile);

        Regex r2 = new Regex("e{a,d}");
        Assert.assertThrows(IllegalArgumentException.class, r2::compile);

        Regex r3 = new Regex("m{4,1}");
        Assert.assertThrows(IllegalArgumentException.class, r3::compile);

        Regex r4 = new Regex("");
        Assert.assertThrows(IllegalArgumentException.class, r4::compile);

        Regex r5 = new Regex("(2:)");
        Assert.assertThrows(IllegalArgumentException.class, r5::compile);

        Regex r6 = new Regex("(4:dd");
        Assert.assertThrows(IllegalArgumentException.class, r6::compile);
    }
}
