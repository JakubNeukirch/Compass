package eu.jakubneukirch.compass.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class OnTextChangedListener implements TextWatcher {
    private OnTextChangedCallback onTextChangedCallback;

    public OnTextChangedListener(OnTextChangedCallback onTextChangedCallback) {
        this.onTextChangedCallback = onTextChangedCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        onTextChangedCallback.accept(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    public interface OnTextChangedCallback {
        void accept(String text);
    }
}
