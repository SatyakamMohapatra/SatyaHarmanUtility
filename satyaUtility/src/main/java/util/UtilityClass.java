/*
 * @(#)UtilityClass.java      10/01/2003
 *
 * Copyright (c) 2002-2003 Symphony Software. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Symphony Software, ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Symphony Software.
 *
 * SYMPHONY SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SYMPHONY SOFTWARE SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version    3.2
 * @author     shamsur
 * @reviewed by 
 * @date reviewed 
 */

package util;

// RPM imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.StringTokenizer;

import com.symphonyrpm.applayer.common.constants.AlertConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.CacheManager;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.dto.TupleDTO;
import com.symphonyrpm.applayer.common.exceptions.AlertException;


/**
 * Utility Class contains some common utility methods to be used 
 * by the rest of the application
 */

public class UtilityClass {

	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, UtilityClass.class.getName());
	private static ArrayList displaybleDimensions;
	/**
	 * @roseuid 3E1A737F012C
	 */
	public UtilityClass() {
	}

	/**
	 * This method will build the tuple query string with the userId appended to it.
	 * 
	 * @param tupleDTO
	 * @return TupleDTO
	 * @roseuid 3E13EAF902AC
	 */
	public static TupleDTO buildTupleQueryString(TupleDTO tupleDTO) throws AlertException {
		String queryString = null;
		StringTokenizer st = null;
		ConfigManager configManager = null;
		ArrayList tokens = new ArrayList();
		Object tokenArray[] = null;
		String builtString = new String();
		StringBuffer sbuff = null;
		String tempString;
		AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, UtilityClass.class.getName());
		try {
			configManager = ConfigManager.getInstance();
			queryString = configManager.getProperty(AlertConstants.ALERT_PROPERTIES, "alerts.TupleString");
		} catch (MissingResourceException mre) {
			logger.debug("Error in gettin the ConfigMangaer ", mre);
			throw (new AlertException(AlertConstants.CFM_NOT_FND, "Config Manager Not Found ", IModules.SERVER));
		}

		st = new StringTokenizer(queryString, "|");
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken().trim());
		}

		tokenArray = tokens.toArray();

		for (int i = 0; i < tokenArray.length; ++i) {
			tempString = (String) tokenArray[i];
			sbuff = new StringBuffer(tempString);
			sbuff.replace(tempString.length() - 1, tempString.length() - 2, tupleDTO.getUserId());
			builtString += sbuff.toString();
		}
		tupleDTO.setTupleQueryString(tupleDTO.getTupleQueryString() + "|" + builtString);
		return tupleDTO;
	}

	/**
	 * This method will construct the HQL Query which will be sent to Whitelight 
	 * through ASConnectionBean
	 * 
	 * @param tupleDTO
	 * @return TupleDTO
	 * @roseuid 3E13EAF902AC
	 */

	public static TupleDTO makeQueryString(TupleDTO tupleDTO) throws AlertException {

		AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, new UtilityClass().getClass().getName());
		ConfigManager configManager = null;
		try {
			configManager = ConfigManager.getInstance();
		} catch (MissingResourceException mre) {
			logger.debug("Error in gettin the ConfigMangaer ", mre);
			throw (new AlertException(AlertConstants.CFM_NOT_FND, "Config Manager Not Found ", "GenerateAlert"));
		}

		String prefix = configManager.getProperty(AlertConstants.ALERT_PROPERTIES, AlertConstants.TUPLE_PREFIX);
		String suffix = configManager.getProperty(AlertConstants.ALERT_PROPERTIES, AlertConstants.TUPPLE_SUFFIX);

		String str = null;

		str = prefix + " " + tupleDTO.getTupleQueryString() + suffix;
		tupleDTO.setTupleQueryString(str);
		return tupleDTO;
	}

	/**
	 * This method will return the hashcode of the string passed to it
	 * 
	 * 
	 * @param str
	 * @return int
	 */

	public static int hash(String str) {
		int constkey = 1024;
		int hash = 0;
		for (int i = 0; i < str.length(); ++i) {
			hash = (hash * 37) + str.charAt(i);
		}
		return hash * constkey;
	}

	/**
	 * This method splits the tuple and returns a map whose key is the dimension name and value the member name
	 */
	 public static Map splitAndGetDimMemberInfoFromTuple(String tuple)
	 {
		Map dispContentsHash = new HashMap();
		if(tuple!=null)
		{
			try
			{	
				StringTokenizer tupDelimiterTokenizer = null;
				StringTokenizer tupDimMembTokenizer = null;
	
				tupDelimiterTokenizer = new StringTokenizer(tuple,"|");
				do
				{
					String tempTupleDimMemb = tupDelimiterTokenizer.nextToken();
					tupDimMembTokenizer = new StringTokenizer(tempTupleDimMemb,".");
					String dimension = tupDimMembTokenizer.nextToken().replace('"',' ').trim();				
					String member = null;
					do
					{
						member= tupDimMembTokenizer.nextToken().replace('"',' ').trim();
					}while(tupDimMembTokenizer.hasMoreTokens());
	
					dispContentsHash.put(dimension,member);
					
				}while(tupDelimiterTokenizer.hasMoreTokens());
		 	}catch ( Exception ee)
			{
				logger.error(" exception being" ,ee);
			}
		}
		logger.info("returning .."+dispContentsHash);
		return dispContentsHash ;
	 }

	/**
	 * This method splits the tuple and returns a map whose key is the dimension name and value the member name
	 * @param Collection of tuples
	 * @return Collection of Map
	 */
	 public static Collection splitAndGetDimMemberInfoFromTuple(Collection tuples)
	 {
		Collection dispContents = new ArrayList();
		try
		{	
			Iterator tupleIterator = tuples.iterator();
			while(tupleIterator.hasNext())
			{
			  String tempTuple =(String)tupleIterator.next();
			  Map dispContentsHash = splitAndGetDimMemberInfoFromTuple(tempTuple);
			  dispContents.add(dispContentsHash);
			}
			//logger.info(" The value of vector " + dispContents);
		}
		catch ( Exception ee)
		{
			logger.error(" exception being" ,ee);
		}
		return dispContents ;
	 }
	 
	/**
	 * This method arranges the dimensions given according to the order spicified
	 * @param Map  dimMemMap - Map of dimension name and member name
	 * @param Collection - default dimension order
	 * @return Collection - returns a collection of member names in the default dimension order
	 */
	 public static Collection arrangeDimMembers(Map dimMemMap,Collection defDimensions)
	 {
		Collection finalDispContents = new ArrayList();
		try
		{
				if(defDimensions == null)
				{
					//get from cache
				}
				
				Iterator defDimensionsIterator = defDimensions.iterator();
				while(defDimensionsIterator.hasNext())	
				{
					String defDimVal = 	(String)defDimensionsIterator.next();
					logger.info("defDIm " + defDimVal);
					if(dimMemMap.containsKey(defDimVal))
					{
						finalDispContents.add(dimMemMap.get(defDimVal));
					}
					else
					{
						finalDispContents.add("");
					}
				}
			}
		catch( Exception eee)
		{
			logger.error("exception base" ,eee);
		}
		return finalDispContents;
	 }

	/**
	 * This method arranges the dimensions given according to the order spicified
	 * @param Collection  displayList - Collection of Maps whose key is dimension name and value as member name
	 * @param Collection - default dimension order
	 * @return Collection - returns a collection of Collections whose member names are in the default dimension order
	 */
	 public static Collection arrangeDimMembers(Collection displayList,Collection defDimensions)
	 {
		Collection finalDisplayList = new ArrayList();
		try
		{
			Collection finalDispContents ;
			Map displayHash = null;
			Iterator displayListIterator = displayList.iterator();
			while(displayListIterator.hasNext())
			{
				finalDispContents = new ArrayList();
				displayHash = (Map) displayListIterator.next();
				finalDisplayList.add(arrangeDimMembers(displayHash,defDimensions));
				logger.info(" The final vector " + finalDisplayList);
			}
		}
		catch( Exception eee)
		{
			logger.error("exception base" ,eee);
		}
		return finalDisplayList;
	 }
	 
	 public static Collection getDisplayableDimensions()throws Exception
	 {
		if(displaybleDimensions == null)
		{
		 	ConfigManager configManager = ConfigManager.getInstance();
			String measureDimension =configManager.getProperty(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME,	ConfigurationConstants.WHITELIGHT_MEASURE_DIMENSION);
			String displaybleDimensionsString=configManager.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,	ConfigurationConstants.DIMENSIONS_TO_DISPLAY);

//			String modelName =	configManager.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,	ConfigurationConstants.MODEL_PROP_NAME);
//			String defaultModelMemberData =	configManager.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,		ConfigurationConstants.WAOCACHE_PROP_NAME);
//			OrderedHashtable defaultDimMember = (OrderedHashtable)CacheManager.getInstance().getData(defaultModelMemberData,modelName);
			Collection dimList = (Collection)CacheManager.getInstance().getData(ConfigurationConstants.DIMENSION_LIST_CACHE_NAME,ConfigurationConstants.DIMENSION_LIST_CACHE_KEY);
			ArrayList allDimensions = null;
			if(dimList!=null)
			{
				allDimensions  = new ArrayList(dimList);
			}
	
			if(displaybleDimensionsString!= null && (displaybleDimensionsString=displaybleDimensionsString.trim()).length()>0)
			{
				ArrayList tempList = new ArrayList();
				StringTokenizer tokenizer = new StringTokenizer(displaybleDimensionsString,",");
				while(tokenizer.hasMoreTokens())
				{
					String token = tokenizer.nextToken();
					if(allDimensions.contains(token))
					{
						tempList.add(token);
					}
				}
				if(allDimensions!=null)
				{
					Iterator allDimensionsIterator = allDimensions.iterator();
					while(allDimensionsIterator.hasNext())
					{
						if(!tempList.contains(allDimensionsIterator.next()))
						{
							allDimensionsIterator.remove();
						}
					}
				}else
				{
					allDimensions = tempList;
				}
			}
			displaybleDimensions = allDimensions;
	
			if(displaybleDimensions.remove(measureDimension))
			{
				displaybleDimensions.add(0,measureDimension);
			}
		}
		return displaybleDimensions;
	 }

	public static ArrayList getMapKeysAsList(Map map)
	{
		ArrayList arr =null;
		if(map!=null)
		{
		 arr = new ArrayList(map.keySet());
	

		}
		return arr;
		
		
		
	}
	 public static void main(String[] args)
	 {
			//System.out.println(splitAndGetDimMemberInfoFromTuple("\"Product\".\"Dist1\".\"Desktops\".\"Presarios\".\"1000001\" | \"Customer\".\"101\" | \"Measures\".\"UnitListPriceDataMap\" | \"Time\".\"2002-01-01\" | \"Organization\".\"Planners\""));
	 }

}
