package com.importexpress.ali1688.control;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ali1688ControllerTest {


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
        String id = "shop1432227742608";
        mockMvc.perform(get("/shop/"+id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].num_iid").value("588766108897"))
                .andDo(print());
    }

//    @Test
//    public void clearNotExistItemInCache() throws Exception {
//        mockMvc.perform(get("/pids/clearNotExistItemInCache"))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }

    @Test
    public void getNotExistItemInCache() throws Exception {
        mockMvc.perform(get("/pids/getNotExistItemInCache"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}


