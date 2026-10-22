package com.ak7.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ClickerAccessibilityService extends AccessibilityService {

    private WindowManager windowManager;
    private View floatingView;
    private boolean isRunning = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int clickCount = 0;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Button toggleBtn = new Button(this);
        toggleBtn.setText("تشغيل / إيقاف");
        toggleBtn.setBackgroundColor(0xFF23E59E);
        toggleBtn.setTextColor(0xFFFFFFFF);

        layout.addView(toggleBtn);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;

        toggleBtn.setOnClickListener(v -> {
            isRunning = !isRunning;
            if (isRunning) {
                toggleBtn.setText("إيقاف (شغال)");
                toggleBtn.setAlpha(0.8f);
                startClicking();
            } else {
                toggleBtn.setText("تشغيل / إيقاف");
                toggleBtn.setAlpha(1.0f);
            }
        });

        windowManager.addView(layout, params);
    }

    private void startClicking() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                clickCount++;
                if (clickCount > 150) {
                    Toast.makeText(getApplicationContext(), "تحذير: نظام الحماية رصد النقر العالي!", Toast.LENGTH_LONG).show();
                    clickCount = 0;
                }

                performClickAt(500, 1000);
                handler.postDelayed(this, 80);
            }
        }, 80);
    }

    private void performClickAt(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
        dispatchGesture(builder.build(), null, null);
    }

    @Override
    public void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}
}
