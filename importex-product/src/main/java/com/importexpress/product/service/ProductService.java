package com.importexpress.product.service;


import com.importexpress.product.mongo.MongoProduct;

import java.util.List;

public interface ProductService {

    MongoProduct findProduct(Long pid);


    int updateProduct(Long pid, int valid);

    List<MongoProduct> findProducts(long[] pids, int valid);
}
