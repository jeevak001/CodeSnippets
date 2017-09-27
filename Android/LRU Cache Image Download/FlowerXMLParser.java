package com.example.jeeva.webapp;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeeva on 2/28/2016.
 */
public class FlowerXMLParser {


    public static List<Flower> parseFeed(String content){

        try{

            boolean inDataItemTag = false;
            String currentTagName = "";
            Flower flower = null;
            List<Flower> flowers = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){

                switch (eventType){

                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if(currentTagName.equals("product")){
                            inDataItemTag = true;
                            flower = new Flower();
                            flowers.add(flower);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("product")){
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;

                    case XmlPullParser.TEXT:
                        if(inDataItemTag && flower != null){
                            switch (currentTagName){
                                case "productId":
                                    flower.setProductID(Integer.parseInt(parser.getText()));
                                    break;

                                case "name":
                                    flower.setName(parser.getText());
                                    break;

                                case "instructions":
                                    flower.setInstructions(parser.getText());
                                    break;

                                case "price":
                                    flower.setPrice(Double.parseDouble(parser.getText()));
                                    break;

                                case "photo":
                                    flower.setPhoto(parser.getText());
                                    break;

                                default:
                                    break;
                            }
                        }
                        break;
                }

                eventType = parser.next();
            }

        return flowers;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }


    }
}
