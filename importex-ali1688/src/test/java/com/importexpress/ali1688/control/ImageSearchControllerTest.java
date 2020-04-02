package com.importexpress.ali1688.control;


import com.importexpress.ali1688.service.Ali1688CacheService;
import com.importexpress.ali1688.service.Ali1688Service;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @Author jack.luo
 * @create 2020/4/2 11:18
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageSearchControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void test() throws Exception {

        MockMultipartFile firstFile = new MockMultipartFile("file", "1111.jpg", "image/jpeg", Files.readAllBytes(Paths.get("C:\\Users\\luohao\\Downloads\\1111.jpg")));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/searchimg/upload")
                .file(firstFile)
                .param("some-random", "4"))
                .andExpect(status().is(200))
                .andExpect(content().string("success"));
    }


}
