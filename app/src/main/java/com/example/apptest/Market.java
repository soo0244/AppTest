package com.example.apptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Market extends ListFragment {

    Api_Client api;
    HashMap<String, String> rgParams;
    String[] marketList = {"BTC", "ETH", "DASH", "LTC", "ETC", "XRP", "BCH", "XMR", "ZEC", "QTUM", "BTG"};
    String[] market_price = new String[marketList.length];
    public boolean isRunning = true;
    ListView listView;
    ArrayList<HashMap<String, String>> list;
    public Market(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market, container, false);
        api = new Api_Client("69416cb6feaf322f66f70694e504957e", "1c4482de5295a9376f716dba62128237");
        rgParams = new HashMap<String, String>();
        rgParams.put("payment_currency", "KRW");


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

    class NetworkThread extends Thread{
        @Override
        public void run() {
            while(isRunning)
            {
                try
                {
//                    for(int i=0; i<marketList.length; i++)
//                    {


                    list = new ArrayList<>();


                    String result = api.callApi("/public/ticker/all", rgParams);
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");
//                    String data_list = obj.optString("data");

                    String data = obj.optString("data");
                    JSONObject objData = new JSONObject(data);

                    Iterator<String> iter = objData.keys();


                    while(iter.hasNext()) {
                        String key = iter.next();
                        if (!key.equals("") && !key.trim().equals("date")) {

                            JSONObject jObjectData = objData.getJSONObject(key.trim());

                            String closePrice = jObjectData.optString("closing_price");
                            Log.d("@@@@@", "closeprice : " + key.trim() + " ----" + closePrice);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("name" , key.trim());
                            map.put("price", closePrice);
                            list.add(map);
                        }

                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            showMarketList();
                            String[] key_array = {"name", "price"};
                            int[] id_array = {android.R.id.text1, android.R.id.text2};

                            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, key_array, id_array);

                            setListAdapter(adapter);

                        }
                    });
                    Thread.sleep(5000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showMarketList()
    {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for(int i=0; i<marketList.length; i++)
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("name" , marketList[i]);
            map.put("price", market_price[i]);
            list.add(map);
        }
        String[] key_array = {"name", "price"};
        int[] id_array = {android.R.id.text1, android.R.id.text2};

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, key_array, id_array);

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), Info.class);
        intent.putExtra("marketName", list.get(position).get("name"));
        intent.putExtra("price", list.get(position).get("price"));
        Log.d("@@@", "마켓 pirce : " + list.get(position).get("price") );
        isRunning=false;
        startActivityForResult(intent,100);
    }
}
