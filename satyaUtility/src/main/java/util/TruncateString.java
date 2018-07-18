/*
 * @(#) TruncateString.java      01/04/2003
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
 * @version			:	3.2
 * @author			:   Abhinav Shekhar
 * @reviewed by		:
 * @date reviewed	:
 */


package util;



/**
 * Utility class for string truncation
 * @version  	:  	1
 * @author   	:	Abhinav Shekhar
 */
public class TruncateString
{
	/**
	 * Returns the given string truncated to the specified number of characters
	 * @return String the truncated string
	 */
	public static String getTruncatedString(String str,int maxdisplaylen)
	{
		String truncatedstring = str;
	    if(str.length()>maxdisplaylen)
	    {
		truncatedstring = str.substring(0,maxdisplaylen)+"..";
	    }
	    return truncatedstring;
	} 
}   
