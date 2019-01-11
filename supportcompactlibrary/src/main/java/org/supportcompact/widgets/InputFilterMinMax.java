package org.supportcompact.widgets;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private Float min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = (float) min;
        this.max = (float) max;
    }

    public InputFilterMinMax(Float min, Float max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = (float) Integer.parseInt(min);
        this.max = (float) Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        try {
            //String data = String.valueOf(dest.toString() + source.toString()).replace(",", "");

            String newVal = dest.toString().substring(0, dStart) + dest.toString().substring(dEnd, dest.toString().length());
            newVal = newVal.substring(0, dStart) + source.toString() + newVal.substring(dStart, newVal.length());

            Float input = Float.parseFloat(newVal/*dest.toString() + source.toString()*/);
            //input = Float.parseFloat(String.format(Locale.getDefault(), "%.2f", input));
            String[] point = String.valueOf(newVal/*dest.toString() + source.toString()*/).split("\\.");
            boolean isTwoDigits = false;
            if (point.length == 1) {
                isTwoDigits = true;
            } else if (point.length <= 2 && point[1].length() <= 2) {
                isTwoDigits = true;
            }
            if (isInRange(min, max, input) && isTwoDigits)
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(Float a, Float b, Float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
