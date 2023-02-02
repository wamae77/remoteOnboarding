package com.smartengines;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("d.M.yyyy");
        Date p = f.parse("8.05.1997");
        String myFormat = "yyy-MM-dd";

        f.applyPattern(myFormat);
        assert p != null;
        String  newd = f.format(p);
        System.out.println(newd);

    }

    @Test
    public void test2() throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date p = f.parse("1997-9-06");
        String myFormat = "yyy-MM-dd";

        f.applyPattern(myFormat);
        String  newd = f.format(p);
        System.out.println(newd);

    }
}