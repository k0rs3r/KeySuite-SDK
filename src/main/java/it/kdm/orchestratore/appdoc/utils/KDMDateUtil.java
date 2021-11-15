package it.kdm.orchestratore.appdoc.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KDMDateUtil {

	public static String  formatDate(String dateString)throws Exception{
		return formatDate(dateString, Locale.ITALY);
	}
	public static String  formatDate(String dateString, Locale locale)throws Exception{
		return formatDate( dateString, locale, "dd-MM-yyyy HH:mm:ss");
	}
	public static String  formatDate(String dateString, Locale locale, String format)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", locale);
		Date date = sdf.parse(dateString);
		SimpleDateFormat out = new SimpleDateFormat(format, locale);
		return out.format(date);
	}
	public static String  formatHtmlDate(String dateString)throws Exception{
		return formatHtmlDate(dateString, Locale.ITALY);
	}
	public static String  formatHtmlDate(String dateString, Locale locale)throws Exception{
		return  formatHtmlDate(dateString, locale, "dd-MM-yyyy HH:mm:ss");
	}
	public static String  formatHtmlDate(String dateString, Locale locale, String format)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", locale);
		Date date = sdf.parse(dateString);
		SimpleDateFormat out = new SimpleDateFormat("dd-MM-yyyy", locale);
		String stringDate = formatDate(dateString, locale, format);
		String stringShortDate = out.format(date);
		
//		return "<span title='" + stringDate + "'>" + stringShortDate + "</span>";
		return stringDate;
	}
	public static Date StringToDate(String dateString)throws Exception{
		DateTimeFormatter parser2 = ISODateTimeFormat.dateTime();
		DateTime dt = parser2.parseDateTime(dateString);
		Date date  = dt.toDate(); 
    	return date;
		 
	}
	
	public static String solrRangeDateLast7Day()throws Exception{

        DateTime from = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(7);
        DateTime to = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().plusDays(1);

        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return String.format("[%s TO %s]", formatter.print(from), formatter.print(to));
    }

	public static String solrRangeDateLast15Day()throws Exception{

		DateTime from = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(15);
		DateTime to = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().plusDays(1);

		DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
		return String.format("[%s TO %s]", formatter.print(from), formatter.print(to));
	}
}
