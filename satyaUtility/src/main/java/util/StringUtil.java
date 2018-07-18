/**
* @(#)StringUtil.java      04/28/2003
*
* Copyright (c) 2004 Symphony Services. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Symphony Services, ("Confidential Information"). You shall not
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
* @version : $Revision: 1.9 $
* @author   : aparmar
*/

package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

public class StringUtil {
	public static final String EMPTY = "";
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, StringUtil.class.getName());

    /**
     * Returns the count of place holders in a prepared statement
     * @param String. Prepared statement string
     * @return int. The count of place holders in a prepared statement
     */
    public static int countPSPlaceHolders(String base) {
        int count = 0;
        if (base != null)
        {
            StringTokenizer stoken = new StringTokenizer(base, "?");
            //count = stoken.countTokens() -1;
            count = stoken.countTokens();
         //   logger.debug("count= " + count);
            stoken = null;
        }
        return count;
    } //end of countPSPlaceHolders method

    /**
     * Returns byte array after compressing the string
     * @param String.
     * @return byte[]
     */
    public static byte[] compressIt(String source)
    {
        try
        {
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            byte[] sourceArr = source.getBytes();
            GZIPOutputStream gOut = new GZIPOutputStream(bis);
            gOut.write(sourceArr, 0, sourceArr.length);
            gOut.finish();
            byte[] outputArr = bis.toByteArray();
            gOut.close();
            bis.close();
            return outputArr;
        }
        catch (IOException ioe)
        {
            return null;
        }
    } //end of compressIt method

    /**
     * Uncompresses a string array containing compressed
     * data and returns a String
     * @param source:byte[]
     * @return String
     */
    public static String unCompressIt(byte[] source)
    {
        try
        {
            String returnString = "";

            if (source == null)
            {
                return returnString;
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(source);
            GZIPInputStream gOut = new GZIPInputStream(bis);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            //int maxLength = 0;

            while (gOut.available() == 1)
            {
                byte[] uncompressedArr = new byte[1024];
                int read = gOut.read(uncompressedArr, 0, uncompressedArr.length);
                if (read != -1)
                {
                    bout.write(uncompressedArr, 0, read);
                }
            }
            returnString = bout.toString();
            bout.close();
            gOut.close();
            bis.close();
            return returnString;
        }
        catch (IOException ioe)
        {
            return null;
        }
    } //end of unCompressIt method

    /**
     * Replaces occurence of a string in the base string.
     * @param String the base string
     * @param String the search string
     * @param String the string to be replaced with
     * @param boolean ignorecase
     * @return String
     */
    public static String findAndReplace(String string, String search, String replace, boolean ignoreCase)
    {
        //Save the search string as a StringBuffer object so
        //we can take advantage of the replace capabilities
        StringBuffer s = new StringBuffer(string);

        //Calculate totals we'll need more than once for speed and clarity
        int stopAt = (s.length() - search.length()) + 1;
        int lengthDifference = replace.length() - search.length();
        int searchLength = search.length();

        //If the search string is bigger than the original string
        //then don't even continue. There would never be any match
        if (stopAt > 0)
        {

            //Loop through looking for the search string
            for (int i = 0; i < stopAt; i++)
            {

                //If we find it, replace it with the new text
                if (s.toString().regionMatches(ignoreCase, i, search, 0, searchLength))
                {
                    //String firstStr = s.toString().substring(i, i + searchLength+1);
                    String firstStr = s.toString().substring(0, i);
                    String secondStr = s.toString().substring(i + searchLength);
                    //s.replace(i, i + searchLength, replace);
                    s = new StringBuffer(firstStr + replace + secondStr);
                    i = i + lengthDifference;
                    stopAt = stopAt + lengthDifference;
                }
            }
        }

        //Convert it back into a string and hand it back
        return s.toString();
    } //end of findAndReplace method
	/**
		 * parse the string based on the given separator.
		 * @param String the base string
		 * @param String the string separator
		 * @return ArrayList of string tokens.
		 */
    public static ArrayList parseString( String givenString ,String separator ) throws Exception{
    	ArrayList listOfString = new ArrayList( );
		StringTokenizer strToken = new StringTokenizer(givenString,separator);
		while( strToken.hasMoreTokens() ){
			listOfString.add( (String)strToken.nextToken( ) ) ;															
		}
		return listOfString ;
	}

	public static boolean isEmpty(String str) {
		if (str != null && str.length() > 0) {
			return false;
		}
		return true;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static boolean toBoolean(String flag) { 
		return "true".equalsIgnoreCase(flag);
	}
	
	public static boolean toBoolean(Object flag) { 
		if(flag instanceof String){
			return "true".equalsIgnoreCase((String) flag);
		}
		return false;
	}
	
	public static ArrayList convertToArrayList(String content, String delim)
	{
		if(content == null || content.length() == 0) return null;
		ArrayList contentList = new ArrayList();
		if(content.indexOf(delim) == -1)
			contentList.add(content);
		else{
			StringTokenizer stok = new StringTokenizer(content, delim);
			while(stok.hasMoreTokens())
			{
				String currToken = stok.nextToken();
				contentList.add(currToken);
			}
		}
		return contentList;
	}
	public static String convertToString(InputStream is)throws IOException
	{
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader in = new BufferedReader(isr);
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}
	public static String getEscapedValue(String str){
		String escStr = "";
		if(str == null || str.length() == 0)
			return escStr;
		escStr = str.replaceAll("\n"," ");
		escStr = escStr.replaceAll("\r","");
		escStr = escStr.replaceAll("'","%27");
		escStr = escStr.replaceAll("\"","%22");
		return escStr;
	}
	public static String getHTMLEscape(String str)
	{
		String temp =str;
		if(str == null || str.length() == 0)
					return "";
		temp=temp.replaceAll("<","&lt;");
		temp=temp.replaceAll(">","&gt;");
		temp=temp.replaceAll("\"","&#34;");
		temp=temp.replaceAll("'","&#39;");
		temp=temp.replaceAll("\\\\","&#92;");
		return temp;
		
	}
	
	public static String convertToString(List list, String delimiter)
	{
		StringBuffer strBuf = new StringBuffer();
		if ( list != null && list.size() > 0 && delimiter != null )
		{
			Iterator iter = list.iterator();
			while ( iter.hasNext() )
			{
				String str = (String)iter.next();
				strBuf.append(str).append(delimiter);
			}
			int length = strBuf.length();
			if ( length > 0 )
			{
				strBuf.deleteCharAt(length-1);
			}
		}
		return strBuf.toString();
	}
	
	public static String convertToString (Set objects,String delimeter){
		StringBuffer buffer = new StringBuffer();
		
		for(Object obj : objects){
			buffer.append(obj).append(delimeter);
		}
		int length = buffer.length();
		
		if ( length > 0 ){
			buffer.deleteCharAt(length-1);
		}
		return buffer.toString();
	}
	
	public static int countChar(String str, String charToCount)
	{
		int noOfOccurences = 0;
		if(str != null && str.length() > 0 && str.indexOf(charToCount) != -1)
		{
			String tempStr = str;
			int charIndex = 0;
			while(charIndex != -1)
			{
				charIndex = tempStr.indexOf(charToCount);
				if(charIndex != -1){
					noOfOccurences ++;
					if(tempStr.length() - 1 >= charIndex + 1)
					{
						tempStr = tempStr.substring(charIndex + 1);
					}else{
						break;
					}
				}else{
					break;
				}
			}
		}
		return noOfOccurences;
	}
	
	/**
	 * Method converts an XML String into a HashMap
	 * Precondition: XML String should be in the format of <property name="NAME" value="VALUE" /> 
	 * @param xmlString that needs to be converted into HashMap.
	 * @return HashMap with property name as the key.
	 */
	public static HashMap convertXMLToHashMap(String xmlString){
		HashMap propertyMap = null;
		try {
			Document propertyDoc = XmlUtility.parseFromString(xmlString);
			Element rootElement = propertyDoc.getDocumentElement();
			
			NodeList properties = rootElement.getElementsByTagName("property");
			int size = properties !=null?properties.getLength():0 ;
			propertyMap = new HashMap();
			for(int i=0; i<size; i++)
			{
				Element property = (Element)properties.item(i);
				String propName = property.getAttribute("name");
				String propValue = property.getAttribute("value");
				
				if(propName != null || propName.trim().length()>0)
				{
					propertyMap.put(propName, propValue);
				}
			}
		} catch (Exception e) {
			logger.error("Error in convertXMLToHashMap method");
		}
		return propertyMap;
	}
	public static String getCharacterEncodeString(String value){
		return getCharacterEncodeString(value,CommonConstants.ENCODING);
	}
	public static String getCharacterEncodeString(String value,String encode){
		if(value != null && encode != null){
			try {
				//#15364-User unable to create filter for member having attribute value containing % symbol.
				value = value.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
				value = value.replace("+", "%2B");
				value = value.replace("%C2%A0", "%20"); // prcpr to support nonbreaking whitespace qc  19719
				String encodedValue = new String(value.getBytes(encode),encode);
				value = URLDecoder.decode(encodedValue, encode);//prabm
				value = value.replace("%2B", "+");
				value = value.replace("&#xa0;", " ");
				return value;
			} catch (UnsupportedEncodingException e) {
				logger.error("Error in getCharacterEncodeString method");
			}catch (IllegalArgumentException e) {
				logger.error("Error in getCharacterEncodeString method");
			}
		}
		return value;
	}
	/**
	 * Expands an array object to the given sizze
	 * @param a
	 * @param bysize
	 * @return
	 */
	public static Object expand(Object a, int bysize)
	{
		Class cl = a.getClass();
	    if (!cl.isArray()) return null;
	    int length = Array.getLength(a);
	    int newLength = length + bysize; // 50% more
	    Class componentType = a.getClass().getComponentType();
	    Object newArray = Array.newInstance(componentType, newLength);
	    System.arraycopy(a, 0, newArray, 0, length);
	    return newArray;
	}
	public static String getEscapedString(String value){
		String escapedStr="";
		if(value != null){
			for(int k=0;k<value.length();k++)
			{
				char ch=value.charAt(k);
				int ival=ch;
				if(ival==46 || ival==32 || (ival>=48 && ival<=57) || (ival>=65 && ival<=90) || (ival>=97 && ival<=122))
				{
					escapedStr = escapedStr+ch;
				}else
				{
					escapedStr = escapedStr+"&#"+ival+";";
				}
			}
		}		
		return escapedStr;
	}

	public static String checkLocaleNumberFormat(String value){
		String appLang = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.APPLICATION_LANGUAGE);
		String tempValue = value;
		if(CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang)){
			tempValue = tempValue.replaceFirst(",", ".");
			try{
				float f = Float.parseFloat(tempValue.replace(" ", ""));
			}catch(Exception e){
				return value;
			}
		}
		return tempValue;
	}
	
	public static String changeNumberToLocaleFormat(String value,boolean needSeperator){
		String appLang = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,ConfigurationConstants.APPLICATION_LANGUAGE);
		String tempValue = value;
		if(CommonConstants.LOCALE_FR.equalsIgnoreCase(appLang)){
 			try{
  			    tempValue =  NumberFormat.getInstance(Locale.FRENCH).format(Double.parseDouble(value));
  			    if(!needSeperator){
  			    	tempValue = value.replaceAll("\\.", ",");
  			    }
  			 }catch(Exception e){
  				value = value.replaceAll("\\.", ",");
				return value;
			}
		}
		return tempValue;
	}
	public static String unescapeString(String strValue)
	{
		if(strValue != null){
			if(strValue.indexOf("&#38;") > -1){
				strValue = strValue.replaceAll("&#38;","&");
			}
			if(strValue.indexOf("&#60;") > -1){
				strValue = strValue.replaceAll("&#60;","<");
			}
			if(strValue.indexOf("&#62;") > -1){
				strValue = strValue.replaceAll("&#62;",">");
			}
			if(strValue.indexOf("&#34;") > -1){
				strValue = strValue.replaceAll("&#34;","\"");
			}
			if(strValue.indexOf("&#39;") > -1){
				strValue = strValue.replaceAll("&#39;","\'");
			}
			if(strValue.indexOf("&#123;") > -1){
				strValue = strValue.replaceAll("&#123;","{");
			}
			if(strValue.indexOf("&#125;") > -1){
				strValue = strValue.replaceAll("&#125;","}");
			}
			if(strValue.indexOf("&#44;") > -1){
				strValue = strValue.replaceAll("&#44;",",");
			}
			if(strValue.indexOf("&#92;") > -1){
				strValue = strValue.replace("&#92;","\\"); //Fix .ReplaceAll won't work with forwardslash.
			}
			if(strValue.indexOf("&#124;") > -1){
				strValue = strValue.replaceAll("&#124;","|");
			}
			if(strValue.indexOf("&amp;") > -1){
				strValue = strValue.replaceAll("&amp;","&");
			}
			if(strValue.indexOf("&apos;") > -1){
				strValue = strValue.replaceAll("&apos;","\'");
			}
			if(strValue.indexOf("&quot;") > -1){
				strValue = strValue.replaceAll("&quot;","\"");
			}
			if(strValue.indexOf("&lt;") > -1){
				strValue = strValue.replaceAll("&lt;","<");
			}
			if(strValue.indexOf("&gt;") > -1){
				strValue = strValue.replaceAll("&gt;",">");
			}
			if(strValue.indexOf("&#58;") > -1){
				strValue = strValue.replaceAll("&#58;",":");
			}
			if(strValue.indexOf("&#45;") > -1){
				strValue = strValue.replaceAll("&#45;","-");
			}
			if(strValue.indexOf("&#95;") > -1){
				strValue = strValue.replaceAll("&#95;","_");
			}
			
		}
		return strValue;
	}	
  public static String unescapeHTML(String s){
	  	if(s==null)return null;
	    StringBuffer result = new StringBuffer(s.length());
	    int ampInd = s.indexOf("&");
	    int lastEnd = 0;
	    while (ampInd >= 0){
	      int nextAmp = s.indexOf("&", ampInd+1);
	      int nextSemi = s.indexOf(";", ampInd+1);
	      if (nextSemi != -1 && (nextAmp == -1 || nextSemi < nextAmp)){
	        int value = -1;
	        String escape = s.substring(ampInd+1,nextSemi);
	        try {
	          if (escape.startsWith("#")){
	            value = Integer.parseInt(escape.substring(1), 10);
	          }
	        } catch (NumberFormatException x){
	        }
	        result.append(s.substring(lastEnd, ampInd));
	        lastEnd = nextSemi + 1;
	        if (value >= 0 && value <= 0xffff){
	          result.append((char)value);
	        } else {
	          result.append("&").append(escape).append(";");
	        }
	      }
	      ampInd = nextAmp;
	    }
	    result.append(s.substring(lastEnd));
	    return result.toString();
	  }
	public static String unEscapeEuro(String str){
		if(str != null){
			String escapedStr = XmlUtility.escape(str);
			if(escapedStr.contains("&#128;")){
				return unescapeHTML(escapedStr.replace("&#128;", "�"));
			}
		}
		return str;
	}
	public static  List<List<String>> getSublistBySize(List<String> bigList,int n){
		List<List<String>> chunks = new ArrayList<List<String>>();

		for (int i = 0; i < bigList.size(); i += n) {
			List<String>chunk = bigList.subList(i, Math.min(bigList.size(), i + n));         
			chunks.add(chunk);
		}
		
		return chunks;
	}
	
	 /*
     * This method will return true if a pane has default pane Title.
     */
    public static boolean isDefaultPaneTitle(String paneTitle){
    	
    	boolean isDefaultPaneTitle = false;
    	if(paneTitle.endsWith("_Copy"));{
    		String[] tempStr = paneTitle.split("_");
    		paneTitle = tempStr[0];
    	}
    	String regex ="(\\d+)";
		Matcher matcher = Pattern.compile( regex ).matcher(paneTitle);
		while (matcher.find( ))
		{
			String lastDigit = matcher.group();
			if(paneTitle.endsWith(lastDigit)){
				isDefaultPaneTitle = true; 
			}
		}
    	return isDefaultPaneTitle;
    }
    
    public static String getExceptionStackTraceAsString(Exception e) {
    	StringBuffer stackTrace = new StringBuffer();
    	Throwable throwable = (Throwable)e;
    	stackTrace.append(getExceptionStackTraceAsString(throwable));
    	int limit = 5;
    	while(throwable.getCause() != null && limit > 0) {
    		Throwable t = throwable.getCause();
    		stackTrace.append(getExceptionStackTraceAsString(t));
    		throwable = t;
    		limit--;
    	}
    	return stackTrace.toString();
    }
    
    public static String getExceptionStackTraceAsString(Throwable e) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	return sw.toString();
    }
    
    /**
     * This method will replace all the matched expression.
     *@param sb
     *@param pattern
     *@param replacement
     */
    public static void replaceAll(StringBuffer sb, String regEx, String replacement) {
    	Pattern pattern = Pattern.compile(regEx);
	    Matcher m = pattern.matcher(sb);
	    while(m.find()) {
	        sb.replace(m.start(), m.end(), replacement);
	    }
	}
    
    public static String convertToQuotedString(Collection collection, String delimiter)
	{
    	String singleQuote = "'";
		StringBuffer strBuf = new StringBuffer();
		if ( collection != null && collection.size() > 0 && delimiter != null )
		{
			Iterator iter = collection.iterator();
			while ( iter.hasNext() )
			{
				String str = (String)iter.next();
				strBuf.append(singleQuote).append(str).append(singleQuote).append(delimiter);
			}
			int length = strBuf.length();
			if ( length > 0 )
			{
				strBuf.deleteCharAt(length-1);
			}
		}
		return strBuf.toString();
	}
        
    public static boolean isInteger(String s) {
    	boolean isValidInteger = false;
    	if(isEmpty(s)){
    		return isValidInteger;
    	}
    	try {
    		Integer.parseInt(s);
    		isValidInteger = true;
    	}
    	catch (NumberFormatException ex){
    	}
    	return isValidInteger;
    }
    
	public static String[] split(String str, String delim, boolean trim){
	    StringTokenizer stok = new StringTokenizer(str, delim);
	    String tokens[] = new String[stok.countTokens()];
	   
	    for(int i=0; i<tokens.length; i++){
	        tokens[i] = stok.nextToken();
	        if(trim){
	            tokens[i] = tokens[i].trim();
	        }
	    }
	    return tokens;
	}
	
	public static String toTitleCase(String input) {
		if(isEmpty(input)) {
			return input;
		}
	    StringBuilder titleCase = new StringBuilder();
	    boolean nextTitleCase = true;

	    for (char c : input.toCharArray()) {
	        if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	        } else if (nextTitleCase) {
	            c = Character.toTitleCase(c);
	            nextTitleCase = false;
	        }

	        titleCase.append(c);
	    }

	    return titleCase.toString();
	}
} //end of StringUtil class