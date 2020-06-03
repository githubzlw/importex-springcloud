package com.importexpress.login.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Value("${IMPORT.GOOGLE.CLIENT.ID}")
    public String IMPORT_GOOGLE_CLIENT_ID;

    @Value("${IMPORT.FACEBOOK.CLIENT.ID}")
    public String IMPORT_FACEBOOK_CLIENT_ID;

    @Value("${IMPORT.FACEBOOK.CLIENT.SECRET}")
    public String IMPORT_FACEBOOK_CLIENT_SECRET;


    @Value("${KIDS.GOOGLE.CLIENT.ID}")
    public String KIDS_GOOGLE_CLIENT_ID;

    @Value("${KIDS.FACEBOOK.CLIENT.ID}")
    public String KIDS_FACEBOOK_CLIENT_ID;

    @Value("${KIDS.FACEBOOK.CLIENT.SECRET}")
    public String KIDS_FACEBOOK_CLIENT_SECRET;


    @Value("${PETS.GOOGLE.CLIENT.ID}")
    public String PETS_GOOGLE_CLIENT_ID;

    @Value("${PETS.FACEBOOK.CLIENT.ID}")
    public String PETS_FACEBOOK_CLIENT_ID;

    @Value("${PETS.FACEBOOK.CLIENT.SECRET}")
    public String PETS_FACEBOOK_CLIENT_SECRET;

}