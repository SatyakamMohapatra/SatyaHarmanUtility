// Package declaration
package util;

// System Imports
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.symphonyrpm.applayer.common.constants.AdminConstants;
import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.CryptoManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/*
 * @(#)LDAPPolicyFileUpdater.java      02/15/2006
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
 * @version  	:  	$Revision: 1.5 $
 * @author     	:	Manoj Kumar M
 */

/**
 * An utility program to configure the 'ldap.pol' file.
 */
public class LDAPPolicyFileUpdater {

	/** Logger. */
	private AppLinkLogger   logger              =
			LogManager.getLogger ( IModules.SERVER, LDAPPolicyFileUpdater.class.getName ( ) ) ;   
	
	private ArrayList       alDomains           = null ;
	private Document        document            = null ;
	private InputStream     inputStream         = null ;
	private File            ldapPolFile         = null ;
	private String          strFileSeparator    = null ;
	private String          strConfigFolderPath = null ;
	private String          strLDAPPolFile      = null ;
	
	private HashMap         hmpDirectoryAttrs   = null ;
	private HashMap 		hmpBindCredentials  = null ;
	private HashMap 		hmpADMapAttrs       = null ;
	private HashMap 		hmpTivoliMapAttrs   = null ;
	private HashMap 		hmpADUserAttrs      = null ;
	private HashMap 		hmpTivoliUserAttrs  = null ;
	
	private void initializeDirectoryAttrs ( ) {

		try {
			hmpDirectoryAttrs  = new HashMap ( 1 ) ;	
			
			hmpDirectoryAttrs.put ( "DIR_NAME", "name" ) ;
			
			hmpBindCredentials = new HashMap ( 4 ) ;
			hmpBindCredentials.put ( "BIND_ROOT_TAG", "bind_credentials" ) ;
			hmpBindCredentials.put ( "BIND_USER_DN", "bind_user_dn" ) ;
			hmpBindCredentials.put ( "BIND_USER_ID", "bind_user_id" ) ;
			hmpBindCredentials.put ( "BIND_USER_PASSWORD", "bind_user_password" ) ;
			
			hmpADMapAttrs      = new HashMap ( 6 ) ;
			hmpADMapAttrs.put ( "distinguishedname", "distinguishedname" ) ;
			hmpADMapAttrs.put ( "description", "description" ) ;
			hmpADMapAttrs.put ( "commonname", "cn" ) ;
			hmpADMapAttrs.put ( "name", "name" ) ;
			hmpADMapAttrs.put ( "modifieddate", "whenChanged" ) ;
			hmpADMapAttrs.put ( "canonicalName", "canonicalname" ) ;	
			
			hmpTivoliMapAttrs  = new HashMap ( 6 ) ;
			hmpTivoliMapAttrs.put ( "distinguishedname", "distinguishedname" ) ;
			hmpTivoliMapAttrs.put ( "description", "description" ) ;
			hmpTivoliMapAttrs.put ( "commonname", "cn" ) ;
			hmpTivoliMapAttrs.put ( "name", "cn" ) ;
			hmpTivoliMapAttrs.put ( "modifieddate", "modifytimestamp" ) ;
			hmpTivoliMapAttrs.put ( "canonicalName", "canonicalname" ) ;
			
			hmpADUserAttrs     = new HashMap ( 8 ) ;
			hmpADUserAttrs.put ( "first_name", "givenname" ) ;
			hmpADUserAttrs.put ( "last_name", "sn" ) ;
			hmpADUserAttrs.put ( "title", "title" ) ;
			hmpADUserAttrs.put ( "emailid", "mail" ) ;
			hmpADUserAttrs.put ( "department", "department" ) ;
			hmpADUserAttrs.put ( "mobile", "mobile" ) ;
			hmpADUserAttrs.put ( "telephonenumber", "telephoneNumber" ) ;
			hmpADUserAttrs.put ( "manager", "manager" ) ;
		
			hmpTivoliUserAttrs = new HashMap ( 8 ) ;
			hmpTivoliUserAttrs.put ( "first_name", "givenname" ) ;
			hmpTivoliUserAttrs.put ( "last_name", "sn" ) ;
			hmpTivoliUserAttrs.put ( "title", "title" ) ;
			hmpTivoliUserAttrs.put ( "emailid", "mail" ) ;
			hmpTivoliUserAttrs.put ( "department", "departmentnumber" ) ;
			hmpTivoliUserAttrs.put ( "mobile", "mobile" ) ;
			hmpTivoliUserAttrs.put ( "telephonenumber", "telephoneNumber" ) ;
			hmpTivoliUserAttrs.put ( "manager", "manager" ) ;

		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
	}
	
	public LDAPPolicyFileUpdater ( ) throws FileNotFoundException {
		try {
			// Initializing the directory attributes.
			initializeDirectoryAttrs ( ) ;
			
			strFileSeparator    = System.getProperty ( "file.separator" ) ;
			strConfigFolderPath = System.getProperty ( 
				ConfigurationConstants.DIRECTORY_PROPERTY_NAME ) ;
			if ( ! strConfigFolderPath.endsWith ( strFileSeparator ) ) {
				strConfigFolderPath += strFileSeparator ; 
			}
			strLDAPPolFile      = 
				strConfigFolderPath + AdminConstants.LDAP_POL_FILE_FOLDER 
				+ strFileSeparator + AdminConstants.LDAP_POL_FILE_NAME ;
			ldapPolFile         = new File ( strLDAPPolFile ) ;
			if ( ! ldapPolFile.exists ( ) ) {
				throw new FileNotFoundException ( "Could not locate ldap.pol file." ) ;
			}
			document            = getXMLDocument ( getContentOfPolFile ( ) ) ;
			loadAllDomains ( ) ;
		} catch ( FileNotFoundException exFNFE ) {
			throw exFNFE ;
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
 
	}
	
	private void loadAllDomains ( ) {
		NamedNodeMap nodeMap             = null ; 
		Node         rootNode            = null ;
		Node         ldapPolNode         = null ;
		Node         childNode           = null ;
		NodeList     ldapPolNodeList     = null ;
		NodeList     ldapPolChildList    = null ;
		int          intNoOfLDAPPolicies = 0 ;
		int          intNoOfChilds       = 0 ;
		try {
			rootNode  = document.getElementsByTagName ( 
				AdminConstants.LDAP_POL_FILE_SECURITY_POL_TAG ).item ( 0 ) ;
			ldapPolNodeList     = rootNode.getChildNodes ( ) ;
			intNoOfLDAPPolicies = ldapPolNodeList.getLength ( ) ;
			alDomains = new ArrayList ( intNoOfLDAPPolicies ) ;
			for ( int intPolIndex = 0 ; 
				intPolIndex < intNoOfLDAPPolicies ; intPolIndex ++ ) {
				ldapPolNode     = ldapPolNodeList.item ( intPolIndex ) ;
				if ( ldapPolNode.getNodeName ( ).equalsIgnoreCase ( 
					AdminConstants.LDAP_POL_FILE_LDAP_POL_TAG ) ) {
					ldapPolChildList = ldapPolNode.getChildNodes ( ) ;
					intNoOfChilds    = ldapPolChildList.getLength ( ) ;
					for ( int intChildIndex = 0 ; 
						intChildIndex < intNoOfChilds ; intChildIndex ++ ) {
						childNode    = ldapPolChildList.item ( intChildIndex ) ;
						if ( childNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_DIRECTORY_TAG ) ) {
							nodeMap  = childNode.getAttributes ( ) ;
							alDomains.add ( nodeMap.getNamedItem ( 
								AdminConstants.LDAP_POL_FILE_DOMAIN_ATTRIBUTE )
								.getNodeValue ( ) ) ;
						}
					}
				} 
			}
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
	}
	
	public ArrayList getDomains ( ) {
		return alDomains ;
	}
	
	public HashMap getBindUserCredentials ( String strDomainName ) {
		CryptoManager cryptManager        = null ;
		HashMap       hmpBindCredentials  = null ;
		NamedNodeMap  nodeMap             = null ; 
		Node          rootNode            = null ;
		Node          ldapPolNode         = null ;
		Node          childNode           = null ;
		Node          credentialChildNode = null ;
		NodeList      ldapPolNodeList     = null ;
		NodeList      ldapPolChildList    = null ;
		NodeList      credentialsList     = null ;
		String        strVendorName       = "" ;     
		String        strTempPassword     = null ;
		boolean       blnValidDomain      = false ;
		int           intNoOfLDAPPolicies = 0 ;
		int           intNoOfChilds       = 0 ;
		int           intNoOfCredentials  = 0 ;
		try {
			cryptManager        = new CryptoManager ( ) ;
			hmpBindCredentials  = new HashMap ( ) ;
			rootNode  = document.getElementsByTagName ( 
				AdminConstants.LDAP_POL_FILE_SECURITY_POL_TAG ).item ( 0 ) ;
			ldapPolNodeList     = rootNode.getChildNodes ( ) ;
			intNoOfLDAPPolicies = ldapPolNodeList.getLength ( ) ;
			for ( int intPolIndex = 0 ; 
				intPolIndex < intNoOfLDAPPolicies ; intPolIndex ++ ) {
				ldapPolNode     = ldapPolNodeList.item ( intPolIndex ) ;
				if ( ldapPolNode.getNodeName ( ).equalsIgnoreCase ( 
					AdminConstants.LDAP_POL_FILE_LDAP_POL_TAG ) ) {
					ldapPolChildList = ldapPolNode.getChildNodes ( ) ;
					intNoOfChilds    = ldapPolChildList.getLength ( ) ;
					for ( int intChildIndex = 0 ; 
						intChildIndex < intNoOfChilds ; intChildIndex ++ ) {
						childNode    = ldapPolChildList.item ( intChildIndex ) ;
						if ( childNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_DIRECTORY_TAG ) ) {
							nodeMap  = childNode.getAttributes ( ) ;
							if ( nodeMap.getNamedItem ( 
								AdminConstants.LDAP_POL_FILE_DOMAIN_ATTRIBUTE )
								.getNodeValue ( ).equalsIgnoreCase ( strDomainName ) ) {
								blnValidDomain = true ;		
							}
							if ( blnValidDomain && ( nodeMap.getNamedItem ( 
								AdminConstants.LDAP_POL_FILE_NAME_ATTRIBUTE ) != null ) ) {
								strVendorName = nodeMap.getNamedItem ( 
									AdminConstants.LDAP_POL_FILE_NAME_ATTRIBUTE ).getNodeValue ( ) ;
								intPolIndex   = intNoOfLDAPPolicies ;
							}
						}
						if ( blnValidDomain == true && ( 
							childNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_BIND_CREDENTIALS_TAG ) ) ) {
							credentialsList    = childNode.getChildNodes ( ) ;
							intNoOfCredentials = credentialsList.getLength ( ) ; 
							for ( int intIndex = 0 ; 
								intIndex < intNoOfCredentials ; intIndex ++ ) {
								credentialChildNode = credentialsList.item ( intIndex ) ; 
								if ( credentialChildNode.getNodeName ( ).equalsIgnoreCase ( 
									AdminConstants.LDAP_POL_FILE_BIND_USER_DN_TAG ) ) {
									hmpBindCredentials.put ( "BindUser", 
										credentialChildNode.getChildNodes ( ).item ( 0 ).getNodeValue ( ) ) ;
								} else if ( credentialChildNode.getNodeName ( ).equalsIgnoreCase ( 
									AdminConstants.LDAP_POL_FILE_BIND_USER_ID_TAG ) ) {
									hmpBindCredentials.put ( "BindUserID", 
										credentialChildNode.getChildNodes ( ).item ( 0 ).getNodeValue ( ) ) ;
								} else if ( credentialChildNode.getNodeName ( ).equalsIgnoreCase ( 
									AdminConstants.LDAP_POL_FILE_BIND_USER_PWD_TAG ) ) {
									strTempPassword = 
										credentialChildNode.getChildNodes ( ).item ( 0 ).getNodeValue ( ) ;
									if ( strTempPassword != null ) {
										strTempPassword = cryptManager.decrypt ( strTempPassword ) ;		 	
									}
									hmpBindCredentials.put ( "BindUserPassword", strTempPassword ) ;
								}
							}
						}
						hmpBindCredentials.put ( "Vendor", strVendorName ) ;
					}
				} 
			}
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
		return hmpBindCredentials ;
	}
	
	/**
	 * Constructs the XML <code>Document</code> from the passed in 
	 * <code>String</code>.
	 * 
	 * @param byteArrData the <code>byte [ ]</code> from which the <code>Document</code> has to be created.
	 * @return document the XML <code>Document</code>.
	 * @see String#getBytes()
	 * @see DocumentBuilderFactory#newInstance()
	 * @see DocumentBuilderFactory#newDocumentBuilder()
	 * @see DocumentBuilder#parse(ByteArrayInputStream)
	 */
	private Document getXMLDocument ( byte [ ]  byteArrData ) {
		ByteArrayInputStream   fileInput         = null ;
		DocumentBuilderFactory docBuilderFactory = null ;
		DocumentBuilder        docBuilder        = null ;
		Document               document          = null ;
		try {
			if ( byteArrData != null ) {
				docBuilderFactory = DocumentBuilderFactory.newInstance ( ) ;
				docBuilder        = docBuilderFactory.newDocumentBuilder ( ) ;
				fileInput         = new ByteArrayInputStream ( byteArrData ) ;
				document          = docBuilder.parse ( fileInput ) ;
			}
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;			
		}
		return document ;
	}
	
	private byte [ ] getContentOfPolFile ( ) throws IOException {
		byte [ ] byteArrData = null ;
		int      intOffset   = 0 ;
		int      intNumRead  = 0 ;
		long     lngFileSize = -1 ;
		try {
			inputStream = new FileInputStream ( ldapPolFile ) ;
			lngFileSize = ldapPolFile.length ( ) ;
			if ( lngFileSize > Integer.MAX_VALUE ) {
				throw new IOException ( "ldap.pol file is too large." ) ;
			}
			byteArrData = new byte [ ( int ) lngFileSize ] ;	
			while ( intOffset < byteArrData.length
				&& ( intNumRead = inputStream.read ( byteArrData, intOffset, 
				byteArrData.length-intOffset ) ) >= 0 ) {
					intOffset += intNumRead ;
			}
			if ( intOffset < byteArrData.length ) {
				throw new IOException ( "Could not completely read ldap.pol file." ) ;
			}
		} catch ( IOException exIOE ) { 
			throw exIOE ;
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		} finally {
			inputStream.close ( ) ;
		}
		return byteArrData ;
	}
	
	public void updateLDAPProperties ( HashMap hmpValues ) {
		NamedNodeMap directoryAttrs     = null ;
		Node         rootNode           = null ;
		Node         ldapNode           = null ;
		Node         ldapChildNode      = null ;
		Node         tempNode           = null ;
		NodeList     directoryList      = null ;
		NodeList     ldapList           = null ;
		String       strVendorName      = null ;
		int          intNoOfDirectories = 0 ;
		int          intNoOfLdapChilds  = 0 ;
		try {
			rootNode           = document.getElementsByTagName ( 
				AdminConstants.LDAP_POL_FILE_SECURITY_POL_TAG ).item ( 0 ) ;
			directoryList      = rootNode.getChildNodes ( ) ;
			intNoOfDirectories = directoryList.getLength ( ) ;
			strVendorName      = ( String ) hmpValues.get ( "Vendor" ) ;
			for ( int intDirIndex = 0 ; 
				intDirIndex < intNoOfDirectories ; intDirIndex ++ ) {
				ldapNode       = directoryList.item ( intDirIndex ) ;
				if ( ldapNode.getNodeName ( ).equalsIgnoreCase ( 
					AdminConstants.LDAP_POL_FILE_LDAP_POL_TAG ) ) {
					ldapList   = ldapNode.getChildNodes ( ) ;
					intNoOfLdapChilds = ldapList.getLength ( ) ;
					for ( int intLdapIndex = 0 ; 
						intLdapIndex < intNoOfLdapChilds ; intLdapIndex ++ ) {
						ldapChildNode = ldapList.item ( intLdapIndex ) ; 
						if ( ldapChildNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_DIRECTORY_TAG ) ) {
							directoryAttrs = ldapChildNode.getAttributes ( ) ;
							if ( directoryAttrs.getNamedItem ( 
								AdminConstants.LDAP_POL_FILE_DOMAIN_ATTRIBUTE ).getNodeValue ( )
								.equalsIgnoreCase ( ( String ) hmpValues.get ( "DomainName" ) ) ) {
								tempNode   = document.createAttribute ( 
									( String ) hmpDirectoryAttrs.get ( "DIR_NAME" ) ) ;
								tempNode.setNodeValue ( strVendorName ) ;
								directoryAttrs.setNamedItem ( tempNode ) ;
								if ( strVendorName.equalsIgnoreCase ( "Active Directory" ) ) {
									addADAttributes ( ldapList ) ;
								} else if ( strVendorName.equalsIgnoreCase ( "Tivoli Directory" ) ) {
									addTivoliAttributes ( ldapList ) ;
								}
								setBindUserCredentials ( hmpValues ) ;
							}
						}
					}
				}
			}
			new FileOutputStream ( ldapPolFile ).write ( 
				XmlUtility.getFormattedXMLString ( document ).getBytes ( ) ) ;
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
	}
	
	private void setBindUserCredentials ( HashMap hmpValues ) {
		CryptoManager cryptManager         = null ;
		NamedNodeMap  directoryAttrs       = null ;
		NamedNodeMap  ldapAttrs            = null ;
		Node          rootNode             = null ;
		Node          ldapPolNode          = null ;
		Node          childNode            = null ;
		Element       bindCredentials      = null ;
		Element       bindUserDNElement    = null ;
		Element       bindUserIDElement    = null ;
		Element       bindUserPwdElement   = null ;
		Text          bindUserDN           = null ;
		Text          bindUserID           = null ;
		Text          bindUserPassword     = null ;
		NodeList      ldapPolNodeList      = null ;
		NodeList      ldapPolChildList     = null ;
		boolean       blnValidDomain       = false ;
		int           intNoOfLDAPPolicies  = 0 ;
		int           intNoOfChilds        = 0 ;
		try {
			cryptManager       = new CryptoManager ( ) ;
			bindCredentials    = document.createElement ( "bind_credentials" ) ;
			bindUserDNElement  = document.createElement ( "bind_user_dn" ) ;
			bindUserIDElement  = document.createElement ( "bind_user_id" ) ;
			bindUserPwdElement = document.createElement ( "bind_user_password" ) ;
			bindUserDN         = document.createTextNode ( "" ) ;
			bindUserID         = document.createTextNode ( "" ) ;
			bindUserPassword   = document.createTextNode ( "" ) ;
			rootNode  = document.getElementsByTagName ( 
				AdminConstants.LDAP_POL_FILE_SECURITY_POL_TAG ).item ( 0 ) ;
			ldapPolNodeList     = rootNode.getChildNodes ( ) ;
			intNoOfLDAPPolicies = ldapPolNodeList.getLength ( ) ;
			for ( int intPolIndex = 0 ; 
				intPolIndex < intNoOfLDAPPolicies ; intPolIndex ++ ) {
				ldapPolNode     = ldapPolNodeList.item ( intPolIndex ) ;
				if ( ldapPolNode.getNodeName ( ).equalsIgnoreCase ( 
					AdminConstants.LDAP_POL_FILE_LDAP_POL_TAG ) ) {
					ldapPolChildList = ldapPolNode.getChildNodes ( ) ;
					for ( int intChildIndex = 0 ; 
						intChildIndex < ldapPolChildList.getLength ( ) ; intChildIndex ++ ) {
						childNode    = ldapPolChildList.item ( intChildIndex ) ;
						if ( childNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_DIRECTORY_TAG ) ) {
							directoryAttrs = childNode.getAttributes ( ) ;
							if ( directoryAttrs.getNamedItem ( 
								AdminConstants.LDAP_POL_FILE_DOMAIN_ATTRIBUTE )
								.getNodeValue ( ).equals ( ( String ) 
								hmpValues.get ( "DomainName" ) ) ) {
								blnValidDomain = true ;			
							}
						}
						if ( blnValidDomain && childNode.getNodeName ( )
							.equalsIgnoreCase ( "bind_credentials" ) ) {
							ldapPolNode.removeChild ( childNode ) ;		
							intChildIndex -- ;
						}
					}
					if ( blnValidDomain ) {
						bindUserDN.setNodeValue ( ( String ) hmpValues.get ( "BindUser" ) ) ;
						bindUserID.setNodeValue ( ( String ) hmpValues.get ( "BindUserID" ) ) ;
						bindUserPassword.setNodeValue ( 
							cryptManager.encrypt ( ( String ) hmpValues.get ( "BindUserPassword" ) ) ) ;
						bindUserDNElement.appendChild ( bindUserDN ) ;
						bindUserIDElement.appendChild ( bindUserID ) ;
						bindUserPwdElement.appendChild ( bindUserPassword ) ;	
						bindCredentials.appendChild ( bindUserDNElement ) ;
						bindCredentials.appendChild ( bindUserIDElement ) ;
						bindCredentials.appendChild ( bindUserPwdElement ) ;
						ldapPolNode.appendChild ( bindCredentials ) ;
						break ;
					}
				}
			}
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
	}
	
	private void addADAttributes ( NodeList childList ) {
		NamedNodeMap entityAttrs          = null ; 
		NamedNodeMap ldapAttrs            = null ;
		Node         childNode            = null ;
		Node         schemaChildNode      = null ;
		Node         entityNode           = null ;
		Node         fnNode               = null ;
		Node         lnNode               = null ;
		Node         titleNode            = null ;
		Node         emailNode            = null ;
		Node         departmentNode       = null ;
		Node         mobileNode           = null ;
		Node         telephoneNode        = null ;
		Node         managerNode          = null ;
		Node         ldapAttrNode         = null ;
		Element      dnNode               = null ;
		Element      descriptionNode      = null ;
		Element      cnNode               = null ;
		Element      nameNode             = null ;
		Element      modifiedDateNode     = null ;
		Element      canonicalNameNode    = null ;
		NodeList     schemaChilds         = null ;
		NodeList     entitiesChilds       = null ;
		NodeList     attrMapChilds        = null ;
		int          intNoOfChilds        = 0 ;
		int          intNoOfSchemaChilds  = 0 ;
		int          intNoOfEntities      = 0 ;
		int          intNoOfAttrMapChilds = 0 ;
		try {
			fnNode            = document.createAttribute ( "first_name" ) ;
			lnNode            = document.createAttribute ( "last_name" ) ;
			titleNode         = document.createAttribute ( "title" ) ;
			emailNode         = document.createAttribute ( "emailid" ) ;
			departmentNode    = document.createAttribute ( "department" ) ;
			mobileNode        = document.createAttribute ( "mobile" ) ;
			telephoneNode     = document.createAttribute ( "telephonenumber" ) ;
			managerNode       = document.createAttribute ( "manager" ) ;
			
			dnNode            = document.createElement ( "ldap_attribute" ) ;
			descriptionNode   = document.createElement ( "ldap_attribute" ) ;
			cnNode            = document.createElement ( "ldap_attribute" ) ;
			nameNode          = document.createElement ( "ldap_attribute" ) ;
			modifiedDateNode  = document.createElement ( "ldap_attribute" ) ;
			canonicalNameNode = document.createElement ( "ldap_attribute" ) ;
			
			dnNode.setAttribute ( "name", "distinguishedname" ) ;
			descriptionNode.setAttribute ( "name", "description" ) ;
			cnNode.setAttribute ( "name", "commonname" ) ;
			nameNode.setAttribute ( "name", "name" ) ;
			modifiedDateNode.setAttribute ( "name", "modifieddate" ) ;
			canonicalNameNode.setAttribute ( "name", "canonicalName" ) ;
						
			intNoOfChilds  = childList.getLength ( ) ;
			for ( int intIndex = 0 ; intIndex < intNoOfChilds ; intIndex ++ ) {
				childNode = childList.item ( intIndex ) ;
				if ( childNode.getNodeName ( ).equalsIgnoreCase ( 
					AdminConstants.LDAP_POL_FILE_SCHEMA_TAG ) ) {
					schemaChilds = childNode.getChildNodes ( ) ;
					intNoOfSchemaChilds = schemaChilds.getLength ( ) ;
					for ( int intSchemaIndex = 0 ; 
						intSchemaIndex < intNoOfSchemaChilds ; intSchemaIndex ++ ) {
						schemaChildNode = schemaChilds.item ( intSchemaIndex ) ;
						if ( schemaChildNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_ENTITIES_TAG ) ) {
							entitiesChilds  = schemaChildNode.getChildNodes ( ) ;
							intNoOfEntities = entitiesChilds.getLength ( ) ;
							for ( int intEntityIndex = 0 ; 
								intEntityIndex < intNoOfEntities ; intEntityIndex ++ ) {
								entityNode  = entitiesChilds.item ( intEntityIndex ) ;
								entityAttrs = entityNode.getAttributes ( ) ;
								if ( entityAttrs != null ) {
									if ( entityAttrs.getNamedItem ( 
										AdminConstants.LDAP_POL_FILE_NAME_ATTRIBUTE )
										.getNodeValue ( ).equalsIgnoreCase ( "user" ) ) {
										fnNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "first_name" ) ) ;
										entityAttrs.setNamedItem ( fnNode ) ;
										lnNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "last_name" ) ) ;
										entityAttrs.setNamedItem ( lnNode ) ;
										titleNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "title" ) ) ;
										entityAttrs.setNamedItem ( titleNode ) ;
										emailNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "emailid" ) ) ;
										entityAttrs.setNamedItem ( emailNode ) ;
										departmentNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "department" ) ) ;
										entityAttrs.setNamedItem ( departmentNode ) ;
										mobileNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "mobile" ) ) ;
										entityAttrs.setNamedItem ( mobileNode ) ;
										telephoneNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "telephonenumber" ) ) ;
										entityAttrs.setNamedItem ( telephoneNode ) ;
										managerNode.setNodeValue ( 
											( String ) hmpADUserAttrs.get ( "manager" ) ) ;
										entityAttrs.setNamedItem ( managerNode ) ;
									}
								}
							}
						}
						if ( schemaChildNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_ATTRIBUTE_MAPPING_TAG ) ) {
							dnNode.setAttribute ( "lookup", 
								( String ) hmpADMapAttrs.get ( "distinguishedname" ) ) ;
							descriptionNode.setAttribute ( "lookup", 
								( String ) hmpADMapAttrs.get ( "description" ) ) ;
							cnNode.setAttribute ( "lookup", 
								( String ) hmpADMapAttrs.get ( "commonname" ) ) ;
							nameNode.setAttribute ( "lookup", 
								( String ) hmpADMapAttrs.get ( "name" ) ) ;
							modifiedDateNode.setAttribute ( "lookup", 
								( String ) hmpADMapAttrs.get ( "modifieddate" ) ) ;
							canonicalNameNode.setAttribute ( "lookup", 
								( String ) hmpADMapAttrs.get ( "canonicalName" ) ) ;
							
							attrMapChilds        = schemaChildNode.getChildNodes ( ) ;
							for ( int intMapIndex = 0 ; 
								intMapIndex < attrMapChilds.getLength ( ) ; intMapIndex ++ ) {
								ldapAttrNode = attrMapChilds.item ( intMapIndex ) ;
								if ( ldapAttrNode != null ) {
									ldapAttrs    = ldapAttrNode.getAttributes ( ) ;
									if ( ldapAttrs != null ) {
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "distinguishedname" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}								
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "description" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "commonname" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "name" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "modifieddate" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "canonicalName" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
									}
								}
							}
							schemaChildNode.appendChild ( dnNode ) ;
							schemaChildNode.appendChild ( descriptionNode ) ;
							schemaChildNode.appendChild ( cnNode ) ;
							schemaChildNode.appendChild ( nameNode ) ;
							schemaChildNode.appendChild ( modifiedDateNode ) ;
							schemaChildNode.appendChild ( canonicalNameNode ) ;
						}
					}
				}
			}
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
	}
	
	private void addTivoliAttributes ( NodeList childList ) {
		NamedNodeMap entityAttrs          = null ; 
		NamedNodeMap ldapAttrs            = null ;
		Node         childNode            = null ;
		Node         schemaChildNode      = null ;
		Node         entityNode           = null ;
		Node         fnNode               = null ;
		Node         lnNode               = null ;
		Node         titleNode            = null ;
		Node         emailNode            = null ;
		Node         departmentNode       = null ;
		Node         mobileNode           = null ;
		Node         telephoneNode        = null ;
		Node         managerNode          = null ;
		Node         ldapAttrNode         = null ;
		Element      dnNode               = null ;
		Element      descriptionNode      = null ;
		Element      cnNode               = null ;
		Element      nameNode             = null ;
		Element      modifiedDateNode     = null ;
		Element      canonicalNameNode    = null ;
		NodeList     schemaChilds         = null ;
		NodeList     entitiesChilds       = null ;
		NodeList     attrMapChilds        = null ;
		int          intNoOfChilds        = 0 ;
		int          intNoOfSchemaChilds  = 0 ;
		int          intNoOfEntities      = 0 ;
		int          intNoOfAttrMapChilds = 0 ;
		try {
			fnNode            = document.createAttribute ( "first_name" ) ;
			lnNode            = document.createAttribute ( "last_name" ) ;
			titleNode         = document.createAttribute ( "title" ) ;
			emailNode         = document.createAttribute ( "emailid" ) ;
			departmentNode    = document.createAttribute ( "department" ) ;
			mobileNode        = document.createAttribute ( "mobile" ) ;
			telephoneNode     = document.createAttribute ( "telephonenumber" ) ;
			managerNode       = document.createAttribute ( "manager" ) ;
			
			dnNode            = document.createElement ( "ldap_attribute" ) ;
			descriptionNode   = document.createElement ( "ldap_attribute" ) ;
			cnNode            = document.createElement ( "ldap_attribute" ) ;
			nameNode          = document.createElement ( "ldap_attribute" ) ;
			modifiedDateNode  = document.createElement ( "ldap_attribute" ) ;
			canonicalNameNode = document.createElement ( "ldap_attribute" ) ;
			
			dnNode.setAttribute ( "name", "distinguishedname" ) ;
			descriptionNode.setAttribute ( "name", "description" ) ;
			cnNode.setAttribute ( "name", "commonname" ) ;
			nameNode.setAttribute ( "name", "name" ) ;
			modifiedDateNode.setAttribute ( "name", "modifieddate" ) ;
			canonicalNameNode.setAttribute ( "name", "canonicalName" ) ;
						
			intNoOfChilds  = childList.getLength ( ) ;
			for ( int intIndex = 0 ; intIndex < intNoOfChilds ; intIndex ++ ) {
				childNode = childList.item ( intIndex ) ;
				if ( childNode.getNodeName ( ).equalsIgnoreCase ( 
					AdminConstants.LDAP_POL_FILE_SCHEMA_TAG ) ) {
					schemaChilds = childNode.getChildNodes ( ) ;
					intNoOfSchemaChilds = schemaChilds.getLength ( ) ;
					for ( int intSchemaIndex = 0 ; 
						intSchemaIndex < intNoOfSchemaChilds ; intSchemaIndex ++ ) {
						schemaChildNode = schemaChilds.item ( intSchemaIndex ) ;
						if ( schemaChildNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_ENTITIES_TAG ) ) {
							entitiesChilds  = schemaChildNode.getChildNodes ( ) ;
							intNoOfEntities = entitiesChilds.getLength ( ) ;
							for ( int intEntityIndex = 0 ; 
								intEntityIndex < intNoOfEntities ; intEntityIndex ++ ) {
								entityNode  = entitiesChilds.item ( intEntityIndex ) ;
								entityAttrs = entityNode.getAttributes ( ) ;
								if ( entityAttrs != null ) {
									if ( entityAttrs.getNamedItem ( 
										AdminConstants.LDAP_POL_FILE_NAME_ATTRIBUTE )
										.getNodeValue ( ).equalsIgnoreCase ( "user" ) ) {
										fnNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "first_name" ) ) ;
										entityAttrs.setNamedItem ( fnNode ) ;
										lnNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "last_name" ) ) ;
										entityAttrs.setNamedItem ( lnNode ) ;
										titleNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "title" ) ) ;
										entityAttrs.setNamedItem ( titleNode ) ;
										emailNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "emailid" ) ) ;
										entityAttrs.setNamedItem ( emailNode ) ;
										departmentNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "department" ) ) ;
										entityAttrs.setNamedItem ( departmentNode ) ;
										mobileNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "mobile" ) ) ;
										entityAttrs.setNamedItem ( mobileNode ) ;
										telephoneNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "telephonenumber" ) ) ;
										entityAttrs.setNamedItem ( telephoneNode ) ;
										managerNode.setNodeValue ( 
											( String ) hmpTivoliUserAttrs.get ( "manager" ) ) ;
										entityAttrs.setNamedItem ( managerNode ) ;
									}
								}
							}
						}
						if ( schemaChildNode.getNodeName ( ).equalsIgnoreCase ( 
							AdminConstants.LDAP_POL_FILE_ATTRIBUTE_MAPPING_TAG ) ) {
							dnNode.setAttribute ( "lookup", 
								( String ) hmpTivoliMapAttrs.get ( "distinguishedname" ) ) ;
							descriptionNode.setAttribute ( "lookup", 
								( String ) hmpTivoliMapAttrs.get ( "description" ) ) ;
							cnNode.setAttribute ( "lookup", 
								( String ) hmpTivoliMapAttrs.get ( "commonname" ) ) ;
							nameNode.setAttribute ( "lookup", 
								( String ) hmpTivoliMapAttrs.get ( "name" ) ) ;
							modifiedDateNode.setAttribute ( "lookup", 
								( String ) hmpTivoliMapAttrs.get ( "modifieddate" ) ) ;
							canonicalNameNode.setAttribute ( "lookup", 
								( String ) hmpTivoliMapAttrs.get ( "canonicalName" ) ) ;
							
							attrMapChilds        = schemaChildNode.getChildNodes ( ) ;
							for ( int intMapIndex = 0 ; 
								intMapIndex < attrMapChilds.getLength ( ) ; intMapIndex ++ ) {
								ldapAttrNode = attrMapChilds.item ( intMapIndex ) ;
								ldapAttrs    = ldapAttrNode.getAttributes ( ) ;
								if ( ldapAttrNode != null ) {
									if ( ldapAttrs != null ) {
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "distinguishedname" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "description" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "commonname" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "name" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "modifieddate" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
										if ( ldapAttrs.getNamedItem ( "name" ).getNodeValue ( )
											.equalsIgnoreCase ( "canonicalName" ) ) {
											schemaChildNode.removeChild ( ldapAttrNode ) ;
											intMapIndex -- ;
										}
									}
								}
							}
							schemaChildNode.appendChild ( dnNode ) ;
							schemaChildNode.appendChild ( descriptionNode ) ;
							schemaChildNode.appendChild ( cnNode ) ;
							schemaChildNode.appendChild ( nameNode ) ;
							schemaChildNode.appendChild ( modifiedDateNode ) ;
							schemaChildNode.appendChild ( canonicalNameNode ) ;
						}
					}
				}
			}
		} catch ( Exception ex ) {
			logger.fatal ( ex ) ;
		}
	}

}
