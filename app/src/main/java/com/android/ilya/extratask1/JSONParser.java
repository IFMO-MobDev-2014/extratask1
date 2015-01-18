package com.android.ilya.extratask1;

/**
 * Created by Ilya on 16.01.2015.
 */
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class JSONParser {

    public JSONParser() {
        json = "";
    }

    JSONObject jObj;
    String json;
    HttpPost httpPost;
    HttpClient client;
    BufferedReader br;


    public JSONObject makeHttpRequest(String url,
                                      List<NameValuePair> params) {

        try {
            client = new DefaultHttpClient();
            httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = client.execute(httpPost);
            br = new BufferedReader(new InputStreamReader(httpResponse
                    .getEntity().getContent(), "UTF-8"));
            String line = br.readLine();
            while (line != null) {
                json += line + "\n";
                line = br.readLine();
            }
            br.close();
            if (json.startsWith("jsonFlickrApi"))
                json = json.substring(14, json.length() - 1);
            jObj = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jObj;

    }
}
