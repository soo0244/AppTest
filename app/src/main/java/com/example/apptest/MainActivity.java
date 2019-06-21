package com.example.apptest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int nMyWallet   = 1;
    private final int nMarket     = 2;
    private final int nChart      = 3;
    private final int nStop       = 4;

    private Button btn_wallet, btn_market, btn_chart, btn_stop;

    Api_Client api;
    HashMap<String, String> rgParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_wallet = (Button)findViewById(R.id.btn_wallet);
        btn_market = (Button)findViewById(R.id.btn_market);
        btn_chart = (Button)findViewById(R.id.btn_chart);
        btn_stop = (Button)findViewById(R.id.btn_stop);

        btn_wallet.setOnClickListener(this);
        btn_market.setOnClickListener(this);
        btn_chart.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        callFragment(nMarket);



//        NetworkThread thread = new NetworkThread();
//        thread.start();

    }

    class NetworkThread extends Thread{
        @Override
        public void run() {
            try {
                api = new Api_Client("69416cb6feaf322f66f70694e504957e","1c4482de5295a9376f716dba62128237");

                rgParams = new HashMap<String, String>();
                rgParams.put("order_currency", "BTC");
                rgParams.put("payment_currency", "KRW");

                final String result = api.callApi("/info/balance", rgParams);
                Log.d("balance : " ,"balance : " +  result);

                JSONObject obj = new JSONObject(result);
                String status = obj.getString("status");

                JSONObject data_list = obj.getJSONObject("data");
                String total_krw = data_list.getString("total_krw");
                String in_use_krw = data_list.getString("in_use_krw");
                String available_krw = data_list.getString("available_krw");
                String total_btc = data_list.getString("total_btc");
                String in_use_btc = data_list.getString("in_use_btc");
                String available_btc = data_list.getString("available_btc");
                String xcoin_last = data_list.getString("xcoin_last");



//                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wallet:
                callFragment(nMyWallet);
                break;
            case R.id.btn_market:
                callFragment(nMarket);
                break;
            case R.id.btn_chart:
                callFragment(nChart);
                break;
            case R.id.btn_stop:
                callFragment(nStop);
                break;
        }
    }

    private void callFragment(int fragmentNo) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragmentNo) {
            case 1:
                Mywallet mywallet = new Mywallet();
                transaction.replace(R.id.fragment_container, mywallet);
                transaction.commit();
                break;

            case 2:
                Market market = new Market();
                transaction.replace(R.id.fragment_container, market);
                transaction.commit();
                break;

            case 3:
                Chart chart = new Chart();
                transaction.replace(R.id.fragment_container, chart);
                transaction.commit();
                break;

            case 4:
                Stop stop = new Stop();
                transaction.replace(R.id.fragment_container, stop);
                transaction.commit();
                break;
        }

    }
}
