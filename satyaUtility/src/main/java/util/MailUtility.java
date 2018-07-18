/*
 * @(#)MailUtility.java      Jun 16, 2003
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

import static com.symphonyrpm.applayer.common.constants.CommonConstants.APPLICATION_CONTEXT_PATH;
import static com.symphonyrpm.applayer.common.constants.CommonConstants.COMMON_BUNDLE;
import static com.symphonyrpm.applayer.common.constants.CommonConstants.ENCODING;
import static com.symphonyrpm.applayer.common.constants.CommonConstants.SCHEDULER_SUCCESS_MSG_ATTACHMENT;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.RENDRER_PROPERTIES_FILE_NAME;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.RPM_PROPERTIES_FILE_NAME;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.SCHEDULER_DATE_FORMAT;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.SPECIAL_CHARACTER_REPLACEWITH;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.WEB_URL;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.WORKSPACECOMMON_PROPERTIES_FILE_NAME;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.ADMIN_MAIL_ID;
import static com.symphonyrpm.applayer.common.constants.MailConstants.ADMIN_MAIL_PASSWORD;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.ERROR_MESSAGE_ATTACHMNET_FILE_NAME;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.ERROR_MESSAGE_EMAIL_ATTACHMENT_OPTION;
import static com.symphonyrpm.applayer.common.constants.MailConstants.IS_EMAIL_AUTHENTICATION_REQUIRED;
import static com.symphonyrpm.applayer.common.constants.MailConstants.MAIL_CONTENT_TYPE;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.SCHEDULER_ADMIN_MAIL_ID_LIST;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.SCHEDULER_ERROR_NOTIFICATION_MODE;
import static com.symphonyrpm.applayer.common.constants.MailConstants.SMTP_AUTH_KEY;
import static com.symphonyrpm.applayer.common.constants.ConfigurationConstants.SMTP_HOST;
import static com.symphonyrpm.applayer.common.constants.MailConstants.SMTP_HOST_KEY;
import static com.symphonyrpm.applayer.common.constants.MailConstants.SMTP_PORT;
import static com.symphonyrpm.applayer.common.constants.MailConstants.SMTP_PORT_KEY;
import static com.symphonyrpm.applayer.common.constants.MailConstants.SMTP_SSL_TRUST_KEY;
import static com.symphonyrpm.applayer.common.constants.MailConstants.SMTP_TTLS_ENABLE_KEY;
import static com.symphonyrpm.applayer.common.constants.MailConstants.TRUE;
import static com.symphonyrpm.applayer.common.constants.WorkspaceConstants.WORKSPACE_COMMON_PROPERTIES_FILE_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.CryptoManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;
import com.symphonyrpm.applayer.common.i18n.I18NManager;

/**
 *This class is a utility class used to send mails
 */
public class MailUtility {
	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,MailUtility.class.getName());
	private static String mailHost;
	private static ConfigManager configManager = ConfigManager.getInstance();
	private static int emailAttachmentOption =  configManager.getPropertyAsInt(WORKSPACECOMMON_PROPERTIES_FILE_NAME, ERROR_MESSAGE_EMAIL_ATTACHMENT_OPTION, 1);
	private static String errorAttachmentFileName = configManager.getPropertyAsString(WORKSPACECOMMON_PROPERTIES_FILE_NAME, ERROR_MESSAGE_ATTACHMNET_FILE_NAME, "ErrorMessage.txt");
	private static int errorEmailOption =  configManager.getPropertyAsInt(WORKSPACECOMMON_PROPERTIES_FILE_NAME, SCHEDULER_ERROR_NOTIFICATION_MODE, 3);
	private static String schedulerErrorNotificationEmailAddresseListStr = configManager.getPropertyAsString(WORKSPACE_COMMON_PROPERTIES_FILE_NAME, SCHEDULER_ADMIN_MAIL_ID_LIST, "");
	private static final boolean isAuthenticationRequired = configManager.getPropertyAsBoolean(RPM_PROPERTIES_FILE_NAME, IS_EMAIL_AUTHENTICATION_REQUIRED, false);
	private static final String adminMailId = configManager.getProperty(RPM_PROPERTIES_FILE_NAME, ADMIN_MAIL_ID);
	private static final String adminMailPassword = ConfigManager.getInstance().getProperty(RPM_PROPERTIES_FILE_NAME, ADMIN_MAIL_PASSWORD);
	private static final String smtpPort = configManager.getProperty(SYSTEM_PROPERTIES_FILE_NAME, SMTP_PORT);
	private static List<String> adminMailList = new ArrayList<>();

	static {
		ConfigManager confManager = ConfigManager.getInstance();
		mailHost = confManager.getProperty(SYSTEM_PROPERTIES_FILE_NAME, SMTP_HOST);	
	}

	public static void sendMail(String to, String from, Collection ccList, String subject, String body) throws Exception {
		Session session = getSession();
		if (session != null) {
			session.setDebug(false);
			MimeMessage msg = new MimeMessage(session);
			if (isAuthenticationRequired)
				msg.setFrom( new InternetAddress(adminMailId));
			else
				msg.setFrom(new InternetAddress(from));
			
			msg.setSubject(subject, ENCODING);
			msg.setContent(body, MAIL_CONTENT_TYPE);
			msg.setSentDate(new Date());
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			int ccListSize = 0;
			if (ccList != null && (ccListSize = ccList.size()) > 0) {
				InternetAddress ccListArray[] = new InternetAddress[ccListSize];
				int i = 0;
				Iterator ccListIterator = ccList.iterator();
				while (ccListIterator.hasNext()) {
					String address = (String) ccListIterator.next();
					ccListArray[i] = new InternetAddress(address);
					i++;
				}
				msg.addRecipients(Message.RecipientType.CC, ccListArray);
			}
			try {
				Transport.send(msg);
				logger.info("Mail send successfully to " + to);
			} catch (SendFailedException se) {
				logger.error("Unable to send mail to " + to + " and error is " + se.getMessage(), se);
				Address address[] = se.getValidUnsentAddresses();
				if (address != null && address.length > 0) {
					Transport.send(msg, address);
				}
			}
		} 
	}

	public static void sendMail(String from, Collection toList, String subject, String body) throws Exception {
		Session session = getSession();
		if (session != null) {
			session.setDebug(false);
			MimeMessage msg = new MimeMessage(session);
			if (isAuthenticationRequired)
				msg.setFrom( new InternetAddress(adminMailId));
			else
				msg.setFrom(new InternetAddress(from));
			msg.setSubject(subject, ENCODING);
			msg.setContent(body, MAIL_CONTENT_TYPE);
			msg.setSentDate(new Date());
			int toListSize = 0;
			if (toList != null && (toListSize = toList.size()) > 0) {
				InternetAddress ccListArray[] = new InternetAddress[toListSize];
				int i = 0;
				Iterator toListIterator = toList.iterator();
				while (toListIterator.hasNext()) {
					String address = (String) toListIterator.next();
					ccListArray[i] = new InternetAddress(address);
					i++;
				}
				msg.addRecipients(Message.RecipientType.TO, ccListArray);
			}
			try {
				Transport.send(msg);
			} catch (SendFailedException se) {
				Address address[] = se.getValidUnsentAddresses();
				if (address != null && address.length > 0) {
					Transport.send(msg, address);
				}
			}
		}
	}

	/**
	 * Formats the mail message
	 * @param message String
	 * @return String
	 */
	public static String formatMessage(String message) {
		char c = '\0';
		StringBuffer tempBuffer = new StringBuffer();
		String mailMessage = null;
		if (message == null ) {
			mailMessage = "" + "\n";
		} else {
			mailMessage = message + "\n";
		}

		for (int i = 0; i < mailMessage.length(); ++i) {
			c = mailMessage.charAt(i);
			if (c == '\n')
				tempBuffer.append("<br>");
			else if(c=='\t')
				tempBuffer.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			else
				tempBuffer.append(c);
		}

		return tempBuffer.toString();
	}
	public static String createSuccessMsgMailBody(int type,String reportName, String modelName,String schedulerName,String workspaceOwner,String orgName,String fileName){
		return createSuccessMsgMailBody( type, reportName,  modelName, schedulerName, workspaceOwner, orgName, fileName,null,"");
	}
	public static String createSuccessMsgMailBody(int type,String reportName, String modelName,String schedulerName,String workspaceOwner,String orgName,String fileName,String body,String workspaceName){
		I18NManager i18nMng = I18NManager.getInstance();
		ConfigManager configManager = ConfigManager.getInstance();
		if(body == null){
			body = i18nMng.getValue(COMMON_BUNDLE, i18nMng.getApplicationLocale(),SCHEDULER_SUCCESS_MSG_ATTACHMENT);	
		}
		String dateFormat =	configManager.getProperty(RPM_PROPERTIES_FILE_NAME,
				SCHEDULER_DATE_FORMAT);
		Date date = new Date();
		SimpleDateFormat dtFormat = DateFormatter.getLocaleDateFormat(dateFormat);
		String strDate = dtFormat.format(date);
		if(type == 0){
			body = body.replace("<<Type>>", "Report");
		}else{
			body = body.replace("<<Type>>", "Workspace");
		}
		reportName = EscapeChars.forHTML(reportName);
		if (reportName != null) {
			body = body.replace("<<Report Name>>", "<b><i>" + reportName + "</i></b>");
		}	

		if (workspaceOwner != null && workspaceOwner.trim().length() > 0) {
			body = body.replace("<<Created By>>", "<b><i>" + workspaceOwner + "</i></b>");
		}
		else {
			body = body.replace("<<Created By>>", "");
		}
		if (workspaceOwner != null && workspaceOwner.trim().length() > 0) {
			body = body.replace("<<Submitted By>>", "<b><i>" + workspaceOwner + "</i></b>");
		}
		else {
			body = body.replace("<<Submitted By>>", "");
		}
		schedulerName = EscapeChars.forHTML(schedulerName);
		if (schedulerName != null && schedulerName.trim().length() > 0) {
			body = body.replace("<<Scheduler>>", "<b><i>" + schedulerName + "</i></b>");
		}
		else {
			body = body.replace("<<Scheduler>>", "");
		}

		String url = configManager.getProperty(SYSTEM_PROPERTIES_FILE_NAME, WEB_URL);
		if (url != null) {
			url += configManager.getProperty(RPM_PROPERTIES_FILE_NAME, APPLICATION_CONTEXT_PATH);
			body = body.replace("<<URL>>", "<b><i>" + url + "</i></b>");
		}

		if (orgName != null && orgName.trim().length() > 0) {
			body = body.replace("<<Organization Name>>", "<b><i>" + orgName + "</i></b>");
		}
		else {
			body = body.replace("<<Organization Name>>", "");
		}

		if(modelName != null && modelName.trim().length() > 0){
			body = body.replace("<<Model>>", "<b><i>" + modelName + "</i></b>");
		}else{
			body = body.replace("of <<Model>>", "");
		}
		if( workspaceName != null){
			body = body.replace("<<Workspace>>", " <b><i>" + workspaceName + "</i></b>");
		}else{
			body = body.replace("<<Workspace>>", "");
		}

		if (strDate != null && strDate.trim().length() > 0) {
			body = body.replace("<<Date>>",strDate);
		}
		fileName = validateFileName(fileName);

		if (fileName != null && fileName.trim().length() > 0) {
			body = body.replace("<<Attachment Name>>", "<b><i>" + EscapeChars.forHTML(fileName) +"</i></b>");
		}
		else {
			body = body.replace("<<Attachment Name>>", EscapeChars.forHTML(reportName));
		}
		return body;
	}
	public static String validateFileName(String fileName) {
		Matcher xlsfilename           = null ;
		Pattern specialcharacters     = null ;
		specialcharacters = Pattern.compile("[\\/:*;?\"<>|]");
		xlsfilename 	  = specialcharacters.matcher(fileName) ;
		fileName 		  = xlsfilename.replaceAll(getCharacterToReplaceWith()) ;	
		return fileName ;
	}
	public static String getCharacterToReplaceWith() {
		String specialcharacterreplacewith = ConfigManager.getInstance().getProperty(RENDRER_PROPERTIES_FILE_NAME, SPECIAL_CHARACTER_REPLACEWITH);
		if(specialcharacterreplacewith == null) {
			specialcharacterreplacewith = "_" ; 
		}
		else if(specialcharacterreplacewith.trim().length() == 0) {
			specialcharacterreplacewith = " " ;
		}
		return specialcharacterreplacewith ;
	}

	public static Vector getErrorMsgAsAttachment(String errorMessage, String scheduledReportDirName){
		BufferedReader reader = null;
		PrintWriter prnt = null;
		/*OrganizationManager orgManager = new OrganizationManager();*/
		try {
			if(errorMessage != null && errorMessage.trim().length() > 2){
				Timestamp timeSt = new Timestamp(new Date().getTime());
				String fileName = validateFileName("ErrorMessage_"+timeSt.toString()+".txt");
				/*String scheduledReportDirName = orgManager.getOrganizationFolder(schldRptDirName);*/
				File errFile = new File(scheduledReportDirName, fileName);;
				if(errFile.exists()){
					errFile.createNewFile();
				}
				prnt = new PrintWriter(new FileOutputStream(errFile),true);
				prnt.write(errorMessage);
				prnt.flush();
				Vector<File> vector=new Vector<File>();
				vector.add(0,errFile);
				return vector;
			}
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException occured while sending error message file attachent");
		} catch (IOException e) {
			logger.error("IOException occured while sending error message file attachent");
		}finally{
			try {
				if(prnt != null){
					prnt.close();
				}
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				logger.error("Error occured while closing the file object");
			}
		}
		return null;

	}
	public static Vector sendErrorMsgAsAttachment(Throwable exception,String errorFileName, String scheduledReportDirName){
		BufferedReader reader = null;
		PrintWriter prnt = null;
		/*OrganizationManager orgManager = new OrganizationManager();*/
		try {
			Timestamp timeSt = new Timestamp(new Date().getTime());
			String fileName = validateFileName("ErrorMessage_"+timeSt.toString()+".txt");
			/*String scheduledReportDirName = orgManager.getOrganizationFolder(schldRptDirName);*/
			File errFile = new File(scheduledReportDirName, fileName);
			if(errFile.exists()){
				errFile.createNewFile();
			}
			File file = null;
			if(errorFileName != null){
				file = new File(scheduledReportDirName, errorFileName);
				if(file.exists()){
					reader = new BufferedReader(new FileReader(file));
					String singleLine = "";
					prnt = new PrintWriter(new FileOutputStream(errFile),true);
					while ((singleLine = reader.readLine()) != null) {
						prnt.write(singleLine);
					}
					prnt.flush();
				}else{
					logger.info("start writing in file");
					prnt = new PrintWriter(new FileOutputStream(errFile),true);
					prnt.flush();
				}
			}else{
				logger.info("start writing in file");
				prnt = new PrintWriter(new FileOutputStream(errFile),true);
				prnt.flush();
			}
			Vector<File> vector=new Vector<File>();
			vector.add(0,errFile);
			return vector;
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException occured while sending error message file attachent");
		} catch (IOException e) {
			logger.error("IOException occured while sending error message file attachent");
		}finally{
			try {
				if(prnt != null){
					prnt.close();
				}
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				logger.error("Error occured while closing the file object");
			}
		}
		return null;
	}
	public static void sendWithAttachments( String aFromEmailAddr,List list,String subject, String message, Vector attach){

		String errorAttachmentFile = errorAttachmentFileName +".txt";
		if (schedulerErrorNotificationEmailAddresseListStr != null) {
			adminMailList = RpmUtil.convertToArrayList(schedulerErrorNotificationEmailAddresseListStr);
		}
		try {
			// 0 � No details
			// 1 � Admin only
			// 2 - Owner only (Default value)
			// 3 � Admin and Owner
			if(errorEmailOption > 0){
				if(errorEmailOption == 1 ){
					if(emailAttachmentOption == 1 || emailAttachmentOption == 3 ){
						sendWithAttachments( aFromEmailAddr, adminMailList, subject, message, attach,errorAttachmentFile);
						logger.info("Scheduler error message email attachment send to "+adminMailList);
					}else{
						sendMail(aFromEmailAddr, adminMailList, subject, message);
						logger.info("Scheduler error message email send to "+adminMailList);
					}
				}else if(errorEmailOption == 2 ){
					if(emailAttachmentOption == 2  || emailAttachmentOption == 3){
						sendWithAttachments( aFromEmailAddr, list, subject, message, attach,errorAttachmentFile);
						logger.info("Scheduler error message email attachment send to "+list);
					}else {
						sendMail(aFromEmailAddr, list, subject, message);
						logger.info("Scheduler error message email send to "+adminMailList);
					}
				}else if(errorEmailOption == 3 ){
					if(emailAttachmentOption == 1){
						try{
							sendWithAttachments( aFromEmailAddr, adminMailList, subject, message, attach,errorAttachmentFile);
							logger.info("Scheduler error message email attachment send to "+adminMailList);
						}catch(Exception e){
							logger.error("Error while sending scheduler error message email to "+adminMailList);
						}
						try {
							sendMail(aFromEmailAddr, list, subject, message);
							logger.info("Scheduler error message email send to "+list);
						} catch (Exception e) {
							logger.error("Error while sending scheduler error message email to "+list);
						}
					}

					if(emailAttachmentOption == 2){
						try{
							sendWithAttachments( aFromEmailAddr, list, subject, message, attach,errorAttachmentFile);
							logger.info("Scheduler error message email attachment send to "+adminMailList);
						}catch(Exception e){
							logger.error("Error while sending scheduler error message email to "+adminMailList);
						}
						try {
							sendMail(aFromEmailAddr, adminMailList, subject, message);
							logger.info("Scheduler error message email send to "+list);
						} catch (Exception e) {
							logger.error("Error while sending scheduler error message email to "+list);
						}
					}

					if(emailAttachmentOption == 3){
						list.addAll(adminMailList);
						sendWithAttachments( aFromEmailAddr, list, subject, message, attach,errorAttachmentFile);
						logger.info("Error while sending scheduler error message email to "+list);
					}
					if(emailAttachmentOption == 0){
						list.addAll(adminMailList);
						sendMail( aFromEmailAddr, list, subject, message);
						logger.info("Error while sending scheduler error message email to "+list);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in sending email");
		}
	}

	/**
	 * @param aFromEmailAddr
	 * @param list
	 * @param subject
	 * @param message
	 * @param attach (vector of all the files )
	 * @throws IOException
	 * @throws AddressException
	 * @throws MessagingException
	 * this method is used to send the mail with attachment(pdf or xls) 
	 */
	public static void sendWithAttachments( String aFromEmailAddr,List list,String subject, String message, Vector attach,String fileNameForDisplay)  throws IOException, AddressException, MessagingException {
		int size = list == null?0:list.size();
		String address = "";
		for( int i = 0 ; i<size ;i++) {
			try {			
				address = (String)list.get(i);
				Message msg = prepareHeader(mailHost, aFromEmailAddr, address, subject);
				if(msg==null){
					continue;
				}
				MimeMultipart mp = new MimeMultipart();

				MimeBodyPart text = new MimeBodyPart();
				text.setDisposition(Part.INLINE);
				text.setContent(message, MAIL_CONTENT_TYPE);
				mp.addBodyPart(text);
				int attach_size = 0;
				if(attach!=null||attach.size()>=0){
					attach_size = attach.size();
				}
				for (int j = 0; j < attach_size; j++) {
					MimeBodyPart file_part = new MimeBodyPart();
					File file = (File) attach.elementAt(j);
					FileDataSource fds = new FileDataSource(file);
					DataHandler dh = new DataHandler(fds);
					//file_part.setFileName(file.getName());
					file_part.setFileName(MimeUtility.encodeText(fileNameForDisplay, ENCODING,"Q"));
					file_part.setDisposition(Part.ATTACHMENT);
					file_part.setDescription("Attached file: " +file.getName());
					file_part.setDataHandler(dh);
					mp.addBodyPart(file_part);
				}
				msg.setContent(mp);
				try {
					Transport.send(msg);
				}
				catch(SendFailedException sfe) {
					Address unsentAddress [] = sfe.getValidUnsentAddresses();				
					if (unsentAddress != null && unsentAddress.length > 0) {
						Transport.send(msg, unsentAddress);		
					}
				}	  
			}
			catch(SendFailedException sfe)
			{
				logger.fatal("Cannot send email. to "+address +" from "+aFromEmailAddr +" due to " + sfe.getLocalizedMessage());
			}
			catch(Exception ex)
			{
				logger.fatal("Cannot send email. to "+address+" from "+aFromEmailAddr +" due to " +ex.getMessage() );
			} 
		}	
		// added to fix the 14698
		Iterator<File> it = attach.iterator();
		while (it.hasNext()){
			File f = it.next();
			if( f.exists() ){
				try{
					f.delete();
				} catch ( Exception e ) {
					logger.error("Error while deleting temporary files " , e);		
				}
			}
		} 
	}

	private static Message prepareHeader(String smtp_host, String from, String addressValue, String subject) throws IOException, AddressException, MessagingException {
		Session session = getSession();
		MimeMessage msg = null;
		if (session != null) {
			session.setDebug(false);
			msg = new MimeMessage(session);
			InternetAddress address = new InternetAddress(addressValue);
			msg.addRecipient(Message.RecipientType.TO, address);
			if (isAuthenticationRequired)
				msg.setFrom( new InternetAddress(adminMailId));
			else
				msg.setFrom(new InternetAddress(from));
			msg.setSubject(subject, ENCODING);
		}
		return msg;
	}

	/**
	 * Description - used to get the session
	 * @return
	 */
	private static Session getSession() {
		Properties props = System.getProperties();
		props.put(SMTP_HOST_KEY, mailHost);
		Session session = null;
		if(isAuthenticationRequired) {
			try{
				CryptoManager cryptoManager = new CryptoManager();
    			final String mailPassword = cryptoManager.decrypt(adminMailPassword);
				props.put(SMTP_AUTH_KEY, TRUE);
				props.put(SMTP_TTLS_ENABLE_KEY, TRUE);
				props.put(SMTP_SSL_TRUST_KEY, mailHost);
				if (smtpPort != null) {
					props.put(SMTP_PORT_KEY, smtpPort);
				}
				session = Session.getDefaultInstance(props, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(adminMailId, mailPassword);
					}
				});
			} catch(final Exception e) {
				logger.error("Authentication Failed while creating mail session.", null, e);
			}
		}
		else {
			session = Session.getDefaultInstance(props, null);
		}
		return session;
	}
}
