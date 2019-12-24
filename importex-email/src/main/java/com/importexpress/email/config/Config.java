package com.importexpress.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    public final static String QUEUE_MAIL = "mail";
    @Value("${save.html.path}")
    public String saveHtmlPath;
    @Value("${amazon.mail.host}")
    public String amazonMailHost;
    @Value("${amazon.mail.user}")
    public String amazonMailUser;
    @Value("${amazon.mail.pass}")
    public String amazonMailPass;
    @Value("${amazon.mail.port}")
    public String amazonMailPort;

}