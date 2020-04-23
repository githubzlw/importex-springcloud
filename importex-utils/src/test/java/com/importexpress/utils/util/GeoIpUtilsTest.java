package com.importexpress.utils.util;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GeoIpUtils geoIpUtils;

    @Test
    public void test()  {

        assertEquals("US",geoIpUtils.getCountry("108.162.215.124").getIsoCode());
        assertEquals("United States",geoIpUtils.getCountry("108.162.215.124").getName());
    }
}