package com.looptech.bakingapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void formattingFlaotingPointNumbers() {
        float d = 2.1200f;
        if (d == (long) d)
            System.out.println(String.format("%d", (long) d));
        else
            System.out.println(String.format("%s", d));
    }
}