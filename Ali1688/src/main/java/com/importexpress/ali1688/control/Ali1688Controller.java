package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.common.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/item")
    public JSONObject getItem(@RequestParam("pid") Long pid) {

        return ali1688Service.getItem(pid);
    }

    @GetMapping("/items")
    public List<JSONObject> getItems(@RequestParam("pids") Long[] pids) {

        return ali1688Service.getItems(pids);
    }

    @GetMapping("/shop")
    public List<Ali1688Item> getItemsInShop(@RequestParam("shopid") String shopid) {

        List<Ali1688Item> lstItems = null;
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
}
