/*
 * @(#)URLRead.java      03/04/2003
 *
 * Copyright � 2002-2003 SymphonyRPM, Inc. or its subsidiaries. All Rights Reserved.
 * This software (the "Software") is supplied under a license agreement entered into with SymphonyRPM, Inc.
 * The Software may only be used or disclosed in accordance with the terms of such agreement.
 * The Software is confidential and proprietary to SymphonyRPM, Inc. and
 * is protected by the terms of such license agreement, copyright law, patent law
 * and other intellectual property law. No part of this Software may be reproduced, transmitted,
 * or translated in any form or by any means, electronic, mechanical, manual, optical, or otherwise,
 * without the prior written permission of SymphonyRPM, Inc. SymphonyRPM, Inc. reserves all copyrights,
 * trademarks, patent rights, trade secrets and all other intellectual property rights in the Software.
 * OTHER THAN THE TERMS OF THE LICENSE UNDER WHICH THIS SOFTWARE WAS SUPPLIED, SYMPHONYRPM, INC.
 * MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SYMPHONYRPM, INC. SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS
 * A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version  	:  	3.2
 * @author     	:	Pyeush Gurha,rbhat
 * @reviewed by 	:
 * @date reviewed :
 */

//Package name
package util;

//System packages
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.GenericException;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/**
 * This class performs a socket i/o operation
 * It reads the html content through sockets
 */
public class URLRead
{
    private AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,this.getClass().getName());

   /**
		  * get html content from socket
		  * @param request  holds all the request details
		  * @param response holds all the response details
		  * @param UrlString holds the url address from where
		  *         the socket read should happen
		  * @param _tempstring output string
		  * @return None
		  * @exception Exception
	  */
	public String getcontent(HttpServletRequest request ,HttpServletResponse response, String UrlString,String _tempstring)
				throws GenericException
	{
		try
		{
			logger.debug("URLRead UrlString received from dashboard_viewpage = "+UrlString);
			URL url = new URL(UrlString);
			HttpURLConnection connect= (HttpURLConnection)url.openConnection();
	 		if (request != null) 
            {    
            	String strContentLength = ""+request.getContentLength();
				connect.setRequestMethod("POST");
				//Setting the content length in the POST header
        	    connect.setRequestProperty ("Content-Length",strContentLength );
            	connect.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
				
				Enumeration headerFields = request.getHeaderNames();
				String headerName = null ;
				String headerValue = null ;
				if (headerFields != null)
				{
					while (headerFields.hasMoreElements())
					{
			  			headerName = (String) headerFields.nextElement();
			  			headerValue = request.getHeader (headerName);			  			
			  			if(!headerName.equals("Content-Length"))
			  			{
			  				connect.setRequestProperty (headerName,headerValue );
							logger.debug("   "+ headerName + " : " + headerValue);
    	                }       	
					}
				}			
			}
						
			// printing the request content
			Enumeration reqParamNames = request.getParameterNames();
			while(reqParamNames.hasMoreElements()){
				String str = (String) reqParamNames.nextElement();
				logger.debug("URLRead param name = "+str);
				logger.debug("URLRead param value = "+request.getParameter(str)+"--");
			}

			logger.debug("URLRead request content length = "+request.getContentLength());
			logger.debug("URLRead request character encoding = "+request.getCharacterEncoding());
			logger.debug("URLRead request context path = "+request.getContextPath());			

			connect.setDoOutput(true);
			connect.setDoInput(true);
			connect.connect();
			InputStream content = connect.getInputStream();
		  	DataInputStream input = input = new DataInputStream(content);
			StringBuffer sOutput = new StringBuffer();
			byte buf[] = new byte[100000];
			int output = 0;
			while((output = input.read(buf)) >= 0)
			{
		    	sOutput.append(new String(buf,0,output));
    		}
			_tempstring = sOutput.toString();
			connect.disconnect();
		}
		catch(MalformedURLException mex)
		{
			logger.error("MalformedURLException in getContent()",mex);
			String message = "Reports could not be loaded. " ;
			throw new GenericException(mex,message,IModules.SERVER);
		}
		catch(IOException iox)
		{
			logger.error("IOException in getContent()",iox);
			String message = "Reports could not be loaded. " ;
			throw new GenericException(iox,message,IModules.SERVER);
		}
		catch(Exception ex)
		{
			logger.error("Exception in getContent()",ex);
			String message = "Reports could not be loaded. " ;
			throw new GenericException(ex,message,IModules.SERVER);
		}
		    return _tempstring;
		}
		
	/**
	 * Get html content from socket as a GZipStream
	  * @param request  holds all the request details
	  * @param response holds all the response details
	  * @param UrlString holds the url address from where
	  *         the socket read should happen
	  * @param _tempstring output string
	  * @return None
	  * @exception Exception
	 */		
	public String getcontentFromZipStream(HttpServletRequest request ,HttpServletResponse response, String UrlString,String _tempstring)
	throws GenericException {
		try
		{
			logger.debug("URLRead UrlString received from dashboard_viewpage = "+UrlString);
			URL url = new URL(UrlString);
			HttpURLConnection connect= (HttpURLConnection)url.openConnection();
	 		if (request != null)
	        {
	        	String strContentLength = ""+request.getContentLength();
				connect.setRequestMethod("POST");
				//Setting the content length in the POST header
	    	    connect.setRequestProperty ("Content-Length",strContentLength );
	        	connect.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	
				Enumeration headerFields = request.getHeaderNames();
				String headerName = null ;
				String headerValue = null ;
				if (headerFields != null)
				{
					while (headerFields.hasMoreElements())
					{
			  			headerName = (String) headerFields.nextElement();
			  			headerValue = request.getHeader (headerName);
			  			if(!headerName.equals("Content-Length"))
			  			{
			  				connect.setRequestProperty (headerName,headerValue );
							logger.debug("   "+ headerName + " : " + headerValue);
		                }
					}
				}
			}
	
			// printing the request content
			Enumeration reqParamNames = request.getParameterNames();
			while(reqParamNames.hasMoreElements()){
				String str = (String) reqParamNames.nextElement();
				logger.debug("URLRead param name = "+str);
				logger.debug("URLRead param value = "+request.getParameter(str)+"--");
			}
	
			logger.debug("URLRead request content length = "+request.getContentLength());
			logger.debug("URLRead request character encoding = "+request.getCharacterEncoding());
			logger.debug("URLRead request context path = "+request.getContextPath());
	
			connect.setDoOutput(true);
			connect.setDoInput(true);
			connect.connect();
			InputStream content = connect.getInputStream();
			InputStream input = null;
			try {
				String compressFlag = ConfigManager.getInstance().getProperty(CommonConstants.SYS_PROPS_FILE,ConfigurationConstants.IS_COMPRESSION_FILTER_ON);
				if(compressFlag != null && compressFlag.trim().equalsIgnoreCase("false")) {
					input = new DataInputStream(content);
				} else {
					input = new GZIPInputStream(content);	
				}
					
			} catch (IOException e) {
				//logger.debug("IOException occured = ",e);  
				if(e.getMessage().trim().equalsIgnoreCase("Not in GZIP format")) {
					logger.debug("Getting the data from data input stream .... ");
					input = new DataInputStream(content);
				}
			}
			StringBuffer sOutput = new StringBuffer();
			byte buf[] = new byte[100000];
			int output = 0;
			while(input != null && (output = input.read(buf)) >= 0)
			{
		    	sOutput.append(new String(buf,0,output));
			}
			_tempstring = sOutput.toString();
	        //logger.debug("_tempstring = " + _tempstring);
			connect.disconnect();
	        }
	        catch(MalformedURLException mex)
	        {
	            logger.error("MalformedURLException in getContent()",mex);
	            String message = "Reports could not be loaded. " ;
	            throw new GenericException(mex,message,IModules.SERVER);
	        }
	        catch(IOException iox)
	        {
	            logger.error("IOException in getContent()",iox);
	            String message = "Reports could not be loaded. " ;
	            throw new GenericException(iox,message,IModules.SERVER);
	        }
	        catch(Exception ex)
	        {
	            logger.error("Exception in getContent()",ex);
	            String message = "Reports could not be loaded. " ;
	            throw new GenericException(ex,message,IModules.SERVER);
	        }
	        return _tempstring;
	}		

}// eof class