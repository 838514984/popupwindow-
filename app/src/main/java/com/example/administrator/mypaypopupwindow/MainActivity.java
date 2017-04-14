package com.example.administrator.mypaypopupwindow;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity implements View.OnClickListener {
    PayPopUpWinDow payPopUpWinDow;;
    ViewGroup viewGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.show).setOnClickListener(this);
        findViewById(R.id.dismiss).setOnClickListener(this);
        findViewById(R.id.timer).setOnClickListener(this);
        viewGroup= (ViewGroup) findViewById(R.id.contai);
        payPopUpWinDow=new PayPopUpWinDow(this,false);
        //payPopUpWinDow.setShowTimerSeconeds(10);
        payPopUpWinDow.setPayBtnListener(new PayPopUpWinDow.PayBtnListener() {
            @Override
            public void onClick(View v, int payChannel) {
                startActivity(new Intent(MainActivity.this,PayActivity.class));
            }
        });
        payPopUpWinDow.setTimeOutListener(new PayPopUpWinDow.TimeOutListener() {
            @Override
            public void timeOut() {
                Log.e("xxx","timeOut");
                payPopUpWinDow.dissmiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show:
                try {
                    payPopUpWinDow.show(viewGroup);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dismiss:
                payPopUpWinDow.dissmiss();
                break;
            case R.id.timer:
                payPopUpWinDow.showTimer(300);
                break;

        }
    }
}
