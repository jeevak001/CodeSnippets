package com.example.jeeva.webapp;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ProgressBar progressBar;
    List<Flower> flowers;
    String xmlUrl = "http://services.hanselandpetal.com/feeds/flowers.json";
    CustomAdapter adapter;
    ListView list;
    RequestQueue queue;

    public static final String BASE = "http://services.hanselandpetal.com/photos/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        list = (ListView) findViewById(R.id.list);
        queue = Volley.newRequestQueue(this);
    }


    public void update(){

        if(flowers != null){

            adapter = new CustomAdapter();
            list.setAdapter(adapter);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch(itemId){

            case R.id.get:
                            if(isOnline())
                                getData(xmlUrl);
                            else
                                Toast.makeText(this,"Not connected to Internet !",Toast.LENGTH_LONG).show();
                            break;
            default:
                            break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData(final String uri){

        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        flowers = FlowerJSONParser.parseFeed(s);
                        update();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this,"Some Error",Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public boolean isOnline(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }


    class CustomAdapter extends BaseAdapter {

        LruCache<Integer, Bitmap> imageCache;

        public CustomAdapter() {

            final int maxMemory = (int) (Runtime.getRuntime().maxMemory()) / 1024;
            final int cacheSize = maxMemory / 8;
            imageCache = new LruCache<>(cacheSize);

        }

        @Override
        public int getCount() {
            return flowers.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_item, parent, false);

            final Flower flower = flowers.get(position);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(flower.getName());

            Bitmap bitmap = imageCache.get(flower.getProductID());

            final ImageView image = (ImageView) view.findViewById(R.id.image);

            if (bitmap != null) {

                image.setImageBitmap(flower.getBitmap());

            } else {

                String url = BASE + flower.getPhoto();

                ImageRequest imageRequest = new ImageRequest(url,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                image.setImageBitmap(bitmap);
                                flower.setBitmap(bitmap);
                                imageCache.put(flower.getProductID(),bitmap);
                            }
                        },
                        80, 80,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d("Error", "Image GET Error");
                            }
                        }
                );

                queue.add(imageRequest);
            }


            return view;

        }
    }


}
