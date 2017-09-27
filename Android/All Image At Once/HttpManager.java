package com.example.jeeva.webapp;


import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jeeva on 2/28/2016.
 */
public class HttpManager {

    public static String getData(String uri){

        BufferedReader reader = null;
        try{

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            return sb.toString();

        }catch(Exception e){
            e.printStackTrace();
        }finally {

            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static String getData(String uri,String username,String password){

        BufferedReader reader = null;

        byte[] loginBytes = (username + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes,Base64.DEFAULT));

        try{

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.addRequestProperty("Authorization",loginBuilder.toString());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            return sb.toString();

        }catch(Exception e){
            e.printStackTrace();
        }finally {

            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }
}