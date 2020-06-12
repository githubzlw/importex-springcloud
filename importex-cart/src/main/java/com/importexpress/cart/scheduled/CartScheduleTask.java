package com.importexpress.cart.scheduled;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.importexpress.cart.pojo.Cart;
import com.importexpress.cart.service.CartService;
import com.importexpress.cart.util.Config;
import com.importexpress.cart.util.SevenZ;
import com.importexpress.comm.pojo.SiteEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.swing.event.ListSelectionEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Configuration
@EnableScheduling
public class CartScheduleTask {

    private final CartService service;

    private final Config config;

    public CartScheduleTask(CartService service, Config config) {
        this.service = service;
        this.config = config;
    }


    @Scheduled(cron = "0 0 0/6 * * ?")
    public void saveAllCartsToFiles() throws IOException {

        List<SiteEnum> siteEnums = Arrays.asList(SiteEnum.IMPORTX, SiteEnum.KIDS, SiteEnum.PETS);
        for (SiteEnum site : siteEnums) {
            List<Cart> cart = this.service.getCart(site);
            String json = new Gson().toJson(cart);
            StringBuilder fileName = getFileName(site);
            FileUtils.writeStringToFile(
                    new File(fileName.toString()),json);

            // get multiple resources files to compress
            File resource = new File(fileName.toString());
            // compress multiple resources
            SevenZ.compress(fileName+".7z", resource);
        }

    }

    public void decompress() throws IOException {

        List<SiteEnum> siteEnums = Arrays.asList(SiteEnum.IMPORTX, SiteEnum.KIDS, SiteEnum.PETS);
        for (SiteEnum site : siteEnums) {
            StringBuilder fileName = getFileName(site);
            SevenZ.decompress(fileName+".7z",new File(config.SAVE_CART_PATH));
        }

    }

    public void readFileToBean() throws IOException {

        List<SiteEnum> siteEnums = Arrays.asList(SiteEnum.IMPORTX, SiteEnum.KIDS, SiteEnum.PETS);
        for (SiteEnum site : siteEnums) {
            StringBuilder fileName = getFileName(site);
            String json = FileUtils.readFileToString(new File(fileName.toString()));
            List<Cart> list = new Gson().fromJson(json, List.class);
            System.out.println(site.toString()+" :  "+list.size());
        }

    }


    /**
     * get file name,sample:i_carts_20200612.json
     * @param site
     * @return
     */
    private StringBuilder getFileName(SiteEnum site) {
        StringBuilder fileName = new StringBuilder();
        String yyyyMMdd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String siteType = site.toString().substring(0, 1).toLowerCase();

        fileName.append(config.SAVE_CART_PATH);
        fileName.append(siteType).append("_carts_").append(yyyyMMdd).append(".json");
        return fileName;
    }

}