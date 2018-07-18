/*
 * @(#)GenericComparator      Feb 15, 2002
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
 * @author     kkumar
 */
package util;

import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;

public class GenericComparator implements Comparator {

	String sortColumn;
	String sortOrder;
	private int sortColumnNumber;


	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object arg0, Object arg1)
	{
		if(arg0 == null)
		{
			return -1;
		}else if(arg1 == null)
		{
			return 1;
		}
		
		if(arg0 instanceof Date && arg1 instanceof Date)
		{
			return compareDate((Date)arg0,(Date)arg1);
		}else if(arg0 instanceof String && arg1 instanceof String)
		{
			return compareString((String)arg0,(String)arg1);
		}else if(arg0 instanceof Comparable && arg1 instanceof Comparable)
		{
			Comparable c1 =(Comparable)arg0;
			return c1.compareTo(arg1);	
		}
		else 
		{
			throw new ClassCastException("This Comparator can compare only Date and String.For Comparing user defined objects extend GenericComparator class and override compare()");
		}
	} 

	protected int compareDate(Date date1,Date date2)
	{
		int returnVal = 0;
		if(date1!=null && date2!=null)
		{
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date1);
			GregorianCalendar gc2 = new GregorianCalendar();
			gc2.setTime(date2);				
			if(gc.before(gc2))
			{
				returnVal = -1;
			}else if(gc.after(gc2))
			{
				returnVal = 1;	
			}
		}else
		{
			if(date1 == null)
			{
				returnVal = -1;
			}else
			{
				returnVal = 1;
			}
		}
		if(ConfigurationConstants.DESC_SORT_ORDER.equals(sortOrder)){
			return -returnVal;
		}				
		return returnVal;
	}
	
	protected int compareString(String s1,String s2)
	{
		int returnVal = 0;
		if(s1!=null && s2!=null)
		{
			returnVal = s1.compareToIgnoreCase(s2);
		}else
		{
			if(s1 == null)
			{
				returnVal = -1;
			}else
			{
				returnVal = 1;
			}
		}

		if(ConfigurationConstants.DESC_SORT_ORDER.equals(sortOrder)){
			return -returnVal;
		}
		return returnVal;
	}

/**
	 * Returns the sortColumn.
	 * @return int
	 */
	public String getSortColumn() {
		return sortColumn;
	}

	/**
	 * Returns the sortOrder.
	 * @return String
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * Sets the sortColumn.
	 * @param sortColumn The sortColumn to set
	 */
	public void setSortColumn(String sortColumn) 
	{
		this.sortColumn = sortColumn;
		//should change sort number accordingly
	}

	/**
	 * Sets the sortOrder.
	 * @param sortOrder The sortOrder to set
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Returns the sortColumnNumber.
	 * @return int
	 */
	public int getSortColumnNumber() {
		return sortColumnNumber;
	}

	/**
	 * Sets the sortColumnNumber.
	 * @param sortColumnNumber The sortColumnNumber to set
	 */
	public void setSortColumnNumber(int sortColumnNumber) {
		this.sortColumnNumber = sortColumnNumber;
	}

	
}
