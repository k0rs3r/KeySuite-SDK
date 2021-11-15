package it.kdm.orchestratore.utils;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.services.ToolkitConnector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class SendExceptionNotification {

    public static final String NT_INSTANCE_ID="instanceId";
    public static final String NT_PROCESS_ID="processId";
    public static final String NT_WORKITEM_ID="workitemId";
    public static final String NT_ERROR_MESSAGE="errorMessage";
    public static final String NT_ENTE="codEnte";
    public static final String NT_AOO="codAoo";
    public static final String NT_WORKITEM_LINK="workitemLink";
    public static final String NT_INSTANCE_LINK="instanceLink";

    //public static final String NT_IP="ip";
    public static final String NT_STACK_TRACE="stackTrace";

    public static final String SUBJECT_TEMPLATE = "Errore istanza #${"+NT_INSTANCE_ID+"} processo:'${"+NT_PROCESS_ID+"}' ente:${"+NT_ENTE+"} aoo:${"+NT_AOO+"}";
    public static final String WORKITEM_LINK_TEMPLATE = "/asyncWorkItemDetails?workItemId=${"+NT_WORKITEM_ID+"}&processInstanceId=${"+NT_INSTANCE_ID+"}";
    public static final String INSTANCE_LINK_TEMPLATE = "/instanceDetail?id=${"+NT_INSTANCE_ID+"}";
    public static final String BODY_TEMPLATE = "<p>Errore workitem #<a href=\"${"+NT_WORKITEM_LINK+"}\">${"+NT_WORKITEM_ID+"}</a> istanza #<a href=\"${"+NT_INSTANCE_LINK+"}\">${"+NT_INSTANCE_ID+"}</a></p>\n<p>Puoi verificare il workitem <a href=\"${"+NT_WORKITEM_LINK+"}\">qui</a></p>\n<p>Di seguito il dettaglio dell'eccezione.</p>\n<p>${"+NT_ERROR_MESSAGE+"}.</p>\n<p><pre>${"+NT_STACK_TRACE+"}</pre></p>";
    public static final String JNDI = "java:jboss/mail/KeysuiteExceptionNotification";

    private static final Logger logger = LoggerFactory.getLogger(SendExceptionNotification.class);

    private String jndiMail;

    private String mailTemplateSubject;

    private String mailTemplateBody;

    //private String regexBlackList;

    private String to;

    //private String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    //private static Pattern pattern;

    //private static Pattern patternBlackList;

    private Matcher matcher;

    private String workitemLinkTmpl = "";
    private String instanceLinkTmpl = "";

    public SendExceptionNotification(){
        jndiMail = ToolkitConnector.getGlobalProperty("keysuiteExceptionNotification.jndiMail", JNDI);
        mailTemplateSubject = ToolkitConnector.getGlobalProperty("keysuiteExceptionNotification.mailTemplateSubject", SUBJECT_TEMPLATE);
        mailTemplateBody = ToolkitConnector.getGlobalProperty("keysuiteExceptionNotification.mailTemplateBody", BODY_TEMPLATE);
        //regexBlackList = ToolkitConnector.getGlobalProperty("keysuiteExceptionNotification.regexBlackList", null);
        to = ToolkitConnector.getGlobalProperty("keysuiteExceptionNotification.to", null);

        String baseUrl = System.getProperty("AppBPM.baseUrl");

        if (Strings.isNullOrEmpty(baseUrl)){
            baseUrl = "http://keysuite:8092/AppBPM";
            logger.error("AppBPM.baseUrl non impostata");
        }

        workitemLinkTmpl = baseUrl+WORKITEM_LINK_TEMPLATE;
        instanceLinkTmpl = baseUrl+INSTANCE_LINK_TEMPLATE;

        //pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        //patternBlackList = Pattern.compile(regexBlackList);
    }

    public  void sendNotification(Map<String,Object> parametri){

        if (Strings.isNullOrEmpty(to))
            return;

        /*Map<String,String> parametri = new HashMap<>();
        for ( String key : input.keySet() ){
            Object v = input.get(key);
            if (v!=null)
                parametri.put(key, v.toString() );
            else
                parametri.put(key, String.format("[%s null]",key));
        }*/

        /*String processId = (String) parametri.get(NT_PROCESS_ID);
        String workItemId = (String) parametri.get(NT_WORKITEM_ID);
        String instanceId = (String) parametri.get(NT_INSTANCE_ID);

        if (Strings.isNullOrEmpty(processId)){
            logger.warn("processId non specificato");
            processId = "null";
        }

        if (Strings.isNullOrEmpty(workItemId)){
            logger.warn("workItemId non specificato");
            workItemId = "null";
        }

        if (Strings.isNullOrEmpty(instanceId)){
            logger.warn("instanceId non specificato");
            instanceId = "null";
        }*/

        String processId = (String) parametri.get(NT_PROCESS_ID);

        if (Strings.isNullOrEmpty(processId)){
            logger.warn("processId non specificato");
            processId = "null";
        }

        /*if ( !Strings.isNullOrEmpty(regexBlackList) && processId.matches(regexBlackList)){
            logger.info("processId '{}' in blacklist",processId);
            return;
        }*/

        String[] toAddresses = to.split("\\,");

        List<String> gmails = new ArrayList<String>();
        List<String> pmails = new ArrayList<String>();

        for(String mail: toAddresses){
            mail = mail.trim();

            if (mail.contains(":")) {
                String[] parts = mail.split(":");
                String prc = parts[0];
                if (processId.startsWith(prc))
                    pmails.add(parts[1]);
            } else {
                gmails.add(mail);
            }
        }

        List<String> mails = (pmails.size()>0) ? pmails : gmails;

        if (mails.size()==0 || (mails.size()==1 && Strings.isNullOrEmpty(mails.get(0)))) {
            logger.info("processId '{}' non ha mail configurata",processId);
            return;
        }

        String workitemLink = new StrSubstitutor(parametri).replace(workitemLinkTmpl);
        String instanceLink = new StrSubstitutor(parametri).replace(instanceLinkTmpl);

        parametri.put(NT_WORKITEM_LINK, workitemLink);
        parametri.put(NT_INSTANCE_LINK, instanceLink);

        String subject = new StrSubstitutor(parametri).replace(mailTemplateSubject);

        String body = new StrSubstitutor(parametri).replace(mailTemplateBody);

        try{
            Session mailSession = InitialContext.doLookup(jndiMail);

            NotificationMail notificationMail = new NotificationMail(mailSession,mails, body,  subject);
            Thread notificationMailThread = new Thread(notificationMail);
            notificationMailThread.start();

        }catch (NamingException e) {
            e.printStackTrace();
            logger.error("SendExceptionNotification: "+jndiMail + "non trovato o non correttamente configurato in standalone.xml");
        }

    }

    class NotificationMail implements Runnable{
        private Session mailSession;
        private List<String> toAddresses;
        private String body;
        private String subject;

        public NotificationMail(Session mailSession, List<String> toAddresses,  String body, String subject){
            super();
            this.body = body;
            this.mailSession = mailSession;
            this.toAddresses = toAddresses;
            this.subject = subject;
        }

        @Override
        public void run() {
            try {
                sendMailNotification();
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("SendExceptionNotification: Errore nell'invio della notifica");
            }
        }

        private void sendMailNotification(){
            try {

                Message msg = new MimeMessage(mailSession);

                msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(StringUtils.join(toAddresses,",")));

                /*List<InternetAddress> emailsCCForGroup = new ArrayList<>();
                msg.addRecipients(Message.RecipientType.CC, emailsCCForGroup.toArray(new InternetAddress[emailsCCForGroup.size()]));


                List<InternetAddress> emailsBCCForGroup = new ArrayList<>();
                msg.addRecipients(Message.RecipientType.BCC, emailsBCCForGroup.toArray(new InternetAddress[emailsBCCForGroup.size()]));*/

                if (mailSession.getProperty("mail.from") != null) {
                    msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
                }

                if (mailSession.getProperty("mail.replyto") != null) {
                    msg.setReplyTo(new InternetAddress[]{
                            new InternetAddress(mailSession.getProperty("mail.replyto"))
                    });
                }

                msg.setDataHandler(new DataHandler(new ByteArrayDataSource(body, "text/html")));

                msg.setSubject(subject);

                msg.setHeader("X-Mailer", "jbpm mail service");

                Date sendDate = new Date();
                String sendDateS = KDMUtils.getISO8601StringForCurrentDate();

                msg.setSentDate(sendDate);

                Transport.send(msg);

            } catch (Exception e) {
                logger.error("SendExceptionNotification: Unable to send email notification due to {}", e.getMessage());
                logger.debug("Stacktrace:", e);
            }

        }
    }



}
