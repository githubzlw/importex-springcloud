package com.importexpress.cart.feign;

import com.importexpress.comm.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author jack.luo
 * @date 2019/12/17
 */
@FeignClient(value = "product-service")
public interface ProductServiceFeign {

    @GetMapping(value = "/pid/{pid}")
    Product findProduct(@PathVariable("pid") long pid);

    @PostMapping(value = "/pid/{pid}")
    int updateProduct(@PathVariable("pid") long pid, @RequestParam(value = "valid") int valid);

    @GetMapping(value = "/pids/{pids}")
    List<Product> findProducts(@PathVariable("pids") long[] pids, @RequestParam(value = "valid", required = false, defaultValue = "-1") int valid);

}
