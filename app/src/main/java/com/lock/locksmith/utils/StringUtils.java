package com.lock.locksmith.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import com.lock.locksmith.LockSmithApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/16
 *     desc  : utils about string
 * </pre>
 */
public final class StringUtils {

    private static final String TAG = "StringUtils";

    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the string is null or 0-length.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * Return whether the string is null or whitespace.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether string1 is equals to string2.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) return true;
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether string1 is equals to string2, ignoring case considerations..
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * Return {@code ""} if string equals null.
     *
     * @param s The string.
     * @return {@code ""} if string equals null
     */
    public static String null2Length0(final String s) {
        return s == null ? "" : s;
    }

    /**
     * Return the length of string.
     *
     * @param s The string.
     * @return the length of string
     */
    public static int length(final CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * Set the first letter of string upper.
     *
     * @param s The string.
     * @return the string with first letter upper.
     */
    public static String upperFirstLetter(final String s) {
        if (s == null || s.length() == 0) return "";
        if (!Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * Set the first letter of string lower.
     *
     * @param s The string.
     * @return the string with first letter lower.
     */
    public static String lowerFirstLetter(final String s) {
        if (s == null || s.length() == 0) return "";
        if (!Character.isUpperCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * Reverse the string.
     *
     * @param s The string.
     * @return the reverse string.
     */
    public static String reverse(final String s) {
        if (s == null) return "";
        int len = s.length();
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * Convert string to DBC.
     *
     * @param s The string.
     * @return the DBC string
     */
    public static String toDBC(final String s) {
        if (s == null || s.length() == 0) return "";
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * Convert string to SBC.
     *
     * @param s The string.
     * @return the SBC string
     */
    public static String toSBC(final String s) {
        if (s == null || s.length() == 0) return "";
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    public static String removeSpace(String s) {
        if (s != null && !s.equals("null")) {
            return s.replace(" ", "");
        }
        return "";
    }

    public static Html.ImageGetter getImageGetter() {
        return source -> {
            int id = Integer.parseInt(source);
            Drawable drawable = ContextCompat.getDrawable(LockSmithApplication.Companion.getContext(), id);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            return drawable;
        };
    }

    public static String getMaxCount(long count, long maxCount) {
        if (count > maxCount) {
            return "+" + maxCount;
        } else {
            return String.valueOf(count);
        }
    }

    public static String getMaxCount2(long count, long maxCount) {
        if (count > maxCount) {
            return maxCount + "+";
        } else {
            return String.valueOf(count);
        }
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return sb.toString();
        }
    }

    public static Map<String, String> getStringMap(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        Map<String, String> map = new HashMap<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                map.put(line.toLowerCase(), line);
            }
        } catch (IOException ignored) {
        }
        return map;
    }

    public static String getString(Context context, @StringRes int resId, Object... args) {
        if (context == null) {
            return "";
        }
        return format(context.getString(resId), args);
    }

    public static String getString(@StringRes int resId, Object... args) {
        Activity activity = LockSmithApplication.Companion.getContext().currentActivity();
        if (activity != null) {
            return getString(activity, resId, args);
        }
        return getString(LockSmithApplication.Companion.getContext(), resId, args);
    }



    public static String format(String text, Object... args) {
        if (text == null || args == null) {
            return "";
        }

        try {
            return String.format(text, args);
        } catch (Exception e) {
            return text;
        }
    }

    public static Spannable getSpannable(String content, String key, @ColorInt int color) {
        return getSpannable(content, key, 0, color, 0, false, null);
    }

    public static Spannable getSpannable(String content, String key, int textSize, int typeface) {
        return getSpannable(content, key, textSize, 0, typeface, false, null);
    }

    public static Spannable getSpannable(String content, String key, int textSize, @ColorInt int color, int typeface, boolean isUnderline, View.OnClickListener listener) {
        Spannable span = new SpannableString(content);
        if(isEmpty(key)){
            return span;
        }
        int beginIndex = content.indexOf(key);
        if(beginIndex < 0) {
            return span;
        }

        //设置文本大小
        if (textSize > 0) {
            span.setSpan(new AbsoluteSizeSpan(textSize), beginIndex, beginIndex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //设置文本颜色
        if (color != 0) {
            span.setSpan(new ForegroundColorSpan(color), beginIndex, beginIndex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //设置字体
        if (typeface > 0) {
            span.setSpan(new StyleSpan(typeface), beginIndex, beginIndex + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //设置下划线
        if (isUnderline) {
            span.setSpan(new UnderlineSpan(), beginIndex, beginIndex + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //设置点击监听
        if (listener != null) {
            span.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(color);
                    ds.setUnderlineText(isUnderline);
                }

                @Override
                public void onClick(@NonNull View widget) {
                    listener.onClick(widget);
                }
            }, beginIndex, beginIndex + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    /**
     * 设置字体加粗
     */
    public static Spannable getSpannableTypefaceBold(String content, String key) {
        if (content == null || key == null) {
            return new SpannableString("");
        }

        Spannable span = new SpannableString(content);

        if (!TextUtils.isEmpty(key)) {
            int beginIndex = content.indexOf(key);
            span.setSpan(new StyleSpan(Typeface.BOLD), beginIndex, beginIndex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    /**
     * 设置颜色
     */
    public static Spannable getSpannable(String content, @ColorInt int color, String... keys) {
        if (content == null || keys == null) {
            return new SpannableString("");
        }

        Spannable span = new SpannableString(content);
        int start = 0, end;
        int index;

        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                while ((index = content.indexOf(key, start)) > -1) {
                    end = index + key.length();
                    span.setSpan(new ForegroundColorSpan(color), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = end;
                }
            }
        }
        return span;
    }

    /**
     * 设置颜色 和 加粗
     * @param content
     * @param color
     * @param keys
     * @return
     */
    public static Spannable getSpannableColorAndBold(String content, @ColorInt int color, String... keys) {
        if (content == null || keys == null) {
            return new SpannableString("");
        }

        Spannable span = new SpannableString(content);
        int start = 0, end;
        int index;

        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                while ((index = content.indexOf(key, start)) > -1) {
                    end = index + key.length();
                    span.setSpan(new StyleSpan(Typeface.BOLD), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new ForegroundColorSpan(color), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = end;
                }
            }
        }
        return span;
    }

    /**
     * 设置多个颜色
     */
    public static Spannable getSpannable(String content, String key, @ColorInt int color, String key2, @ColorInt int color2) {
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(key2);
        List<Integer> colors = new ArrayList<>();
        colors.add(color);
        colors.add(color2);
        return getSpannable(content, keys, colors);
    }

    /**
     * 集合为空
     */
    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    /**
     * 集合为空
     */
    public static boolean isEmpty(ArrayList list) {
        return list == null || list.isEmpty();
    }

    /**
     * 集合不为空
     */
    public static boolean notEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    /**
     * 设置多个颜色
     */
    public static Spannable getSpannable(String content, List<String> keys, @ColorInt List<Integer> colors) {
        if (TextUtils.isEmpty(content)) {
            return new SpannableString("");
        }

        if (isEmpty(keys) || isEmpty(colors) || keys.size() != colors.size()) {
            return new SpannableString(content);
        }

        Spannable span = new SpannableString(content);

        int start = 0, end;
        int index;

        for (String key : keys) {
            if (!TextUtils.isEmpty(key)) {
                while ((index = content.indexOf(key, start)) > -1) {
                    end = index + key.length();
                    span.setSpan(new ForegroundColorSpan(colors.get(keys.indexOf(key))), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = end;
                }
            }
        }
        return span;
    }

    /**
     * 处理字符过多，拼接问题
     * @param key 添加到hashmap 当中的key
     * @param disposeContent 文本过长 ，拆分的内容
     * */
    public static void disposeString(Map<String,Object> hashMap,String key,String disposeContent) {
        if (hashMap == null || TextUtils.isEmpty(disposeContent)) {
            return;
        }

        int index = 0;
        int round = 0;

        while (index < disposeContent.length()) {
            if (index+100 > disposeContent.length()) {
                hashMap.put(key+(round+1),disposeContent.substring(round*100));
                index = disposeContent.length();
            } else {
                hashMap.put(key+(round+1),disposeContent.substring(round*100 , (round+1)*100));
                index += 100;
            }
            round++;
        }
    }

    /**
     * 给数字每三位加一个逗号的处理
     *
     * @param str
     * @return
     */
    public static String addComma(String str) {
        try {

            DecimalFormat myformat = new DecimalFormat();
            myformat.applyPattern("#,###");
            return myformat.format(Double.parseDouble(str));
        } catch (Throwable throwable) {
        }
        return str;
    }

    /**
     * 安全替换字符串
     * @param originText 原始文本
     * @param oldStr 需要替换的字符串
     * @param newStr 替换的字符串
     * @return 替换后的字符串
     */
    public static String safeReplace(String originText, String oldStr, String newStr) {
        try {
            return originText.replace(oldStr, newStr);
        } catch (Exception e) {
            e.printStackTrace();
            return originText;
        }
    }

    /**
     * 安全替换字符串
     * @param originText 原始文本
     * @param oldStr 需要替换的字符串
     * @param newStr 替换的字符串
     * @return 替换后的字符串
     */
    public static String safeReplaceAll(String originText, String oldStr, String newStr) {
        try {
            return originText.replaceAll(oldStr, newStr);
        } catch (Exception e) {
            e.printStackTrace();
            return originText;
        }
    }
}