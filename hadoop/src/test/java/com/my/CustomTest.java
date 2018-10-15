package com.my;

import org.junit.Test;

import java.util.StringTokenizer;

public class CustomTest {

    @Test
    public void StringTokenzierTest() {
        String line = "hello , world";
        StringTokenizer str = new StringTokenizer(line);
        while (str.hasMoreTokens()) {
            System.out.println(str.nextToken());
        }
    }

}
