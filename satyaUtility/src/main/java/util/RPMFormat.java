/*
 * Created on Sep 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.iri.localization.common.LocaleContextFactory;
import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.constants.FormatMaskConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/**
 * @author kshekhra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RPMFormat implements Serializable {

private Format format ;
private String defaultPattern;
private String pattern ;
private String appLang = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.APPLICATION_LANGUAGE);
private String customLanguages = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.CUSTOM_FORMAT_LANGUAGE);
private Locale locale;
private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,RPMFormat.class.getName());

	public RPMFormat()
	{

	}

	public RPMFormat(Locale locale)
	{
		this.locale = locale;
	}
	
	public RPMFormat(String pattern)
	{
		this.pattern = pattern ;
	}

	public void applyPattern(String pattern)
	{
		this.pattern = pattern ;
	}
	

	public String format(Object object)
	{
		try {
			String localeName = LocaleContextFactory.getInstance().getLocaleContextInfo().getCurrentLocale();
			if(localeName != null) {
				this.locale = Locale.forLanguageTag(localeName);
			}
			//logger.info("Locale Name = " + localeName + " and CURRENT thread ID = " + Thread.currentThread().getId()) ;
		} catch(Exception e) {
			logger.error(e);
		}
		//special cases handled
		if(pattern != null) {
			pattern = pattern.toUpperCase();
			//For Rank members
			if(pattern.equalsIgnoreCase(FormatMaskConstants.RANK_FORMAT_MASK)) {
				pattern = FormatMaskConstants.RANK_FORMAT_MASK_VALUE;
			}
			
			//For negative red and negative red parenthesis
			if(pattern.indexOf("[RED]") != -1) {
				pattern = pattern.replaceAll("\\[RED\\]", "");
			}
		}		
		
		String formatedString = "";
		if(object !=null && object instanceof Number)
		{
			if(format == null || !(format instanceof DecimalFormat)) {
				if(locale != null)
					format = getLocaleDecimalFormat(locale);
				else
					format = getLocaleDecimalFormat();
			}
			else {
				this.applyPatternInternal((DecimalFormat)this.format, this.pattern);
			}

			Number valueObj = (Number)object ;
			double value = valueObj.doubleValue();
			long diviser = 1;

			DecimalFormat decimalFormat = (DecimalFormat)format;

			/*if(locale != null)
				decimalFormat = getLocaleDecimalFormat(locale);
			else
				decimalFormat = getLocaleDecimalFormat();
			 */

			//no format mask applied if pattern is null or blank string
			if(pattern == null || pattern.trim().equals("")) {
				String numStr = "";
				try{
					numStr = decimalFormat.format(valueObj);
				}catch(Exception e){
					return String.valueOf(valueObj);
				}
				return numStr;
			}
			
			if(pattern!= null && (pattern.endsWith("K")||pattern.endsWith("k")))
			{
				diviser = (long)Math.pow(10, 3);
			}
			else if(pattern!= null && (pattern.endsWith("M")||pattern.endsWith("m")))
			{
				diviser = (long)Math.pow(10, 6);
			}
			else if(pattern!= null && (pattern.endsWith("B")||pattern.endsWith("b")))
			{
				diviser = (long)Math.pow(10, 9);
			}
			//formatedString = value==0?"0":decimalFormat.format(value/diviser);
			//format should be applied for 0 value also, PSRCQ00005226
			formatedString = decimalFormat.format(value/diviser);
		}
		else if(object !=null && object instanceof Date)
		{
			if(format == null || !(format instanceof SimpleDateFormat))
			{
				format = new SimpleDateFormat();
			}
			SimpleDateFormat dateFormat = (SimpleDateFormat)format ;
			if(pattern!=null && pattern.trim().length()>0)
			{
				dateFormat.applyPattern(pattern);
			}

			Date dateObj = (Date)object ;
			formatedString = dateFormat.format(dateObj);
		}
		else if(object !=null && object instanceof Object[])
		{
			Object[] arguments = (Object[])object ;
			if(pattern!=null && pattern.trim().length()>0)
			{
				formatedString = MessageFormat.format(pattern, arguments);
			}
		}
		return formatedString ;
	}
	
	
	public String toString()
	{
		return this.pattern;
	}

	public DecimalFormat getLocaleDecimalFormat(){
		
   		Locale loc = Locale.US;
   		if (CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang)){
   			return applyCustomPattern();
   		}
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat)nf;
		defaultPattern = df.toPattern();
		if(pattern != null && ! pattern.trim().equals("")){
			df.applyPattern(pattern);
		}
		return df;
	}
	
	private DecimalFormat getLocaleDecimalFormat(Locale locale){
   		Locale loc = Locale.US;
   		
   		if(locale != null) {
   			loc = locale;
   			String langTag = locale.toLanguageTag();
			if (CommonConstants.DR_LOCALE_FR.equals(locale.toString()) || CommonConstants.LOCALE_FR.equals(locale.toString())
					|| (customLanguages != null && langTag != null && customLanguages.toLowerCase().contains(langTag.toLowerCase()))) {
				return applyCustomPattern();
			}
   		}
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat)nf;
		defaultPattern = df.toPattern();
		if(pattern != null && ! pattern.trim().equals("")){
			df.applyPattern(pattern);
		}
		return df;
	}

	public DecimalFormat applyCustomPattern(){
		DecimalFormat df;
		try {
				df = new DecimalFormat();
				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setGroupingSeparator(' ');
				dfs.setDecimalSeparator(',');
				df.setDecimalFormatSymbols(dfs);
				this.defaultPattern = df.toPattern();
				
			if(pattern != null && ! pattern.trim().equals("")){
				/*
				 * added this if-else because user have permission to change the
				 * language and due to this pattern having suffix with $ symbol
				 * is not displaying after applying the mask.
				 *  
				 * e.g $#,##0 this format in en-US and application language is en-US and user
				 * chose the language as fr-FR with this check output of the
				 * formatted data is "123 456 790" instead of "$123 456 790".
				 */
				if (CommonConstants.DR_LOCALE_FR.equals(appLang) || CommonConstants.LOCALE_FR.equals(appLang)) {
					/*
					 * adding this try-catch block because user pattern is in
					 * en-US ("$#,##0") format but application language and user
					 * chose language is in fr-FR, at this point
					 * applyLocalizedPattern will fail as the pattern does not
					 * match for fr-FR locale. So if fails then add the
					 * fallback, applyingPattern. At least it keep the pattern
					 * what user requested for.
					 */
					try {
						df.applyLocalizedPattern(pattern);
					} catch (IllegalArgumentException illegalArgumentException) {
						df.applyPattern(pattern);
					}
				} else {
					df.applyPattern(pattern);
				}
			}else{
//				df = applyDefaultFrenchPattern();
			}
		} catch (IllegalArgumentException iae) {
			this.pattern = null;
			return applyCustomPattern();
		}
		return df;
	}
	public DecimalFormat applyDefaultFrenchPattern(){
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRENCH);
		DecimalFormat df = (DecimalFormat)nf;
		this.defaultPattern = df.toPattern();
		return df;
	}
	
	private void applyPatternInternal(DecimalFormat df, String ptrn)
	{
		if (df==null) return;

		if(ptrn != null && ! ptrn.trim().equals("")){
			if (CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang)){
				df.applyLocalizedPattern(pattern);
			}else{
				df.applyPattern(pattern);
			}
		}
		else{
				df.applyPattern(this.defaultPattern);
		}
			
	}



	public static void main(String args[])throws Exception
	{
		String pattern = "\u20AC#,##0.00";

//      pattern and argument for message formating
//		pattern = "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}." ;
//		Object[] arguments = {
//			 new Integer(7),
//			 new Date(System.currentTimeMillis()),
//			 "a disturbance in the Force"
//		 };

		Object argument = new Double(-3243483589.80);

		RPMFormat rpmFormat = new RPMFormat();
		rpmFormat.applyPattern(pattern);
		String formatedString = rpmFormat.format(argument);

		System.out.print("formatedString : "+formatedString);
	}


}
