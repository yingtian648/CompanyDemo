package com.exa.companydemo;

import org.junit.Test;

/**
 * @author lsh
 * @date 2024/5/30 18:13
 * @description
 */
public class JavaTest {

    @Test
    public void test(){
        System.out.println("\n---------------------test-start-------------------\n\n");
        doTest();
        System.out.println("\n\n---------------------test-complete-------------------\n");
    }

    private void doTest(){
        byte[] bytes = {71, 65, 67, 95, 88, 54, 54, 95, 73, 68, 67, 95, 81, 78, 88, 95, 88, 57, 69, 95, 50, 48, 50, 52, 48, 53, 49, 55, 95, 48, 50, 48, 52};
        System.out.println("result:" + new String(bytes));
    }
}
