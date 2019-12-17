package com.importexpress.shopify.feign;

import com.importexpress.comm.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author luohao
 * @date 2019/12/13
 */
@FeignClient(value = "product-service")
public interface ProductServiceFeign {

    @GetMapping(value = "/hello")
    String hello();

    @GetMapping(value = "/pid/{pid}")
    Product findProduct(@PathVariable("pid") long pid);

    @GetMapping(value = "/pids/{pids}")
    List<Product> findProducts(@PathVariable("pids") long[] pids, @RequestParam(value = "valid", required = false, defaultValue = "-1") int valid);
}
