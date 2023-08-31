package com.digicoffer.lauditor.common;

public class Constants {
    public static String base_URL = "http://10.0.2.2:8011/consumer/";
    public static String TOKEN = "";
    public static String NAME = "";
    public static String PROBIZ_TYPE = "";
    public static String ENTITY_ID = "";
    public static String pdfFilePath;
    public static Boolean ISPRODUCTION = false;
    public static String MATTER_TYPE = "";

//    public static String MyDay_KPI =
    public static String PROF_URL = ISPRODUCTION ? "https://api.digicoffer.com/professional/" : "https://apidev2.digicoffer.com/professional/";
    public static String BIZ_URL = ISPRODUCTION ? "https://api.digicoffer.com/business/" : "https://apidev.digicoffer.com/business/";
    //    public static String PROF_URL = "http://10.0.2.2:8011/professional/";
//    public static String BIZ_URL = "http://10.0.2.2:8011/business/";
//    public static String PROF_URL = "http://10.0.2.2:8011/professional/";
//    public static String BIZ_URL = "http://10.0.2.2:8011/business/";
//    public static String base_URL = "";
    //    public static String base_URL = "https://apidev.digicoffer.com/business/";
    public static String USER_ID ="";
    public static String FIRM_NAME ="";
    public static boolean IS_ADMIN = true;
    public static String UID ="";
    public static String ROLE = "";
    public static String COUNT = "";
    public static String VERSION = ISPRODUCTION? "1.0.2" : "1.0.24";
    //    public static String XMPP_DOMAIN = "dev.chat.digisecitus.com";
    public static String XMPP_DOMAIN = ISPRODUCTION ? "chat.digicoffer.com" : "devchat.vitacape.com";
    public static String DOWNLOAD_VIEWFILE_TAG = "DOWNLOAD_VIEWFILE";

    public static String email = "rajendra.sai@digicoffer.com";
    public static String password = "Test@123";
}
