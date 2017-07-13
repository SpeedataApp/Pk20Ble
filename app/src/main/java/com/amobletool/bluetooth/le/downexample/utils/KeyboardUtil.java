package com.amobletool.bluetooth.le.downexample.utils;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.amobletool.bluetooth.le.R;

public class KeyboardUtil {
    private Context ctx;
    private Keyboard k1;// 字母键盘
    private KeyboardView keyboardView;
    private EditText ed;

    public KeyboardUtil(KeyboardView keyboardView, Context ctx, EditText edit) {
        this.ctx = ctx;
        this.ed = edit;
        this.keyboardView = keyboardView;
        k1 = new Keyboard(ctx, R.xml.qwerty);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if ("DEL".equals(text)) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if ("完成".equals(text)) {
                hideKeyboard();
            } else {
                if (start >= 6) {
                    hideKeyboard();
                    return;
                }
                editable.insert(start, text);
            }
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
        }
    };

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.GONE);
        }
    }

}
