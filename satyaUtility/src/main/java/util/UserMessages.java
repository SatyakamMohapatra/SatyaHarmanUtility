/*
 * @(#)UserMessages.java      Jan 13, 2003
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
 * @author     Kiran Kumar
 */


package util;

import java.io.Serializable;
import java.util.ArrayList;

public class UserMessages implements Serializable{

	private ArrayList errors;
	private ArrayList warnings;
	private ArrayList messages;	

	private boolean hasMessages;
	
	/**
	 * @see Object#Object()
	 */
	public UserMessages()
	{
		errors = new ArrayList();
		warnings = new ArrayList();
		messages = new ArrayList();
	}
	
	/**
	 * Method addError.
	 * @param message
	 */
	public void addError(String message)
	{
		hasMessages = true;
		errors.add(message);
	}

	/**
	 * Method addExceptionMessage.
	 * @param t
	 */
	public void addException(Throwable t)
	{
		hasMessages = true;
		errors.add(t.getMessage());
	}
	
	/**
	 * Method addWarning.
	 * @param message
	 */
	public void addWarning(String message)
	{
		hasMessages = true;
		warnings.add(message);
	}
	
	/**
	 * Method addMessage.
	 * @param message
	 */
	public void addMessage(String message)
	{
		hasMessages = true;
		messages.add(message);	
	}
	
	/**
	 * Method getErrors.
	 * @return ArrayList
	 */
	public ArrayList getErrors()
	{
		return errors;
	}

	/**
	 * Method getWarnings.
	 * @return ArrayList
	 */
	public ArrayList getWarnings()
	{
		return warnings;
	}
	
	/**
	 * Method getMessages.
	 * @return ArrayList
	 */
	public ArrayList getMessages()
	{
		return messages;
	}
	public static void main(String[] args) {

	}

	/**
	 * Returns the hasMessages.
	 * @return boolean
	 */
	public boolean hasMessages() {
		return hasMessages;
	}


}
