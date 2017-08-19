package com.freedom.code.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 验证工具类
 * @author luo
 *
 */
public class ValidateUtils {
	 /**
     * 验证是否为int类型
     * 
     * @param n
     * @return
     */
    public static boolean isInt(String arg) {
        if (arg == null)
            return false;
        try {
            Integer.valueOf(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 验证是否为float类型
     * 
     * @param n
     * @return
     */
    public static boolean isFloat(String arg) {
        if (arg == null)
            return false;
        try {
            Float.valueOf(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 验证long型
     * @param arg
     * @return
     */
    public static boolean isLong(String arg) {
        if (arg == null)
            return false;
        try {
            Long.valueOf(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 验证double型
     * @param arg
     * @return
     */
    public static boolean isDouble(String arg) {
        if (arg == null)
            return false;
        try {
            Double.valueOf(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 验收是否是时间
     * @param arg
     * @return
     */
    public static boolean isDateTime(String arg) {
        if (arg == null)
            return false;
        try {
            return toDateTime(arg) != null;
        } catch (IllegalArgumentException e) {

        }
        return false;
    }
    
    /**
     * 时间转换类
     * @param arg
     * @return
     */
    private static Timestamp toDateTime(String arg) {
        Locale locale;
        Timestamp ret;
        locale = Locale.ENGLISH;
        if (arg == null || "".equals(arg))
            return null;
        ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e2) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e3) {

        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
            return new Timestamp(sdf.parse(arg).getTime());
        } catch (Exception e4) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.M", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e5) {

        }
        throw new IllegalArgumentException("参数非法:" + arg);
    }
    
    /**
     * 验证是否为null
     * @param value
     * @return
     */
    public static boolean isNull(String value) {
        return value == null || "".equals(value.trim())
                || "null".equals(value.trim());
    }
    
    public static boolean isNull(Object value) {
        if (value == null)
            return true;
        if (value instanceof String)
            return isNull((String) value);
        else
            return false;
    }
    
    /**
     * 验证非空
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message) {
        if (object == null)
            throw new IllegalArgumentException(message);
        else
            return;
    }
    
    public static void notNull(Object object) {
        notNull(object,
                "[Assertion failed] - this argument is required; it must not null");
    }
    
    /**
     * 验证字符串是否大于指定长度
     * @param value
     * @param length
     * @return
     */
    public static boolean exceedLength(String value, int length) {
        return value == null || "".equals(value.trim())
                || "null".equals(value.trim()) || value.length() > length;
    }
    
    /**
     * 字符串是否小于指定长度
     * @param value
     * @param length
     * @return
     */
    public static boolean lessLength(String value, int length) {
        return value == null || "".equals(value.trim())
                || "null".equals(value.trim()) || value.length() < length;
    }
    
    /**
     * 是否包含html
     * @param value
     * @return
     */
    public static boolean containHTML(String value) {
        return value.indexOf("<") >= 0 || value.indexOf(">") >= 0
                || value.indexOf("&") >= 0 || value.indexOf("\"") >= 0
                || value.indexOf("'") >= 0 || value.indexOf("\\") >= 0;
    }

    /**
     * 判断数组中是否存在某个值
     * @param array
     * @param value
     * @return
     */
    public static boolean contains(String array[], String value) {
        for (int i = 0; array != null && i < array.length; i++)
            if (array[i].equals(value))
                return true;

        return false;
    }
    
    /**
     * 是否邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (isNull(email))
            return false;
        if (!allValidChars(email))
            return false;
        if (email.indexOf("@") < 1)
            return false;
        if (email.lastIndexOf(".") <= email.indexOf("@"))
            return false;
        if (email.indexOf("@") == email.length())
            return false;
        if (email.indexOf("..") >= 0)
            return false;
        return email.indexOf(".") != email.length();
    }
    
    /**
     * 验证是否手机
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (isNull(mobile))
            return false;
        return Pattern
                .matches(
                        "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|17[0|3|6|7|8]|18[0-9])\\d{8}$",
                        mobile);
    }
    
    /**
     * 是否是身份证
     * @param idNumber
     * @return
     */
    public static boolean isIdNumber(String idNumber) {
        if (isNull(idNumber))
            return false;
        String reg = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
        if (Pattern.matches(reg, idNumber)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 验证所有邮箱字符
     * @param c
     * @return
     */
    public static boolean allValidChars(String c) {
        c = c.toLowerCase();
        boolean parsed = true;
        String validchars = "abcdefghijklmnopqrstuvwxyz0123456789@.-_";
        for (int i = 0; i < c.length(); i++) {
            char letter = c.charAt(i);
            if (validchars.indexOf(letter) != -1)
                continue;
            parsed = false;
            break;
        }

        return parsed;
    }
    
    /**
     * 验证是否是null
     * @param str
     * @return
     */
    public static String NullToDoubleQuotes(String str) {
        if (isNull(str))
            return "";
        return str;
    }
    
    /**
     * 检测是否有emoji字符
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean isEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmoji(codePoint)) {
                // do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }
    
    public static boolean isEmoji(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
    
    /**
     * 等于判断
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equals(String s1, String s2) {
        if(s1==null && s2==null){
     	   return true;
        }else if(s1==null && s2!=null){
     	  return s2.equals(s1);
        }else if(s1!=null && s2==null){
     	   return s1.equals(s2);
        }else{
     	  return s1.equals(s2);
        }
     }
    
}
