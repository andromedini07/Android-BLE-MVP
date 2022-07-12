package com.programmingdev.androidblemvp.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class HexInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder sb = new StringBuilder();

        // Here you can add more controls, e.g. allow only hex chars etc

        for (int i = start; i < end; i++) {
            if ((source.charAt(i) >= 0x30 && source.charAt(i) <= 0x39) ||
                    (source.charAt(i) >= 0x41 && source.charAt(i) <= 0x46) ||
                    (source.charAt(i) >= 0x61 && source.charAt(i) <= 0x66)) {
                    sb.append(source.charAt(i));
            }
        }

        return sb.toString().toUpperCase();
    }
}