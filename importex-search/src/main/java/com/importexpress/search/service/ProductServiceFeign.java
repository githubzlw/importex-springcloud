package com.importexpress.search.service;

import com.importexpress.comm.pojo.Product;
import com.importexpress.search.mongo.CatidGroup;
import com.importexpress.search.pojo.SearchParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/findProductByCatid")
    List<com.importexpress.search.mongo.Product> findProductByCatid(@RequestBody SearchParam param);

    @PostMapping(value = "/findProductByCatidCount")
    Long findProductByCatidCount(@RequestBody SearchParam param);

    @PostMapping(value = "/findCatidGroup")
    List<CatidGroup> findCatidGroup(@RequestBody List<String> list);

    @PostMapping(value = "/findProductImport")
    List<com.importexpress.search.mongo.Product> findProductImport(@RequestBody SearchParam param);

    @PostMapping(value = "/findProductCountImport")
    Long findProductCountImport(@RequestBody SearchParam param);

    @PostMapping(value = "/findCatidGroupImport")
    List<CatidGroup> findCatidGroupImport(@RequestBody List<String> list);
}



