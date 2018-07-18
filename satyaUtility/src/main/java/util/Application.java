/*
 *
 * Copyright � 2003-2004 SymphonyRPM, Inc. or its subsidiaries. All Rights Reserved.
 *
 * This software (the Software) is supplied under a license agreement
 * entered into with SymphonyRPM, Inc. The Software may only be used or disclosed in
 * accordance with the terms of such agreement. The Software is confidential and
 * proprietary to SymphonyRPM, Inc. and is protected by the terms of such license
 * agreement, copyright law, patent law and other intellectual property law.
 * No part of this Software may be reproduced, transmitted, or translated in any form
 * or by any means, electronic, mechanical, manual, optical, or otherwise, without
 * the prior written permission of SymphonyRPM, Inc. SymphonyRPM, Inc. reserves all
 * copyrights, trademarks, patent rights, trade secrets and all other intellectual
 * property rights in the Software.
 *
 * OTHER THAN THE TERMS OF THE LICENSE UNDER WHICH THIS SOFTWARE WAS SUPPLIED,
 * SYMPHONYRPM, INC. MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
 * SYMPHONYRPM, INC. SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.

 *
 * @version  	:  	
 * @author     	:	sdas
 */
package util;

import com.symphonyrpm.applayer.common.constants.AdminConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;

/**
 * @author sdas
 *
 * This class stored the RPM application details e.g. Current version,
 *  RPM Adapter version, Database currently running on
 * etc. This singleton class would be updated by SystemStartup class
 * during application initialization.
 */
public class Application 
{
	private static Application rpmApp = null ;
	private static String RPMVersion = null ;
	private static String RPMAdapterVersion = null ;
	private static String Database = null ;
	private static String configDirectory = null ;

	private Application()
	{
		
	}

	public static synchronized Application getInstance()
	{
		if (rpmApp == null)
			rpmApp = new Application() ;
		return rpmApp ;			
	}
	/**
	 * @return
	 */
	public String getConfigDirectory() {
		return configDirectory;
	}

	/**
	 * @return
	 */
	public String getDatabase() 
	{
		return ConfigManager.getInstance().getProperty(
			ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,
			ConfigurationConstants.DATABASE_VENDOR_PROP);
	}

	/**
	 * @return
	 */
	public String getRPMAdapterVersion() {
		return RPMAdapterVersion;
	}

	/**
	 * @return
	 */
	public String getRPMVersion() {
		return RPMVersion;
	}

	/**
	 * @param string
	 */
	public void setConfigDirectory(String string) {
		configDirectory = string;
	}

	/**
	 * @param string
	 */
	public void setDatabase(String string) {
		Database = string;
	}

	/**
	 * @param string
	 */
	public void setRPMAdapterVersion(String string) {
		RPMAdapterVersion = string;
	}

	/**
	 * @param string
	 */
	public void setRPMVersion(String string) {
		RPMVersion = string;
	}

	

	/**
	 * @return
	 */
	public static String getLDAPProperty() 
	{
		return ConfigManager.getInstance().getProperty(
			ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,
			AdminConstants.IS_LDAP_ENABLED);
	}

}
