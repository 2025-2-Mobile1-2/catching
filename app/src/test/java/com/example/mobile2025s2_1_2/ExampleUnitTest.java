package com.example.mobile2025s2_1_2;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExampleUnitTest {

    // ② 성공 테스트
    @Test
    public void testSuccessExample0() {
        int result = 2 * 3;
        assertEquals(6, result);
    }

    // ② 성공 테스트
    @Test
    public void testSuccessExample1() {
        int result = 2 * 3;
        assertEquals(6, result);
    }

    // ③ 성공 테스트
    @Test
    public void testSuccessExample2() {
        boolean flag = "abc".contains("a");
        assertTrue(flag);
    }
}
