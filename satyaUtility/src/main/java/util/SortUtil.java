package util;

/**
 * @(#)SortUtil.java										Apr 29, 2003
 *
 * Copyright (c)2002-2003 Symphony Software. All Rights Reserved.
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
 * @version  	:  	
 * @author     	:	skorlapa
 * @reviewed by 	:	
 * @date reviewed :	dateReviewed (mm/dd/yyyy)
 */

/** java packages */
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.dto.GenericDTO;

/** 
 * A class Pair holding name and number value for map sorting.
 */
 class Pair
{
	private final String name;
	private final Long number;
 
	public static final Comparator NUMBER_ORDER = new Comparator()
	{
		public int compare(Object a, Object b)
		{
			Pair pairA = (Pair)a;
			Pair pairB = (Pair)b;
 
			int ret = pairA.number.compareTo(pairB.number);
 
			if (ret == 0)
			{
				ret = pairA.name.compareTo(pairB.name);
			}
			return ret;
		}
	};
 
	public Pair(String name, long number)
	{
		this(name, new Long(number));
	}
 
	public Pair(String name, Long number)
	{
		if (name  == null)
		{
			throw new NullPointerException("name parameter is null");
		}
 
		if (number == null)
		{
			throw new NullPointerException("number parameter is null");
		}
		this.name = name;
		this.number = number;
	}
 
	/** 
	 * Two Pair objects are equal if they have the same name and number.
	 * 
	 * @param obj a Pair object.
	 * @return true if this Pair equals the Pair parameter, false otherwise.
	 */
	public boolean equals(Object obj)
	{
		if (obj == null) return false;
 
		Pair that = (Pair)obj;
 
		return this.name.equals(that.name) &&
			this.number.equals(that.number);
	}
 
	public String toString()
	{
		return name + " " + number;
	}
 
	public String name() { return name; }
 
	public Long number() { return number; }
 
	public long num() { return number.longValue(); }
}


public class SortUtil {
	
	private static SortUtil util=null;
	//private AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,this.getClass().getName());
	private SortUtil(){
	
	}
	
	public static SortUtil getInstance(){
		if(util == null)
			util = new SortUtil();
		return util;	
	}

	public static LinkedHashMap sortMapOnNumberValues(Map map) throws Exception
	{
		LinkedHashMap sortedMap = new LinkedHashMap();
		
		Set sortedSet = new TreeSet(Pair.NUMBER_ORDER);
		Iterator mapKeyIter = map.keySet().iterator();
		while(mapKeyIter.hasNext())
		{
			String key = (String)mapKeyIter.next();
			Long value = (Long)map.get(key);
			
			Pair pair = new Pair(key, value);
			sortedSet.add(pair);
		}
		
		for (Iterator iter = sortedSet.iterator(); iter.hasNext();)
		{ 
			Pair pair = (Pair)iter.next();
			sortedMap.put(pair.name(), pair.number());
		}
		
		return sortedMap;
	}
	
	public ArrayList sort(ArrayList summaryList, String orderBy, String sortOrder){
		//AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,this.getClass().getName());
		//logger.debug("SortUtil : sort() method");
		if(orderBy != null) orderBy =orderBy.trim();
		if(sortOrder != null) sortOrder = sortOrder.trim();
		
		//logger.debug("orderBy = "+orderBy);
		//logger.debug("sortOrder = "+sortOrder);
		
		//logger.debug("Sorting the contents of the Summary ArrayList ");
		if(summaryList != null){
			int summSize = summaryList.size();
			for(int i=0; i<summSize; i++){
				//logger.debug("i = "+i);
				if(i > 0)
					break;
				GenericDTO genericdto = (GenericDTO)summaryList.get(i);
				if(orderBy != null){
					genericdto.setOrderBy(orderBy);
				}
				else{
					//logger.debug("Setting the sory criteria to the Created Date");
					genericdto.setOrderBy(CommonConstants.ORDER_BY_CREATED_DATE);
				}
				
				//set the sort order. 
				//logger.debug("sortOrder before setting it to DTO = "+sortOrder);
				//logger.debug("CommonConstants.DESC_SORT_ORDER = "+CommonConstants.DESC_SORT_ORDER);
				//logger.debug("CommonConstants.AESC_SORT_ORDER = "+CommonConstants.AESC_SORT_ORDER);
				if(sortOrder != null){
					genericdto.setSortOrder(sortOrder);
				}
				else{
					//logger.debug("by default : Setting the sort order to descending");
					genericdto.setSortOrder(CommonConstants.DESC_SORT_ORDER);
				}
			}

			//sort the collection. 
			Collections.sort(summaryList);  
		}
		return summaryList;
	}
	
	public static short sortDate(Timestamp ts1, Timestamp ts2, String sortOrder) {
		
		GregorianCalendar gc1 = new GregorianCalendar(ts1.getYear(),
								ts1.getMonth(),ts1.getDate(),
								ts1.getHours(),ts1.getMinutes(),
								ts1.getSeconds());
		
		GregorianCalendar gc2 = new GregorianCalendar(ts2.getYear(),
								ts2.getMonth(),ts2.getDate(),
								ts2.getHours(),ts2.getMinutes(),
								ts2.getSeconds());
		if(sortOrder.equalsIgnoreCase(CommonConstants.DESC_SORT_ORDER)) {
			if(gc1.before(gc2)) return 1;
			if(gc1.after(gc2)) return -1;
		}
		else {
			if(gc1.before(gc2)) return -1;
			if(gc1.after(gc2)) return 1;	
		}
		return 0;	
	}
	
	public static int sortString(String s1, String s2, String sortOrder) {
		return sortString(s1, s2, sortOrder, false);
	}
	
	public static int sortStringIngnoreCase(String s1, String s2, String sortOrder) {
		return sortString(s1, s2, sortOrder, true);
	}
		
	private static int sortString(String s1, String s2, String sortOrder, boolean ignoreCase) {
		int returnVal = 0;
		if(s1 == null || s2==null)
			return returnVal;
		if(ignoreCase == true)	
			returnVal = s1.trim().toLowerCase().compareTo(s2.trim().toLowerCase());
		else
			returnVal = s1.trim().compareTo(s2.trim());
		if(sortOrder.equalsIgnoreCase(CommonConstants.DESC_SORT_ORDER)) {
			return -returnVal;
		}
		return returnVal;
	}
	public static void sortNumber(double [] array, int len)
	{
		int a,b;
		double temp;
		int sortTheNumbers = len - 1;
		for (a = 0; a < sortTheNumbers; ++a)
		{
			for (b = 0; b < sortTheNumbers; ++b)
			if(array[b] < array[b + 1])
			{
			temp = array[b];
			array[b] = array[b + 1];
			array[b + 1] = temp; 
			}
		}
	}
	

	
}
