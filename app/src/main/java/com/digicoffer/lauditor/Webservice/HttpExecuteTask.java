package com.digicoffer.lauditor.Webservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.digicoffer.lauditor.LoginActivity.LoginActivity;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpExecuteTask extends AsyncTask<String, Integer, HttpResultDo> {

    public static AtomicInteger openCounter = new AtomicInteger(0);
    public static final int MAX_CONCURRENCY = 1;
    private HttpResultDo httpResult = null;
    private String requestId = null;
    private String requestType = null;
    private boolean sslFlag = false;
    WebServiceHelper.RestMethodType restMethodType = WebServiceHelper.RestMethodType.GET;
    private String URL = "";
    private AsyncTaskCompleteListener callback;
    private Context activity = null;

    public HttpExecuteTask(String requestId, boolean sslFlag, WebServiceHelper.RestMethodType restMethodType, String baseURL,
                           AsyncTaskCompleteListener callback, Context activity, String requestType) {
        super();
        this.requestId = requestId;
        this.sslFlag = sslFlag;
        this.restMethodType = restMethodType;
        this.URL = baseURL;
        this.callback = callback;
        this.activity = activity;
        this.requestType = requestType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected HttpResultDo doInBackground(String... params) {
        String data = "";
        HttpURLConnection httpURLConnection = null;
        HttpResultDo httpResult = new HttpResultDo();
//        if(!AndroidUtils.isNetworkAvailable(activity))
//        {
//            httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
//            httpResult.setResponseContent("Please check your internet connectivity...");
//            return httpResult;
//        }
        try {

            httpURLConnection = (HttpURLConnection) new URL(Constants.base_URL + URL).openConnection();
            Log.d("URL",":"+Constants.base_URL + URL);

            if (requestType != "LOGIN" && requestType != "SIGNUP" && requestType != "FORGET_PASSWORD" && requestType != "VERIFY_TOKEN") {
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + Constants.TOKEN);
                Log.d("Token",":"+"Bearer " + (Constants.TOKEN )+":"+httpURLConnection);
            }
            switch (restMethodType) {
                case GET:
                    httpURLConnection.setRequestMethod("GET");
                    break;
                case POST:
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes("" + params[0]);
                    wr.flush();
                    wr.close();
                    break;
                case PUT:
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.setDoInput(true);
                    DataOutputStream wr1 = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr1.writeBytes("" + params[0]);
                    wr1.flush();
                    wr1.close();
                    break;
                case DELETE:
                    httpURLConnection.setRequestMethod("DELETE");
                    if (params[0].length() != 0) {
                        httpURLConnection.setDoInput(true);
                        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                        dos.writeBytes("" + params[0]);
                        dos.flush();
                        dos.close();
                    }
                    break;
            }

            int status_code = httpURLConnection.getResponseCode();
            Log.i("status_code:",String.valueOf(status_code));
            httpResult.setStatus_code(status_code);
            Log.d("SCode:",String.valueOf(status_code));
            if (status_code == 200) {
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader r = new BufferedReader(inputStreamReader);
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n'); }
                data = total.toString();
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Success);
                httpResult.setResponseContent(data);
            }
            else if(status_code == 401){
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                httpResult.setResponseContent("Session expired, Login again");
            }   else if (status_code== 404){
                SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isLogin", false);
                editor.commit();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            }
            else {
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                httpResult.setResponseContent("Errr connection, Please try again");
            }

        } catch (ConnectTimeoutException e) {
            httpResult.setResult(WebServiceHelper.ServiceCallStatus.Exception);
            httpResult.setErrorMessage("Exception: Connection Timeout " + e.getMessage());
        } catch (IOException e) {
            httpResult.setResult(WebServiceHelper.ServiceCallStatus.Exception);
            httpResult.setErrorMessage("Exception: " + e.getMessage());
            AndroidUtils.logMsg("WebServiceHelper.callWebService(): IO Exception " + e.getMessage());
        } catch (Exception e) {
            AndroidUtils.logMsg("HttpExecuteTask.doInBackground(): Exception " + e.getMessage());
            httpResult = new HttpResultDo();
            httpResult.setResult(WebServiceHelper.ServiceCallStatus.Exception);
            httpResult.setResponseContent(e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return httpResult;
    }

    protected void onProgressUpdate(Integer... progress) {

    }
    protected void onPostExecute(HttpResultDo httpResult) {
        openCounter.decrementAndGet();
        httpResult.setRequestId(requestId);
        httpResult.setRequestType(requestType);
//        AndroidUtils.logMsg("HttpExecuteTask.onPostExecute() " + httpResult);
        super.onPostExecute(httpResult);
        try {
            if (httpResult.getStatus_code() == 401) {
                Intent in = new Intent(activity, LoginActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(in);
                AndroidUtils.showToast("Session expired, Login agiain", activity);
            } else
                callback.onAsyncTaskComplete(httpResult);
        } catch (Exception e) {
//            AndroidUtils.logMsg("HttpExecuteTask.onPostExecute() : Exception " + e.getMessage());
        }
    }

}
