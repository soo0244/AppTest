package com.example.apptest;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Order extends Fragment {

    ListView hogaListView = null;
    ArrayList<HogaData> hogaList = null;
    HogaListAdapter hogaListAdapter = null;
    String marketName;

    Api_Client api;
    HashMap<String, String> rgParams;
    boolean isRunning;

    boolean isTouch = false;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        if(getArguments() != null)
        {
            marketName = getArguments().getString("marketName");
        }

        hogaListView = v.findViewById(R.id.hogaList);
        hogaList = new ArrayList<>();

        api = new Api_Client("69416cb6feaf322f66f70694e504957e", "1c4482de5295a9376f716dba62128237");
        rgParams = new HashMap<String, String>();
        rgParams.put("payment_currency", "KRW");

        hogaListAdapter = new HogaListAdapter(hogaList);
        hogaListView.setAdapter(hogaListAdapter);
        hogaListView.smoothScrollToPositionFromTop(18, 0);

        // 스크롤 동작시 컨트롤
        hogaListView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

            if ( event.getAction() == MotionEvent.ACTION_UP) {
                isTouch = false;
            }
            else
            {
                isTouch = true;
            }
                return false;
            }
        });

//        hogaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), hogaList.get(position).getStrHogaPrice(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkThread thread = new NetworkThread();
        isRunning = true;
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
    }

    class NetworkThread extends  Thread {
        @Override
        public void run() {
            super.run();
            while( isRunning )
            {
                try
                {
                    // 1초당 요청 데이터 수가 제한되어있기 때문에 꼭 요청 간격 sleep을 주어야 함
                    SystemClock.sleep(1000);
                    hogaList.clear();

                    //bithumb에 bithumb 거래소 판/구매 등록 대기 또는 거래 중 내역 정보
                    String orderbookResult = api.callApi("/public/orderbook/" + marketName + "?count=10", rgParams);

                    JSONObject obj2 = new JSONObject(orderbookResult);
                    JSONObject obj3 = obj2.getJSONObject("data");
                    JSONArray data_list4 = obj3.getJSONArray("asks");
                    //데이터를 위에서부터 높은 가격으로 넣어주기 위해 for 구문의 순서를 바꾸었다.
                    for(int i= data_list4.length()-1; i>=0; i--)
                    {
                        JSONObject data_list_obj4 = data_list4.getJSONObject(i);
                        double asksQuantity = Double.parseDouble(data_list_obj4.getString("quantity"));
                        int asksPrice = Integer.parseInt(data_list_obj4.getString("price"));

                        String strAskQuantity = String.format("%.3f",asksQuantity);

                        HogaData hogaData = new HogaData();
                        hogaData.setStrHogaPrice(String.valueOf(asksPrice));
                        hogaData.setStrHogaVol(String.valueOf(strAskQuantity));
                        hogaList.add(hogaData);
                    }

                    JSONArray data_list3 = obj3.getJSONArray("bids");
                    for(int i=0; i<data_list3.length(); i++)
                    {
                        JSONObject data_list_obj3 = data_list3.getJSONObject(i);
                        double bidsQuantity = Double.parseDouble(data_list_obj3.getString("quantity"));
                        int bidsPrice = Integer.parseInt(data_list_obj3.getString("price"));

                        String strBidsQuantity = String.format("%.3f",bidsQuantity);

                        HogaData hogaData = new HogaData();
                        hogaData.setStrHogaPrice(String.valueOf(bidsPrice));
                        hogaData.setStrHogaVol(strBidsQuantity);
                        hogaList.add(hogaData);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ( !isTouch ) {
                                HogaListAdapter adapter = (HogaListAdapter) hogaListView.getAdapter();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                catch ( Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
