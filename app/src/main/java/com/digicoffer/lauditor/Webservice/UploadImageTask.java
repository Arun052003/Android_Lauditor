package com.digicoffer.lauditor.Webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;

import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class UploadImageTask extends AsyncTask<String, String, HttpResultDo> {
    File upload_file;
    private String requestType = null;
    private boolean sslFlag = false;
    WebServiceHelper.RestMethodType restMethodType = WebServiceHelper.RestMethodType.GET;
    private String URL = "";
    private AsyncTaskCompleteListener callback;
    private Context activity = null;

    public UploadImageTask(File file, boolean sslFlag, WebServiceHelper.RestMethodType restMethodType, String baseURL,
                           AsyncTaskCompleteListener callback, Context activity, String requestType) {
        super();
        this.upload_file = file;
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

    @Override
    protected HttpResultDo doInBackground(String... params) {
        String resp = "";
        String boundary = "*****";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String fieldName = "file";
        String response = "";
        HttpResultDo httpResult = new HttpResultDo();
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(Constants.base_URL + this.URL).openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true); // indicates POST method
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + Constants.TOKEN);
            httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
//                httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
            httpURLConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            JSONObject json = new JSONObject(params[0]);

            DataOutputStream request = new DataOutputStream(httpURLConnection.getOutputStream());
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = json.get(key);
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n");
                    request.writeBytes(json.getString(key) + crlf);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            request.writeBytes(twoHyphens+boundary+crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"content_type\"\r\n\r\n");
            request.writeBytes(getMimeType(upload_file.getAbsolutePath()) + crlf);
            String fileName = upload_file.getName();
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    fieldName + "\";filename=\"" +
                    fileName + "\"" + crlf);
            request.writeBytes(crlf);
            byte[] file_bytes = readBytesFromFile(upload_file);
            request.write(file_bytes);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = new
                        BufferedInputStream(httpURLConnection.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();
                response = stringBuilder.toString();
                httpResult.setResult(WebServiceHelper.ServiceCallStatus.Success);
                httpResult.setResponseContent(response);
//
            } else {
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

    protected void onPostExecute(HttpResultDo httpResult) {
        httpResult.setRequestType(requestType);
        super.onPostExecute(httpResult);
        try {
            callback.onAsyncTaskComplete(httpResult);
        } catch (Exception e) {
        }
    }

    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static byte[] readBytesFromFile(File file) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

//            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.fillInStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }

        }

        return bytesArray;

    }
}
