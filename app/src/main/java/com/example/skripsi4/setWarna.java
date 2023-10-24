package com.example.skripsi4;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.ArrayList;

public class setWarna {
    public static SpannableString setWarna(String originalText, ArrayList<String> temp, ArrayList<String> temp1) {
        SpannableString spannableString = new SpannableString(originalText);
        ArrayList<String> merge = new ArrayList<>();
        merge.addAll(temp1);
        merge.addAll(temp);
        int i = 0;
        Log.d("merge1", merge.toString());
        // Iterasi melalui setiap kata yang ingin diubah warnanya
        for (String word : merge) {
            int startIndex = originalText.indexOf(word);
            String pattern = "\\b" + word + "\\b";
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = regex.matcher(originalText);

            if (matcher.find()) {
                SpannableString spannableStringX = new SpannableString(originalText);

                // Mengubah warna teks untuk setiap kecocokan kataDicari
                do {
                    int start = matcher.start();
                    int end = matcher.end();

                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
                    spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } while (matcher.find());

                // Menetapkan SpannableString pada TextView
                return spannableStringX;
                //textView.setText(spannableString);
            }


            if (startIndex == -1) {
                continue;
            }
            int endIndex = startIndex + word.length();
            int color = 0;
            int colorIndex = word.indexOf(word);
            if (temp.get(i).equals("-")) {
                color = Color.RED;
            } else {
                color = Color.GREEN;
            }
            color = Color.RED;
            Log.d("boolThis", Boolean.toString(word.equals("-")));
            i = (i < temp1.size() - 1) ? i + 1 : i + 0;
            //int color = (word.equals(temp.get(i)) && word.equals(temp1.get(i)))?Color.GREEN:Color.RED;

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    public static SpannableString setWarnaX(String originalText, ArrayList<String> temp, ArrayList<String> temp1) {
        SpannableString spannableString = new SpannableString(originalText);
        ArrayList<String> merge = new ArrayList<>();
        merge.addAll(temp1);
        merge.addAll(temp);
        int i = 0;
        for (String word : merge) {
            int startIndex = originalText.indexOf(word);
            String pattern = "\\b" + word + "\\b";
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = regex.matcher(originalText);

            if (matcher.find()) {
                do {
                    int start = matcher.start();
                    int end = matcher.end();

                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
                    spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } while (matcher.find());
            }
        }
        return spannableString;
    }
}

