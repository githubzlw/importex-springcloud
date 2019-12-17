package com.importexpress.product.mongo;

import com.importexpress.comm.pojo.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author luohao
 * @String 2018/11/28
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "product")
public class MongoProduct extends Product {

    @Id
    private String _id;



}
