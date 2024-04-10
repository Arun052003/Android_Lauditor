package com.digicoffer.lauditor.Webservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.digicoffer.lauditor.LoginActivity.LoginActivity;
import com.digicoffer.lauditor.LoginActivity.biometric_page;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MultiPartHttpExecute extends AsyncTask<String, Integer, HttpResultDo> {

    public static AtomicInteger openCounter = new AtomicInteger(0);
    public static final int MAX_CONCURRENCY = 1;
//    private HttpResultDo httpResult = null;
    private String requestId = null;
    private String requestType = null;
    private boolean sslFlag = false;
    WebServiceHelper.RestMethodType restMethodType = WebServiceHelper.RestMethodType.GET;
    private String URL = "";
    private AsyncTaskCompleteListener callback;
    private Context activity = null;

    HttpResultDo httpResult = new HttpResultDo();

    public MultiPartHttpExecute(String requestId, boolean sslFlag, WebServiceHelper.RestMethodType restMethodType, String baseURL,
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
        String fullUrl = "";


        try {
            if (requestType.equals("Label") || (requestType.equals("auth")) || (requestType.equals("messages_rows")) || (URL.contains(Constants.EMAIL_UPLOAD_URL))) {
                fullUrl = URL;
            } else {
                fullUrl = Constants.base_URL + URL;
            }
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, params[0]);
            Request request = new Request.Builder()
                    .url(fullUrl)
                    .method(this.restMethodType.toString(), body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + Constants.TOKEN)
                    .build();
            // Perform the request asynchronously
            Response response = client.newCall(request).execute();

            int status_code = response.code();
            Log.i("status_code:", String.valueOf(status_code));
            httpResult.setStatus_code(status_code);
            // Check if the response is successful
            if (response.isSuccessful()) {
                // Handle successful response
                String responseData = response.body().string();
                Log.d("Response", responseData);
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Success);
                httpResult.setResponseContent(response.body().string());
                return httpResult;
            } else {
                // Handle unsuccessful response
                String responseData = response.body().string();
                Log.e("Response", "Error: " + response.code());
                Log.e("Response", "Body: " + responseData);
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                httpResult.setResponseContent(responseData);
                return httpResult;
            }
           /* client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        String responseData = response.body().string();
                        Log.d("Response", responseData);
                        httpResult.setResult(WebServiceHelper.ServiceCallStatus.Success);
                        httpResult.setResponseContent(response.body().string());
                    } else {
                        // Handle unsuccessful response
                        Log.e("Response", "Error: " + response.code());
                        Log.e("Response", "Error: " + response.body().string());
                        httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                        httpResult.setResponseContent(response.body().string());
                        return httpResult;
                    }
                }
            }); */
        }
        /*    int status_code = httpURLConnection.getResponseCode();
            Log.i("status_code:", String.valueOf(status_code));
//            AndroidUtils.showAlert(String.valueOf(status_code), activity.getApplicationContext());
            httpResult.setStatus_code(status_code);
            Log.e("SCode:", String.valueOf(status_code));
            if (status_code == 200) {
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader r = new BufferedReader(inputStreamReader);
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                data = total.toString();
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Success);
                httpResult.setResponseContent(data);
            } else if (status_code == 401) {
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                httpResult.setResponseContent("Session expired, Login again");
            } else if (status_code == 404) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isLogin", false);
                editor.commit();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            } else if (status_code == 400) {
                try {
//                    InputStream in = httpURLConnection.getErrorStream();
////                    InputStreamReader inputStreamReader = new InputStreamReader(in);
////                    InputStream in = httpURLConnection.getInputStream();
//                    InputStreamReader inputStreamReader = new InputStreamReader(in);
//                    BufferedReader r = new BufferedReader(inputStreamReader);
                    BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    data = total.toString();
                    httpResult.setResult(WebServiceHelper.ServiceCallStatus.Success);
                    httpResult.setResponseContent(data);
//                    JSONObject result = new JSONObject(httpResult.getResponseContent());
//                    startActivity(new Intent(this, MainActivity.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Failed);
                httpResult.setResponseContent("Errr connection, Please try again");
            }
        }*/
        catch (Exception e) {
            AndroidUtils.logMsg("HttpExecuteTask.doInBackground(): Exception " + e.getMessage());
            httpResult = new HttpResultDo();
            httpResult.setResult(WebServiceHelper.ServiceCallStatus.Exception);
            httpResult.setResponseContent(e.getMessage());
            return httpResult;
        } finally {

        }
    }

    protected void onPostExecute(HttpResultDo httpResult) {
        openCounter.decrementAndGet();
        httpResult.setRequestId(requestId);
        httpResult.setRequestType(requestType);
//        AndroidUtils.logMsg("HttpExecuteTask.onPostExecute() " + httpResult);
        super.onPostExecute(httpResult);
        try {
            Log.e("Response_Msg",httpResult.getResponseContent());
            if (httpResult.getStatus_code() == 401 && !(requestType.equals("Label")) && !(requestType.equals("auth"))
                    && !(requestType.equals("messages_rows"))  &&! (URL.contains(Constants.EMAIL_UPLOAD_URL)) && !(requestType.equals("Dashboard"))) {
                Intent in = new Intent(activity, LoginActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(in);
                AndroidUtils.showToast("Session expired, Login again", activity);
            } else if ((requestType.equals("Dashboard")) && (httpResult.getStatus_code() == 401)) {
                Constants.Valid_Token = false;
                Intent nav_bio;
                if (Constants.Biometric_checked) {
                    nav_bio = new Intent(activity, biometric_page.class);
                } else {
                    nav_bio = new Intent(activity, LoginActivity.class);
                }
                activity.startActivity(nav_bio);
            } else
                Constants.Valid_Token = true;
            callback.onAsyncTaskComplete(httpResult);
        } catch (Exception e) {
            Log.d("Error_mg", Objects.requireNonNull(e.getMessage()));
            AndroidUtils.logMsg("HttpExecuteTask.onPostExecute() : Exception " + e.getMessage());
        }
    }
}
