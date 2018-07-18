/*
* @(#)LogLinkUtil.java  02/05/2013
*
* Copyright (c) 2012 Symphony Services. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Symphony Services, ("Confidential Information"). You shall not
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
* @version  :
* @author   : Shobha Nand Das
*/
package util;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogLinkInfo;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.coreservices.jdbc.LogLinkInfoFactory;
import com.symphonyrpm.applayer.common.util.id.GUIDGenerator;

/**
 * @author sdas02
 *
 */
public class LogLinkUtil {
	
	LogLinkInfo logLinkInfo = null;
	
	private static AppLinkLogger  logger = LogManager.getLogger (IModules.SERVER, "LogLinkUtil");
	/**
	 * 
	 */
	public LogLinkUtil(){
		logLinkInfo = LogLinkInfoFactory.getInstance().getLogLinkInfo();
	}
	
	/**
	 * 
	 * @param reportDTO
	 */
	public String generateLogLinkID(String reportId){
		String logLinkId = "";
		if(reportId != null){		
			logLinkId = new GUIDGenerator().getId().toString();
			logLinkInfo.getLogLinkMap().put(reportId, logLinkId);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Generated logLinkID: " + logLinkId + " for reportId: " + reportId);
		}
		return logLinkId;
	}
	
	/**
	 * 
	 * @param reportId
	 * @return
	 */
	public String getLogLinkID(String reportId){
		String logLinkId = null;
		if(reportId != null) {
			logLinkId = (String)logLinkInfo.getLogLinkMap().get(reportId);
		}
//		if(logger.isDebugEnabled()) {
//			logger.debug("Get logLinkID: " + logLinkId + " from Map for reportId: " + reportId);
//		}
		return logLinkId;
	}
	
	/**
	 * 
	 * @param reportId
	 */
	public void clearLogLinkID(String reportId){
		logLinkInfo.getLogLinkMap().remove(reportId);
	}	
}
