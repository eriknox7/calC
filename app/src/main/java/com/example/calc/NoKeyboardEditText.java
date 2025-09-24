package com.example.calc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatEditText;

public class NoKeyboardEditText extends AppCompatEditText {

    public NoKeyboardEditText(Context context) {
        super(context);
        init();
    }

    public NoKeyboardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoKeyboardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Ensure the cursor is visible
        setCursorVisible(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Always return true to consume touch events
        return true;
    }
}