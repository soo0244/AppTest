package com.example.apptest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;

public class OrderBid extends Fragment {

    String marketName;
    Api_Client api;

    boolean isRunning = true;
    Spinner compToCurrentSpinner, amountSpinner;
    String[] amountSpinnerData = {"가능", "최대", "50%", "25%", "10%"};
    String[] compToCurrentData = {"현재가 대비%", "20%", "15%", "10%", "5%", "0", "-5%", "-10%", "-15%", "-20%" };
// user
    EditText editPrice, editAmount;
    String orderType="bid";
    boolean isCheckedMarketPrice = false;

    String menuState = "bid";

    CheckBox marketPrice;
    Button sellbuyCheck;
    Button sellBtn, buyBtn;
    Button resetBtn;

    TextView possibleOrder, possibleAmount, commission, totalOrderPrice;

    int price;
    double amount = 0;
    String availableKRW;
    String availableCoin;

    double possibleAmountData;
    int editPriceVal;
    double editAmountVal;
    int totalOrderPriceVal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_bid, container, false);

        if(getArguments() != null)
        {
            marketName = getArguments().getString("marketName");
            price = Integer.parseInt(getArguments().getString("price"));
        }

        api = new Api_Client("69416cb6feaf322f66f70694e504957e", "1c4482de5295a9376f716dba62128237");

        // 뷰의 주소값 가져오기
        possibleOrder = v.findViewById(R.id.possibleOrder);
        possibleAmount = v.findViewById(R.id.possibleAmount);
        commission = v.findViewById(R.id.commission);
        totalOrderPrice = v.findViewById(R.id.totalOrderPrice);
        totalOrderPrice.setText("0 KRW");

        showPossibleOrderInfo();

        amountSpinner = (Spinner) v.findViewById(R.id.amountSpinner);
        SpinnerAdapter adapter1 = new SpinnerAdapter(amountSpinnerData, getActivity());
        amountSpinner.setAdapter(adapter1);

        compToCurrentSpinner =  (Spinner) v.findViewById(R.id.compToCurrentSpinner);
        SpinnerAdapter adapter2 = new SpinnerAdapter(compToCurrentData, getActivity());
        compToCurrentSpinner.setAdapter(adapter2);

        resetBtn = v.findViewById(R.id.reset);

        editAmount = v.findViewById(R.id.amount);
        editPrice = v.findViewById(R.id.price);

        editPrice.setText("" + price);


        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                {
                    totalOrderPrice.setText("0 KRW");
                }
                else
                {
                    amount = Double.parseDouble(s.toString());
                    long sum = (long) (price * amount);
                    totalOrderPrice.setText(sum + " KRW");
                }
            }
        });

        editPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                {
                    totalOrderPrice.setText("0 KRW");
                }
                else
                {
                    price = Integer.parseInt(s.toString());
                    long sum = (long)(price * amount);
                    totalOrderPrice.setText(sum + " KRW");
                }
            }
        });

        resetBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                initBtnMethod(v);

            }
        });

        amountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String spinnerText = compToCurrentData[position];
                double amount = Double.parseDouble(editAmount.getText().toString());

                int amountRate;

                if(spinnerText.equals("최대"))
                {
                    amountRate = 100;
                }
                else
                {
                    amountRate = Integer.parseInt(spinnerText.replace("%",""));
                }

                if(menuState.equals("ask")) // 매도
                {
                    if(amountRate == 100)
                    {
                        editAmount.setText("" );
                    }
                    else
                    {

                    }
                }
                else                        // 매수
                {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        compToCurrentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String spinnerText = compToCurrentData[position];
                String[] compToCurrentData = {"현재가 대비%", "20%", "15%", "10%", "5%", "0", "-5%", "-10%", "-15%", "-20%" };

                if(!spinnerText.equals("현재가 대비%"))
                {
                    int price = Integer.parseInt(editPrice.getText().toString());
                    double daebiPrice = 0;
                    double a = 0.0;

                    if(spinnerText.equals("20%") || spinnerText.equals("15%") || spinnerText.equals("10%") || spinnerText.equals("5%"))
                    {
                        daebiPrice = Integer.parseInt(spinnerText.replace("%", ""));

                        a = (double)price * ((100 + daebiPrice) / 100);
                    }
                    else if(spinnerText.equals("-5%") || spinnerText.equals("-10%") || spinnerText.equals("-15%") || spinnerText.equals("-20%"))
                    {
                        daebiPrice = Integer.parseInt(spinnerText.replace("%", ""));
                        a = (double)price * ((100 + daebiPrice) / 100);
                    }
                    else
                    {
                        a = price;
                    }

                    editPrice.setText("" + Math.round(a));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        marketPrice = v.findViewById(R.id.marketPrice);
//
//        marketPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked == true)
//                {
//                    editPrice.setFocusable(false);
//                    editPrice.setFocusableInTouchMode(false);
//                    isCheckedMarketPrice=true;
//                }
//                else
//                {
//                    editPrice.setFocusable(true);
//                    editPrice.setFocusableInTouchMode(true);
//                    isCheckedMarketPrice =false;
//                }
//            }
//        });

        sellBtn = v.findViewById(R.id.btn_ask);
        buyBtn = v.findViewById(R.id.btn_bid);
        buyBtn.setTextColor(Color.parseColor("#f08080"));

        MenuBtnListener menuListener = new MenuBtnListener();
        sellBtn.setOnClickListener(menuListener);
        buyBtn.setOnClickListener(menuListener);

        sellbuyCheck = v.findViewById(R.id.sellbuycheck);
        sellbuyCheck.setBackgroundColor(Color.parseColor("#f08080"));

        sellbuyCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buycheckMehthod(v);
            }
        });

        return v;
    }

    public void showPossibleOrderInfo() {
        int list_idx = 0;

        for(int i=0; i<Constant.MARKETLIST.length; i++)
        {
            if(Constant.MARKETLIST[i].equals(marketName))
            {
                list_idx = i;
            }
        }

        // 최소 주문 수량 가져오기
        possibleAmountData = Constant.MIN_UNITS[list_idx];
        possibleAmount.setText("" + possibleAmountData);
        // 수수료는 고정 0.05%
        commission.setText(Constant.COMMISSION+"%");
        // 주문가능 금액을 알아오기 위한 빗썸 데이터 요청 스레드
        NetInfoBalanceThread thread = new NetInfoBalanceThread();
        thread.start();

    }

    class MenuBtnListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_bid:
                    menuState ="bid";
                    // 주문가능 금액 화폐단위 바꾸기
                    possibleOrder.setText(availableKRW + " KRW");
                    // 버튼 클릭시 위의 메뉴 버튼의 글씨 색상 변경
                    buyBtn.setTextColor(Color.parseColor("#f08080"));
                    sellBtn.setTextColor(Color.BLACK);
                    // 중간의 매도/매수 버튼 이름 변경
                    sellbuyCheck.setText("매수");
                    sellbuyCheck.setBackgroundColor(Color.parseColor("#f08080"));
                    break;
                case R.id.btn_ask :
                    menuState ="ask";
                    // 주문가능 금액 화폐단위 바꾸기
                    possibleOrder.setText(availableCoin+" " + marketName);
                    // 버튼 클릭시 위의 메뉴 버튼의 글씨 색상 변경
//                    sellBtn.setTextColor(Color.parseColor("#9370db"));
                    sellBtn.setTextColor(Color.BLUE);
                    buyBtn.setTextColor(Color.BLACK);
                    // 중간의 매도/매수 버튼 이름변경
                    sellbuyCheck.setText("매도");
//                    sellbuyCheck.setBackgroundColor(Color.parseColor("#9370db"));
                    sellbuyCheck.setBackgroundColor(Color.BLUE);
                    break;
            }
        }
    }

    // 주문 가능 금액을 알아오기 위한 빗썸 데이터 요청 스레드
    class NetInfoBalanceThread extends Thread {
        @Override
        public void run() {
            try
            {
                HashMap<String, String> rgParams = new HashMap<String, String>();
                rgParams.put("currency", marketName);
                rgParams.put("payment_currency", "KRW");

                final String result = api.callApi("/info/balance", rgParams);
                JSONObject obj = new JSONObject(result);
                JSONObject data_list = obj.getJSONObject("data");
                availableKRW = data_list.getString("available_krw");
                availableCoin = data_list.getString("available_"+marketName.toLowerCase());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 해당 뷰의 내용을 수정
                        if(menuState.equals("bid"))
                        {
                            possibleOrder.setText(availableKRW + " KRW");
                        }
                        else
                        {
                            possibleOrder.setText(availableCoin);
                        }
                    }
                });
            }
            catch (Exception e)
            {

            }
        }
    }

    // 초기화 버튼 클릭시
    public void initBtnMethod(View v)
    {
        editAmount.setText("");
        editPrice.setText("");
    }

    public void buycheckMehthod(View v)
    {
        if(Double.parseDouble(editAmount.getText().toString()) >= possibleAmountData)
        {
            if(menuState.equals("bid"))
            {
                buyStock();
            }
            else
            {
                sellStock();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("거래 주문 확인");
            builder.setMessage("수량이 최소주문수량보다 작습니다(" + possibleAmountData + ")");
            builder.setPositiveButton("확인", null);
            builder.show();

        }
    }

    public void buyStock() {
        if(editPrice.getText().toString().equals(""))
        {
            Toast.makeText(getActivity(), "매수 가격을 입력하세요." , Toast.LENGTH_SHORT);
        }
        else if(editAmount.getText().toString().equals(""))
        {
            Toast.makeText(getActivity(), "매수 수량을 입력하세요.", Toast.LENGTH_SHORT);
        }
        else
        {
            editPriceVal = Integer.parseInt(editPrice.getText().toString());
            editAmountVal = Double.parseDouble(editAmount.getText().toString());

            String strTotalPrice = totalOrderPrice.getText().toString().replace(" KRW", "");
            totalOrderPriceVal = Integer.parseInt(strTotalPrice);

            if(totalOrderPriceVal > Integer.parseInt(availableKRW))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("매수 주문 오류");
                builder.setMessage("보유 KRW를 확인하세요.");
                builder.setPositiveButton("확인", null);
                builder.show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("매수 주문 확인");
                builder.setMessage("매수 주문을 등록하시겠습니까?");
                DialogListener listener = new DialogListener();
                builder.setPositiveButton("확인", listener);
                builder.setNegativeButton("취소", listener);
                builder.show();
            }
        }
    }

    public void sellStock() {
        if(editPrice.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "매도 가격을 입력하세요.", Toast.LENGTH_SHORT);
        } else if (editAmount.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "매도 수량을 입력하세요.", Toast.LENGTH_SHORT);
        } else {
            editPriceVal = Integer.parseInt(editPrice.getText().toString());
            editAmountVal = Double.parseDouble(editAmount.getText().toString());

            if(editAmountVal > Double.parseDouble(availableCoin))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("매도 주문 오류");
                builder.setMessage("보유 "+ marketName +" 를 확인하세요.");
                builder.setPositiveButton("확인", null);
                builder.show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("매도 주문 확인");
                builder.setMessage("매도 주문을 등록하시겠습니까?");
                DialogListener listener = new DialogListener();
                builder.setPositiveButton("확인", listener);
                builder.setNegativeButton("취소", listener);
                builder.show();
            }
        }
    }

//     다이얼로그 리스너
    class DialogListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    if(isCheckedMarketPrice== false)
                    {
                        TransactionThread thread = new TransactionThread(editPriceVal, editAmountVal);
                        thread.start();
                    }
                    else
                    {
//                        MarketPriceTransactionThread thread = new MarketPriceTransactionThread();
//                        thread.start();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    }

    class TransactionThread extends Thread {

        private long price;
        private double amount;

        public TransactionThread(long price, double amount)
        {
            this.price = price;
            this.amount = amount;
        }

        @Override
        public void run() {
            // 거래 주문 등록 요청
            HashMap<String, String> rgParams = new HashMap<String, String>();
            rgParams.put("order_currency", ""+marketName);
            rgParams.put("Payment_currency", "KRW");
            rgParams.put("units", ""+amount);
            rgParams.put("price", ""+price);

            if(menuState.equals("bid"))
            {
                rgParams.put("type", "bid");
            }
            else if(menuState.equals("ask"))
            {
                rgParams.put("type", "ask");
            }
            String tradeResult = api.callApi("/trade/place", rgParams);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 입력창 초기화
                    editAmount.setText("");
                    editPrice.setText("");
                    // 키보드 자판 내리기
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editPrice.getWindowToken(),0);
                    // 거래확인 다이얼로그창 띄우기
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("거래 주문 확인");
                    builder.setMessage("거래 주문이 등록되었습니다.");
                    builder.setPositiveButton("확인", null);
                    builder.show();
                }
            });
        }
    }

}

























