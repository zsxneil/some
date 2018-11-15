package com.my;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        byte[] bytes = {1, 2, 3};
        for (int i=0 ;i<bytes.length; i++) {
            System.out.println(i == (bytes[i] - 1));
        }
    }
}
