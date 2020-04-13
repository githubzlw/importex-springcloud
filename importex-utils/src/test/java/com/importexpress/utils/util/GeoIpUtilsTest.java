package com.importexpress.utils.util;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @Author jack.luo
 * @create 2020/4/13 9:57
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GeoIpUtilsTest extends TestCase {

    @Test
    public void test()  {

        assertEquals("US",GeoIpUtils.getInstance().getCountryCode("108.162.215.124"));
    }
}