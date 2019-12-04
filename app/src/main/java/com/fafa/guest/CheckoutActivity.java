package com.fafa.guest;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.TimerTask;

public class CheckoutActivity extends AppCompatActivity {

    EditText pinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fullscreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pinCode = findViewById(R.id.pinCode);
        findViewById(R.id.restBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinCode.setText("");
            }
        });

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setTimer();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        System.out.println("----onWindowFocusChanged-----" + hasFocus);
        fullscreen(this);
        super.onWindowFocusChanged(hasFocus);
    }

    public void fullscreen(Activity activity) {
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        systemUiVisibility |= flags;
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private static final int TIMER = 999;
    private MyTimeTask task;

    private void setTimer() {
        if (task == null) {
            task = new MyTimeTask(1000, new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(TIMER);
                    //或者发广播，启动服务都是可以的
                }
            });
            task.start();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TIMER) {
                fullscreen(CheckoutActivity.this);
            }
        }
    };

    private void stopTimer() {
        task.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
