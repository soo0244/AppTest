package com.example.apptest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionActivity extends AppCompatActivity {

    String marketName_pre;
    Api_Client api;
    HashMap<String, String> rgParams;
    TextView marketNameView, priceView, cpYester1View, cpYester2View;
    ListView asksPriceList, asksQuantityList, bidsPriceList, bidsQuantityList;
    ArrayList<Integer> asksPriceData, bidsPriceData;
    ArrayList<Double> asksQuantityData, bidsQuantityData;
    boolean isRunning;
    int price, cpYesterday;
    double cpYesterdayPercent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        marketName_pre = bundle.getString("marketName");

        api = new Api_Client("69416cb6feaf322f66f70694e504957e", "1c4482de5295a9376f716dba62128237");
        rgParams = new HashMap<String, String>();
        rgParams.put("payment_currency", "KRW");

        // 현재종목이름, 현재가, 전일대비가격, 전일대비 등략률 뷰 객체 생성
        marketNameView = (TextView) findViewById(R.id.marketName);
        priceView = (TextView) findViewById(R.id.price);
        cpYester1View = (TextView) findViewById(R.id.cpYester1);
        cpYester2View = (TextView) findViewById(R.id.cpYester2);

        // 리스트뷰 4개 생성
        asksPriceList = (ListView) findViewById(R.id.asksPrice);
        asksQuantityList = (ListView) findViewById(R.id.asksQuantity);
        bidsPriceList = (ListView) findViewById(R.id.bidsPrice);
        bidsQuantityList = (ListView) findViewById(R.id.bidsQuantity);

        // 데이터를 담을 ArrayList 생성
        asksPriceData = new ArrayList<>();
        asksQuantityData = new ArrayList<>();
        bidsPriceData = new ArrayList<>();
        bidsQuantityData = new ArrayList<>();

        // ArrayAdapater 객체 생성
        ArrayAdapter<Integer> asksPriceAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, asksPriceData)
        {
            @NonNull
            @Override
            // ArrayAdapter에 존재하는 리스트 아이템을 가져와서 속성값을 변경시켜준다.
            public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#f08080")); // 오렌지 레드색상
                text.setTextSize(10);   // 글자크기 줄이기

                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height=40; // 리스트 간격 조절
                view.setLayoutParams(params);
                return view;
            }
        };

        ArrayAdapter<Double> asksQuantityAdapter = new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1, asksQuantityData)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#f08080"));
                text.setTextSize(10);

                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height=40;
                view.setLayoutParams(params);
                return view;
            }
        };

        ArrayAdapter<Integer> bidsPriceAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, bidsPriceData)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#9370db")); // 연보라색
                text.setTextSize(10);

                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height=40;
                view.setLayoutParams(params);
                return view;
            }
        };

        ArrayAdapter<Double> bidsQuantityAdapter =new ArrayAdapter<Double>(this,android.R.layout.simple_list_item_1,bidsQuantityData)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#9370db"));
                text.setTextSize(10);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height=40;
                view.setLayoutParams(params);
                return view;
            }
        };

        asksPriceList.setAdapter(asksPriceAdapter);
        asksQuantityList.setAdapter(asksQuantityAdapter);
        bidsPriceList.setAdapter(bidsPriceAdapter);
        bidsQuantityList.setAdapter(bidsQuantityAdapter);

        marketNameView.setText(marketName_pre);
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
                    asksPriceData.clear();
                    bidsPriceData.clear();
                    asksQuantityData.clear();
                    bidsQuantityData.clear();
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

                    //bithumb에 bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
                    String orderbookResult = api.callApi("/public/orderbook/" + marketName_pre + "?count=10", rgParams);
                    JSONObject obj2 = new JSONObject(orderbookResult);
                    JSONObject obj3 = obj2.getJSONObject("data");
                    JSONArray data_list3 = obj3.getJSONArray("bids");
                    for(int i=0; i<data_list3.length(); i++)
                    {
                        JSONObject data_list_obj3 = data_list3.getJSONObject(i);
                        double bidsQuantity = Double.parseDouble(data_list_obj3.getString("quantity"));
                        int bidsPrice = Integer.parseInt(data_list_obj3.getString("price"));

                        // arrayList에 데이터 넣어주기
                        bidsPriceData.add(bidsPrice);
                        bidsQuantityData.add(bidsQuantity);
                    }
                    JSONArray data_list4 = obj3.getJSONArray("asks");
                    //데이터를 위에서부터 높은 가격으로 넣어주기 위해 for 구문의 순서를 바꾸었다.
                    for(int i= data_list4.length()-1; i>=0; i--)
                    {
                        JSONObject data_list_obj4 = data_list4.getJSONObject(i);
                        double asksQuantity = Double.parseDouble(data_list_obj4.getString("quantity"));
                        int asksPrice = Integer.parseInt(data_list_obj4.getString("price"));

                        asksPriceData.add(asksPrice);
                        asksQuantityData.add(asksQuantity);
                    }
                    // 뷰의 정보를 바꾸기 위한 익명구현ㄴ객체 생성
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 전일대비 가격이 상승인지 하락인지에 따라 색상 바꾸기
                            if(cpYesterday>0)
                            {
                                priceView.setText("" + price);
                                cpYester1View.setText("" + cpYesterday);
                                cpYester1View.setTextColor(Color.parseColor("#f08080"));
                                cpYester2View.setText("+" + String.format("%.2f",cpYesterdayPercent)  + "%");
                                cpYester2View.setTextColor(Color.parseColor("#f08080"));
                            }
                            else
                            {
                                priceView.setText("" + price);
                                cpYester1View.setText("" + cpYesterday);
                                cpYester1View.setTextColor(Color.parseColor("#9370db"));
                                cpYester2View.setText(String.format("%.2f",cpYesterdayPercent)  + "%");
                                cpYester2View.setTextColor(Color.parseColor("#9370db"));
                            }

                            // 리스트의 내용 업데이트하기
                            ArrayAdapter<Integer> adapter1 = (ArrayAdapter)asksPriceList.getAdapter();
                            adapter1.notifyDataSetChanged();
                            ArrayAdapter<Double> adapter2 = (ArrayAdapter)asksQuantityList.getAdapter();
                            adapter2.notifyDataSetChanged();
                            ArrayAdapter<Integer> adapter3 = (ArrayAdapter)bidsPriceList.getAdapter();
                            adapter3.notifyDataSetChanged();
                            ArrayAdapter<Double> adapter4 = (ArrayAdapter)bidsQuantityList.getAdapter();
                            adapter4.notifyDataSetChanged();
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
}
