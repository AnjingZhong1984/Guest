package com.fafa.guest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fullscreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.visitorBtnImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                TextView tv = new TextView(MainActivity.this);
                tv.setPadding(20,20,20,20);
                tv.setTextSize(18);
                tv.setText("空气化工产品（中国）投资有限公司来访者安全须知\n" +
                        "\n" +
                        "\uF0B2\t来访者需在前台处登记，佩戴访客贴纸\n" +
                        "\uF0B2\t来访者需在受访者的陪同下进行访问\n" +
                        "\uF0B2\t大楼内禁止奔跑\n" +
                        "\uF0B2\t大楼内禁止吸烟\n" +
                        "\uF0B2\t上下楼梯请扶扶手\n" +
                        "\uF0B2\t未经空气产品公司允许，禁止在空气产品公司内部拍照、摄影摄像（包括手机拍照、摄影摄像）\n" +
                        "\uF0B2\t听到警铃响起时，请立即撤离大楼并在受访者的陪同下在紧急集合点集合\n" +
                        "\n" +
                        "我已清楚知晓并将执行空气产品公司来访者安全须知\n" +
                        "\n" +
                        "Air Products Safety and Security Notice for Visitors\n" +
                        "\n" +
                        "\uF0B2\tAll Visitors shall register at the reception area and wear visitor sticker when going inside the building\n" +
                        "\uF0B2\tAll Visitors shall be escorted by Air Products employees when going inside the building\n" +
                        "\uF0B2\tNo running in the building\n" +
                        "\uF0B2\tSmoking is prohibited in the building\n" +
                        "\uF0B2\tHand on rail when walking on the stair\n" +
                        "\uF0B2\tWithout permission by Air Products, no visitors are allowed to take any photos or videos in the Air Products building (including cell phone with digital camera)\n" +
                        "\uF0B2\tWhen hearing the alarm rings, please evacuate the building immediately and gather to the muster point with the escort by Air Products employees\n" +
                        "\n" +
                        "I acknowledge and will follow Air Products Safety and Security Notice for Visitors\n");
                ScrollView sv = new ScrollView(MainActivity.this);
                sv.addView(tv);
                builder.setIcon(R.drawable.logo).setTitle("AP访客须知");
                builder.setView(sv);
                builder.setNegativeButton("Cancel(取消)", null);
                builder.setPositiveButton("Agree(同意)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, VisitorRegistActivity.class);
                        startActivity(intent);
                    }
                });

                final AlertDialog protocolDialog = builder.create();
                protocolDialog.show();
            }
        });

        findViewById(R.id.coBtnImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
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
    }
}
