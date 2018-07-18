package util;

/*
 * @(#)RequestParser.java      07/24/2002
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
 * @version    1.0
 * @author     skesha@symphserv.com
 */

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
	
public class RequestParser
{
	/**
	 * Constructor
	 * @param void
	 * @return void
	 */
	public RequestParser()
	{
		
	}
	

	/**
	 * Parses and HTTP Request Object and creates a HashMap with the keys and values
	 * The key taken from the request, would get converted to UPPER CASE before
	 * it gets added to the HashMap.
	 * @param request:HttpServletRequest
	 * @return HashMap
	 */
	public static HashMap parse(HttpServletRequest request)
	{
		
		HashMap hashMap = new HashMap();
		Enumeration requestKeys = request.getParameterNames();
		while(requestKeys.hasMoreElements())
		{
			String singleKey = (String)requestKeys.nextElement();
			//get the value of it from the request object
			String singleValue = (String)request.getParameter(singleKey);
			
			//convert the singleKey to the upper case just to maintain uniformity
			singleKey = singleKey.toUpperCase();
			if(singleValue!=null) hashMap.put(singleKey,singleValue);
		}
		return hashMap;
	}

}//class ends

