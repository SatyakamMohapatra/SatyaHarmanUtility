/*
 * @(#)DateFormatter.java      01/04/2003
 *
 * Copyright (c) 2002 Symphony Services. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Symphony Services, ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Symphony Services.
 *
 * SYMPHONY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SYMPHONY SERVICES SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version			:	1.0
 * @author			:	rbhat
 * @reviewed by		:
 * @date reviewed	:
 */


package util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.constants.DashboardConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;


/**
 * Utility class for date - time conversions.
 */

public class DateFormatter
{
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,HelperUtility.class.getName());	

	 /**
	 * This method accepts a java.sql.Timestamp value and converts it
	 * into the format specified in system.properties.
	 * <p>
	 * 
	 * @param Timestamp dateTimeStamp - the dateTimeStamp value
	 * @author rbhat
	 * @return String date in the specified format
	 * */
	public static String convertToDate(Timestamp dateTimeStamp)
	{
		if(dateTimeStamp == null)
			return "";
		// pick up the pattern from the system.properties file
		String formatPattern = null ;
		//		ConfigManager cfm = ConfigManager.getInstance();
//		Properties props = cfm.getProperties(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME);
//		formatPattern = props.getProperty(DashboardConstants.DisplayDateFormat);
		formatPattern = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,DashboardConstants.DisplayDateFormat);
		if(formatPattern == null)
		{
			// use the default
			formatPattern = "MM/dd/yy";
		}
		SimpleDateFormat dateFormat = getLocaleDateFormat(formatPattern);	
		String dateString = dateFormat.format(dateTimeStamp);		
		dateString.trim() ;
		return  dateString;
	}
	
	/**
		 * This method accepts a java.sql.TimeStamp value and converts it
		 * into the MM/dd/yy HH:mm:ss format.
		 * <p>
		 * 
		 * @param java.sql.TimeStamp TimeStamp - the TimeStamp value
		 * @author smanicka
		 * @return String dateTime in the specified format
		 * */
		public static String convertToDateTime(Timestamp TimeStamp)
		{
			// pick up the pattern from the system.properties file
			String formatPattern = "MM/dd/yyyy HH:mm:ss";
			ConfigManager cfm = ConfigManager.getInstance();
			formatPattern = cfm.getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.DEFAULT_DATE_TIME_FORMAT );
			SimpleDateFormat dateFormat = getLocaleDateFormat(formatPattern);	
			String dateTimeString = dateFormat.format(TimeStamp);		
			//dateTimeString.trim() ;
			return  dateTimeString;
		}
	
	/**
	 * This method accepts a java.util.Date value and converts it
	 * into the format specified in system.properties.
	 * <p>
	 * 
	 * @param java.util.Date dateTimeStamp - the dateTimeStamp value
	 * @author rbhat
	 * @return String date in the specified format
	 * */
	public static String convertToDate(Date dateTimeStamp)
	{
		// pick up the pattern from the system.properties file
		String formatPattern = null ;
		//		ConfigManager cfm = ConfigManager.getInstance();
//		Properties props = cfm.getProperties(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME);
//		formatPattern = props.getProperty(DashboardConstants.DisplayDateFormat);	
		formatPattern = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,DashboardConstants.DisplayDateFormat);
		if(formatPattern == null)
		{
			// use the default
			formatPattern = "MM/dd/yy";
		}
		SimpleDateFormat dateFormat = getLocaleDateFormat(formatPattern);	
		String dateString = dateFormat.format(dateTimeStamp);		
		dateString.trim() ;
		return  dateString;
	}
	
	/**
		* This method accepts a java.sql.Timestamp value and converts it
		* into the format specified in system.properties.
		* <p>
		* 
		* @param Timestamp dateTimeStamp - the dateTimeStamp value
		* @author nkeshava
		* @return String date in the specified format
		* */
	   public static String convertToDate(Timestamp dateTimeStamp , String dateFormatPattern)
	   {
		   if(dateTimeStamp == null)
			   return "";

		   String formatPattern = dateFormatPattern ;
		  	
		   SimpleDateFormat dateFormat = getLocaleDateFormat(formatPattern);	
		   String dateString = dateFormat.format(dateTimeStamp);		
		   dateString.trim() ;
		   return  dateString;
	   }
	
	/**
		 * This method accepts a String Milliseconds  and DateFormat and converts Milliseconds
		 * into the format specified in DateFormat.
		 * <p>
		 * 
		 * @param String dateTimeStampStr - the dateTimeStamp value
		 * @param String dateFormatPattern - the dateFormatPattern value
		 * @author nkeshava
		 * @return String date in the specified format
		 * */
		public static String convertToDate(String dateTimeStampStr, String dateFormatPattern)
		{		
			SimpleDateFormat dateFormat = getLocaleDateFormat(dateFormatPattern);	
		
			Date d = new Date();
			if(dateTimeStampStr != null && dateTimeStampStr.length() > 0){
				long dateTimeStamp = Long.parseLong(dateTimeStampStr);	
				
				d = new Date(dateTimeStamp);
			}
			String dateString = dateFormat.format(d);
			dateString.trim() ;

			return  dateString;
		}
	
	
		/**
		 * This methods returns a readable message of time difference between two points of execution
		 * @param ts : Timestamp of initial time
		 * @param tsF : Timestamp of final time
		 * @return
		 */
	   	public String getTimeTakenMsg(Timestamp ts,Timestamp tsF)
		{
			long tsInitial=ts.getTime();
			long initialNanos = ts.getNanos() / 1000000;
			tsInitial = tsInitial + initialNanos;
			long tsFinal=tsF.getTime();
			long finalNanos = tsF.getNanos() / 1000000;
			tsFinal=tsFinal + finalNanos;
			long millisecs=tsFinal-tsInitial;
			
			String sTimeTaken="";
			sTimeTaken = String.valueOf(millisecs);
			
			//Change made on request by QA Performance
			/*
			long currtime=0;
			long timeremaining=0;
			sTimeTaken ="Minutes : ";
			currtime=millisecs/(1000L*60);
			sTimeTaken = sTimeTaken + String.valueOf(currtime);
			currtime =millisecs % (1000L*60);
			sTimeTaken = sTimeTaken + ",Seconds : ";
			long sectime=currtime/(1000L);
			sTimeTaken = sTimeTaken + String.valueOf(sectime);
			sTimeTaken = sTimeTaken + ",milliseconds : ";
			currtime=currtime % 1000L;
			sTimeTaken = sTimeTaken + String.valueOf(currtime);*/
			return sTimeTaken;
		}
	   	public static String getCurrentDateString(){
		    Date date = new Date();
			ConfigManager configManager = ConfigManager.getInstance();
			String dateFormat =	configManager.getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.DEFAULT_DATE_TIME_FORMAT);
			String strDate = HelperUtility.getDateAsFormattedString(date, dateFormat);
			return strDate;
	   	
	   	}
	   	public static SimpleDateFormat getLocaleDateFormat(String dateFormatPattern){
	   		String appLang = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.APPLICATION_LANGUAGE);
	   		SimpleDateFormat dateFormat;
			try {
				dateFormat = new SimpleDateFormat(dateFormatPattern,Locale.US);
			} catch (Exception e) {
				logger.error(" Applied date format "+dateFormatPattern+" is wrong so its changed to default US date format : dd/MM/yy");
				dateFormatPattern = "dd/MM/yy";
				dateFormat = new SimpleDateFormat(dateFormatPattern,Locale.US);
			}
	   		if (CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang)){
	   			dateFormat = new SimpleDateFormat(dateFormatPattern,Locale.FRENCH);
	   		}
	   		dateFormat.applyPattern(dateFormatPattern);
	   		return dateFormat;
	   	}
		/**
		 * @Description The hour of the given timeFormat may lowercase or uppercase,but minute and second always lowercase.
		 * EX:-HH:mm:ss or hh:mm:ss,if the hour is lowercase date is in the format of 12 hours
		 * if the hour is uppercase date is in the format of 24 hours
		 * @author prmgr
		 * @param timeFormat
		 * @return String
		 */
		public static String getCurrentTime(String timeFormat){
			String appLang = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.APPLICATION_LANGUAGE);
			Calendar calendar = new GregorianCalendar();
			String currentTime=null;
			SimpleDateFormat simpleDateFormat;
			if (CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang))
				simpleDateFormat = new SimpleDateFormat(timeFormat,Locale.FRENCH);
			else
				simpleDateFormat=new SimpleDateFormat(timeFormat,Locale.US);
			
			if(!Character.isUpperCase(timeFormat.charAt(0))){
				String am_pm=calendar.get(Calendar.AM_PM) == 0?"AM":"PM";
			    currentTime =simpleDateFormat.format(calendar.getTime());
			    currentTime=currentTime+" "+am_pm;
			}else{
				currentTime =simpleDateFormat.format(calendar.getTime());
			}
			return currentTime;
		}

		public static String getGMTTime(String format){
			Calendar gmtCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

			DateFormat gmtFormat = new SimpleDateFormat(format);
			gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			StringBuilder timer = new StringBuilder();
			timer.append(gmtFormat.format(new Date()));
			timer.append(" ");
			timer.append(gmtCalendar.get(Calendar.AM_PM) == 0?"AM":"PM");

			return timer.toString();
		}
		
		/**
		 * This method is added because of published date format going wrong while 
		 * publishing report to Unify and this is happening in French date format. 
		 * the publish configuration page required the generic format to publish report. 
		 * @return
		 */
		public static String getLocaleDateAndTimeInGenericFormat() {
			Date date = new Date();
			String dateFormatStr =	"MM/dd/yy HH:mm:ss"; //us default format
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr,Locale.US);
			String strDate = dateFormat.format(date);
			return strDate;
		}
	}	