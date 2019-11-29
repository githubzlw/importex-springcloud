package com.importexpress.ali1688;


import com.google.common.base.CharMatcher;

/**
 * @author luohao
 * @date 2019/11/29
 */
public class Test {

    @org.junit.Test
    public void test1(){
        System.out.println(CharMatcher.anyOf("0123456789.").retainFrom("abc1111]"));



        System.out.println(CharMatcher.anyOf("0123456789.").retainFrom("[1213.10]"));
    }
}
