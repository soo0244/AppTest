package com.example.apptest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;
import java.util.HashMap;

public class Mywallet extends Fragment {

    public Mywallet(){
    }

    TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_mywallet, container, false);
          text = v.findViewById(R.id.textView2);
          NetworkThread thread = new NetworkThread();
          thread.start();

          return v;
    }

    class NetworkThread extends Thread{
        @Override
        public void run() {
            try {
                Api_Client api = new Api_Client("69416cb6feaf322f66f70694e504957e","1c4482de5295a9376f716dba62128237");

                HashMap<String, String> rgParams = new HashMap<String, String>();
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

                final String str2 = "total krw : " + total_krw + "\n" +
                        "in_use_krw : " + in_use_krw + "\n" +
                        "available_krw : " + available_krw + "\n" +
                        "total_btc : " + total_btc + "\n" +
                        "in_use_btc : " + in_use_btc + "\n" +
                        "available_btc : " + available_btc + "\n" +
                        "xcoin_last : " + xcoin_last + "\n";                        ;

                MainActivity activity = (MainActivity) getActivity();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText(str2);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
