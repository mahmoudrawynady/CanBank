package com.example.kashasha.rvm;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button serach;
    EditText phone;
    TextView pointsNumbers,message;
    CardView points, careem;
    FrameLayout line;
    int pots;
   ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serach = (Button) findViewById(R.id.action);
        phone = (EditText) findViewById(R.id.phone);
        points = (CardView) findViewById(R.id.points);
        careem = (CardView) findViewById(R.id.careem);
        pointsNumbers = (TextView) findViewById(R.id.points_numbers);
        line= (FrameLayout) findViewById(R.id.line);
        message= (TextView) findViewById(R.id.message);
        progressBar= (ProgressBar) findViewById(R.id.progress);
        serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter a Number", Toast.LENGTH_LONG).show();
                }
                else  if(phone.getText().toString().length()>11 || phone.getText().toString().length()<11){
                    Toast.makeText(getApplicationContext(),"please Enter a Valid Number",Toast.LENGTH_LONG).show();
                }
                else if(phone.getText().toString().regionMatches(0,"010",0,3)!=true&&
                        phone.getText().toString().regionMatches(0,"011",0,3)!=true&&
                phone.getText().toString().regionMatches(0,"012",0,3)!=true){

                    Toast.makeText(getApplicationContext(),"please Enter a Valid Number",Toast.LENGTH_LONG).show();

                }
                else {
                    if(isConnected()==false){

                        Toast.makeText(getApplicationContext(),"There is not Connection",Toast.LENGTH_LONG).show();
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        new AsyncTaskRunner().execute();
                    }
                }
            }
        });
        careem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pots<0){
                    Toast.makeText(getApplicationContext(),"You have no enough points",Toast.LENGTH_LONG).show();

                }
                else {
                    pots--;
                    pointsNumbers.setText("You have "+ pots+" Points");
                    new  AsyncTaskRunner2().execute();
                }
            }
        });


    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, String> {

        private String resp;
        ProgressDialog progressDialog;


        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Response response=null;
            String returned=null;
            try {
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, "\r\n \"accumlator\": {\r\n        \"type\": \"integer\",\r\n        \"value\": \"0\",\r\n        \"metadata\": {}\r\n    },\r\n    \"lastvalue\": {\r\n        \"type\": \"integer\",\r\n        \"value\": \"2\",\r\n    },\r\n    \"redeem\": {\r\n        \"type\": \"string\",\r\n        \"value\": \"1\",\r\n        \"metadata\": {}\r\n    }");
                Request request = new Request.Builder()
                        .url("http://130.206.125.10:1026/v2/entities/"+phone.getText().toString())
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "d4d7f4a7-dc6c-fdb1-7b94-f8bdcaec582d")
                        .build();


                response = client.newCall(request).execute();
                if(response.isSuccessful()) {
                    returned = response.body().string();
                }
                else  return null;
            }
                catch (IOException e) {
                    e.printStackTrace();
                }
           return  returned;


        }


        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            if(result==null){
                Toast.makeText(getApplicationContext(),"There is an Error check your Network or you have no points",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject user= null;
            try {
                user = new JSONObject(result);

               //=user.getInt("lastvalue");
                Log.e("rawy", ""+user.getJSONObject("lastvalue").getInt("value"));
                pots=user.getJSONObject("lastvalue").getInt("value");
                pointsNumbers.setText("You have "+ pots+" Points");
                points.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                careem.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    private class AsyncTaskRunner2 extends AsyncTask<Void, Void, String> {

        private String resp;
        ProgressDialog progressDialog;


        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Response response=null;
            String returned=null;
            try {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, " {\"accumlator\": {\n        \"type\": \"integer\",\n        \"value\": \"9\",\n        \"metadata\": {}\n    },\n    \"lastvalue\": {\n        \"type\": \"integer\",\n        \"value\":"+String.valueOf(pots)+ ",\n        \"metadata\": {}\n    },\n    \"redeem\": {\n        \"type\": \"string\",\n        \"value\": \"1\",\n        \"metadata\": {}\n    }\n }");

                Request request = new Request.Builder()
                        .url("http://130.206.125.10:1026/v2/entities/"+phone.getText().toString()+"/attrs")
                        .patch(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "29111007-e68f-5c70-976e-98b9c59c139b")
                        .build();

                response = client.newCall(request).execute();
                if(response.isSuccessful()) {
                    returned = response.body().string();
                }
                else  return null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return  returned;


        }


        @Override
        protected void onPostExecute(String result) {
            if(result==null){
                Toast.makeText(getApplicationContext(),"There is an Error check your Network or you have no points",Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(),"Your Points Now are "+pots,Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"Your Careem Code is  "+"CFHYT35"+pots,Toast.LENGTH_LONG).show();


            JSONObject user= null;
            try {
                user = new JSONObject(result);
                //=user.getInt("lastvalue");
                Log.e("rawy", ""+user.getJSONObject("lastvalue").getInt("value"));
                pointsNumbers.setText("You have "+ user.getJSONObject("lastvalue").getInt("value")+" Points");
                points.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
                careem.setVisibility(View.VISIBLE);
                points.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }
}