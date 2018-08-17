package com.emrehmrc.harcamalarm.utils;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class Methodes {
    public static SpannableString ColorString(Integer out, Integer colorId) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String string="";
        if (out>0){string = Util.IN + out.toString();}
        else if(out<0){string =out.toString();}
        else if(out==0){string = out.toString();}
        string=string+Util.MONEY_SYMBOL;

        SpannableString string1 = new SpannableString(string);
        string1.setSpan(new ForegroundColorSpan(colorId), 0, string.length(), 0);
        builder.append(string1);
        return string1;
    }
    public static SpannableString ColorString(String out, Integer colorId) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String string=out;
        SpannableString string1 = new SpannableString(string);
        string1.setSpan(new ForegroundColorSpan(colorId), 0, string.length(), 0);
        builder.append(string1);
        return string1;
    }

}
