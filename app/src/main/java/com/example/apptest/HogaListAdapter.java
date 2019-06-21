package com.example.apptest;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HogaListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<HogaData> hogaList = null;
    private int nListCnt = 0;
    TextView hogaPrice, hogaVol;

    public HogaListAdapter(ArrayList<HogaData> hogaList1) {
        hogaList = hogaList1;
        nListCnt = hogaList.size();
    }

    @Override
    public int getCount() {
        return hogaList.size();
    }

    @Override
    public Object getItem(int position) {
        return hogaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            final Context context = parent.getContext();
            if(inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_hoga, parent, false);
        }

            hogaPrice = (TextView) convertView.findViewById(R.id.hogaPrice);
            hogaVol = (TextView) convertView.findViewById(R.id.hogaVol);

            hogaPrice.setTextSize(10); // 글자 크기 줄이기
            hogaVol.setTextSize(10);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.height=40; // 리스트 간격 조절

            convertView.setLayoutParams(params);

            try {
                hogaPrice.setText(hogaList.get(position).getStrHogaPrice());
                hogaVol.setText(hogaList.get(position).getStrHogaVol());

                hogaPrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(ge hogaPrice, Toast.LENGTH_SHORT).show();
                        Log.d("@@@", "@@@" + hogaList.get(position).getStrHogaPrice());
                    }

                });

                hogaVol.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(ge hogaPrice, Toast.LENGTH_SHORT).show();
                        Log.d("@@@", "@@@" + hogaList.get(position).getStrHogaVol());
                    }
                });
            }
            catch (IndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }

            if(position >= 10)   // 매수
            {
                convertView.setBackgroundColor(Color.rgb(255,240,245));
            }
            else                // 매도
            {
                convertView.setBackgroundColor(Color.rgb(235,251,255));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
    }

}
