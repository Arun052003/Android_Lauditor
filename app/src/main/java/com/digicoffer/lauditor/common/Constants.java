package com.digicoffer.lauditor.common;

import com.digicoffer.lauditor.Chat.Model.ChildDO;
import com.digicoffer.lauditor.Matter.Models.GroupsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static String base_URL = "http://10.0.2.2:8011/consumer/";
    public static String TOKEN = "";
    public static JSONArray doc_id=new JSONArray();
    public static boolean Biometric_checked = false;
    public static boolean is_biometric = false;
    public static String NAME = "";
    public static String PROBIZ_TYPE = "";
    public static String ENTITY_ID = "";
    public static String pdfFilePath;
    public static String is_meeting = "Create";


    public static String Matter_CreateOrViewDetails = "Create";

    public static ArrayList<GroupsModel> groupsList_Access = new ArrayList<>();
    public static Boolean ISPRODUCTION = false;
    public static boolean IS_STAGING = true;
    public static String MATTER_TYPE = "";
    public static String msg_id = "";
    public static String part_id = "";
    public static boolean create_matter = true;

    //    public static String MyDay_KPI =
    public static String PROF_URL;
    public static String Matter_id;
    public static String EMAIL_BASE_URL;

    public static void check_url() {
        if (ISPRODUCTION) {
            PROF_URL = "https://api.digicoffer.com/professional/";
            EMAIL_BASE_URL = "https://mailapi.digicoffer.com/api/v1/";
        } else if (IS_STAGING) {
            PROF_URL = "https://api.staging.digicoffer.com/professional/";
            EMAIL_BASE_URL = "https://mailapi.digicoffer.com/api/v1/";
        } else {
            EMAIL_BASE_URL = "https://dev.utils.mail.digicoffer.com/api/v1/";
            PROF_URL = "https://apidev2.digicoffer.com/professional/";
        }
    }

    //https://api.digicoffer.com/professional/login

//    https://apidev2.digicoffer.com/professional/login

    //    https://api.staging.digicoffer.com/professional/login
//    public static String PROF_URL = ISPRODUCTION ? "https://api.digicoffer.com/professional/" : "https://apidev2.digicoffer.com/professional/";
    public static String BIZ_URL = ISPRODUCTION ? "https://api.digicoffer.com/business/" : "https://apidev.digicoffer.com/business/";
    //    public static final String EMAIL_BASE_URL = ISPRODUCTION ? "https://mailapi.digicoffer.com/api/v1/" : "https://dev.utils.mail.digicoffer.com/api/v1/";
    public static final String EMAIL_UPLOAD_URL = "https://mailapi.digicoffer.com/api/v1/";
    public static final String EMAIL_LISTING_URL = "https://mailapi.digicoffer.com/api/v1/";

    public static final String gmail_label = "gmail/label/";
    public static final String gmail_messages = "gmail/messages/";
    public static final String gmail_document = "gmail/message/attachment/upload/";
    public static final String sending_email = "gmail/sendmail/attach/documents/";

    //    public static String PROF_URL = "http://10.0.2.2:8011/professional/";
//    public static String BIZ_URL = "http://10.0.2.2:8011/business/";
//    public static String PROF_URL = "http://10.0.2.2:8011/professional/";
//    public static String BIZ_URL = "http://10.0.2.2:8011/business/";
//    public static String base_URL = "";
    //    public static String base_URL = "https://apidev.digicoffer.com/business/";
    public static String USER_ID = "";
    public static String FIRM_NAME = "";
    public static boolean IS_ADMIN = true;
    public static boolean Valid_Token = true;
    public static String UID = "";
    public static String ROLE = "";
    public static String PASSWORD_MODE = "";
    public static String OLD_PASSWORD = "";
    public static String COUNT = "";
    public static String PK = "";
    public static String Old_Token = "";
    public static String VERSION = ISPRODUCTION ? "1.0.2" : "1.0.29";
    //    public static String XMPP_DOMAIN = "dev.chat.digisecitus.com";
    public static String XMPP_DOMAIN = ISPRODUCTION ? "chat.digicoffer.com" : "devchat.vitacape.com";
    public static String DOWNLOAD_VIEWFILE_TAG = "DOWNLOAD_VIEWFILE";

    //public static String email = "akhilaTM2@mailinator.com";
    public static String email = "akhila.bs@lauditor.com";//akhila.bs@lauditor.com, soundaryavembaiyan@yahoo.com
    public static String password = "Test@123";

    public static JSONArray teamResArray = new JSONArray();

    public static Map<String, ArrayList<ChildDO>> teamMapChatList = new HashMap<String, ArrayList<ChildDO>>();

    public static String chat_SENT = "SENT";
    public static JSONObject jsonObject_dashboard;
    public static String chat_RECEIVE = "RECEIVE";
    public static String meetingApiEndpoint = "v3/dashboard/meeting/-330";
    public static String chatRelationshipApiEndpoint = "v3/dashboard/otherapi";
    public static String Dashboard = "v3/dashboard/layout";
    public static String clientTeamApiEndpoint = "v3/dashboard/chat-team";
    public static String emailApiEndpoint = "v3/dashboard/email";
    public static String hoursEndpoint = "v3/dashboard/hours";
    public static String hiringEndpoint = "v3/dashboard/hiring";
    public static String storageEndpoint = "v3/dashboard/storage";
    public static String timesheetEndpoint = "v3/dashboard/timesheet";
    public static String matterEndpoint = "v3/dashboard/matter";
    public static String subscriptionEndpoint = "v3/dashboard/subscription";
    public static String groupsEndpoint = "v3/dashboard/groupsandtms";
    public static String newclientEndpoint = "v3/dashboard/new-clients";
    public static String externalCounselsEndpoint = "v3/dashboard/external-counsels";
    public static String relationshipsEndpoint = "v3/dashboard/relationships";
    public static String notificationEndpoint = "v3/dashboard/notification";
}
