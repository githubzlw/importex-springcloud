package com.importexpress.search.service;

import com.importexpress.comm.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Description:产品微服务调用接口
 *
 * @author : Administrator
 * @date : 2020-02-20
 */
@FeignClient(value = "product-service")
public interface ProductServiceFeign {
    @GetMapping(value = "/pid/{pid}")
    Product findProduct(@PathVariable("pid") long pid);
}
