package com.codepath.insync.utils;

import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import java.util.NavigableMap;
import java.util.TreeMap;


public class FormatUtil {

    final static StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
    final static StyleSpan nss = new StyleSpan(Typeface.NORMAL); //Span to make text normal


    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case
        if (value < 10000 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            return decimalFormat.format(value);

        }
        TreeMap.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static SpannableStringBuilder buildSpan(String formatString, int nStart, int nLength, int bStart, int bLength) {

        SpannableStringBuilder sb = new SpannableStringBuilder(formatString);

        // make count characters Bold
        sb.setSpan(bss, bStart, bLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(nss, nStart, nLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return sb;
    }

}
