package com.freedom.code.common.utils.date;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 常见时间工具类
 * sqldate 和date
 * @author luo
 *
 */
public class DateUtil {
	/**
	 * 返回Timestamp类型的时间
	 * @param arg
	 * @return
	 */
	public static Timestamp toDateTime(String arg) {
		//类对象表示了特定的地理，政治和文化地区
        Locale locale;
        //时间戳类型的时间
        Timestamp ret;
        locale = Locale.ENGLISH;
        if (arg == null || "".equals(arg))
            return null;
        ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                    locale);
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
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss z yyyy", locale);
            return new Timestamp(sdf.parse(arg).getTime());
        } catch (Exception e4) {

        }
        throw new IllegalArgumentException("参数非法:" + arg);
    }
	
	public static Timestamp toDateTime(String arg, boolean defaultValue) {
        Timestamp ret;
        if (!defaultValue)
            return toDateTime(arg);
        if (arg == null || "".equals(arg))
            return new Timestamp(System.currentTimeMillis());
        ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e2) {

        }
        //如果都转换都有问题，则用当前系统时间返回
        return new Timestamp(System.currentTimeMillis());
    }
	
	/**
	 * 返回date类型的时间
	 * @param arg
	 * @return
	 */
	 public static Date toDate(String arg) {
	        try {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            return sdf.parse(arg);
	        } catch (ParseException e) {

	        }
	        try {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	            return sdf.parse(arg);
	        } catch (ParseException e1) {

	        }
	        try {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            return sdf.parse(arg);
	        } catch (ParseException e2) {

	        }
	        return new Date();
	   }
	
	 public static String toDateFormat(Timestamp datetime) {
	        return toDateFormat(datetime, null);
	 }
	 
	 public static String toDateFormat(Date datetime) {
	        return toDateFormat(datetime, null);
	 }
	 
	 /**
	  * 美化 Timestamp时间类型
	  * @param datetime
	  * @param pattern
	  * @return
	  */
	 public static String toDateFormat(Timestamp datetime, String pattern) {
	        SimpleDateFormat sdf = null;
	        if (datetime == null)
	            return null;
	        try {
	            if (pattern == null)
	                pattern = "yyyy-MM-dd HH:ss";
	            sdf = new SimpleDateFormat(pattern);
	        } catch (Exception e) {
	            sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss");
	        }
	        return sdf.format(datetime);
	 }
	 
	 /**
	  * 美化 Date时间类型
	  * @param datetime
	  * @param pattern
	  * @return
	  */
	 public static String toDateFormat(Date datetime, String pattern) {
	        SimpleDateFormat sdf = null;
	        if (datetime == null)
	            return null;
	        try {
	            if (pattern == null)
	                pattern = "yyyy-MM-dd HH:ss";
	            sdf = new SimpleDateFormat(pattern);
	        } catch (Exception e) {
	            sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss");
	        }
	        return sdf.format(datetime);
	 }
	 
	 /**
	   * 得到某一天的凌晨时间
	   * @param date
	   * @return
	 */
     public static Date getToday(Date date){
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(date);
		 cal.set(Calendar.HOUR_OF_DAY, 0);
	     cal.set(Calendar.MINUTE, 0);
	     cal.set(Calendar.SECOND, 0);
	     cal.set(Calendar.MILLISECOND, 0);
	     return cal.getTime();
	 }
     
     /**
 	 * 得到某一天的23：59 时间
 	 * @param date
 	 * @return
 	 */
 	public static Date getTodayLast(Date date){
 	    Calendar cal = Calendar.getInstance();
 	    cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
 	}
 	
 	/**
	 * 得到今天凌晨的时间.
	 * GameFunction.getToday().getTime() 相当于 php中 date('Y-m-d');
	 * @return
	 */
	public static Date getToday() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	 
	 /**
     * 将Unix时间戳转为Java秒数
     * 
     * @param unixTimeStamp
     * @return
     */
    public static long parseToMillis(int unixTimeStamp) {
        return (long) unixTimeStamp * (long) 1000;
    }

    /**
     * 将Unix时间戳转为字符串
     * 
     * @param unixTimeStamp
     * @return
     */
    public static String parseToDateString(int unixTimeStamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date((long) unixTimeStamp
                * (long) 1000));
    }

    /**
     * 将时间戳转为年月日小时分
     * 
     * @param unixTimeStamp
     * @return
     * @author lain
     */
    public static String parseToDateHourString(int unixTimeStamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date((long) unixTimeStamp
                * (long) 1000));
    }

    /**
     * 将Unix时间戳转为Date类型
     * 
     * @param unixTimeStamp
     * @return
     */
    public static Date parseToDate(int unixTimeStamp) {
        Long timeStamp = new Long((long) unixTimeStamp) * 1000;
        return new java.util.Date(timeStamp);
    }

    /**
     * 将时间戳转换为日期格式
     * 
     * @param unixTimeStamp
     * @return
     * @author lain
     */
    public static Date parseToDate(long unixTimeStamp) {
        return new java.util.Date(unixTimeStamp);
    }
    
    /**
     * 日期转为字符串
     * 
     * @param date
     * @param fmt
     * @return
     */
    public static String formatDate(Date date, String fmt) {
        return new SimpleDateFormat(fmt).format(date);
    }
    
    /**
     * 获取默认时间值 0000-00-00 00:00:00
     * 
     * @return
     */
    public static Date getDefaultDate() {
        return new Date(0);
    }

    /**
     * 得到明天凌晨的时间. GameFunction.getTomorrow().getTime() 相当于 php中 date('Y-m-d');
     * 
     * @return
     */
    public static Date getTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    /**
     * 得到今日23：59 时间
     * 
     * @return
     */
    public static Date getTodayLast() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 得到某一天的凌晨时间
     * 
     * @param date
     * @return
     */
    public static Date getTodaydate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 得到某一天的23：59 时间
     * 
     * @param date
     * @return
     */
    public static Date getTodayLastdate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 昨天当前时间
     * 
     * @return
     */
    public static Date getYesterday() {
        Date today = Calendar.getInstance().getTime();
        long t = today.getTime();
        Date date = new Date(t - 24 * 60 * 60 * 1000l);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {

        }
        return date;
    }

    // 得到几天前当天时间===================================================================
    public static Date getSomeDate(Date date, int dayNum) {
        Calendar cal = Calendar.getInstance();
        long DAY = 1000 * 3600 * 24;
        cal.setTimeInMillis(date.getTime() + DAY * (long) dayNum);
        return cal.getTime();
    }
	
	public static void main(String[] args) {
		System.out.println(toDateTime("测试",true));
	}
}
