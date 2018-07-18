/*
 * @(#)HttpAuthenticator      Apr 9, 2001
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

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * @author kkumar
 *
 *Class Description 
 */
public class HttpAuthenticator extends Authenticator
{
	String userName;
	String password;
	char[] passwordArray;
	
	public HttpAuthenticator(String usrName,String password)
	{
		this.userName = usrName;
		this.password = password;
		passwordArray = password.toCharArray();
		Authenticator.setDefault(this);
	}
	protected PasswordAuthentication getPasswordAuthentication()
	{
		PasswordAuthentication passwordAuth = new PasswordAuthentication(userName,passwordArray);
		return passwordAuth;
	}
	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the userName.
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the userName.
	 * @param userName The userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
