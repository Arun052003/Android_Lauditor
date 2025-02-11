package com.digicoffer.lauditor.chatservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class ChatConnectionService extends Service {
    private static final String TAG = "RoosterService";

    public static final String UI_AUTHENTICATED = "com.digisec.digicoffer.uiauthenticated";
    public static final String SEND_MESSAGE = "com.digisec.digicoffer.sendmessage";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_MESSAGE_SUBJECT = "b_subject";
    public static final String BUNDLE_TO = "b_to";
    public static final String NEW_MESSAGE = "com.digisec.digicoffer.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";
    public static ChatConnection.ConnectionState sConnectionState;
    public static ChatConnection.LoggedInState sLoggedInState;
    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler;//We use this handler to post messages to
    //the background thread.
    private static ChatConnection mConnection;

    public ChatConnectionService() {

    }

    public static ChatConnection.ConnectionState getState() {
        if (sConnectionState == null) {
//            sConnectionState = ChatConnection.ConnectionState.CONNECTED;
            return ChatConnection.ConnectionState.DISCONNECTED;
//            mConnection.disconnect();
//            initConnection();
        }
        return sConnectionState;
    }

    public static ChatConnection.LoggedInState getLoggedInState() {
        if (sLoggedInState == null) {
            return ChatConnection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    private void initConnection() {
        Log.d(TAG, "initConnection()");
        if (mConnection == null) {
            mConnection = new ChatConnection(this);
        }
        try {
            mConnection.connect();

        } catch (IOException | SmackException | XMPPException e) {
            Log.d(TAG, "Something went wrong while connecting ,make sure the credentials are right and try again");
            e.fillInStackTrace();
            //Stop the service all together.
            stopSelf();
        }

    }


    public void start() {
        Log.d(TAG, " Service Start() function called.");
        if (!mActive) {
            mActive = true;
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }

        }

    }

    public void stop() {
        Log.d(TAG, "stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stop();
    }



}
