package com.fafa.guest;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义Dialog 加载弹框
 */
public class CustomDialog extends Dialog {
    private static final String TAG = "CustomDialog";
    private String mMessage; // 加载中文字
    private boolean mCancelable;
    private RotateAnimation mRotateAnimation;
    private int mImageId;

    public CustomDialog(@NonNull Context context, String message, int imageId) {
        this(context, R.style.LoadingDialog, message, imageId, false);
    }

    public CustomDialog(@NonNull Context context, int themeResId, String message, int imageId, boolean cancelable) {
        super(context, themeResId);
        mMessage = message;
        mImageId = imageId;
        mCancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        setContentView(R.layout.loading);


        int magnify = 10000;
        int toDegrees = 360;
        int duration = 2000;
        toDegrees *= magnify;
        duration *= magnify;//扩大一万倍，缩小动画卡顿

        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        // 设置窗口背景透明度
        attributes.alpha = 0.6f;
        // 设置窗口宽高
        attributes.width = screenWidth / 1;
        attributes.height = screenHeight / 1;
        getWindow().setAttributes(attributes);
        setCancelable(mCancelable);
        TextView tv_loading = findViewById(R.id.tv_loading);
        ImageView iv_loading = findViewById(R.id.iv_loading);
        tv_loading.setText(mMessage);
        iv_loading.setImageResource(mImageId);
        // 先对imageView进行测量，以便拿到它的宽高（否则getMeasuredWidth为0）
        iv_loading.measure(0, 0);

        System.out.println("iv_loading:" + iv_loading.getMeasuredWidth() / 2);
        System.out.println("iv_loading:" + iv_loading.getMeasuredHeight() / 2);
        // 设置选择动画
        mRotateAnimation = new RotateAnimation(0, toDegrees, iv_loading.getMeasuredWidth() / 2, iv_loading.getMeasuredHeight() / 2);
        mRotateAnimation.setInterpolator(new LinearInterpolator());//匀速插值器 解决卡顿问题
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(-1);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        iv_loading.startAnimation(mRotateAnimation);
    }

    @Override
    public void dismiss() {
        mRotateAnimation.cancel();
        super.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 屏蔽返回键
            return mCancelable;
        }
        return super.onKeyDown(keyCode, event);
    }
}
