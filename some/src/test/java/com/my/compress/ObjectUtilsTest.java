package com.my.compress;

import org.junit.Test;

import java.util.Objects;

public class ObjectUtilsTest {

    @Test
    public void equalsTest() {
        System.out.println(Objects.equals("a", "a"));
        System.out.println(Objects.equals(null, "a"));
        System.out.println(Objects.equals(null, null));
        System.out.println(Objects.equals("a", null));
    }

    @Test
    public void deepEqualsTest() {
        System.out.println(Objects.deepEquals("a", "a"));
        System.out.println(Objects.deepEquals(null, "b"));

    }
}
