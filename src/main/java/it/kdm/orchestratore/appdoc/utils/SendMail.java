package it.kdm.orchestratore.appdoc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

public class SendMail {
	

	public static Message sendMailTLS(String SMTPserver,String SMTPport,final String username,final String password,String from, String fromHeader, String replyTo, String to[],String oggetto,String message,List<File> files) throws MessagingException{
		return sendMailTLS(SMTPserver,SMTPport,username,password,from, fromHeader, replyTo, to,oggetto,message,files, "");
	}
	public static Message sendMailTLS(String SMTPserver,String SMTPport,final String username,final String password,String from, String fromHeader, String replyTo, String to[],String oggetto,String message,List<File> files, String mailCC) throws MessagingException{
		//GOOGLE PORT 587
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", SMTPserver);
		props.put("mail.smtp.port", SMTPport);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 		
		
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(message);
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		
		for (File file : files) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			messageBodyPart.setFileName(file.getName());
			messageBodyPart.setDataHandler(new DataHandler(source));
			multipart.addBodyPart(messageBodyPart);
		}
		
		Message mail = new MimeMessage(session);
		
		mail.setFrom(new InternetAddress(fromHeader));

		
		if(!"".equals(mailCC))
			mail.addRecipient(RecipientType.CC, new InternetAddress(mailCC));
			
		
		InternetAddress replyToArr[]  = new InternetAddress[1];
		replyToArr[0]=new InternetAddress(replyTo);
		mail.setReplyTo(replyToArr);
		
		
		for(String tmp:to){
		mail.setRecipients(Message.RecipientType.TO,InternetAddress.parse(tmp));
		}
		mail.setSubject(oggetto);
		mail.setContent(multipart);
		mail.setSentDate(new Date());
 		Transport.send(mail);
 		
 		return mail;
		
		
	}


	public static Message sendMailSSL(String SMTPserver,String SMTPport,final String username,final String password,String from, String fromHeader, String replyTo, String to[],String oggetto,String message,List<File> files) throws MessagingException{
		return sendMailTLS(SMTPserver,SMTPport,username,password,from, fromHeader, replyTo, to,oggetto,message,files, "");
	}	
	public static Message sendMailSSL(String SMTPserver,String SMTPport,final String username,final String password,String from,String fromHeader, String replyTo, String to[],String oggetto,String message,List<File> files, String mailCC) throws MessagingException,FileNotFoundException,IOException{
		//GOOGLE PORT 465
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPserver);
		props.put("mail.smtp.socketFactory.port", SMTPport);
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", SMTPport);
 
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username,password);
				}
			});
		InternetAddress[] addressTo = new InternetAddress[to.length];
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(message);
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		for (File file : files) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			messageBodyPart.setFileName(file.getName());
			messageBodyPart.setDataHandler(new DataHandler(source));
			multipart.addBodyPart(messageBodyPart);
		}
	
		Message mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(fromHeader, fromHeader));
		
		if(!"".equals(mailCC))
			mail.addRecipient(RecipientType.CC, new InternetAddress(mailCC));
		
		
		InternetAddress replyToArr[]  = new InternetAddress[1];
		replyToArr[0]=new InternetAddress(replyTo);
		mail.setReplyTo(replyToArr);
		
		
		
		int i=0;
		for(String tmp:to){
			addressTo[i]=new InternetAddress(tmp.trim());
			i++;
		}
		mail.setRecipients(Message.RecipientType.TO,addressTo);
		mail.setSubject(oggetto);
		mail.setContent(multipart);
 		mail.setSentDate(new Date());
 		Transport.send(mail);
 		return mail;
	}
	public static Message sendMailSTD(String SMTPserver,String SMTPport,final String username,final String password,String from,String fromHeader, String replyTo, String to[],String oggetto,String message,List<File> files, String mailCC) throws MessagingException,FileNotFoundException,IOException{
		//GOOGLE PORT 465
		Properties props = new Properties();
		String[] hostAndParameter =null;
		try {
			hostAndParameter = SMTPserver.split("\\|");
			if (hostAndParameter != null && hostAndParameter.length > 1) {
				for (int i = 1; i < hostAndParameter.length; i++) {
					String property = hostAndParameter[i];
					String[] propertiesArray = property.split(":");
					if (propertiesArray.length == 2) {
						props.setProperty(propertiesArray[0], propertiesArray[1]);
					}
				}

				SMTPserver = hostAndParameter[0];
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		props.put("mail.smtp.host", SMTPserver);
 

	      // Get the default Session object.
	      Session session = Session.getInstance(props);
	      
		InternetAddress[] addressTo = new InternetAddress[to.length];
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(message);
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		for (File file : files) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			messageBodyPart.setFileName(file.getName());
			messageBodyPart.setDataHandler(new DataHandler(source));
			multipart.addBodyPart(messageBodyPart);
		}
	
		Message mail = new MimeMessage(session);
		mail.setFrom(new InternetAddress(fromHeader, fromHeader));
		
		if(!"".equals(mailCC))
			mail.addRecipient(RecipientType.CC, new InternetAddress(mailCC));
		
		
		InternetAddress replyToArr[]  = new InternetAddress[1];
		replyToArr[0]=new InternetAddress(replyTo);
		mail.setReplyTo(replyToArr);
		
		int i=0;
		for(String tmp:to){
			addressTo[i]=new InternetAddress(tmp.trim());
			i++;
		}
		mail.setRecipients(Message.RecipientType.TO,addressTo);
		mail.setSubject(oggetto);
		mail.setContent(multipart);
 		mail.setSentDate(new Date());
 		Transport.send(mail);
 		return mail;
	}
}

