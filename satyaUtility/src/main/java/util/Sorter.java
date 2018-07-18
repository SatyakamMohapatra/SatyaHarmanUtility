/*
 * @(#)Sorter      May 8, 2003
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author kkumar
 *
 *Class Description 
 */
public class Sorter 
{

	//private static GenericComparator comparator = new GenericComparator(); 
	
	private static void mergeSort(Object src[], Object dest[],int low, int high) 
	{
		int length = high - low;

		if (length < 7) 
		{
			for (int i=low; i<high; i++)
			{
				for (int j=i; j>low && (compare(dest[j-1],dest[j]))>0; j--)
				{
				    swap(dest, j, j-1);
				}
			}
			return;
		}
        // Recursively sort halves of dest into src
        int mid = (low + high)/2;
        
        mergeSort(dest, src, low, mid);
        mergeSort(dest, src, mid, high);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (compare(src[mid-1],src[mid])<= 0) 
		{
           System.arraycopy(src, low, dest, low, length);
           return;
        }

        // Merge sorted halves (now in src) into dest
        for(int i = low, p = low, q = mid; i < high; i++) 
		{
            if (q>=high || p<mid && (compare(src[p],src[q]))<=0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

	private static int compare(Object o1,Object o2)
	{
		int result = 0;
		if(o1 instanceof String && o2 instanceof String)
		{
			return ((String)o1).compareTo((String)o2);		
		}
		return result;
	}
    private static void swap(Object x[], int a, int b) 
	{
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
    }

	public static String[] sortStringEnumeration(Enumeration<String> enumObj ,int enumSize) 
	{
		String [] sortedArray = new String[enumSize];
		int i=0;
		while ( enumObj.hasMoreElements() ) 
		{
		   sortedArray[i++]=(String)enumObj.nextElement();
		}
		return sortStringArray(sortedArray);
    }

	public static String[] sortStringVector(Vector vecObj) 
	{
		if(vecObj == null)
		{
			return null;
		}
		int enumSize = vecObj.size();
		String [] sortedArray = new String[enumSize];
		for(int i=0;i<enumSize;i++)
		{
		   sortedArray[i]=(String)vecObj.elementAt(i);
		}
		return sortStringArray(sortedArray);
    }

	public static String[] sortStringArray(String [] strArray) 
	{
		String rArray[] = null;
		if(strArray!=null)
		{
			Object [] dArray = (Object[])strArray.clone();			
			mergeSort(strArray,dArray,0,strArray.length);
			rArray = new String[dArray.length];
			System.arraycopy(dArray,0,rArray,0,dArray.length);			
		}
		return rArray ;
    }

	public static void main(String[] args) 
	{
		//String srcArray[] = {"kiran","tkiran","akiran","kiran","tkiran","akiran","kiran","tkiran","akiran","kiran","tkiran","akiran","kiran","tkiran","akiran","kiran","tkiran","akiran"};
		//Object decArray[] = sortStringArray(srcArray);		
		Hashtable htable = new Hashtable();
		htable.put("kiran","");
		htable.put("Akiran","");
		htable.put("bkiran","");
		htable.put("akiran","");
		Object decArray[] = sortStringEnumeration(htable.keys(),htable.size());
	}
}
