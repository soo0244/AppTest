package com.example.apptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;

public class MainActivity_backup extends AppCompatActivity {

    Api_Client api;
    HashMap<String, String> rgParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new Api_Client("69416cb6feaf322f66f70694e504957e",
                "1c4482de5295a9376f716dba62128237");

        rgParams = new HashMap<String, String>();
        rgParams.put("order_currency", "BTC");
        rgParams.put("payment_currency", "KRW");

        NetworkThread thread = new NetworkThread();
        thread.start();

    }

    class NetworkThread extends Thread{
        @Override
        public void run() {
            try {
                String result = api.callApi("/info/balance", rgParams);
                Log.d("balance : " ,"balance : " +  result);
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
