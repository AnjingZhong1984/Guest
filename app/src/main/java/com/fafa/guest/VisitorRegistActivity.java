package com.fafa.guest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class VisitorRegistActivity extends AppCompatActivity implements View.OnLayoutChangeListener {

    EditText visitCompany;
    EditText visitName;
    EditText visitMobile;
    EditText reason;
    AutoCompleteTextView name;
    EditText email;
    EditText visitNum;
    CustomDialog customDialog;//自定义loading dialog
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private UsbPrintUtil usbPrintUtil = new UsbPrintUtil(VisitorRegistActivity.this);

    private TextWatcher editclick = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (visitName.getText().toString().trim().length() > 0 && visitCompany.getText().toString().trim().length() > 0) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) name.getAdapter();
                adapter.addAll("aaa", "bbb", "ccc");
                //设置联想的字符集合
                Map<String, String> m = new HashMap<>();

                new Thread(new HttpSend("post", m, "http://demo.fafa.com.cn:6161/fengqi/reserve/checkname", new Callback<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        System.out.println(o);
                        return null;
                    }
                })).start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fullscreen(this);
//        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_regist);

        initView();
        customDialog = new CustomDialog(this, "请求中", R.drawable.loading);
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
                visitNum.setText("");
            }
        });

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = visitMobile.getText().toString().trim();
                String visitEmail = email.getText().toString().trim();
                String accPersonNum = visitNum.getText().toString().trim();
                if (!checkNull()) {
                    Toast.makeText(VisitorRegistActivity.this, "请填写完整内容", Toast.LENGTH_LONG).show();
                } else if (!checkMobile(mobile)) {
                    Toast.makeText(VisitorRegistActivity.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
                } else if (!checkEmail(visitEmail)) {
                    Toast.makeText(VisitorRegistActivity.this, "邮箱格式不正确", Toast.LENGTH_LONG).show();
                } else {

                    customDialog.show();//显示loading

                    //随行人数不输入，默认为1
                    if (accPersonNum.length() == 0) {
                        accPersonNum = "1";
                    }
                    int pn = Integer.parseInt(accPersonNum);
                    if (pn > 15) {
                        Toast.makeText(VisitorRegistActivity.this, "您的随行人数太多了，请联系您的被访对象，确认您的行程", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final Map<String, String> m = new HashMap<>();
                    m.put("date", sdf.format(new Date()));
                    m.put("visitName", visitName.getText().toString());
                    m.put("name", name.getText().toString());
                    m.put("pinCode", pinCode());
                    m.put("visitMobile", mobile);
                    m.put("reason", reason.getText().toString());
                    m.put("email", visitEmail);
                    m.put("visitNum", accPersonNum);
                    final String finalAccPersonNum = accPersonNum;
                    Thread t = new Thread(new HttpSend("post", m, "http://demo.fafa.com.cn:6161/fengqi/reserve/save", new Callback<Object, Object>() {
                        @Override
                        public Object call(Object o) {
                            System.out.println(o);
                            m.put("visitTime", Objects.requireNonNull(m.get("date")));
                            JSONObject ret;
                            try {
                                ret = new JSONObject(o.toString());
                                if (ret.optBoolean("success")) {
                                    m.put("visitTime", ret.optJSONObject("data").optString("visitTime"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int num = Integer.parseInt(finalAccPersonNum);
                            try {
                                for (int i = 0; i < num; i++) {
                                    usbPrintUtil.print(m);
                                }
                            } catch (Exception e) {
                                Toast.makeText(VisitorRegistActivity.this, "打印机连接出错，打印失败", Toast.LENGTH_LONG).show();
                            }
                            customDialog.dismiss();
                            finish();
                            return null;
                        }
                    }));
                    t.start();
                }
            }
        });
    }

    /**
     * 验证是否为空 空返回false
     *
     * @return
     */
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


    /**
     * 判断邮箱格式
     *
     * @param email 正确格式返回true
     * @return
     */
    private static boolean checkEmail(String email) {
        boolean tag = true;
        if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
            tag = false;
        }
        return tag;
    }


    /**
     * 判断字符串是否符合手机号码格式  正确格式返回true
     * 第一位1开头 第二位 不能为012
     *
     * @param mobile
     * @return 手机号
     */
    private static boolean checkMobile(String mobile) {

        String telRegex = "^(1[3-9])\\d{9}$";
        if (mobile == null || ("").equals(mobile))
            return false;
        else {
            return mobile.matches(telRegex);
        }

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
        visitNum = findViewById(R.id.visitNum);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item);
        name.setAdapter(adapter);
        visitName.addTextChangedListener(editclick);
        visitCompany.addTextChangedListener(editclick);
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
