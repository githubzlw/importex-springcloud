package com.importexpress.serialport.scheduled;

import com.google.gson.Gson;
import com.importexpress.serialport.bean.GoodsBean;
import com.importexpress.serialport.service.SerialPortService;
import com.importexpress.serialport.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author jack.luo
 * @create 2020/6/29 16:37
 * Description
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class FinderScheduleTask {

    private final SerialPortService service;

    private final Config config;

    public FinderScheduleTask(SerialPortService serialPortService, Config config) {
        this.service = serialPortService;
        this.config = config;
    }

    /**
     * 定时任务，每天执行一次
     * @throws IOException
     */
    ///@Scheduled(cron = "${finder.cron.exp}")
    public void saveFinderToFiles() throws IOException {

        List<GoodsBean> lstGoodsBean = this.service.findAllGoodsByGrid();

        String json = new Gson().toJson(lstGoodsBean);

        String fileName =
                this.service.getJsonFileName(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        FileUtils.writeStringToFile(
                new File(fileName),json);

        log.info("save finder json file({}) succeed. ",fileName);
    }


}
