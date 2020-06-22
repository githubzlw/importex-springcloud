package com.importexpress.cart.scheduled;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.importexpress.cart.pojo.Cart;
import com.importexpress.cart.service.CartService;
import com.importexpress.cart.util.Config;
import com.importexpress.cart.util.SFtpUtil;
import com.importexpress.cart.util.SevenZ;
import com.importexpress.comm.pojo.SiteEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * @author jack.luo
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class CartScheduleTask {

    private final CartService service;

    private final Config config;

    private final SFtpUtil sFtpUtil;

    public CartScheduleTask(CartService service, Config config, SFtpUtil sFtpUtil) {
        this.service = service;
        this.config = config;
        this.sFtpUtil = sFtpUtil;
    }


    /**
     * 定时任务，每天执行一次，将各个网站的所有购物车分别写入json文件，并且压缩成7z格式
     * @throws IOException
     */
    @Scheduled(cron = "${cart.cron.exp}")
    public void saveAllCartsToFiles() throws IOException {

        List<SiteEnum> siteEnums = Arrays.asList(SiteEnum.IMPORTX, SiteEnum.KIDS, SiteEnum.PETS);
        for (SiteEnum site : siteEnums) {
            List<Cart> carts = this.service.getCart(site);
            String json = streamIntoJsonString(carts);
            StringBuilder fileName = getFileName(site);
            FileUtils.writeStringToFile(
                    new File(fileName.toString()),json);

            // get multiple resources files to compress
            File resource = new File(fileName.toString());
            // compress multiple resources
            String fileName7z=fileName+".7z";
            SevenZ.compress(fileName7z, resource);

            boolean result = FileUtils.deleteQuietly(new File(fileName.toString()));
            log.info("delete file:[{}] result:[{}]",fileName.toString(),result);

            //sftp upload
            int beginIndex = fileName7z.lastIndexOf('/');
            if(beginIndex==-1){
                //windows场合
                beginIndex = fileName7z.lastIndexOf('\\');
            }
            sFtpUtil.uploadFile(fileName7z.substring(beginIndex+1));
        }

    }

    /**
     * 通过流的方式写入json（避免OutOfMemoryError)
     * @param carts
     * @return
     */
    private String streamIntoJsonString(List<Cart> carts) {


        StringBuilder sb = new StringBuilder();
        List<List<Cart>> parts = Lists.partition(carts, 50);
        parts.stream().forEach(list -> {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JsonWriter writer = null;
            try {
                log.debug("begin write json file ,size:[{}]",list.size());
                Gson gson = new Gson();
                writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.setIndent("  ");
                writer.beginArray();
                for (Cart cart : list) {
                    gson.toJson(cart, Cart.class, writer);
                }
                writer.endArray();
                writer.close();
                sb.append(out.toString("UTF-8"));
                out.close();
            } catch (IOException ioe) {
                log.error("streamIntoJsonString",ioe);
            }
        });

        return sb.toString();

    }

    /**
     * 解压7z文件（测试用）
     * @throws IOException
     */
    public void decompress() throws IOException {

        List<SiteEnum> siteEnums = Arrays.asList(SiteEnum.IMPORTX, SiteEnum.KIDS, SiteEnum.PETS);
        for (SiteEnum site : siteEnums) {
            StringBuilder fileName = getFileName(site);
            SevenZ.decompress(fileName+".7z",new File(config.SAVE_CART_PATH));
        }

    }

    /**
     * 读取json文件并且转成bean（测试用）
     * @throws IOException
     */
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