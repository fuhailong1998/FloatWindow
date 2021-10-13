package com.fxkxb.floatwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;


/**
 * @author : Leon(fxkxb.com)
 * @version : 1.0
 * @file : FloatingService.class
 * @date : October 12,2021 15:29
 * @description :
 */

public class FloatingButtonService extends Service {
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Button button;

    @Override
    public void onCreate() {
        super.onCreate();


        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layoutParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
//            对不同版本的Android系统进行适配
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }


// 设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;

//      gravity不是说你添加到WindowManager中的View相对屏幕的几种放置,而是说你可以设置参考系
//        例如:mWinParams.gravity= Gravity.LEFT | Gravity.TOP;意思是以屏幕左上角为参考系,那么屏幕左上角的坐标就是(0,0),这是你后面摆放View位置的唯一依据.
//        当你设置为mWinParams.gravity = Gravity.CENTER;那么你的屏幕中心为参考系,坐标(0,0).一般我们用屏幕左上角为参考系.
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.alpha = 0.5F;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SECURE;
        layoutParams.width = 400;
        layoutParams.height = 400;
        layoutParams.x = 0;
        layoutParams.y = 0;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            button = new Button(getApplicationContext());
//            button.setText("Floating Window");
            button.setBackgroundColor(Color.MAGENTA);
            windowManager.addView(button, layoutParams);
            button.setOnTouchListener((View.OnTouchListener) new FloatingOnTouchListener());
//            button.setOnClickListener(v -> Toast.makeText(getApplicationContext(),"You Click the Float Window", Toast.LENGTH_SHORT).show());

        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();


                    Toast.makeText(getApplicationContext(),
                            "Location: x = " + x + " , y = " + y, Toast.LENGTH_SHORT).show();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    Log.e("Locaiton", "onTouch: X is :" + x + " Y is :" + y);
                    break;
                default:
                    break;
            }
            return false;
        }

    }
}
