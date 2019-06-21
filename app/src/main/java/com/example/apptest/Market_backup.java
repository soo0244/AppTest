package com.example.apptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Market_backup extends ListFragment {

    Api_Client api;
    HashMap<String, String> rgParams;
    String[] marketList = {"BTC", "ETH", "DASH", "LTC", "ETC", "XRP", "BCH", "XMR", "ZEC", "QTUM", "BTG"};
    String[] market_price = new String[marketList.length];
    public boolean isRunning = true;
    ListView listView;

    public Market_backup(){

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
                    for(int i=0; i<marketList.length; i++)
                    {
                        String result = api.callApi("/public/transaction_history/" + marketList[i] + "?count=1", rgParams);
                        JSONObject obj = new JSONObject(result);
                        String status = obj.getString("status");
                        JSONArray data_list = obj.getJSONArray("data");
                        JSONObject data_list_obj = data_list.getJSONObject(0);

                        String price = data_list_obj.getString("price");
                        market_price[i] = price;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMarketList();
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
        intent.putExtra("marketName", marketList[(int)id]);
        isRunning=false;
        startActivityForResult(intent,100);
    }
}
