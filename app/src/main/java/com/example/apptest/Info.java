package com.example.apptest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Info extends AppCompatActivity {

    String marketName_pre, price_pre;
    TextView marketNameView, priceView, daebiView, daebiRateView;
    Api_Client api;
    HashMap<String, String> rgParams;
    boolean isRunning;
    int price, cpYesterday;
    double cpYesterdayPercent;

    private final int nOrder   = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        marketName_pre = bundle.getString("marketName");
        price_pre = bundle.getString("price");

        api = new Api_Client("69416cb6feaf322f66f70694e504957e", "1c4482de5295a9376f716dba62128237");
        rgParams = new HashMap<String, String>();
        rgParams.put("payment_currency", "KRW");

        // 현재종목이름, 현재가, 전일대비가격, 전일대비 등략률 뷰 객체 생성
        marketNameView = (TextView) findViewById(R.id.marketName);
        priceView = (TextView) findViewById(R.id.price);
        daebiView = (TextView) findViewById(R.id.daebi);
        daebiRateView = (TextView) findViewById(R.id.daebiRate);

        marketNameView.setText(marketName_pre);

        callFragment(nOrder);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        NetworkThread thread = new NetworkThread();
        isRunning = true;
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning=false;
    }

    class NetworkThread extends Thread {
        @Override
        public void run() {
            while(isRunning)
            {
                try
                {
                    // 1초당 요청 데이터 수가 제한되어있기 때문에 꼭 요청 간격 sleep을 주어야 함
                    SystemClock.sleep(1000);
                    // 리스트뷰를 계속 초기화 해주기 위한 clear
                    //bithumb 거래소 거래 체결 완료 내역 요청하기
                    String recentTransaction = api.callApi("/public/transaction_history/" + marketName_pre, rgParams);
                    JSONObject obj = new JSONObject(recentTransaction);
                    String status = obj.getString("data");
                    JSONArray data_list = obj.getJSONArray("data");
                    JSONObject data_list_obj = data_list.getJSONObject(0);
                    price = Integer.parseInt(data_list_obj.getString("price"));

                    //bithumb에 bithumb 거래소 마지막 거래 정보 요청
                    String tickerResult = api.callApi("/public/ticker/" + marketName_pre, rgParams);
                    JSONObject obj1 = new JSONObject(tickerResult);
                    JSONObject data_list1 = obj1.getJSONObject("data");
                    int openingPrice = Integer.parseInt(data_list1.getString("opening_price"));
                    int closingPrice = Integer.parseInt(data_list1.getString("closing_price"));
                    cpYesterday = closingPrice - openingPrice;
                    cpYesterdayPercent = ((double)cpYesterday / openingPrice) * 100.0;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 전일대비 가격이 상승인지 하락인지에 따라 색상 바꾸기
                            if(cpYesterday>0)
                            {
                                priceView.setText("" + price);
                                priceView.setTextColor(Color.rgb(205,0,0));
                                daebiView.setText("" + cpYesterday);
                                daebiView.setTextColor(Color.rgb(205,0,0));
                                daebiRateView.setText("+" + String.format("%.2f",cpYesterdayPercent)  + "%");
                                daebiRateView.setTextColor(Color.rgb(205,0,0));
                            }
                            else
                            {
                                priceView.setText("" + price);
                                priceView.setTextColor(Color.rgb(0,100,255));
                                daebiView.setText("" + cpYesterday);
                                daebiView.setTextColor(Color.rgb(0,100,255));
                                daebiRateView.setText(String.format("%.2f",cpYesterdayPercent)  + "%");
                                daebiRateView.setTextColor(Color.rgb(0,100,255));
                            }

                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void callFragment(int fragmentNo) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        switch (fragmentNo) {
            case 1:
                // 호가창
                Order order = new Order();
                Bundle bundle = new Bundle();
                bundle.putString("marketName",  marketName_pre);
                order.setArguments(bundle);
                transaction.replace(R.id.fragment_info, order);
                transaction.commit();

                // 매수창
                OrderBid orderBid = new OrderBid();
                bundle.putString("price", price_pre);
                orderBid.setArguments(bundle);
                transaction2.replace(R.id.fragment_orderType, orderBid);
                transaction2.commit();
                break;
        }
    }
}
