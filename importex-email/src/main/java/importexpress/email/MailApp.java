package importexpress.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author jack.luo
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class MailApp {

    public static void main(String[] args)  {

        SpringApplication.run(MailApp.class, args);
    }
}
