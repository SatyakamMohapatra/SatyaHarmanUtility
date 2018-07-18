package test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @since Unify 3.2
 * @see This utility has been added to convert JulianDate to Java.Util.Date and 
 * 		vice versa
 *
 */
public class DateUtility {

	public static int JGREG= 15 + 31*(10+12*1582);
	public static double HALFSECOND = 0.5;

	/**
	 * @param ymd
	 * @return
	 */
	private static double toJulian(int[] ymd) {
		int year=ymd[0];
		int month=ymd[1]; // jan=1, feb=2,...
		int day=ymd[2];    
		int julianYear = year;
		if (year < 0) julianYear++;
		int julianMonth = month;
		if (month > 2) {
			julianMonth++;
		}
		else {
			julianYear--;
			julianMonth += 13;
		}

		double julian = (Math.floor(365.25 * julianYear)
				+ Math.floor(30.6001*julianMonth) + day + 1720995.0);
		if (day + 31 * (month + 12 * year) >= JGREG) {
			// change over to Gregorian calendar
			int ja = (int)(0.01 * julianYear);
			julian += 2 - ja + (0.25 * ja);
		}
		return Math.floor(julian);
	}


	/**
	 * @param injulian
	 * @return
	 */
	private static int[] fromJulian(double injulian) {
		int jalpha,ja,jb,jc,jd,je,year,month,day;
		ja = (int) injulian;
		if (ja>= JGREG) {    
			jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
			ja = ja + 1 + jalpha - jalpha / 4;
		}

		jb = ja + 1524;
		jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
		jd = 365 * jc + jc / 4;
		je = (int) ((jb - jd) / 30.6001);
		day = jb - jd - (int) (30.6001 * je);
		month = je - 1;
		if (month > 12) month = month - 12;
		year = jc - 4715;
		if (month > 2) year--;
		if (year <= 0) year--;

		return new int[] {year, month, day};
	}
	/**
	 * @param injulian
	 * @since Unify 3.2
	 * @see This utility has been added to convert JulianDate to {@link Date}
	 * @return {@link Date}

	 */
	public static Date fromJulianToDate(double injulian) {

		int[] i = fromJulian(injulian);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setLenient(false);
		gc.set(GregorianCalendar.YEAR, i[0]);
		gc.set(GregorianCalendar.MONTH, (i[1]-1));
		gc.set(GregorianCalendar.DATE, i[2]);
		return gc.getTime();

	}
	/**
	 * @param date
	 * @since Unify 3.2
	 * @see This utility has been added to convert {@link Date} to JulianDate
	 * @return Double
	 */
	public static Double fromDateToJulian(Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.get(Calendar.DAY_OF_MONTH);
		int[] i ={gc.get(gc.YEAR),gc.get(gc.MONTH)+1,gc.get(gc.DAY_OF_MONTH)};
		return toJulian(i);

	}
}
