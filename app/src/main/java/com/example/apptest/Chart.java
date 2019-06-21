package com.example.apptest;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chart extends Fragment {

    Api_Client api;
    HashMap<String, String> rgParams;
    final int NUMBER_OF_DATA = 20;
    List<Integer> price_list = new ArrayList<Integer>();
    List<Integer> total_list = new ArrayList<Integer>();
    LineGraphSeries<DataPoint> series;
    BarGraphSeries<DataPoint> series2;
    GraphView lineGraph, barGraph;
    boolean isFirst = true;
    int cnt =20;
    int cnt1 = 20;

    public Chart(){
    }

    @Override
    public void onResume() {
        super.onResume();

        NetWorkThread thread = new NetWorkThread();
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_chart, container, false);

        View v = inflater.inflate(R.layout.fragment_chart, container, false);

        api = new Api_Client("69416cb6feaf322f66f70694e504957e","1c4482de5295a9376f716dba62128237");
        rgParams = new HashMap<String, String>();
        rgParams.put("order_currency", "BTC");
        rgParams.put("payment_currency", "KRW");
        lineGraph = (GraphView)v.findViewById(R.id.lineGraph);
        barGraph = (GraphView)v.findViewById(R.id.barGraph);

//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//        });
//        lineGraph.addSeries(series);
        return v;
    }

    class NetWorkThread extends Thread {
        @Override
        public void run() {
            while(true)
            {
                if(isFirst)
                {
                    try
                    {
                        String result = api.callApi("/public/transaction_history/BTC?count=" + NUMBER_OF_DATA, rgParams);

                        JSONObject obj = new JSONObject(result);
                        String status = obj.getString("status");
                        JSONArray data_list = obj.getJSONArray("data");
                        for(int i=0; i<data_list.length(); i++) {
                            JSONObject data_list_obj = data_list.getJSONObject(i);
                            String transaction_data = data_list_obj.getString("transaction_date");
                            final int price = Integer.parseInt(data_list_obj.getString("price"));
                            final int total = Integer.parseInt(data_list_obj.getString("total"));

                            price_list.add(price);
                            total_list.add(total);
                        }
                        drawLineGraph();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    isFirst = false;
                }
                // 데이터가 없어도 1초마다 가져옴
                else
                {
                    try
                    {
                        String result = api.callApi("/public/transaction_history/BTC?count=1", rgParams);
                        JSONObject obj = new JSONObject(result);
                        String status = obj.getString("status");
                        JSONArray data_list = obj.getJSONArray("data");
                        for(int i=0; i<data_list.length(); i++) {
                            JSONObject data_list_obj = data_list.getJSONObject(i);
                            String transaction_data = data_list_obj.getString("transaction_date");
                            final int price = Integer.parseInt(data_list_obj.getString("price"));
                            final int total = Integer.parseInt(data_list_obj.getString("total"));

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run() {
                                    addEntry(price, total);
                                }
                            });
                            sleep(1000);
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    isFirst=false;
                }
            }


        }
    }

    public synchronized void drawLineGraph() throws Exception{
        DataPoint[] pricePoints = new DataPoint[price_list.size()];
        DataPoint[] totalPoints = new DataPoint[price_list.size()];
        for(int i=0; i< pricePoints.length; i++) {
            pricePoints[i] = new DataPoint(i, price_list.get(i));
            totalPoints[i] = new DataPoint(i, total_list.get(i));
        }
        series = new LineGraphSeries<>(pricePoints);
        series2 = new BarGraphSeries<>(totalPoints);

        // 그래프에 점 추가
        lineGraph.addSeries(series);
        // x축 연장 가능성 true
        lineGraph.getViewport().setXAxisBoundsManual(true);
        lineGraph.getViewport().setYAxisBoundsManual(true);
        // 데이터가 늘어날때 x축의 scroll이 생기도록 설정
        lineGraph.getViewport().setScrollable(true);
        // 데이터가 늘어날때 y축의 scroll이 생기도록 설정
        lineGraph.getViewport().setScrollableY(true);

        barGraph.addSeries(series2);
        barGraph.getViewport().setXAxisBoundsManual(true);
        barGraph.getViewport().setYAxisBoundsManual(true);
        barGraph.getViewport().setScrollable(true);
        barGraph.getViewport().setScrollableY(true);
    }

    public void addEntry(int x, int y) {
        Log.d("@@@@@@" , "추가");
        series.appendData(new DataPoint(cnt++, x), true, 10);
        series2.appendData(new DataPoint(cnt1++, y), true, 10);
    }
}
