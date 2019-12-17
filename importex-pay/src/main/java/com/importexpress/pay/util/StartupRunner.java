package com.importexpress.pay.util;

import com.importexpress.pay.mq.RPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author jack.luo
 * @date 2019/12/6
 */
@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private RPCServer rpcServer;

    @Override
    public void run(String... args) throws Exception {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(rpcServer);
    }
}
