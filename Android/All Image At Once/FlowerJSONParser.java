package com.example.jeeva.webapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeeva on 2/28/2016.
 */
public class FlowerJSONParser {

    public static List<Flower> parseFeed(String content){

        try {
            JSONArray ar = new JSONArray(content);
            List<Flower> flowers = new ArrayList<>();


            for(int i=0;i<ar.length();i++){


                JSONObject object = ar.getJSONObject(i);
                Flower flower = new Flower();


                flower.setProductID(object.getInt("productId"));
                flower.setPrice(object.getDouble("price"));
                flower.setName(object.getString("name"));
                flower.setPhoto(object.getString("photo"));
                flower.setCategory(object.getString("category"));
                flower.setInstructions(object.getString("instructions"));

                flowers.add(flower);


            }
            return flowers;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }
}
