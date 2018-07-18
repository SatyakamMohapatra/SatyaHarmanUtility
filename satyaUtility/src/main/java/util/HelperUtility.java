/*
 * @(#)HelperUtility      01/04/2003
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
 * @version    3.2
 * @author     Kiran Kumar.T
 */

package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.constants.MemberTemplateConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/**
 * Utility class which has some common methods
 */
public class HelperUtility {

	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,HelperUtility.class.getName());	
	
	private static int defaultPrec;
	private static String defaultDateFormat= "MM/dd/yyyy";
	private static String defaultDateTimeFormat = "yy/dd/MM HH:mm:ss";
	private static String defaultNumberFormat = "###,###";
	
	static
	{
		ConfigManager cf = ConfigManager.getInstance();
		defaultDateFormat = cf.getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.DEFAULT_DATE_FORMAT,defaultDateFormat);
		defaultNumberFormat  = cf.getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.DEFAULT_NUMBER_FORMAT,defaultNumberFormat);
		defaultDateTimeFormat = cf.getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.DEFAULT_DATE_TIME_FORMAT,defaultDateTimeFormat);
		defaultPrec = cf.getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.DEFAULT_PRECISION,3);
	}
	
	public static Object executeMethod(
		String className,
		String methodName,
		Object[] constrArgs,
		Object[] methodArgs,
		boolean isStatic)
		throws Exception {
		
		return executeMethod(className,methodName,constrArgs,methodArgs,getObjectTypes(methodArgs),isStatic);
	}
	
	/**
	 * Executes the specified method on the specfied class
	 */
	public static Object executeMethod(
		String className,
		String methodName,
		Object[] constrArgs,
		Object[] methodArgs,Class[]methodArgsTypes,
		boolean isStatic)
		throws Exception {
		Object result = null;
		Class classVar = Class.forName(className);

		//Class[] methodArgsTypes = getObjectTypes(methodArgs);
		Method method = classVar.getMethod(methodName, methodArgsTypes);

		Object classInstance = null;
		if (!isStatic) {
			Class[] constructorArgTypes = getObjectTypes(constrArgs);
			Constructor constructor =
				classVar.getDeclaredConstructor(constructorArgTypes);
			classInstance = constructor.newInstance(constrArgs);
		}

		result = method.invoke(classInstance, methodArgs);

		return result;
	}

	/**
	 * This method returns the public methods of the class given.
	 * @param String - className whose methods are required.
	 * @return Map - the key in the map is the methodName in the class and the value is a string array which holds the type of arugments the method takes
	 * @throws Exception if the class is not found or if there is no access
	 */
	public static Map getAllMethodsWithSignature(String className)throws Exception
	{
		String prop = ConfigManager.getInstance().getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,className);
		if(prop!=null && (prop=prop.trim()).length()> 0)
		{
			className = prop;
		}
		Class classVar = Class.forName(className);				
		Method [] methods = classVar.getMethods();

		Map methodMap = new HashMap(methods.length);
		
		for(int i=0;i<methods.length;i++)
		{
			String methodName = methods[i].getName();
			Class argTypes []= methods[i].getParameterTypes();
			
			String [] argTypeStrings = new String[argTypes.length];
			for(int j=0;j<argTypes.length;j++)
			{
				argTypeStrings[j] = argTypes[j].getName();	
			}
			
			methodMap.put(methodName,argTypeStrings);	
		}
		return methodMap;
	}
	

	public static Class[] getObjectTypes(Object args[]) {
		Class[] types = null;
		if (args != null) {
			int len = args.length;
			types = new Class[len];
			for (int i = 0; i < len; i++) {
				types[i] = args[i].getClass();
			}
		}
		return types;

	}

	/**
	 * prints the contents of the object specfied.If the object is a map or collection it prints all 
	 * the values in the map or collection
	 */
	public static void checkAndPrintObject(String objName, Object value) {
		if (objName != null) {
			if (logger.isDebugEnabled())logger.debug("Printing contents of <name = '" + objName+"'>");
		}
		if (value != null) {
			if (value instanceof Object[]) {
				logger.debug("<type ='Array'/>");
				printArray((Object[]) value);
			} else if (value instanceof Map) {
				logger.debug("<type ='Map'/>");
				printMap((Map) value);
			} else if (value instanceof Collection) {
				logger.debug("<type ='Collection'/>");
				printCollection((Collection) value);
			} else {
				logger.debug("<type ='Object'/>");
				logger.debug(value.toString());
			}
		} else {
			logger.debug("	<value =null/>");
		}
		if (objName != null) {
			if (logger.isDebugEnabled())logger.debug("End printing contents of <name = '" + objName+"'>");
		}
	}

	private static void printArray(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			checkAndPrintObject(null, array[i]);
		}
	}
	private static void printMap(Map map) {
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object name = iterator.next();
			checkAndPrintObject(name.toString(), map.get(name));
		}
	}
	private static void printCollection(Collection collection) {
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
			checkAndPrintObject(null, iterator.next());
		}
	}

	/**
	 * this functions trims the values in the map
	 * 	
	 */
	public static Map trimContents(Map map)
	{
		Iterator iterator = map.keySet().iterator();
		HashMap trimmedMap = new HashMap();
		while(iterator.hasNext())
		{
			String key =  (String)iterator.next();	
			Object value = map.get(key);
			if(value instanceof String && value!=null)
			{
				trimmedMap.put(key,((String)value).trim());
			}
		}
		return trimmedMap;
	}

	/**
	 * this functions trims the values in the collection
	 * 	
	 */
	
	public static Collection trimContents(Collection collection)
	{
		Iterator iterator = collection.iterator();
		ArrayList trimmedList = new ArrayList();
		while(iterator.hasNext())
		{
			String key =  (String)iterator.next();	
			if(key instanceof String && key!=null)
			{
				trimmedList.add(((String)key).trim());
			}
		}
		return trimmedList;
	}
	
	public static String getDateTimeAsFormattedString(Timestamp inDateTime)
	{
		String formatString = defaultDateTimeFormat; // read preformated date from config file.
		
		String dateOut = DateFormatter.getLocaleDateFormat(formatString).format(inDateTime);		
		
		return dateOut;
	}
	
	
	public static String getDateAsFormattedString(Date inDate, String formatString)
	{
		if(inDate == null)
			inDate = new Date();
		if(formatString == null || formatString.equalsIgnoreCase(""))
			formatString = defaultDateFormat; // read preformated date from config file.
		
		String dateOut = DateFormatter.getLocaleDateFormat(formatString).format(inDate);		
		
		return dateOut;
	}

	public static Date getDateFromString(String dateString, String formatString)throws Exception
	{
		Date inDate = null;
		if(formatString == null || formatString.equalsIgnoreCase(""))
			formatString = defaultDateFormat; // read preformated date from config file.
		
		inDate = DateFormatter.getLocaleDateFormat(formatString).parse(dateString);		
		
		return inDate;
	}
	
	public static String getDoubleAsFormattedString(double doubleVal,int precesion)
	{
		return getDoubleAsFormattedString(null,doubleVal,precesion);
	}

	private static String getDoubleAsFormattedString(String formatString,double doubleVal,int precesion)
	{
		if(precesion < 0)
		{
			precesion = defaultPrec;
		}
		DecimalFormat df1 = null;

		if(precesion>0)
		{  
			if(formatString == null)
				formatString = "##";
			df1 = new DecimalFormat(formatString);			
			df1.setMinimumFractionDigits(precesion); 
			df1.setMaximumFractionDigits(precesion); 			
			df1.setMinimumIntegerDigits(1); 
		}else
		{
			if(formatString == null)
				formatString = "###,###";
			df1 = new DecimalFormat(formatString);			
		}
		DecimalFormatSymbols symb = df1.getDecimalFormatSymbols();
		symb.setInfinity("N/A");
		df1.setDecimalFormatSymbols(symb);
		String formattedString = df1.format(new Double(doubleVal)).toString();
		checkAndPrintObject("Formatted String of "+doubleVal,formattedString);
		if((Locale.FRENCH.toString()).equalsIgnoreCase(checkForAppLang()) && formattedString != null){
			formattedString = formattedString.replace(".", ",");
		}

		return formattedString;
	}
	
	public static String getDoubleAsFormattedString(double doubleVal)
	{
		return getDoubleAsFormattedString(doubleVal,defaultPrec);
	}

	public static String getDoubleAsFormattedString(double doubleVal,String format)
	{
		return getDoubleAsFormattedString(doubleVal,defaultPrec,format);
	}

	public static String getDoubleAsFormattedString(double doubleVal,int precision,String format)
	{
		if(format==null)
		{
			format = defaultNumberFormat;
		}
		return getDoubleAsFormattedString(format,doubleVal,precision);
	}
	
	public static String replaceString(String src,String stringToBeReplaced,String newString)	
	{
		if(src==null || stringToBeReplaced==null)
		{
			return null;
		}
		
		if(newString == null)
		{
			newString="";
		}
		String replacedString = src;
		
		String tokens [] =  tokenize(src,stringToBeReplaced,true);
		if(tokens != null && tokens.length>0)
		{
			StringBuffer resultBuffer = new StringBuffer();
			resultBuffer.append(tokens[0]);
			
			for(int i=1;i<tokens.length;i++)
			{
				resultBuffer.append(newString);
				resultBuffer.append(tokens[i]);
			}
			replacedString = resultBuffer.toString();
		}
		if(src.endsWith(stringToBeReplaced))replacedString = replacedString.concat(newString);
		return replacedString;
	}
	
	
	
	public static String replaceString(String src,String stringToBeReplaced,String newString,int from,int to )	
	{
		String prefix = null;
		String tempString = null;
		String suffix = null;
		if(src==null || stringToBeReplaced==null)
		{
			return null;
		}
		
		if (logger.isDebugEnabled())logger.debug("Source = " + src  + "\t replacement =" + stringToBeReplaced +"\tnewString = " + newString+ "\t from= " + from + "\to=" + to );
		if(from > src.length() || from <=-1 ||  to <=-1 || from >=  to || to >= src.length()){
			if (logger.isDebugEnabled())logger.debug("Conditions satisfied returning with original string");
			return src;
		}
		
		prefix = src.substring(0,from);
		if (logger.isDebugEnabled())logger.debug("Prefix got is" + prefix);
		tempString = src.substring(from,to+1);
		if (logger.isDebugEnabled())logger.debug("removable string  got is" + tempString);
		suffix = src.substring(to+1,src.length());
		if (logger.isDebugEnabled())logger.debug("suffix got is" + suffix);
		tempString = replaceString(tempString,stringToBeReplaced,newString);
		if (logger.isDebugEnabled())logger.debug("String got after removal is" + tempString);
		
		return prefix + tempString + suffix;
	}


	public static String getTruncatedString(String str,int maxDisplayLen) 
	{
      if(str == null)return str;
      String result = str;
      if(str.length()>maxDisplayLen)
      {
         result = str.substring(0,maxDisplayLen)+"..";
      }
	  return result;
	}
	public static String getTruncatedString(String str)
	{
		return getTruncatedString(str,20);
	} 
	public static String replaceQuote(String src,String replaceString)	
	{
		return replaceString(src,"\"",replaceString);
	}
	//	String tokenizer with current behavior

	 public static String [] tokenize(String input, String delimiters,boolean delimiterAsGroup)
	 {
		 Vector v = new Vector();

		 String toks[] = null;

		 if (!delimiterAsGroup)
		 {
			 StringTokenizer t = new StringTokenizer(input, delimiters);
			 while (t.hasMoreTokens())
				 v.addElement(t.nextToken());
		 }
		 else
		 {
			 int start = 0;
			 int end = input.length();

			 while (start < end)
			 {
					 int delimIdx = input.indexOf(delimiters,start);
					 if (delimIdx < 0)
					 {
							 String tok = input.substring(start);
							 v.addElement(tok);
							 start = end;
					 }
					 else
					 {
							 String tok = input.substring(start, delimIdx);
							 v.addElement(tok);
							 start = delimIdx + delimiters.length();
					 }
			 }
		 }

		 int cnt = v.size();
		 if (cnt > 0)
		 {
			 toks = new String[cnt];
			 v.copyInto(toks);
		 }
        
		 return toks;
	 }

	/**
	 * Creates a Vector out of the values passed in as an array
	 * <p>
	 * @param values:String[]
	 * @return Vector
	 */
	public static ArrayList convertToArrayList(String[] values)
	{
		
		ArrayList list = new ArrayList();
		if (values != null)
		{
			for (int i = 0; i < values.length; i++)
			{
				list.add(values[i]);
			}
		}

		return list;
	} //convertToVector ends


	public static String getEscapedStringForDisplay(String toEscape)
	{
		if(toEscape==null)return toEscape;
		char charArray [] = toEscape.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<charArray.length;i++)
		{
			switch(charArray[i])
			{
				case  '<' : buffer.append("&lt;"); 
							 break;
				case  '>' : buffer.append("&gt;"); 
							 break;
				default    : buffer.append(charArray[i]);			 			
			}
		}
		return buffer.toString();
	}
	
	public static void main(String s[])throws Exception
	{
		double x =Double.MIN_VALUE;
//		System.out.println(getDoubleAsFormattedString(1000.0230,null));
//		System.out.println(getDateAsFormattedString(new Date(),null));
//		System.out.println(replaceString(",kiran,kumar,",",","."));		
//		checkAndPrintObject("Method Map ",getAllMethodsWithSignature("com.symphonyrpm.applayer.common.util.HelperUtility"));
//		System.out.println(replaceString("My mail id && is kkii","&","&amp;"));
	}
	public static String readFile(String filePath) throws Exception
	{

		File file = new File(filePath);
		FileReader reader = new FileReader(file);
		BufferedReader buffReader = new BufferedReader(reader);

		StringBuffer buff = new StringBuffer();
		String record = new String();
		while ((record = buffReader.readLine()) != null)
		{
			buff.append(record);
		}
		buffReader.close();
		reader.close();
		
		return buff.toString();

	}
	
	public static String getASDateFormat() {
		ConfigManager manager = ConfigManager.getInstance();
		String dateFormat = manager.getPropertyAsString(
								ConfigurationConstants.MEMBER_TEMPLATE_PROPERTIES_FILE_NAME,
								MemberTemplateConstants.AS_DATE_FORMAT,
								MemberTemplateConstants.DATEFORMAT);
		return dateFormat;
	}
   	public static String checkForAppLang(){
   		String appLang = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.APPLICATION_LANGUAGE);
   		String locale = CommonConstants.LOCALE_US;
   		if (CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang)){
   	   		return Locale.FRENCH.toString();
   		}
		return locale;
   	}
	
}
