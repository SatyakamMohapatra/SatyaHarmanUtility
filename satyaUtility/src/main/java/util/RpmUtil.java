/**
 * @(#)RPMUtil.java      01/13/2003
 * Copyright � 2002-2003 SymphonyRPM, Inc. or its subsidiaries.
 * All Rights Reserved.
 * 
 * This software (the "Software") is supplied under a license agreement 
 * entered into with SymphonyRPM, Inc. The Software may only be used or 
 * disclosed in accordance with the terms of such agreement. The Software 
 * is confidential and proprietary to SymphonyRPM, Inc. and is protected 
 * by the terms of such license agreement, copyright law, patent law and 
 * other intellectual property law. No part of this Software may be reproduced, 
 * transmitted, or translated in any form or by any means, electronic, 
 * mechanical, manual, optical, or otherwise, without the prior written 
 * permission of SymphonyRPM, Inc. SymphonyRPM, Inc. reserves all copyrights, 
 * trademarks, patent rights, trade secrets and all other intellectual 
 * property rights in the Software.
 * 
 * OTHER THAN THE TERMS OF THE LICENSE UNDER WHICH THIS SOFTWARE WAS SUPPLIED, 
 * SYMPHONYRPM, INC. MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE 
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT 
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SYMPHONYRPM, INC. SHALL NOT BE 
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * @version  		:  	$Revision: 1.39 $
 * @author     		:	skorla,rbhat
 * @reviewed by 	:	
 * @date reviewed 	:	
 **/

package util;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.symphonyrpm.applayer.common.constants.AdminConstants;
import com.symphonyrpm.applayer.common.constants.AssetMasterConstants;
import com.symphonyrpm.applayer.common.constants.CacheConstants;
import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.constants.SummaryConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.CryptoManager;
import com.symphonyrpm.applayer.common.coreservices.GenericDAOFactory;
import com.symphonyrpm.applayer.common.coreservices.IGenericDAO;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.i18n.I18NManager;

/**
 * This class contains static utility methods.
 */

public class RpmUtil {
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, "RpmUtil");
	private static int filesModified = 0;
	private static String module = "SERVER";

	/** Value of isLDAPEnabled property. */
	private String strIsLDAPEnabled = ConfigManager.getInstance().getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME, AdminConstants.IS_LDAP_ENABLED);

	// The internationalization manager instance
	private static I18NManager i18nManager = null;

	/** <code>Locale</code> to be used */
	private static Locale locale = null;

	/** Language to be used to create the <code>Locale</code>. */
	private static String strLocaleLanguage = null;

	/** Country to be used to create the <code>Locale</code>. */
	private static String strLocaleCountry = null;

	private static ConfigManager configManager = ConfigManager.getInstance();

	// added for i18n support
	// initialize the resource bundle file
	private static String summaryPropsFile = buildResourceBundleName(SummaryConstants.SUMMARY_BUNDLE_KEY);
	private static String messagesSummaryPropsFile = buildResourceBundleName(SummaryConstants.TAGLIBS_BUNDLE_KEY);
	private static boolean isSQLServer = false;
	private static boolean isBlobNotSupported = false;

	// this static block initializes the locale variable upon class loading
	static {
		i18nManager = I18NManager.getInstance();
		strLocaleLanguage = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, AdminConstants.LOCALE_LANGAUGE);
		strLocaleCountry = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, AdminConstants.LOCALE_COUNTRY);
		if (((strLocaleLanguage != null) && (strLocaleLanguage.length() > 0)) && ((strLocaleCountry != null) && (strLocaleCountry.length() > 0))) {
			locale = new Locale(strLocaleLanguage, strLocaleCountry);
		} else {
			locale = new Locale("en", "US");
		}
	}
	static {
		String database = ConfigManager.getInstance().getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME, ConfigurationConstants.DATABASE_VENDOR_PROP);
		if ("SQLServer".equalsIgnoreCase(database))
			isSQLServer = true;		
		if ((ConfigurationConstants.POST_GRE_SQL).equalsIgnoreCase(database)){
			isBlobNotSupported = true;
		}
	}

	/**
	 * This method takes the file name prefix and returns the complete resource
	 * bundle name depending upon the locale.
	 **/
	public static String getResourceBundleName(String bundleFileNamePrefix) {
		// execute the code if summaryPropsFile is not set else just return the
		// summaryPropsFile name
		// also if bundleFileNamePrefix is taglibs return
		// messagesSummaryPropsFile
		String resourceBundleFileName = null;
		if (bundleFileNamePrefix != null
				&& bundleFileNamePrefix
						.equalsIgnoreCase(SummaryConstants.SUMMARY_BUNDLE_KEY)) {
			resourceBundleFileName = summaryPropsFile;
		} else if (bundleFileNamePrefix != null
				&& bundleFileNamePrefix
						.equalsIgnoreCase(SummaryConstants.TAGLIBS_BUNDLE_KEY)) {
			resourceBundleFileName = messagesSummaryPropsFile;
		}
		return resourceBundleFileName;
	}

	/**
	 * This method constructs the resource bundle name and returns it.
	 * 
	 * @param bundleFileNamePrefix
	 * @return
	 */
	private static String buildResourceBundleName(String bundleFileNamePrefix) {
		String bundleFileName = SummaryConstants.SUMMARY_PROPS_FILE_EN;
		String countryLang = configManager.getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, CommonConstants.LOCALE_LANGUAGE);
		String countryName = ConfigManager.getInstance().getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, CommonConstants.LOCALE_COUNTRY);
		if ((countryLang != null && countryLang.trim().equalsIgnoreCase(CommonConstants.frenchLocaleLanguageID))
						&& (countryName != null && countryName.trim().equalsIgnoreCase(CommonConstants.frenchLocaleCountryID))) {
			// means the locale is set to french
			bundleFileName = bundleFileNamePrefix + "Bundle_" + countryLang + "_" + countryName + ".properties";
		} else if ((countryLang != null && countryLang.trim().equalsIgnoreCase(CommonConstants.englishLocaleLanguageID))
				&& (countryName != null && countryName.trim().equalsIgnoreCase(CommonConstants.englishLocaleCountryID))) {
			// means the bundle file is english
			bundleFileName = bundleFileNamePrefix + "Bundle_" + countryLang + "_" + countryName + ".properties";
		}
		return bundleFileName;
	}

	/**
	 * This method returns the localized string based upon the locale set in
	 * system.properties.
	 * 
	 * @param bundleName
	 * @param key
	 * @return
	 */
	public static String getLocalizedString(String bundleName, String key) {
		if (bundleName != null
				&& bundleName.trim().equalsIgnoreCase(
						SummaryConstants.SUMMARY_BUNDLE_KEY)) {
			String summaryPropsFile = getResourceBundleName(SummaryConstants.SUMMARY_BUNDLE_KEY);
			return ConfigManager.getInstance().getProperty(summaryPropsFile,
					key);
		} else {
			return i18nManager.getValue(bundleName, locale, key);
		}
	}

	/**
	 * returns true if 'isLDServicesJUnit' is set to true in system.properties file
	 */
	public static boolean isJUnitModule() {
		//configManager variable is changed to ConfigManager.getInstance() as the variable will not be instantiated when this method gets called and it will hold null value which causes NullPointerException  
		return ConfigManager.getInstance().getPropertyAsBoolean(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME, "isLDServicesJUnit", false);
	}
	
	/**
	 * returns true if this method is called on the Appserver or false
	 */
	public static boolean isServerModule() {
		return true;
	}

	public static String[] convertListToStringArray(List list) {
		String[] strArray = null;
		if (list != null) {
			strArray = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				strArray[i] = new String((String) list.get(i));
			}
		}
		return strArray;
	}

	/**
	 * Converts List elements as comma separated string by invoking list
	 * elements toString method.
	 * 
	 * @param list
	 *            List of elements.
	 * @return List elements as comma separated string by invoking list elements
	 *         toString method.
	 */
	public static String convertListToString(List list) {
		return convertListToString(list, CommonConstants.COMMA_DELIMITOR);
	}

	/**
	 * Converts List elements as delimiter separated string by invoking list
	 * elements toString method.
	 * 
	 * @param list
	 *            List of elements.
	 * @return List elements as delimiter separated string by invoking list
	 *         elements toString method.
	 */
	public static String convertListToString(List list, String delimiter) {
		String retStr = "";
		if (list == null || list.isEmpty()) {
			return retStr;
		}
		
		
		for (int i = 0; i < list.size(); i++) {
			Object elemObj = list.get(i);
			if(elemObj!=null){
				String elemStr = elemObj.toString();
				if (i == (list.size() - 1)) {
					retStr += elemStr;
				} else {
					retStr += elemStr + delimiter;
				}
			}
		}
		return retStr;
	}

	/**
	 * Converts a String to a MDX HashMap.
	 * <p>
	 * 
	 * @param map1Str
	 *            A String containing the MDX query.
	 * @return returns a HashMap.
	 */
	public static HashMap convertMDXStringToHashMap(String map1Str) {
		HashMap returnMap = new HashMap();
		if (map1Str == null) {
			return returnMap;
		}

		int length = map1Str.length();
		if (length > 0) {
			if (map1Str.equalsIgnoreCase("{}")) {
				return returnMap;
			}

			int index1 = map1Str.indexOf("{");
			int lastIndex1 = map1Str.lastIndexOf("}");
			if (index1 != -1 && lastIndex1 != -1) {
				String remainingStr = map1Str.substring(index1 + 1, length - 1);
				if (remainingStr != null && remainingStr.trim().length() == 0) {
					return returnMap;
				}

				ConfigManager configManager = ConfigManager.getInstance();
				String spdelimiterforChildMap = configManager.getProperty(
						ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,
						"OptimizedQueryDelimiter");
				String delim = spdelimiterforChildMap;
				StringTokenizer strToken = new StringTokenizer(remainingStr,
						delim);

				while (strToken.hasMoreTokens()) {
					String str = (String) strToken.nextToken();
					if (str != null) {
						str = str.trim();
					}
					if (str.length() > 0) {
						int index2 = str.indexOf("=");
						if (index2 != -1) {
							String key = str.substring(0, index2);
							String val = str.substring(index2 + 1);
							returnMap.put(key, val);
						}
					}
				}
			}
		}
		return returnMap;
	}

	/**
	 * Converts a MDX HashMap to a String.
	 * <p>
	 * 
	 * @param map
	 *            A HashMap containing the MDX query.
	 * @return returns the converted String.
	 */
	public static String convertMDXHashmapToString(HashMap map) {
		ConfigManager configManager = ConfigManager.getInstance();
		/* get the delimiter from the system properties file */
		String delim = configManager.getProperty(
				ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,
				"OptimizedQueryDelimiter");
		StringBuffer returnStr = new StringBuffer("");
		returnStr.append("{");
		returnStr.append(convertHashMapToString(map, delim));
		returnStr.append("}");
		return returnStr.toString();
	}

	/**
	 * Converts the HashMap to a String. This uses the default delimiter as ','.
	 * <p>
	 * 
	 * @param map
	 *            HashMap to be converted.
	 * @return The converted String.
	 */
	public static String convertHashMapToString(Map map) {
		/* the default delimiter is ','(comma) */
		String defaultDelimiter = ",";
		return convertHashMapToString(map, defaultDelimiter);
	}

	/**
	 * Converts the HashMap to a string using the specified delimiter.
	 * <p>
	 * 
	 * @param map
	 *            HashMap to be converted.
	 * @param delim
	 *            delimiter to be used.
	 * @return returns the converted string.
	 */
	public static String convertHashMapToString(Map map, String delim) {
		StringBuffer returnStr = new StringBuffer("");

		int size = 0;
		if (map != null){
			size = map.size();
		}
		int i = 0;
		if (map != null && size > 0) {
			String finalStr = "";
			Set set1 = map.keySet();
			Iterator it = set1.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object val = map.get(key);

				/* if the val is not instance of String, continue */
				if (!(val instanceof String)) {
					continue;
				}

				if (i != size - 1) {
					finalStr = key + "=" + val + delim;
				} else {
					finalStr = key + "=" + val;
				}
				returnStr.append(finalStr);
				i++;
			}
		}
		return returnStr.toString();
	}

	/**
	 * Converts the HashMap to a string using the specified delimiter.
	 * <p>
	 * 
	 * @param map
	 *            HashMap to be converted.
	 * @param delim1
	 *            delimiter to be used for eack key-value pair.
	 * @param delim2
	 *            delimiter to be used between key and value.
	 * @return returns the converted string.
	 */
	public static String convertHashMapToString(Map map, String delim1,
			String delim2) {
		StringBuffer returnStr = new StringBuffer("");

		int size = map.size();

		int i = 0;
		if (map != null && size > 0) {
			String finalStr = "";
			Set set1 = map.keySet();
			Iterator it = set1.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object val = map.get(key);

				/* if the val is not instance of String, continue */
				if (!(val instanceof String)) {
					continue;
				}

				if (i != size - 1) {
					finalStr = key + delim2 + val + delim1;
				} else {
					finalStr = key + delim2 + val;
				}
				returnStr.append(finalStr);
				i++;
			}
		}
		return returnStr.toString();
	}
	
	/**
	 * @param propertiesMap
	 * @return
	 */
	public static String convertMapToXML(Map propertiesMap) {
		StringBuffer sBuf = new StringBuffer();
		sBuf.append("<PropertiesXml><Properties>");
		for (Object propNameObj : propertiesMap.keySet()) {
			Object propValueObj = propertiesMap.get(propNameObj);
			if(propValueObj == null) continue;
			
			String objectType = "String";
			String propVal = null;
			if (propValueObj instanceof List) {
				objectType = "List";
				propVal = convertListToString((List)propValueObj);
			}	
			else if (propValueObj instanceof Boolean) {
				objectType = "Boolean";
				propVal = propValueObj.toString();
			}
			else if (propValueObj instanceof Integer) {
				objectType = "Integer";
				propVal = propValueObj.toString();
			}
			else if (propValueObj instanceof Double) {
				objectType = "Double";
				propVal = propValueObj.toString();
			}
			else if (propValueObj instanceof Float) {
				objectType = "Float";
				propVal = propValueObj.toString();
			}
			else if (propValueObj instanceof Long) {
				objectType = "Long";
				propVal = propValueObj.toString();
			}
			else {
				propVal = propValueObj.toString();
			}	
			sBuf.append("<Property name=\"" + propNameObj +  "\" objectType=\"" + objectType + "\">");
			sBuf.append(propVal);
			sBuf.append("</Property>");
		}
		sBuf.append("</Properties></PropertiesXml>");
		return sBuf.toString();
	}
	
	/**
	 * @param propertiesXml
	 * @return
	 */
	public static Map convertPropertiesXMLToMap(String propertiesXml) {
		Map propertiesMap = new HashMap();
		try {
			Document propsDoc = XmlUtility.parseFromString(propertiesXml);
			NodeList propertyNodes = propsDoc.getElementsByTagName("Property");
			for (int i = 0; i < propertyNodes.getLength(); i++) {
				Element propElement = (Element)propertyNodes.item(i);
				String propName = propElement.getAttribute("name");
				String objType = propElement.getAttribute("objectType");
				String propValue = propElement.getFirstChild().getNodeValue();
				if ("String".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, propValue);
				}
				else if ("Boolean".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, Boolean.valueOf(propValue));
				}
				else if ("Double".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, Double.parseDouble(propValue));
				}
				else if ("Integer".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, Integer.parseInt(propValue));
				}
				else if ("Float".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, Float.parseFloat(propValue));
				}
				else if ("Long".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, Long.parseLong(propValue));
				}
				else if ("List".equalsIgnoreCase(objType)) {
					propertiesMap.put(propName, convertToArrayList(propValue));
				}
			}
		} 
		catch (Exception e) {
			logger.error("Error in convertMapToXML " + propertiesXml, e);
		}
		
		return propertiesMap;
	}
	

	/**
	 * Method that browses the folder path and invokes modifyFile() on every
	 * file it finds in the path.
	 * <p>
	 * 
	 * @param f
	 *            - the path to the parent folder
	 * @param origString
	 *            - the text to be replaced
	 * @param repString
	 *            - the replacing text
	 */
	public static void browseFolder(File f, String origString, String repString) {
		String path = null;
		if (f.isDirectory()) {
			// get the list of files in this folder
			String list[] = f.list();
			int listSize = list.length - 1;
			for (int i = 0; i < list.length; i++) {
				path = f.getAbsolutePath() + File.separator + list[i];
				File temp = new File(path);
				if (temp.isFile()) {
					// modify the file contents
					if (origString.equalsIgnoreCase("debug")) {
						// prefix logger statements
						prefixString(path);
					} else {
						// replace text
						modifyFile(path, origString, repString);
					}
				}
				// if all files have been processed then move on to the sub
				// folders
				if (i == listSize) {
					for (int k = 0; k < list.length; k++) {
						temp = new File(f, list[k]);
						if (temp.isDirectory()) {
							browseFolder(temp, origString, repString);
						}
					}
				}
			}
		}
	}

	/**
	 * Helper method that prefixes a string before another in a file
	 * <p>
	 * 
	 * @param filePath
	 *            - the path to the file that needs to be modified
	 */
	static public void prefixString(String filePath) {
		// variable to check if the counter has been already incremented
		boolean ctrModified = false;

		// modify all the logger.debug statements
		String debugLoggerString = "logger.debug";
		String debugLoggerPrefix = "if(logger.isDebugEnabled()) ";
		boolean mod = prefixStr(filePath, debugLoggerString, debugLoggerPrefix);
		if (mod) {
			filesModified++;
			ctrModified = true;
		}

		String infoLoggerString = "logger.info";
		String infoLoggerPrefix = "if(logger.isInfoEnabled()) ";
		mod = prefixStr(filePath, infoLoggerString, infoLoggerPrefix);
		if ((mod == true) && (ctrModified == false)) {
			// means only logger.info statements were modified
			filesModified++;
		}

	}

	/**
	 * Helper function that prefixes a string with another
	 * 
	 * @param filePath
	 *            - the path to the file that needs to be modified
	 * @param origString
	 *            - the string that needs to be prefixed
	 * @param prefixString
	 *            - the prefix
	 */
	private static boolean prefixStr(String filePath, String origString,
			String prefixString) {
		boolean modified = false;
		try {
			// the 32kb buffer
			StringBuffer buffer = new StringBuffer(32768);
			// read the file into a byte array
			byte[] fileData = readFile(filePath);
			// String that represents the original file
			String fileText = new String(fileData);

			// find all instances of origString and prefix it with prefixString
			int pos = 0;
			while (true) {
				int index = fileText.indexOf(origString, pos);
				int nIndex = 0;
				if (index < 0) {
					buffer.append(fileText.substring(pos));
					break;
				} else {
					// check if the prefix string is already present
					nIndex = fileText.indexOf(prefixString, pos);
					if (nIndex < 0) {
						// prefix it with prefixString and continue
						buffer.append(fileText.substring(pos, index));
						buffer.append(prefixString);
						modified = true;
						// append the content till the next logger statement
						pos = index;
						index = index + origString.length() + 1;
						buffer.append(fileText.substring(pos, index));
						pos = index;
					} else {
						// just append the text without prefixing
						buffer.append(fileText.substring(pos, index));
						// append the content till the next logger statement
						pos = index;
						index = index + origString.length() + 1;
						buffer.append(fileText.substring(pos, index));
						pos = index;
					}
				}
			}
			fileText = buffer.toString();
			// write the output string to file
			boolean temp = writeFile(filePath, fileText.getBytes());
			if (temp) {
				return modified;
			} else {
				return false;
			}
		} catch (IOException ioe) {
			logger.error(ioe);
			return false;
		} catch (Exception ex) {
			logger.error(ex);
			return false;
		}
	}

	/**
	 * This method replaces all occurences of the text in the file
	 * <p>
	 * 
	 * @param filePath
	 *            - the path to the file
	 * @param origString
	 *            - the text to be replaced
	 * @param repString
	 *            - the replacing text
	 */
	public static void modifyFile(String filePath, String origString,
			String repString) {
		try {
			// the 32kb buffer
			StringBuffer buffer = new StringBuffer(32768);
			// read the file into a bute array
			byte[] fileData = readFile(filePath);
			// String that represents the original file
			String fileText = new String(fileData);
			// find all instances of origString and replace it with repString
			int pos = 0;
			while (true) {
				int index = fileText.indexOf(origString, pos);
				if (index < 0) {
					buffer.append(fileText.substring(pos));
					break;
				}
				// otherwise, replace it with repString and continue
				else {
					// go ahead and replace the original text
					buffer.append(fileText.substring(pos, index));
					buffer.append(repString);
					pos = index + origString.length();
				}
			}
			fileText = buffer.toString();
			// write the output string to file
			writeFile(filePath, fileText.getBytes());
		} catch (IOException ioe) {
			logger.error("Error in modifyFile method");
		} catch (Exception ex) {
			logger.error("Error in modifyFile method");
		}
	}

	/**
	 * Helper function to read a file into a byte array
	 * <p>
	 * 
	 * @param filePath
	 *            - the path to the file
	 * @return a byte [] containing the file contents
	 * @throws Exception
	 */
	static public final byte[] readFile(String filePath) throws Exception {
		BufferedInputStream inStream = null;
		try {
			int nSize = 32768;
			// open the input file stream
			inStream = new BufferedInputStream(new FileInputStream(filePath),
					nSize);
			byte[] pBuffer = new byte[nSize];
			int nPos = 0;
			// read bytes into a buffer
			nPos += inStream.read(pBuffer, nPos, nSize - nPos);
			// while the buffer is filled, double the buffer size and read more
			while (nPos == nSize) {
				byte[] pTemp = pBuffer;
				nSize *= 2;
				pBuffer = new byte[nSize];
				System.arraycopy(pTemp, 0, pBuffer, 0, nPos);
				nPos += inStream.read(pBuffer, nPos, nSize - nPos);
			}
			if (nPos <= 0) {
				return "".getBytes();
			}
			// return data read into the buffer as a byte array
			byte[] pData = new byte[nPos];
			System.arraycopy(pBuffer, 0, pData, 0, nPos);
			return pData;
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception ex) {
			throw ex;
		} finally {
			// close the input stream
			inStream.close();
		}
	}

	/**
	 * Helper function to write a byte array into a file
	 * 
	 * @param strFile location where the file will be written to
	 * @param pData byte array that contains the data to be written
	 * @throws Exception
	 */
	static public final boolean writeFile(String strFile, byte[] pData) throws Exception {
		boolean written = false;
		BufferedOutputStream outStream = null;
		try {
			File file1 = new File(strFile);
			// test if the file is READ ONLY
			if (file1.canWrite()) {
				outStream = new BufferedOutputStream(
						new FileOutputStream(file1), 32768);
				if (pData.length > 0) {
					outStream.write(pData, 0, pData.length);
					written = true;
				}
			} else {
			}
			return written;
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (outStream != null) {
				outStream.close();
				return written;
			}
		}
	}

	public static void print(Object[] objs) {
		if (objs != null) {
			print(convertObjectArrayToArrayList(objs));
		}
	}

	public static void print(Collection objs) {
		if (objs != null) {
			int i = 0;
			for (Iterator iter = objs.iterator(); iter.hasNext();) {
				Object element = (Object) iter.next();
				logger.debug("object number(" + i + ")" + element);
				i++;
			}
		}
	}

	public static Collection convertObjectArrayToArrayList(Object[] objs) {
		return convertObjectArrayToArrayList(objs, false);
	}

	public static Collection convertObjectArrayToArrayList(Object[] objs, boolean stringDataType) {
		ArrayList arrList = new ArrayList();
		if (objs != null) {
			int objsLen = objs.length;
			for (int i = 0; i < objsLen; i++) {
				arrList.add((stringDataType == true ? objs[i].toString() : objs[i]));
			}
		}
		return arrList;
	}

	public static String replace(String source, String s1, String s2) {
		if (source != null) {
			int index1 = source.indexOf(s1);
			if (index1 != -1) {
				String tmpStr1 = source.substring(0, index1);
				String tmpStr2 = source.substring(index1 + s1.length());
				return tmpStr1 + s2 + tmpStr2;
			} else {
				return source;
			}

		}
		return null;
	}

	/**
	 * This method takes the DAO key and returns the class an object of a class
	 * which extends the GenericDAO.
	 * 
	 * @param moduleDao String containing the module key for DAO.
	 * @return A subclass which implements IGenericDAO interface.
	 * @throws Exception
	 */
	public static IGenericDAO getDAO(String moduleDao) throws Exception {
		IGenericDAO genericDao = null;
		GenericDAOFactory daoFactory = GenericDAOFactory.getInstance();
		try {
			genericDao = daoFactory.getDAO(moduleDao);
		} catch (Exception e) {
			logger.fatal("DAO instance could not be created", e);
			throw e;
		}
		return genericDao;
	}

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("applink.platform.config.directory", "E:\\wsad\\work\\RPM4.1\\config");
		props.put("module", "server");
		Map map = new HashMap();
		map.put("1", "One");
		map.put("2", "Two");

	}

	public static void testStringReplace() {
		String source = "SureshKorlapati";
		String s1 = "Sur";
		String s2 = "Ram";
	}

	/**
	 * Converts the ArrayList of strings to a string.
	 * 
	 * @param displayFieldsList ArrayList contains strings
	 * @return String containing the contents of ArrayList. The values are comma separated.
	 */
	public static String convertStringArrayListToString(List displayFieldsList) {
		String displayFieldsString = "";
		if (displayFieldsList != null) {
			String str = displayFieldsList.toString();
			displayFieldsString = str.substring(1, str.length() - 1);
		}
		return displayFieldsString;
	}

	public static List convertStringArrayToList(String[] strArray) {
		List list = new ArrayList();
		if (strArray != null) {
			int length = strArray.length;
			for (int i = 0; i < length; i++) {
				String str = strArray[i];
				list.add(str);
			}
		}
		return list;
	}

	/**
	 * formats the values list in the form that rpm understands output will
	 * remove the format of abx
	 * 
	 * @param pageValueList
	 * @return
	 */
	public static List getWellFormedMemberUniqueNameList(List pageValueList) {
		if (pageValueList == null)
			return null;
		List wellFormedList = new ArrayList();
		for (Iterator iter = pageValueList.iterator(); iter.hasNext();) {
			wellFormedList.add(getWellFormedMemberUniqueName((String) iter.next()));
		}
		return wellFormedList;
	}

	public static String getWellFormedMemberUniqueName(String element) {
		if (element == null)
			return null;

		if (element.startsWith("<")
				&& (!(element.endsWith("/>") || element.endsWith("/m>")))) {
			int idStartIndex = element.indexOf("<");
			int idEndIndex = element.indexOf(">");
			if (idStartIndex >= 0 && idEndIndex > 0) {
				String id = element.substring(idStartIndex + 1, idEndIndex);
				String tempStr = element.substring(idEndIndex + 1);
				int dotIndex = tempStr.indexOf(".");
				if (dotIndex > 0) {
					String qPath = tempStr.substring(dotIndex + 1);
					element = qPath + "<" + id + ">";
				}
			}
		}

		return element;
	}

	/**
	 * Retruns the Object type for the assetId.
	 * <p>
	 * 
	 * @param assetId
	 *            String
	 * @return A string which represents the object type.
	 */
	public static String getObjectType(String assetId) {
		if (assetId != null) {
			int size = AssetMasterConstants.ASSET_ID_ARRAY.length;

			for (int i = 0; i < size; i++) {
				String tmpId = AssetMasterConstants.ASSET_ID_ARRAY[i];
				if (tmpId.trim().equals(assetId.trim())) {
					return AssetMasterConstants.OBJECT_TYPE_NAME_ARRAY[i];
				}
			}
		}
		return "";
	}

	public static String getSearchObjectType(String assetId) {
		if (assetId != null) {
			int size = AssetMasterConstants.SEARCH_ASSET_ID_ARRAY.length;
			for (int i = 0; i < size; i++) {
				String tmpId = AssetMasterConstants.SEARCH_ASSET_ID_ARRAY[i];
				if (tmpId.trim().equals(assetId.trim())) {
					return AssetMasterConstants.SEARCH_OBJECT_TYPE_ARRAY[i];
				}
			}
		}
		return "";
	}


	public static int[] getStartAndEndRowForAPage(String currentPageno,
			String objectType, int totalBodyRows) throws Exception {
		int[] startAndEnd = new int[2];
		String rowsPerPage = null;
		String defaultRowsPerPage = "18";
		if (currentPageno != null) {
			ConfigManager configManager = ConfigManager.getInstance();
			/* get the number of rows per page */
			/*Since the row count for role and group management is moved to rpm.properties, if condition is used to check
			 whether the objectType is ADMN_GROUP or ADMN_ROLE.*/
			if(objectType.equals(SummaryConstants.ROLE_MANAGEMENT_OBJECT_TYPE) ||objectType.equals(SummaryConstants.GROUP_MANAGEMENT_OBJECT_TYPE)) {
				rowsPerPage = configManager.getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, objectType + "_" + SummaryConstants.ROWS_PER_PAGE,defaultRowsPerPage);
			}else{
				rowsPerPage = configManager.getProperty(summaryPropsFile, objectType + "_" + SummaryConstants.ROWS_PER_PAGE);	
			}
			if (objectType.equalsIgnoreCase(SummaryConstants.WORKSPACE_OBJECT_TYPE_NAME	+ "_ICONVIEW")) {
				rowsPerPage = configManager.getProperty(summaryPropsFile, SummaryConstants.WORKSPACES_ICONS_PER_PAGE);
			}

			startAndEnd[1] = Integer.parseInt(currentPageno)* Integer.parseInt(rowsPerPage);
			startAndEnd[0] = (startAndEnd[1] - Integer.parseInt(rowsPerPage)) + 1;

			/* check if the end row is greater than the total rows */
			if (startAndEnd[1] > totalBodyRows) {
				startAndEnd[1] = totalBodyRows;
			}
			return startAndEnd;
		}
		return startAndEnd;
	}

	public List getSearchModulesAssetIdList() {
		List modulesAssetIdList = new ArrayList();
		for (int i = 0; i < AssetMasterConstants.SEARCH_ASSET_ID_ARRAY.length; i++) {
			modulesAssetIdList
					.add(AssetMasterConstants.SEARCH_ASSET_ID_ARRAY[i]);
		}
		return modulesAssetIdList;
	}

	/**
	 * @param : sCoordinates
	 * @return : Rectangle
	 * @comments : added for Release 3.2.1 due to change in ABX api
	 */

	public static Rectangle convertStringToRectangle(String sCoordinates) {
		if (sCoordinates.length() > 0)
			sCoordinates = sCoordinates.substring(0, sCoordinates.length() - 1);
		logger.debug("String passed to be converted into RECT is "
				+ sCoordinates);
		int nWidth = 0, nHeight = 0, nLeft = 0, nTop = 0;
		int toksCtr = 1;
		StringTokenizer stoks = new StringTokenizer(sCoordinates, ",");
		while (stoks.hasMoreTokens()) {
			String sRectPoint = stoks.nextToken().trim();
			int indxComma = sRectPoint.indexOf("=");
			int tokVal = 0;
			if (indxComma == -1)
				tokVal = 0;
			else
				tokVal = Integer.parseInt(sRectPoint.substring(indxComma + 1));

			switch (toksCtr) {
			case 1: {
				nTop = tokVal;
				break;
			}
			case 2: {
				nLeft = tokVal;
				break;
			}
			case 3: {
				nWidth = tokVal;
				break;
			}
			case 4: {
				nHeight = tokVal;
				break;
			}
			}
			toksCtr++;
		}
		Rectangle rectCurrent = new Rectangle(nTop, nLeft, nWidth, nHeight);
		return rectCurrent;
	}

	public static ArrayList convertToArrayList(String values) {
		// logger.debug("Creating a Array List for:" + values);
		ArrayList aList = new ArrayList();
		StringTokenizer scst = new StringTokenizer(values,
				CommonConstants.COMMA_DELIMITOR);
		while (scst.hasMoreTokens()) {
			aList.add(((String) scst.nextToken()).trim());
		}
		// logger.debug("Returing Array List with:" + aList.size());
		return aList;
	}

	/**
	 * This method convert comma separated values into a Map. Each token has two part(key, value) 
	 * must be separated with | symbol.
	 * @param values
	 * @return
	 */
	public static Map convertToMap(String values) {
		Map aMap = new HashMap();
		if (values != null){
			StringTokenizer scst =	new StringTokenizer(values, ",");
			while (scst.hasMoreTokens()) {
				String splitStr = ((String) scst.nextToken()).trim();
				if (splitStr != null && splitStr.contains("|")){
					String[] mapString = splitStr.split("\\|");
					String key = mapString[0];
					String value = mapString[1];
					aMap.put(key, value);
				}
			}
		}
		return aMap;
	}

	/**
	 * This method removes Duplicate objects from the list
	 * 
	 * @param dimMemList
	 * @return List
	 */
	public static List removeDuplicatesFromList(List dimMemList) {
		ArrayList uniqueList = new ArrayList();
		if (dimMemList != null) {
			for (int i = 0; i < dimMemList.size(); i++) {
				if (!uniqueList.contains(dimMemList.get(i))) {
					uniqueList.add(dimMemList.get(i));
				}
			}
		}
		return uniqueList;
	}

	public static boolean isNumeric(String inString) {
		if (inString == null) {
			return false;
		}
		CharacterIterator theIterator = new StringCharacterIterator(inString);

		for (char ch = theIterator.first(); ch != CharacterIterator.DONE; ch = theIterator
				.next()) {
			if (!Character.isDigit(ch)) {
				return false;
			}
		}

		return true;
	}

	public ArrayList getSuperUserCredentials() throws Exception {
		ArrayList list = new ArrayList(3);
		ConfigManager cfm = ConfigManager.getInstance();
		CryptoManager cryptoMgr = null;
		HashMap hmpBindCredentials = null;
		String strUserName = null;
		String strPassword = null;
		String strDomainName = "";
		if (!strIsLDAPEnabled.equalsIgnoreCase("Yes")) {
			strUserName = cfm.getProperty(
					ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,
					ConfigurationConstants.WHITELIGHT_LOGIN_NAME);
			strPassword = cfm.getProperty(
					ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,
					ConfigurationConstants.WHITELIGHT_PASSWORD);
			if (strUserName == null) {
				strUserName = "superuser";
			}
			if (strPassword == null) {
				strPassword = "";
			} else {
				// Decrypt the password
				cryptoMgr = new CryptoManager();
				strPassword = cryptoMgr.decrypt(strPassword);
			}
		} else {
			hmpBindCredentials = getBindUserCredentials();
			strUserName = (String) hmpBindCredentials
					.get(AdminConstants.BIND_USER_ID);
			strPassword = (String) hmpBindCredentials
					.get(AdminConstants.BIND_USER_PASSWORD);
			strDomainName = (String) hmpBindCredentials
					.get(AdminConstants.DOMAIN_NAME);

			if (strPassword != null && (strPassword.length() > 0)) {
				cryptoMgr = new CryptoManager();
				strPassword = cryptoMgr.decrypt(strPassword);
			}
		}
		list.add(0, strUserName);
		list.add(1, strPassword);
		list.add(2, strDomainName);
		return list;
	}

	public HashMap getBindUserCredentials() {
		Document document = null;
		Element element = null;
		HashMap hmpBindCredentials = null;
		Node rootNode = null;
		Node ldapPolNode = null;
		NodeList ldapPolNodeList = null;
		int intNoOfLDAPPolicies = 0;
		try {
			document = getXMLDocument(getContentOfPolFile(getLDAPPolFile()));
			rootNode = document.getElementsByTagName(
					AdminConstants.LDAP_POL_FILE_SECURITY_POL_TAG).item(0);
			ldapPolNodeList = rootNode.getChildNodes();
			intNoOfLDAPPolicies = ldapPolNodeList.getLength();
			for (int intPolIndex = 0; intPolIndex < intNoOfLDAPPolicies; intPolIndex++) {
				ldapPolNode = ldapPolNodeList.item(intPolIndex);
				if (ldapPolNode.getNodeName().equalsIgnoreCase(
						AdminConstants.LDAP_POL_FILE_LDAP_POL_TAG)) {
					hmpBindCredentials = getBindCredentials(getBindCredentialsTag(ldapPolNode));
					hmpBindCredentials.put(AdminConstants.DOMAIN_NAME,
							getDomainName(getDirectoryTag(ldapPolNode)));
					if (hmpBindCredentials != null
							&& hmpBindCredentials.size() > 0) {
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return hmpBindCredentials;
	}

	private String getDomainName(Node directoryNode) {
		NamedNodeMap nodeMap = null;
		String strDomainName = null;
		try {
			nodeMap = directoryNode.getAttributes();
			strDomainName = nodeMap.getNamedItem(
					AdminConstants.LDAP_POL_FILE_DOMAIN_ATTRIBUTE)
					.getNodeValue();
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return strDomainName;
	}

	private Node getBindCredentialsTag(Node ldapNode) {
		Node nodeBindCredentials = null;
		Node childNode = null;
		NodeList childNodeList = null;
		int intNoOfChildNodes = 0;
		try {
			childNodeList = ldapNode.getChildNodes();
			intNoOfChildNodes = childNodeList.getLength();
			for (int intChildIndex = 0; intChildIndex < intNoOfChildNodes; intChildIndex++) {
				childNode = childNodeList.item(intChildIndex);
				if (childNode.getNodeName().equalsIgnoreCase(
						AdminConstants.LDAP_POL_FILE_BIND_CREDENTIALS_TAG)) {
					nodeBindCredentials = childNode;
				}
			}
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return nodeBindCredentials;
	}

	private Node getDirectoryTag(Node ldapNode) {
		Node nodeDirectory = null;
		Node childNode = null;
		NodeList childNodeList = null;
		int intNoOfChildNodes = 0;
		try {
			childNodeList = ldapNode.getChildNodes();
			intNoOfChildNodes = childNodeList.getLength();
			for (int intChildIndex = 0; intChildIndex < intNoOfChildNodes; intChildIndex++) {
				childNode = childNodeList.item(intChildIndex);
				if (childNode.getNodeName().equalsIgnoreCase(
						AdminConstants.LDAP_POL_FILE_DIRECTORY_TAG)) {
					nodeDirectory = childNode;
				}
			}
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return nodeDirectory;
	}

	private HashMap getBindCredentials(Node bindCredentialsNode) {
		HashMap hmpBindCredentials = null;
		Node node = null;
		NodeList nodeList = null;
		int intNoOfChilds = 0;
		try {
			hmpBindCredentials = new HashMap();
			nodeList = bindCredentialsNode.getChildNodes();
			intNoOfChilds = nodeList.getLength();
			for (int intChildIndex = 0; intChildIndex < intNoOfChilds; intChildIndex++) {
				node = nodeList.item(intChildIndex);
				if (node.getNodeName().equalsIgnoreCase(AdminConstants.LDAP_POL_FILE_BIND_USER_ID_TAG)) {
					hmpBindCredentials.put(AdminConstants.BIND_USER_ID, node.getChildNodes().item(0).getNodeValue());
				} else if (node.getNodeName().equalsIgnoreCase(AdminConstants.LDAP_POL_FILE_BIND_USER_PWD_TAG)) {
					hmpBindCredentials.put(AdminConstants.BIND_USER_PASSWORD, node.getChildNodes().item(0).getNodeValue());
				}
			}
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return hmpBindCredentials;
	}

	private File getLDAPPolFile() {
		File ldapPolFile = null;
		String strFileSeparator = null;
		String strConfigFolderPath = null;
		String strLDAPPolFile = null;
		try {
			strFileSeparator = System.getProperty("file.separator");
			strConfigFolderPath = System.getProperty(ConfigurationConstants.DIRECTORY_PROPERTY_NAME);
			if (!strConfigFolderPath.endsWith(strFileSeparator)) {
				strConfigFolderPath += strFileSeparator;
			}
			strLDAPPolFile = strConfigFolderPath + AdminConstants.LDAP_POL_FILE_FOLDER + strFileSeparator + AdminConstants.LDAP_POL_FILE_NAME;
			ldapPolFile = new File(strLDAPPolFile);
			if (!ldapPolFile.exists()) {
				throw new FileNotFoundException("Could not locate ldap.pol file.");
			}
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return ldapPolFile;
	}

	private byte[] getContentOfPolFile(File ldapPolFile) throws IOException {
		InputStream inputStream = null;
		byte[] byteArrData = null;
		int intOffset = 0;
		int intNumRead = 0;
		long lngFileSize = -1;
		try {
			inputStream = new FileInputStream(ldapPolFile);
			lngFileSize = ldapPolFile.length();
			if (lngFileSize > Integer.MAX_VALUE) {
				throw new IOException("ldap.pol file is too large.");
			}
			byteArrData = new byte[(int) lngFileSize];
			while (intOffset < byteArrData.length && (intNumRead = inputStream.read(byteArrData, intOffset, byteArrData.length - intOffset)) >= 0) {
				intOffset += intNumRead;
			}
			if (intOffset < byteArrData.length) {
				throw new IOException("Could not completely read ldap.pol file.");
			}
		} catch (IOException exIOE) {
			throw exIOE;
		} catch (Exception ex) {
			logger.fatal(ex);
		} finally {
			inputStream.close();
		}
		return byteArrData;
	}

	private Document getXMLDocument(byte[] byteArrData) {
		ByteArrayInputStream fileInput = null;
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document document = null;
		try {
			if (byteArrData != null) {
				docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilder = docBuilderFactory.newDocumentBuilder();
				fileInput = new ByteArrayInputStream(byteArrData);
				document = docBuilder.parse(fileInput);
			}
		} catch (Exception ex) {
			logger.fatal(ex);
		}
		return document;
	}

	public static String convertABXUniqueNameToRPMFormat(String memberUniqueName) {
		return convertABXUniqueNameToRPMFormat(memberUniqueName, null);
	}

	public static String convertABXUniqueNameToRPMFormat(String memberUniqueName, String functionName) {
		String rpmMemberUniqueName = "";
		if (memberUniqueName != null) {
			int startIndex = memberUniqueName.indexOf("<");
			int endIndex = memberUniqueName.indexOf(">");

			if (startIndex != -1 && endIndex != -1) {
				String memberId = memberUniqueName.substring(startIndex + 1, endIndex);
				String fullpath = memberUniqueName.substring(endIndex + 1);

				fullpath = fullpath.substring(fullpath.indexOf(".") + 1);
				fullpath = XmlUtility.escape(fullpath);

				if (memberId != null && fullpath != null) {
					rpmMemberUniqueName = fullpath + "<" + memberId + ">" + "<>";

					if (functionName != null && functionName.trim().length() > 0) {
						rpmMemberUniqueName += "<" + functionName + "><>";
					} else {
						rpmMemberUniqueName += "<><>";
					}
				}
			}
		}
		return rpmMemberUniqueName;
	}

	public static Map getPropMapFromString(String propMapStr, String propSeperator, String propKeyValueSeperator) {
		Map propsMap = new HashMap();
		if (propMapStr != null && (propMapStr = propMapStr.trim()).length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(propMapStr, propSeperator);
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				int gIndex = token.indexOf(propKeyValueSeperator);
				if (gIndex > 0) {
					String propKey = (token.substring(0, gIndex));
					String propValue = token.substring(gIndex + 1);
					propsMap.put(propKey, propValue);
				}
			}
		}
		return propsMap;
	}

	public static String getStringFromPropMap(Map propsMap, String propSeperator, String propKeyValueSeperator) {
		String propMapStr = "";
		if (propsMap != null && propsMap.size() > 0) {
			for (Iterator i = propsMap.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				String keyValueStr = ((String) e.getKey()) + propKeyValueSeperator + (String) e.getValue();
				if (propSeperator.equals("")) {
					propSeperator = keyValueStr;
				} else {
					propSeperator = propMapStr + propSeperator + propKeyValueSeperator;
				}
			}
		}
		return propMapStr;
	}

	public static boolean isSQLServerRepository() {
		return isSQLServer;
	}

	public static boolean isBlobNotSupported() {
		return isBlobNotSupported;
	}

	public static void setBlobNotSupported(boolean isBlobNotSupported) {
		RpmUtil.isBlobNotSupported = isBlobNotSupported;
	}

	public static String getValidUserID(String strUserID) {
		String strTempUserID = null;
		if (strUserID != null) {
			if (strUserID.indexOf("\\") > -1) {
				strTempUserID = strUserID.substring(strUserID.indexOf("\\") + 1);
			} else if (strUserID != null) {
				strTempUserID = strUserID;
			}
		}
		return strTempUserID;
	}

	/**
	 * This method has been added to get a valid sessionId by truncation depending on appserver 
	 * @param sessionId
	 * @return
	 */
	public static String getValidSessionID(String sessionId) {
		ConfigManager confMgr = ConfigManager.getInstance();
		String appServName = confMgr.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME, ConfigurationConstants.APPSERVER_PROP_NAME);
		if (appServName != null && appServName.equalsIgnoreCase("weblogic")) {
			if (sessionId != null && sessionId.length() > 52) {
				sessionId = sessionId.substring(0, 52);
			}
		}
		return sessionId;
	}
	
	public static boolean isCachingEnabled(){
		int reportExecutionMode = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, CacheConstants.REPORT_EXECUTION_MODE, CacheConstants.REPORT_EXECUTION_CACHE_WITH_ASYNC);
		if(reportExecutionMode >= CacheConstants.REPORT_EXECUTION_WITH_CACHE && reportExecutionMode <= CacheConstants.REPORT_EXECUTION_CACHE_WITH_ASYNC){
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isCachingEnabledForAE(boolean isAdhocReport) {
		int reportExecutionMode = -1;
		boolean cacheEnabled = false;
		if (isAdhocReport) {
			reportExecutionMode = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, CacheConstants.REPORT_EXECUTION_MODE_FOR_AE_ADHOC, CacheConstants.AE_ADHOC_REPORT_EXECUTION_CACHE_WITH_ASYNC);
			if (reportExecutionMode == CacheConstants.AE_ADHOC_REPORT_EXECUTION_CACHE_WITH_ASYNC) {
				cacheEnabled =  true;
			}
		} else {
			reportExecutionMode = ConfigManager.getInstance().getPropertyAsInt(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, CacheConstants.REPORT_EXECUTION_MODE_FOR_AE, CacheConstants.AE_REPORT_EXECUTION_CACHE_WITH_ASYNC_YES);
			if (reportExecutionMode == CacheConstants.AE_REPORT_EXECUTION_CACHE_WITH_ASYNC_YES || reportExecutionMode == CacheConstants.AE_REPORT_EXECUTION_CACHE_WITH_ASYNC || reportExecutionMode == CacheConstants.AE_REPORT_EXECUTION_CACHE) { 
				cacheEnabled = true;
			}
		}
		return cacheEnabled;
	}
	
/*	public static boolean isAppLevelAsyncFeatureSupported(String Application){
        String asyncSupportedAppStr = ConfigManager.getInstance().getPropertyAsString(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, ConfigurationConstants.ASYNC_SUPPORTED_APPS, "AE,AP,AO");
        List<String> apps = convertToArrayList(asyncSupportedAppStr);
        if(apps.contains(Application) || apps.size()==0){
              return true;
        }else{
              return false;
        }
    } */
	
	
	/**
	 * This method returns the localized string based upon the locale set in
	 * system.properties.
	 * @param bundleName
	 * @param key
	 * @return
	 */
	public static Map<String, String> getLocalizedMap(String bundleName, String key) {
		Map<String, String> localeMap = i18nManager.getLocaleMap(bundleName, locale, key);
		return localeMap;
		}
	}

