package util;

/*
 * @(#)Compressor.java      05/07/2003 
 * 
 * Copyright � 2002-2003 SymphonyRPM, Inc. or its subsidiaries. All Rights Reserved.
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
 * @version  	:  	$Revision: 1.3 $
 * @author     :	Sachin Keshavan P
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;

public class Compressor 
{
		
	/**
	 * Compresses a string, and returns a byte array
	 * 
	 * @param source a String containing data to be compressed
	 * @return byte[] which is in zipped format
	 */
	public static byte[] compressIt(String source)
	{
		try
		{
			boolean  isUtf8EncodingEnabled = ConfigManager.getInstance().getPropertyAsBoolean(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, ConfigurationConstants.IS_UTF8_ENCODING_ENABLED, false);
			byte[] sourceArr = null;
			if(source == null)
				return null;
			ByteArrayOutputStream bis = new ByteArrayOutputStream();
			if(isUtf8EncodingEnabled){
				sourceArr = source.getBytes("UTF-8");
			}
			else{
				sourceArr = source.getBytes();
			}

			source.getBytes();
			GZIPOutputStream gOut = new GZIPOutputStream(bis);
			gOut.write(sourceArr,0,sourceArr.length);
			gOut.finish();
			byte[] outputArr = bis.toByteArray();
			gOut.close();
			bis.close();
			return outputArr;
		}
		catch(IOException ioe)
		{
			return null;	
		}
	}
	
	/**
	 * Uncompresses a string array containing compressed
	 * data and returns a String
	 * 
	 * @param source a byte[] in zipped format
	 * @return the uncompressed data as String
	 */
	public static String unCompressIt(byte[] source)
	{
		try
		{
			if(source == null)
				return null;

			boolean  isUtf8EncodingEnabled = ConfigManager.getInstance().getPropertyAsBoolean(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, ConfigurationConstants.IS_UTF8_ENCODING_ENABLED, false);
			ByteArrayInputStream bis = new ByteArrayInputStream(source);
			GZIPInputStream gOut = new GZIPInputStream(bis);			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();		
			int maxLength = 0;
			String returnString="";
			while(gOut.available()==1) 
			{
				byte[] uncompressedArr = new byte[1024];
				int read = gOut.read(uncompressedArr,0,uncompressedArr.length);
				if(read!=-1)bout.write(uncompressedArr,0,read);
			}
			if(isUtf8EncodingEnabled){
				returnString = bout.toString("UTF-8"); 	
			}
			else{
				returnString = bout.toString(); 	
			}

			bout.close();
			gOut.close();
			bis.close();
			return returnString;
		}
		catch(IOException ioe)
		{
			return null;	
		}			
	}
	
	public static void main(String args[])
	{
		String test = "Symphony RPM, the best in the world";
	/*	System.out.println(test.getBytes().length);
		System.out.println(Compressor.compressIt(test).length);		
		System.out.println(Compressor.unCompressIt(Compressor.compressIt(test)));*/
	}
}//Compressor ends
