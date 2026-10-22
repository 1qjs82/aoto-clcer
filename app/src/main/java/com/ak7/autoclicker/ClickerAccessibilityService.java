
package com.ak7.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
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
    public void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        // بناء زر التحكم العائم المصغر
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        Button toggleBtn = new Button(this);
        toggleBtn.setText("تشغيل/إيقاف");
        toggleBtn.setBackgroundColor(0xFF22c55e);
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
                toggleBtn.setText("إيقاف (شفاف)");
                // تحويل الزر إلى شفاف بالكامل أثناء التشغيل بناءً على طلبك!
                toggleBtn.setAlpha(0.05f); 
                startClicking();
            } else {
                toggleBtn.setText("تشغيل/إيقاف");
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
                
                // نظام الحماية الذكي والمتطور (يقيس تكرار الضربات ويحاكي الأمان)
                clickCount++;
                if (clickCount > 150) {
                    // محاكاة نظام الكشف الذكي لسيرفرات ماين كرافت
                    Toast.makeText(getApplicationContext(), "⚠️ تحذير خطير: نظام الحماية رصد الكليكر! اخرج فوراً!", Toast.LENGTH_LONG).show();
                    clickCount = 0;
                }

                // تنفيذ الضغطة في إحداثيات الدائرة بالمنتصف أو المكان المخصص
                performClickAt(500, 1000);
                
                handler.postDelayed(this, 80); // سرعة الضرب (كل 80 ميلي ثانية)
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
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}
}
