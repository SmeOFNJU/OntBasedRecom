package cn.thinkingdata.crawler.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

	private static DateFormat partitionDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static DateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	private static DateFormat monthStyleDateFormat = new SimpleDateFormat("yyyy年M月");

	private static DateFormat preciseShortDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static DateFormat completeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getDatePath(Date date) {
		return dateFormat.format(date);
	}

	public static String getShortDatePath(Date date) {
		return shortDateFormat.format(date);
	}

	public static Date parseDateString(String dateStr) {
		int length = dateStr.length();
		DateFormat format;
		
		if(8 == length){
			format = new SimpleDateFormat("yyyyMMdd");
			try {
				return format.parse(dateStr);
			} catch (ParseException e) {

			}					
		}else if(14 == length){
			format = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				return format.parse(dateStr);
			} catch (ParseException e) {
				
			}
		}else if(10 == length){
			format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return format.parse(dateStr);
			} catch (ParseException e) {

			}
			
			format = new SimpleDateFormat("yyyy/MM/dd");
			try {
				return format.parse(dateStr);
			} catch (ParseException e) {

			}
		}else if(19 == length){
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return format.parse(dateStr);
			} catch (ParseException e) {

			}
		}
		throw new RuntimeException("can not parse date string :" + dateStr);
	}
	
	public synchronized static Date parseDateStringTwice(String dateStr) {
		try{
			Date date = completeDateFormat.parse(dateStr);
			return date;
		}catch(Exception e){			
		}
		
		try{
			Date date = partitionDateFormat.parse(dateStr);
			return date;
		}catch(Exception e){			
		}
		throw new RuntimeException("can not parse date string :" + dateStr);
	}

	public static String getPartitionString(Date date) {
		return partitionDateFormat.format(date);
	}
	
	public static String getCompleteString(Date date){
		return completeDateFormat.format(date);
	}

	public static String yesterday(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, -1);
		return partitionDateFormat.format(calendar.getTime());
	}
	
	public static String tomorrow(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, 1);
		return partitionDateFormat.format(calendar.getTime());
	}
	
	public static String lastMonth(Date currentDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, -1);
		return partitionDateFormat.format(calendar.getTime());
	}
	
	public static String lastYear(Date currentDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.YEAR, -1);
		return partitionDateFormat.format(calendar.getTime());
	}
	
	public static java.sql.Timestamp toSQLTimestamp(String dateString){
		return java.sql.Timestamp.valueOf(dateString);
	}
	
	public static java.sql.Date toSQLDate(String dateString) {
		Date date = parseDateString(dateString);
		return new java.sql.Date(date.getTime());
	}
	
	public static java.sql.Date toSQLDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static Date getOffsetDate(Date currentDate, int offsetDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, offsetDate);
		return calendar.getTime();
	}
	
	public static String getOffsetDatePartitionString(Date currentDate, int offsetDate){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, offsetDate);
		return partitionDateFormat.format(calendar.getTime());
	}
	
	public static Date getOffsetHour(Date currentDate, int offsetHour){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.HOUR, offsetHour);
		return calendar.getTime();
	}
	
	public static String getMonthStyleString(Date date){
		return monthStyleDateFormat.format(date);
	}
	
	public static String getPreciseShortDateString(Date date){
		return preciseShortDateFormat.format(date);
	}
	
	public static String getCurrentPreciseDateString(){
		return completeDateFormat.format(new Date());
	}
}





