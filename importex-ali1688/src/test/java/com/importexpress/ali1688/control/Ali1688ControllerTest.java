package com.importexpress.ali1688.control;

import com.importexpress.ali1688.service.Ali1688CacheService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ali1688ControllerTest {

    @Autowired
    private Ali1688CacheService ali1688CacheService;


    @Autowired
    private WebApplicationContext wac;


    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }


    @Test
    public void pid() throws Exception {
        String pid = "548125319390";
        mockMvc.perform(get("/pids/"+pid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].item").exists())
                .andExpect(jsonPath("$.[0].item.num_iid").value(pid))
                .andDo(print());
    }

    @Test
    public void pids() throws Exception {
        String pid = "548125319390,550142549080";
        mockMvc.perform(get("/pids/"+pid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].item.num_iid").exists())
                .andExpect(jsonPath("$.[1].item.num_iid").exists())
                .andDo(print());
    }

    @Test
    public void shopid() throws Exception {
        String id = "aodazhiyichang";
        mockMvc.perform(get("/shop/"+id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andDo(print());
    }

    //清空redis中没有数据的所有pid
    @Test
    public void clearNotExistItemInCache() throws Exception {
        mockMvc.perform(get("/pids/clearNotExistItemInCache"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getNotExistItemInCache() throws Exception {
        mockMvc.perform(get("/pids/getNotExistItemInCache"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void setItemsExpire1() throws Exception {
        mockMvc.perform(get("/pids/setItemsExpire").param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    public void setItemsExpire2() throws Exception {
        mockMvc.perform(get("/pids/setItemsExpire").param("days", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"));
    }

    @Test
    public void setItemsExpire3() throws Exception {
        mockMvc.perform(get("/pids/setItemsExpire").param("days", "-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"));
    }

    @Test(expected = AssertionError.class)
    public void setItemsExpire4() throws Exception {
        mockMvc.perform(get("/pids/setItemsExpire").param("days", ""))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void regetNoDescPids() {

        Pair<List<String>, List<String>> pair = ali1688CacheService.checkDescInAllPids(true);

        pair.getLeft().stream().parallel().forEach( pid -> {
            try {
                long lngPid = Long.parseLong(pid.split(":")[3]);
                System.out.println("recatch pid:"+lngPid);
                mockMvc.perform(get("/pids/"+lngPid))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.[0].item").exists())
                        .andExpect(jsonPath("$.[0].item.num_iid").value(lngPid))
                        .andDo(print());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }
}