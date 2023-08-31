package com.digicoffer.lauditor.chatservice;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.digicoffer.lauditor.LoginActivity.ConfirmationLogin;
import com.digicoffer.lauditor.MainActivity;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ChatConnection implements ConnectionListener {

    private static final String TAG = "RoosterConnection";

    private final Context mApplicationContext;
    private final String mUsername;
    private final String mPassword;
    private final String mServiceName;
    public static XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver;//Receives messages from the ui thread.
    private NotificationManager mManager;
    private StanzaListener presencePacketListener;


    public static enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED;
    }

    public static enum LoggedInState {
        LOGGED_IN, LOGGED_OUT;
    }


    public ChatConnection(Context context) {
        Log.d(TAG, "RoosterConnection Constructor called.");
        mApplicationContext = context.getApplicationContext();
        String jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_jid", null);
        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_password", null);

        if (jid != null) {
//            mUsername = jid.split("@")[0];
//            mServiceName = jid.split("@")[1];
            mUsername = jid+"@"+Constants.XMPP_DOMAIN;
            mServiceName = Constants.XMPP_DOMAIN;//"digicoffer.com";
        } else {
            mUsername = "";
            mServiceName = "";
        }
    }

    public SSLContext createContext() {
        KeyStore trustStore;

        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                trustStore = KeyStore.getInstance("AndroidCAStore");

            } else {

                trustStore = KeyStore.getInstance("BKS");

            }


            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            return sslContext;

        } catch (Exception e) {

            AndroidUtils.logMsg(e.getMessage());

            return null;

        }
    }


    public void connect() throws IOException, XMPPException, SmackException {
        Log.d(TAG, "Connecting to server " + mServiceName);
        Log.d(TAG, "Connecting to server " + mUsername);
//        URL url = new URL("ec2-3-17-191-76.us-east-2.compute.amazonaws.com");
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();

            XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(Constants.XMPP_DOMAIN)
                    .setHost(Constants.XMPP_DOMAIN)
//                    .setResource(mServiceName)
                    //Was facing this issue
                    //https://discourse.igniterealtime.org/t/connection-with-ssl-fails-with-java-security-keystoreexception-jks-not-found/62566
//                    .setKeystoreType(null) //This line seems to get rid of the problem
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
//                    .setSocketFactory(NoSSLv3Factory)
                    .setCustomSSLContext(sslContext)
                    .setSendPresence(true)
                    .setCompressionEnabled(true).build();

            Log.d(TAG, "Username : " + mUsername);
            Log.d(TAG, "Password : " + mPassword);
            Log.d(TAG, "Server : " + mServiceName);


            //Set up the ui thread broadcast message receiver.
            setupUiThreadBroadCastMessageReceiver();

            mConnection = new XMPPTCPConnection(conf);
        } catch (Exception e) {
            e.getMessage();
            Log.d(TAG, "Connecting to server " + e.getMessage());
        }
        mConnection.addConnectionListener(this);

        try {
            Log.d(TAG, "Calling connect() ");
            mConnection.connect();
            mConnection.login(mUsername, mPassword);
            Presence presence = new Presence(Presence.Type.subscribe);
            presence.setStatus("online");
            presence.setMode(Presence.Mode.available);
            Log.d(TAG, " login() Called ");
            mConnection.addSyncStanzaListener(new PresencePacketListener(mApplicationContext), new StanzaTypeFilter(Presence.class));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();
        ChatManager.getInstanceFor(mConnection).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid messageFrom, Message message, Chat chat) {
                ///ADDED
                Log.d(TAG, "message.getBody() :" + message.getBody());
                Log.d(TAG, "message.getFrom() :" + message.getFrom());

                String from = message.getFrom().toString();

                String contactJid = "";
                if (from.contains("/")) {
                    contactJid = from.split("/")[0];
                    Log.d(TAG, "The real jid is :" + contactJid);
                    Log.d(TAG, "The message is from :" + from);
                } else {
                    contactJid = from;
                }

                //Bundle up the intent and send the broadcast.
                show_notification(contactJid.split("@")[0], message.getBody(), message.getSubject());
                Intent intent = new Intent(ChatConnectionService.NEW_MESSAGE);
                intent.setPackage(mApplicationContext.getPackageName());
                intent.putExtra(ChatConnectionService.BUNDLE_FROM_JID, contactJid);
                intent.putExtra(ChatConnectionService.BUNDLE_MESSAGE_BODY, message.getBody());
                intent.putExtra(ChatConnectionService.BUNDLE_MESSAGE_SUBJECT, message.getSubject());
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(ChatConnectionService.NEW_MESSAGE);
//                mApplicationContext.registerReceiver(uiThreadMessageReceiver, filter);
                mApplicationContext.sendBroadcast(intent);
//                sendMessage(message.getBody(), contactJid);
                Log.d(TAG, "Received message from :" + contactJid + " broadcast sent.");
                ///ADDED

            }
        });

    }

    private void setupUiThreadBroadCastMessageReceiver() {
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check if the Intents purpose is to send the message.
                String action = intent.getAction();
                if (action.equals(ChatConnectionService.SEND_MESSAGE)) {
                    //Send the message.
                    sendMessage(intent.getStringExtra(ChatConnectionService.BUNDLE_MESSAGE_BODY),
                            intent.getStringExtra(ChatConnectionService.BUNDLE_TO), intent.getStringExtra(ChatConnectionService.BUNDLE_MESSAGE_SUBJECT));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ChatConnectionService.SEND_MESSAGE);
        mApplicationContext.registerReceiver(uiThreadMessageReceiver, filter);

    }

    private void sendMessage(String body, String toJid, String subject) {
        Log.d(TAG, "Sending message to :" + toJid);

        EntityBareJid jid = null;


        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);

        try {
            jid = JidCreate.entityBareFrom(toJid + "@" + Constants.XMPP_DOMAIN);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        Chat chat = chatManager.chatWith(jid);
        try {
            Message message = new Message(jid, Message.Type.chat);
            message.setBody(body);
            message.setSubject(subject);
            chat.send(message);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        Log.d(TAG, "Disconnecting from serser " + mServiceName);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
        prefs.edit().putBoolean("xmpp_logged_in", false).commit();


        if (mConnection != null) {
            mConnection.disconnect();
        }

        mConnection = null;
        // Unregister the message broadcast receiver.
        if (uiThreadMessageReceiver != null) {
            mApplicationContext.unregisterReceiver(uiThreadMessageReceiver);
            uiThreadMessageReceiver = null;
        }

    }


    @Override
    public void connected(XMPPConnection connection) {
        ChatConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "Connected Successfully");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        ChatConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "Authenticated Successfully");
//        showContactListActivityWhenAuthenticated();
        AndroidUtils.showToast("Connection established", mApplicationContext);
    }


    @Override
    public void connectionClosed() {
        ChatConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "Connectionclosed()");

    }

    //    @Override
    public void connectionClosedOnError(Exception e) {
        ChatConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "ConnectionClosedOnError, error " + e.toString());

    }

    //    @Override
    public void reconnectingIn(int seconds) {
        ChatConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG, "ReconnectingIn() ");

    }

    //    @Override
    public void reconnectionSuccessful() {
        ChatConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG, "ReconnectionSuccessful()");

    }

    //    @Override
    public void reconnectionFailed(Exception e) {
        ChatConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "ReconnectionFailed()");
    }

    private void showContactListActivityWhenAuthenticated() {
        Intent i = new Intent(ChatConnectionService.UI_AUTHENTICATED);
        i.setPackage(mApplicationContext.getPackageName());
        mApplicationContext.sendBroadcast(i);
        Log.d(TAG, "Sent the broadcast that we are authenticated");
    }

    @SuppressLint("NotificationPermission")
    public void show_notification(String contact, String message, String subject) {
        String notification_type = "";
        String relationship_id = "";
        String[] chat_details = new String[2];
        String[] split_value;
        Boolean allow_notification = true;
        if (subject.contains("##")) {
            split_value = subject.split("##");
            if (split_value[1].contains("-") && split_value[0].toLowerCase().trim().equals("relationship")) {
                String[] value = split_value[1].split("-");
                notification_type = value[0];
                relationship_id = value[1];
            } else {
//                notification_type = subject.contains("#N#") ? "chat" : split_value[1];
                notification_type = "chat";
                chat_details[0] = split_value[1];
                chat_details[1] = split_value[2].split("#N#")[1];
            }
        } else
            notification_type = subject;

        if (notification_type.equals("chat")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
            if(prefs.getString("current_fragment", "").toLowerCase().equals("messageslist")) {
                if(prefs.getString("CURRENTCHAT_JID", "").equals(chat_details[0]))
                {
                    allow_notification = false;
                }
            }
        }

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(allow_notification) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel androidChannel = new NotificationChannel("com.digisec.digicoffer",
                        "My Notification", NotificationManager.IMPORTANCE_HIGH);
                // Sets whether notifications posted to this channel should display notification lights
                androidChannel.enableLights(true);
                // Sets whether notification posted to this channel should vibrate.
                androidChannel.enableVibration(true);
                // Sets the notification light color for notifications posted to this channel
                androidChannel.setLightColor(Color.GREEN);
                androidChannel.setShowBadge(true);
                // Sets whether notifications posted to this channel appear on the lockscreen or not
                androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                getManager().createNotificationChannel(androidChannel);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder nb = getAndroidChannelNotification(contact, message, notification_type, chat_details);
                int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                getManager().notify(m, nb.build());
            } else {
                NotificationCompat.Builder notification = getNotificationOldMobile(contact, message, notification_type, chat_details);

                NotificationManager notificationManager = (NotificationManager)
                        mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                notificationManager.notify(m, notification.build());
            }
        }
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) mApplicationContext.getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public Notification.Builder getAndroidChannelNotification(String title, String body, String notification_type, String[] notify_details) {
        PendingIntent penint;
        PendingIntent pendingIntentConfirm = null;
        String title_notification = body;
        String body_notification = "";
        if (body.contains("\n")) {
            String[] msg = body.split("\n");
            title_notification = msg[0];
            for (int i = 1; i < msg.length; i++) {
                body_notification += msg[i];
            }
        }
        if (notification_type.equals("B") || notification_type.equals("C") || notification_type.equals("P")) {
            Intent intent = new Intent(mApplicationContext, MainActivity.class);
            String biz_type = AndroidUtils.getSharedPreferenceStringData("proBizType", mApplicationContext);
//            String rel_type = (biz_type == "BUSINESS") ? "B2BRelationship" : "ClientRelationship";
            String rel_type = "Relationship";
            intent.putExtra("fragment", "Relationship");
            intent.putExtra("TYPE", notification_type);
            penint = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else if(notification_type.equals("chat")) {
            Intent intent = new Intent(mApplicationContext, MainActivity.class);
            intent.putExtra("fragment", "CHAT");
            intent.putExtra("CONTACT_JID", notify_details[0]);
            intent.putExtra("CONTACT_NAME", notify_details[1]);
            body_notification = title_notification;
            title_notification = notify_details[1];
            penint = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent intent = new Intent(mApplicationContext, ConfirmationLogin.class);
            penint = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentConfirm = new Intent(mApplicationContext, ConfirmationLogin.class);
            intentConfirm.setAction("CONFIRM");
            intentConfirm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntentConfirm = PendingIntent.getActivity(mApplicationContext, 0, intentConfirm, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder notification = new Notification.Builder(mApplicationContext.getApplicationContext(), "com.digisec.digicoffer")
                    .setContentTitle(title_notification)
                    .setContentText(body_notification)
//                    .setSmallIcon(R.mipmap.digicofferlogo)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(body_notification))
                    .setContentIntent(penint)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            notification.setContentIntent(penint).getNotification();
//            if (!(notification_type.equals("B") || notification_type.equals("C") || notification_type.equals("P")))
//                notification.addAction(R.mipmap.delete, "Confirm", pendingIntentConfirm);
            if (Build.VERSION.SDK_INT >= 21) {
                notification.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE);
                notification.setPriority(Notification.PRIORITY_HIGH);
//                notification.setVibrate(new long[0]);

            }
            return notification;
        }
        return null;
    }

    public NotificationCompat.Builder getNotificationOldMobile(String title, String message, String notification_type, String[] notify_details) {
        PendingIntent penint;
        PendingIntent pendingIntentConfirm = null;
        String title_notification = message;
        String body_notification = "";
        if (message.contains("\n")) {
            String[] msg = message.split("\n");
            title_notification = msg[0];
            for (int i = 1; i < msg.length; i++) {
                title_notification += msg[i];
            }
        }
        if (notification_type.equals("B") || notification_type.equals("C") || notification_type.equals("P")) {
            Intent intent = new Intent(mApplicationContext, MainActivity.class);

            intent.putExtra("fragment", "Relationship");
            intent.putExtra("TYPE", notification_type);
            penint = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else if(notification_type.equals("chat")) {
            Intent intent = new Intent(mApplicationContext, MainActivity.class);
            intent.putExtra("fragment", "CHAT");
            intent.putExtra("EXTRA_CONTACT_JID", notify_details[0]);
            intent.putExtra("EXTRA_CONTACT_NAME", notify_details[1]);
            penint = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            Intent intent = new Intent(mApplicationContext, ConfirmationLogin.class);
            penint = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentConfirm = new Intent(mApplicationContext, ConfirmationLogin.class);
            intentConfirm.setAction("CONFIRM");
            intentConfirm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntentConfirm = PendingIntent.getActivity(mApplicationContext, 0, intentConfirm, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mApplicationContext)
                .setContentTitle(title_notification)
                .setContentText(body_notification)
                .setContentIntent(penint)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body_notification));
//                .setSmallIcon(R.mipmap.digicofferlogo);
//        if (!(notification_type.equals("B") || notification_type.equals("C") || notification_type.equals("P")))
//            notification.addAction(R.mipmap.delete, "Confirm", pendingIntentConfirm);
        if (Build.VERSION.SDK_INT >= 21) {
            notification.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE);
            notification.setPriority(Notification.PRIORITY_HIGH);
//                notification.setVibrate(new long[0]);

        }
        return notification;
    }

//    public static MamManager.MamQuery getArchivedMessages(String jid, int maxResults, String currentJID) {
//
//
//        try {
//            MamManager mamManager = MamManager.getInstanceFor(mConnection);
//            MamManager.MamQueryArgs mamQueryArgs = MamManager.MamQueryArgs.builder()
//                    .limitResultsToJid(JidCreate.entityBareFrom(currentJID + "@" + Constants.XMPP_DOMAIN))
//                    .setResultPageSize(10)
//                    .queryLastPage()
//                    .build();
//            MamManager.MamQuery mamQuery = mamManager.queryMostRecentPage(JidCreate.entityBareFrom(currentJID + "@" + Constants.XMPP_DOMAIN), 10);
//            return mamQuery;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static void getUserStatus(String toJid)
    {
        mConnection.setFromMode(XMPPConnection.FromMode.valueOf(toJid));
        mConnection.getFromMode();
    }

}


//PresencePacketListener
class PresencePacketListener implements StanzaListener {
    private Context context;

    PresencePacketListener(Context context) {
        this.context = context;
    }

    @Override
    public void processStanza(Stanza packet) {
        Presence presence = (Presence)packet;
        Presence.Type presenceType = presence.getType();
        //Do sth with presence
    }
}
