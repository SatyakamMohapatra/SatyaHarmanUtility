// Package Declaration
package util;

// System Imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.CryptoManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/*
 * @(#)LDAPPropsCreator.java      11/03/2005
 *
 * Copyright � 2004-2005 SymphonyRPM, Inc. or its subsidiaries. All Rights Reserved.
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
 * @version  	:  	$Revision: 1.7 $
 * @author     	:	Manoj Kumar M
 */

/**
 * An utility program to configure the 'ldap.properties' file.
 * This program accepts the data through command line arguments.
 */

public class LDAPPropsCreator {

	private String strLdapFile            = "ldap.properties" ;
	private String strHostName            = "HostName" ;
	private String strPortNumber          = "PortNumber" ;
	private String strSearchBaseForUsers  = "SearchBaseForUsers" ;
	private String strSearchBaseForGroups = "SearchBaseForGroups" ;
	private String strBaseGroup           = "BaseGroup" ;
	private String strBindUserDN          = "BindUser" ;
	private String strBindUserID          = "BindUserID" ;
	private String strBindUserPassword    = "BindUserPassword" ;
	private String strIsBaseGroupUsersReq = "IsUsersOfBaseGroupNeeded" ; 
	private AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,this.getClass().getName());
	
	private File getFile ( ) throws Exception {

		File           ldapFile         = null ;
		String         strPath          = null ;
		String         strPathSeparator = null ;
		boolean        blnFileFound     = false ;
		boolean        blnFirstTry      = true ;

		try {
			logger.info( "\nAbout to configure LDAP properties. Please press <Enter> every time after providing the data." ) ;
			strPathSeparator = System.getProperty ( "file.separator" ) ;
			while ( ! blnFileFound ) {
				if ( blnFirstTry ) {
					System.out.print ( "\nEnter the path of 'ldap.properties' file : " ) ;
					blnFirstTry = false ;
				}
				strPath      = readInput ( ) ;
				if ( ! strPath.endsWith ( strPathSeparator ) ) {
					strPath += strPathSeparator ;
				}
				ldapFile = new File ( strPath + strLdapFile ) ;
				if ( ! ldapFile.exists ( ) ) {
					System.out.print ( "Could not locate the file in path \'"
						+ strPath + "\'.\n\nPlease enter the correct path of 'ldap.properties' file. : " ) ;
				} else {
					logger.info( "\nFound the file. Please provide the following information, to proceed further." ) ;
					blnFileFound = true ;
				}
			}

		} catch ( NegativeArraySizeException exNAS ) {
			ldapFile = null ;
			throw exNAS ;
		} catch ( Exception ex ) {
			ldapFile = null ;
			logger.error("Error in getFile method");
		}

		return ldapFile ;

	}

	private File getFile ( String strPath ) throws Exception {

		File           ldapFile         = null ;
		String         strPathSeparator = null ;
		boolean        blnFileFound     = false ;
		boolean        blnFirstTry      = true ;

		try {
			strPathSeparator = System.getProperty ( "file.separator" ) ;
				if ( ! strPath.endsWith ( strPathSeparator ) ) {
					strPath += strPathSeparator ;
				}
				ldapFile = new File ( strPath + strLdapFile ) ;
				if ( ! ldapFile.exists ( ) ) {
					throw new FileNotFoundException ( "Could not locate file 'ldap.properties' in the given path." ) ;
				}
		} catch ( NegativeArraySizeException exNAS ) {
			ldapFile = null ;
			throw exNAS ;
		} catch ( FileNotFoundException exFNFE ) { 
			ldapFile = null ;
			throw exFNFE ;
	    } catch ( Exception ex ) {
			ldapFile = null ;
			//ex.printStackTrace ( ) ;
			throw ex ;
		}
		return ldapFile ;
	}


	private String readInput ( ) throws Exception {

		String         strData       = null ;
		byte [ ]       size          = new byte [ 512 ] ;
		byte [ ]       data          = null ;
		int            intNoOfData   = -1 ;
		int            intDataSize   = -1 ;

		try {

			//Reading the data.
			intNoOfData  = System.in.read ( size ) ;

			//Cleaning the unwanted data.
			data         = new byte [ intNoOfData ] ;
			for ( int intIndex = 0 ; intIndex < intNoOfData ; intIndex ++ ) {
				data [ intIndex ] = size [ intIndex ] ;
			}

			//Creating the string
			strData      = new String ( data ) ;
			intDataSize  = strData.length ( ) ;
			strData      = strData.substring ( 0, ( intDataSize - 2 ) ) ;

		} catch ( NegativeArraySizeException exNAS ) {
			throw exNAS ;
		} catch ( Exception ex ) {
			logger.error("Error in readInput method");
		}

		return strData ;

	}

	public void configureLDAPProperties ( ) {

		CryptoManager  cryptManager  = null ;
		File           ldapPropFile  = null ;
		Properties     prop          = null ;
		String [ ] [ ] strConditions = { { strHostName, "\nEnter the host name where the directory resides. : ", "No" },
										 { strPortNumber, "\nEnter the port number at which the directory listens. : ", "No" },
										 { strSearchBaseForUsers, "\nEnter the search base for users.\ne.g. if users belongs to an organization unit 'HR' in a domain 'ABC.COM',\nthen search base would be 'OU=HR, DC=ABC, DC=COM'. : ", "No" },
										 { strSearchBaseForGroups, "\nEnter the search base for groups.\ne.g. if groups belongs to an organization unit 'HR' in a domain 'ABC.COM',\nthen search base would be 'OU=HR, DC=ABC, DC=COM'. : ", "No" },
										 { strBaseGroup, "\nEnter the root group. : ", "No" },
										 { strBindUserDN, "\nEnter the admin user's distinguish name.\ne.g. if a admin user 'John Doe' belongs to an organization unit 'HR' in a domain 'ABC.COM',\nthen his distinguished name would be 'CN=John Doe, OU=HR, DC=ABC, DC=COM'. : ", "No" },
										 { strBindUserID, "\nEnter the admin user id. : ", "No" },
										 { strBindUserPassword, "\nEnter the admin user password. : ", "Yes" },
										 { strIsBaseGroupUsersReq, "\nIs base group users need, enter yes / no. : ", "No" } } ;
		String         strData       = null ;

		try {
			//ldapPropFile    = new File ( strLdapFile ) ;
			ldapPropFile    = getFile ( ) ;
			cryptManager    = new CryptoManager ( ) ;

			if ( ldapPropFile != null ) {
				prop            = new Properties ( ) ;
				prop.load ( new FileInputStream ( ldapPropFile ) ) ;

				for ( int intFirstIndex = 0 ; intFirstIndex < 8 ; intFirstIndex ++ ) {
					//Asking the user to enter data.
					System.out.print ( strConditions [ intFirstIndex ] [ 1 ] ) ;
					strData = readInput ( ) ;

					if ( strData != null ) {
						//Checking whether encryption is required or not
						if ( strConditions [ intFirstIndex ] [ 2 ].equalsIgnoreCase ( "Yes" ) ) {
							if ( strData.length ( ) > 0 ) {
								prop.put ( strConditions [ intFirstIndex ] [ 0 ], cryptManager.encrypt ( strData ) ) ;
							} else {
								prop.put ( strConditions [ intFirstIndex ] [ 0 ], strData ) ;
							}

						} else {
							prop.put ( strConditions [ intFirstIndex ] [ 0 ], strData )  ;
						}
						if ( ( strData.length ( ) > 0 ) && intFirstIndex == 4 ) {
							prop.put ( strConditions [ 8 ] [ 0 ], "Yes" )  ;
						} else if ( ( strData.length ( ) == 0 ) && intFirstIndex == 4 ) {
							prop.put ( strConditions [ 8 ] [ 0 ], "No" )  ;
						}
					}
				}

				//Writing to the property file.
				prop.store ( new FileOutputStream ( ldapPropFile ), "Directory Services Information" ) ;

				logger.info( "\nSuccesfully configured the LDAP properties." ) ;
			}

		} catch ( NegativeArraySizeException exNAS ) {
			logger.error( "\n\nIllegal termination of process. Could not configure LDAP properties." ) ;
		} catch ( Exception ex ) {
			logger.error("Error while configuring LDAP Properties");
		}

	}
	
	public void configureLDAPProperties ( HashMap hmpValues, String strFilePath ) {
		CryptoManager  cryptManager  = null ;
		File           ldapPropFile  = null ;
		Properties     prop          = null ;
		String         strRootGroup  = null ;
		String         strPassword   = null ;

		try {
			ldapPropFile    = getFile ( strFilePath ) ;
			cryptManager    = new CryptoManager ( ) ;

			if ( ldapPropFile != null ) {
				prop            = new Properties ( ) ;
				prop.load ( new FileInputStream ( ldapPropFile ) ) ;

				prop.put ( strHostName, 
					( String ) hmpValues.get ( strHostName ) ) ;
				prop.put ( strPortNumber, 
					( String ) hmpValues.get ( strPortNumber ) ) ;
				prop.put ( strSearchBaseForUsers, 
					( String ) hmpValues.get ( strSearchBaseForUsers ) ) ;
				prop.put ( strSearchBaseForGroups, 
					( String ) hmpValues.get ( strSearchBaseForGroups ) ) ;
				
				strRootGroup = ( String ) hmpValues.get ( strBaseGroup ) ; 
				prop.put ( strBaseGroup, strRootGroup ) ;
				
				prop.put ( strBindUserDN, 
					( String ) hmpValues.get ( strBindUserDN ) ) ;
				prop.put ( strBindUserID, 
					( String ) hmpValues.get ( strBindUserID ) ) ;
					
				strPassword = ( String ) hmpValues.get ( strBindUserPassword ) ;
				if ( strPassword != null && ( strPassword.length ( ) > 0 ) ) {
					prop.put ( strBindUserPassword, 
						cryptManager.encrypt ( strPassword ) ) ;
				} else {
					prop.put ( strBindUserPassword, strPassword ) ;
				}
										

				if ( strRootGroup.length ( ) > 0  ) {
					prop.put ( strIsBaseGroupUsersReq, "Yes" )  ;
				} else if ( strRootGroup.length ( ) == 0 ) {
					prop.put ( strIsBaseGroupUsersReq, "No" )  ;
				}

				//Writing to the property file.
				prop.store ( new FileOutputStream ( ldapPropFile ), "Directory Services Information" ) ;

				logger.info( "\nSuccesfully configured the LDAP properties." ) ;
			}

		} catch ( NegativeArraySizeException exNAS ) {
			logger.error( "\n\nIllegal termination of process. Could not configure LDAP properties." ) ;
		} catch ( Exception ex ) {
			logger.error( "\n\nIllegal termination of process. Could not configure LDAP properties." ) ;
		}
		
	}
	
	public HashMap getPropertiesForDisplay ( String strFilePath ) {
		CryptoManager  cryptManager  = null ;
		File           ldapPropFile  = null ;
		HashMap        hmpValues     = null ;
		Properties     prop          = null ;
		String         strRootGroup  = null ;
		String         strPassword   = null ;

		try {
			ldapPropFile    = getFile ( strFilePath ) ;
			cryptManager    = new CryptoManager ( ) ;

			if ( ldapPropFile != null ) {
				prop        = new Properties ( ) ;
				hmpValues   = new HashMap ( ) ;
				prop.load ( new FileInputStream ( ldapPropFile ) ) ;

				hmpValues.put ( strHostName, 
					( String ) prop.get ( strHostName ) ) ;
				hmpValues.put ( strPortNumber, 
					( String ) prop.get ( strPortNumber ) ) ;
				hmpValues.put ( strSearchBaseForUsers, 
					( String ) prop.get ( strSearchBaseForUsers ) ) ;
				hmpValues.put ( strSearchBaseForGroups, 
					( String ) prop.get ( strSearchBaseForGroups ) ) ;
				
				if ( prop.get ( strBaseGroup ) == null ) {
					strRootGroup = "" ;
				} else {
					strRootGroup = ( String ) prop.get ( strBaseGroup ) ;	
				}
				 
				
				hmpValues.put ( strBaseGroup, strRootGroup ) ;
				
				hmpValues.put ( strBindUserDN, 
					( String ) prop.get ( strBindUserDN ) ) ;
				hmpValues.put ( strBindUserID, 
					( String ) prop.get ( strBindUserID ) ) ;
					
				strPassword = ( String ) prop.get ( strBindUserPassword ) ;
				if ( strPassword != null && ( strPassword.length ( ) > 0 ) ) {
					hmpValues.put ( strBindUserPassword, 
						cryptManager.decrypt ( strPassword ) ) ;
				} else {
					hmpValues.put ( strBindUserPassword, strPassword ) ;
				}
										
				if ( strRootGroup.length ( ) > 0  ) {
					hmpValues.put ( strIsBaseGroupUsersReq, "Yes" )  ;
				} else if ( strRootGroup.length ( ) == 0 ) {
					hmpValues.put ( strIsBaseGroupUsersReq, "No" )  ;
				}
			}

		} catch ( NegativeArraySizeException exNAS ) {
			logger.error("Error in getPropertiesForDisplay method");
		} catch ( Exception ex ) {
			logger.error("Error in getPropertiesForDisplay method");
		}
		return hmpValues ;
	}

	public static void main ( String [ ] args ) {

		LDAPPropsCreator info = new LDAPPropsCreator ( ) ;
		info.configureLDAPProperties ( ) ;

	}
}