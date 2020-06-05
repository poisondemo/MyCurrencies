package com.poisondemo.mycurrencies;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {
    static InputStream sInputStream = null;
    static JSONObject sReturnJsonObject = null;
    static String sRawJsonString = "";
    public JSONParser() {}
    public JSONObject getJSONFromUrl(String url) {
        try{
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                sInputStream = connection.getInputStream();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(sInputStream,"iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!=null){
                stringBuilder.append(line + "\n");
            }
            sInputStream.close();
            sRawJsonString = stringBuilder.toString();
        }
        catch (Exception e){
            Log.e( this.getClass().getSimpleName(),"Error reading from Buffer:"+e.toString());
        }
        try {
            sReturnJsonObject = new JSONObject(sRawJsonString);
        }
        catch (JSONException e){
            Log.e("Parser","Error when parsing data "+e.toString());
        }
        return sReturnJsonObject;
    }
}
