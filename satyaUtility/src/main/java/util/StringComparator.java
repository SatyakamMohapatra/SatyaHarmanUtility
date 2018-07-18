/*
 * @(#)StringComparator.java	03/16/2003
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
 * @version  		:  	3.2
 * @author     		:	Simi
 * @reviewed by 	:
 * @date reviewed 	:	
 */

package util;

import java.util.Comparator;

public class StringComparator implements Comparator {

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		if (o1 != null && o2 != null) {
			String s1= (String)o1;
			String s2= (String)o2;
			retVal = (s1).compareToIgnoreCase(s2);
		}
       return retVal;
	}
	
	public boolean equals(Object o){
		return super.equals(o);
	}     
}
