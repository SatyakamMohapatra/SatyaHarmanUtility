package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class DemoForTimeAggrigate {
	public static void main(String[] args) throws ParseException{

		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		date = dateFormat.parse("12/3/2011");
		//System.out.println(JulianDate.fromJulianToDate(2455899));
		System.out.println(DateUtility.fromDateToJulian(date));
		System.out.println(dateFormat.format(DateUtility.fromJulianToDate(2455899)));
	}
}
