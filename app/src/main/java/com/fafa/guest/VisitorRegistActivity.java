package com.fafa.guest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class VisitorRegistActivity extends AppCompatActivity implements View.OnLayoutChangeListener {

    EditText visitCompany;
    EditText visitName;
    EditText visitMobile;
    EditText reason;
    EditText name;
    EditText email;

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private UsbPrintUtil usbPrintUtil = new UsbPrintUtil(VisitorRegistActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fullscreen(this);
//        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_regist);

        initView();
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.restBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitCompany.setText("");
                visitMobile.setText("");
                visitName.setText("");
                reason.setText("");
                name.setText("");
                email.setText("");
            }
        });

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNull()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("date", sdf.format(new Date()));
                    m.put("visitName", visitName.getText().toString());
                    m.put("name", name.getText().toString());
                    m.put("pinCode", pinCode());
                    m.put("visitMobile",visitMobile.getText().toString());
                    m.put("reason",reason.getText().toString());
                    m.put("email",email.getText().toString());
                    Thread t = new Thread(new HttpSend("post", m, "http://192.168.43.181:6161/fengqi/reserve/save", new Callback<Object, Object>() {
                        @Override
                        public Object call(Object o) {
                            System.out.println(o);
                            return null;
                        }
                    }));
                    t.start();
                    try {
                        t.join(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        usbPrintUtil.print(m);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    Toast.makeText(VisitorRegistActivity.this, "请填写完整内容", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkNull() {
        if (visitCompany == null || visitCompany.getText().toString().trim().length() == 0
                || visitName == null || visitName.getText().toString().trim().length() == 0
                || visitMobile == null || visitMobile.getText().toString().trim().length() == 0
                || reason == null || reason.getText().toString().trim().length() == 0
                || name == null || name.getText().toString().trim().length() == 0
                || email == null || email.getText().toString().trim().length() == 0) {
            return false;
        }
        return true;
    }

    Random random = new Random();

    private String pinCode() {
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4) {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuilder sb = new StringBuilder();
        for (Integer i : set) {
            sb.append("").append(i);
        }
        return sb.toString();
    }

    private void initView() {
        visitCompany = findViewById(R.id.visitCompany);
        visitMobile = findViewById(R.id.visitMobile);
        visitName = findViewById(R.id.visitName);
        reason = findViewById(R.id.reason);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
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
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        System.out.println("=================");
    }
}
