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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ProgressBar progressBar;
    List<Flower> flowers;
    String xmlUrl = "http://services.hanselandpetal.com/secure/flowers.json";
    CustomAdapter adapter;
    ListView list;

    public static final String BASE = "http://services.hanselandpetal.com/photos/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        list = (ListView) findViewById(R.id.list);
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
                                getData();
                            else
                                Toast.makeText(this,"Not connected to Internet !",Toast.LENGTH_LONG).show();
                            break;
            default:
                            break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData(){
        MyTask task = new MyTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,xmlUrl);
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

    class MyTask extends AsyncTask<String,String,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String data = HttpManager.getData(params[0],"feeduser","feedpassword");

            flowers = FlowerJSONParser.parseFeed(data);

            for (Flower flower:flowers) {

                String url = BASE + flower.getPhoto();
                try {
                    InputStream in = (InputStream) new URL(url).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    flower.setBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return data;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            update();

        }
    }


    class CustomAdapter extends BaseAdapter{

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
            View view = inflater.inflate(R.layout.list_item,parent,false);

            Flower flower = flowers.get(position);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(flower.getName());



            //Imageview here


            ImageView image = (ImageView) view.findViewById(R.id.image);
            image.setImageBitmap(flower.getBitmap());

            return view;

        }
    }
}
