package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.common.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luohao
 */
@RestController
@Slf4j
public class Ali1688Controller {

    private Ali1688Service ali1688Service;

    @Autowired
    public Ali1688Controller(Ali1688Service ali1688Service){
        this.ali1688Service=ali1688Service;
    }

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }

    @GetMapping("/pids/{pids}")
    public List<JSONObject> pid(@PathVariable("pids") Long[] pids) {

        if(pids !=null && pids.length==1){

            List<JSONObject> lstResult = new ArrayList<JSONObject>(1);
            lstResult.add(ali1688Service.getItem(pids[0]));
            return lstResult;
        }else{
            return ali1688Service.getItems(pids);
        }
    }


    @GetMapping("/shop/{shopid}")
    public List<Ali1688Item> getItemsInShop(@PathVariable("shopid") String shopid) {

        List<Ali1688Item> lstItems ;
        try{
            lstItems = ali1688Service.getItemsInShop(shopid);
        }catch(IllegalStateException e1){
            try{
                log.warn("retry getItemsInShop() , 1 times");
                lstItems = ali1688Service.getItemsInShop(shopid);
            }catch(IllegalStateException e2){
                log.warn("retry getItemsInShop() , 2 times");
                lstItems = ali1688Service.getItemsInShop(shopid);
            }
        }
        return lstItems;
    }

    @GetMapping("/pids/clearNotExistItemInCache")
    public int clearNotExistItemInCache() {

            return ali1688Service.clearNotExistItemInCache();
    }

    @GetMapping("/pids/getNotExistItemInCache")
    public int getNotExistItemInCache() {

            return ali1688Service.getNotExistItemInCache();
    }
}
