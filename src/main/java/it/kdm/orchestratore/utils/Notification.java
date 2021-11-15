package it.kdm.orchestratore.utils;

import com.google.common.base.Strings;
import it.kdm.orchestratore.entity.CustomNotification;
import it.kdm.orchestratore.entity.CustomNotificationUser;
import it.kdm.orchestratore.session.ActorsCache;
import keysuite.docer.client.User;
import org.jbpm.process.core.timer.DateTimeUtils;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Notification extends HashMap<String,Object> {

    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String TASK_ID = "taskId";
    public static final String DOC_NUM = "docNum";

    public void setEntityManager(EntityManager em){
        this.put("EntityManager", em);
    }

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    //PROPERTIES
    private static final String MAIL_JNDI_KEY = "mail_jndi_key";

    //INPUT PINS
    private static final String MAIL_FROM = "mail_from"; //text
    private static final String MAIL_REPLYTO = "mail_replyto"; //text
    private static final String MAIL_TO = "mail_to"; //text[]
    private static final String MAIL_CC = "mail_cc"; //text[]
    private static final String MAIL_BCC = "mail_bcc"; //text[]
    private static final String MAIL_SUBJECT = "mail_subject"; //text
    private static final String MAIL_BODY = "mail_body"; //text
    private static final String MAIL_ATTACHMENTS = "mail_attachments"; //file[]

    //OUTPUT PINS
    private static final String RESULT_SENDDATE = "send_date"; //text
    private static final String RESULT_TO = "result_to"; //text

    private static final String NOTIFY_MAIL = "notify_mail";
    private static final String NOTIFY_DESKTOP = "notify_desktop";
    private static final String NOTIFY_EXPIRE= "notify_expire";


    private static final String NOTIFY_PRIORITY = "notify_priority";//0/1 vedi preferiti mail

    private static Collection<User> getCacheUserFromUserOrGroup(String entity){
        Collection<User> cacheUsers = new ArrayList<User>();

        if(entity.contains("<at>") || entity.contains("@")){
            entity = entity.replace("<at>","@");
            User cu = new User();
            cu.setEmail(entity);
            cacheUsers.add(cu);
        }else {
            User cacheUser = ActorsCache.getInstance().getUserForUsername(entity);

            if (cacheUser == null) {
                cacheUsers = ActorsCache.getInstance().getUsersInGroup(entity);
            } else {
                cacheUsers.add(cacheUser);
            }
        }
        return  cacheUsers;
    }

    public void setSubject(String subject){
        this.put(MAIL_SUBJECT,subject);
    }

    public void setProcessInstanceId(Long processInstanceId){
        this.put(PROCESS_INSTANCE_ID,processInstanceId);
    }

    public void setTaskId(Long taskId){
        this.put(TASK_ID,taskId);
    }

    public void setDocNum(String docNum){
        this.put(DOC_NUM,docNum);
    }

    public void setBody(String body){
        this.put(MAIL_BODY,body);
    }

    public void setPriority(Boolean bool){
        this.put(NOTIFY_PRIORITY,bool);
    }

    public void setExpiration(String dueDateString){
        this.put(NOTIFY_EXPIRE,dueDateString);
    }

    public void setExpiration(Date dueDate){
        if (dueDate==null){
            this.put(NOTIFY_EXPIRE, null );
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.put(NOTIFY_EXPIRE, dateFormat.format(dueDate) );
    }

    public void setJNDIKey(String key){
        this.put(MAIL_JNDI_KEY,key);
    }

    public void setMailFrom(String from){
        this.put(MAIL_FROM,from);
    }

    public void setReplyTo(String ReplyTo){
        this.put(MAIL_REPLYTO,ReplyTo);
    }

    public void addAttachment(String attachment){
        Collection<String> atts = (Collection<String>) this.get(MAIL_ATTACHMENTS);
        if (atts==null){
            atts = new ArrayList<>();
            this.put(MAIL_ATTACHMENTS,atts);
        }
        atts.add(attachment);
    }

    public void addRecipients(Collection<String> recipients, String type){
        Collection<String> recps = (Collection<String>) this.get(type);
        if (recps==null){
            recps = new ArrayList<>();
            this.put(type,recps);
        }
        recps.addAll(recipients);
    }

    public void addRecipientsTO(Collection<String> recipients){
        addRecipients(recipients,MAIL_TO);
    }

    public void addRecipientsCC(Collection<String> recipients){
        addRecipients(recipients,MAIL_CC);
    }

    public void addRecipientsBCC(Collection<String> recipients){
        addRecipients(recipients,MAIL_BCC);
    }

    public void addRecipientTO(String recipient){
        addRecipientsTO(Collections.singletonList(recipient));
    }

    public void addRecipientCC(String recipient){
        addRecipientsCC(Collections.singletonList(recipient));
    }

    public void addRecipientBCC(String recipient){
        addRecipientsBCC(Collections.singletonList(recipient));
    }

    private static List<String> sendEmailNotification( Map<String,Object> params ){

        /********************** PARAMETRI ***************************/

        String mail_to = KDMUtils.checkActor(params, MAIL_TO);
        String mail_cc = KDMUtils.checkActor(params, MAIL_CC);
        String mail_bcc = KDMUtils.checkActor(params, MAIL_BCC);
        String subject = (String) params.get(MAIL_SUBJECT);
        String body = (String) params.get(MAIL_BODY);
        String JNDI_KEY = (String) params.get(MAIL_JNDI_KEY);
        String mail_from = KDMUtils.checkActor(params, MAIL_FROM);
        String mail_replyto = KDMUtils.checkActor(params, MAIL_REPLYTO);

        List<String> attachments = null;

        if (params.get(MAIL_ATTACHMENTS) instanceof List)
            attachments = (List<String>) params.get(MAIL_ATTACHMENTS);
        else if (params.get(MAIL_ATTACHMENTS)!=null)
            attachments = org.apache.tools.ant.util.StringUtils.split(params.get(MAIL_ATTACHMENTS).toString(), ',');

        /***************************************************************/

        List<String> toAddresses;
        List<String> ccAddresses = new ArrayList<>();
        List<String> bccAddresses = new ArrayList<>();

        if (!Strings.isNullOrEmpty(mail_to))
            toAddresses = org.apache.tools.ant.util.StringUtils.split(mail_to, ',');
        else
            throw new RuntimeException("mail_to is mandatory");

        subject = (String) TemplateRuntime.eval(subject, params);
        body = (String) TemplateRuntime.eval(body, params);

        if (Strings.isNullOrEmpty(subject))
            throw new RuntimeException("mail_subject is mandatory");

        if (Strings.isNullOrEmpty(body))
            body = "";

        if (!Strings.isNullOrEmpty(mail_cc))
            ccAddresses = org.apache.tools.ant.util.StringUtils.split(mail_cc, ',');

        if (!Strings.isNullOrEmpty(mail_bcc))
            bccAddresses = org.apache.tools.ant.util.StringUtils.split(mail_bcc, ',');

        if (!Strings.isNullOrEmpty(mail_from) && mail_from.contains("mail/")){
            JNDI_KEY = mail_from;
            mail_from = null;
        }

        if (Strings.isNullOrEmpty(JNDI_KEY)) {
            JNDI_KEY = System.getProperty("notification.jndi", System.getProperty("org.kie.mail.session", "mail/jbpmMailSession"));
        }

        Session mailSession;

        try {
            mailSession = InitialContext.doLookup(JNDI_KEY);
        } catch (NamingException e) {

            try {
                //Jboss versione  vecchia
                mailSession = InitialContext.doLookup("java:jboss/mail/"+JNDI_KEY);
            } catch (NamingException e2) {
                logger.debug("Mail session was not found in JNDI under {} trying to look up email.properties on classspath", MAIL_JNDI_KEY);
                throw new RuntimeException(e2);
            }
        }

        List<String> recipients = new ArrayList<>();

        try {

            Message msg = new MimeMessage(mailSession);

            List<InternetAddress> emailsToForGroup = new ArrayList<>();
            for (int i = 0; i < toAddresses.size(); i++) {
                Collection<User> cacheUsers = getCacheUserFromUserOrGroup(toAddresses.get(i));
                for(User cu: cacheUsers){
                    String emailAddress = cu.getEmail();
                    emailsToForGroup.add(InternetAddress.parse(emailAddress, false)[0]);
                    //String emailAddress = getEmailForEntity(toAddresses.get(i));
                    recipients.add(emailAddress);
                }
            }

            msg.addRecipients(Message.RecipientType.TO, emailsToForGroup.toArray(new InternetAddress[emailsToForGroup.size()]));

            List<InternetAddress> emailsCCForGroup = new ArrayList<>();
            for (int i = 0; i < ccAddresses.size(); i++) {

                Collection<User> cacheUsers = getCacheUserFromUserOrGroup(ccAddresses.get(i));
                for(User cu: cacheUsers){
                    String emailAddress = cu.getEmail();
                    emailsCCForGroup.add(InternetAddress.parse(emailAddress, false)[0]);
                    recipients.add(emailAddress);
                }

//                    String emailAddress = getEmailForEntity(ccAddresses.get(i));
//                    msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(emailAddress, false));
//                    recipients.add(emailAddress);
            }
            msg.addRecipients(Message.RecipientType.CC, emailsCCForGroup.toArray(new InternetAddress[emailsCCForGroup.size()]));


            List<InternetAddress> emailsBCCForGroup = new ArrayList<>();
            for (int i = 0; i < bccAddresses.size(); i++) {
                Collection<User> cacheUsers = getCacheUserFromUserOrGroup(bccAddresses.get(i));
                for(User cu: cacheUsers){
                    String emailAddress = cu.getEmail();
                    emailsBCCForGroup.add(InternetAddress.parse(emailAddress, false)[0]);
                    recipients.add(emailAddress);
                }
//                    String emailAddress = getEmailForEntity(bccAddresses.get(i));
//                    msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailAddress, false));
//                    recipients.add(emailAddress);
            }
            msg.addRecipients(Message.RecipientType.BCC, emailsBCCForGroup.toArray(new InternetAddress[emailsBCCForGroup.size()]));

            if (!Strings.isNullOrEmpty(mail_from)) {
                Collection<User> cUsers = getCacheUserFromUserOrGroup(mail_from);
                if (cUsers.size() > 0){
                    msg.setFrom(new InternetAddress(getCacheUserFromUserOrGroup(mail_from).iterator().next().getEmail()));
                }else{
                    msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
                }
            } else if (mailSession.getProperty("mail.from") != null) {
                msg.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
            }

            if (!Strings.isNullOrEmpty(mail_replyto)) {
                Collection<User> cUsers = getCacheUserFromUserOrGroup(mail_replyto);
                if (cUsers.size() > 0) {
                    msg.setReplyTo(new InternetAddress[]{
                            new InternetAddress(getCacheUserFromUserOrGroup(mail_replyto).iterator().next().getEmail())
                    });
                }else{
                    msg.setReplyTo(new InternetAddress[]{
                            new InternetAddress(mailSession.getProperty("mail.replyto"))
                    });
                }
            } else if (mailSession.getProperty("mail.replyto") != null) {
                msg.setReplyTo(new InternetAddress[]{
                        new InternetAddress(mailSession.getProperty("mail.replyto"))
                });
            }

            if (attachments != null && attachments.size() > 0) {
                Multipart multipart = new MimeMultipart();
                // prepare body as first mime body part
                MimeBodyPart messageBodyPart = new MimeBodyPart();

                messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(body, "text/html")));
                multipart.addBodyPart(messageBodyPart);

                int idx = 0;
                for (String attachment : attachments) {
                    MimeBodyPart attachementBodyPart = new MimeBodyPart();

                    if (attachment.startsWith("file://")) {
                        URL attachmentUrl = new URL(attachment);
                        String contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(attachmentUrl.getFile());
                        attachementBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentUrl.openStream(), contentType)));
                        String fileName = new File(attachmentUrl.getFile()).getName();
                        attachementBodyPart.setFileName(fileName);
                        attachementBodyPart.setContentID("<" + fileName + ">");
                    } else {
                        attachementBodyPart.setText(attachment);
                        attachementBodyPart.setFileName("attachment" + idx++);
                        attachementBodyPart.setContentID("<" + attachementBodyPart.getFileName() + ">");
                    }

                    multipart.addBodyPart(attachementBodyPart);
                }
                // Put parts in message
                msg.setContent(multipart);
            } else {
                msg.setDataHandler(new DataHandler(new ByteArrayDataSource(body, "text/html")));
            }

            msg.setSubject(subject);

            msg.setHeader("X-Mailer", "jbpm mail service");

            Date sendDate = new Date();

            msg.setSentDate(sendDate);

            Transport.send(msg);

            return recipients;

        } catch (Exception e) {
            logger.error("Unable to send email notification due to {}", e.getMessage());
            logger.debug("Stacktrace:", e);
            throw new RuntimeException(e);
        }
    }

    private static List<String> sendDesktopNotification( Map<String,Object> params ){

        /***************************************************************/

        String subject = (String) params.get(MAIL_SUBJECT);
        String body = (String) params.get(MAIL_BODY);
        String mail_to = KDMUtils.checkActor(params, MAIL_TO);
        String mail_cc = KDMUtils.checkActor(params, MAIL_CC);
        String mail_bcc = KDMUtils.checkActor(params, MAIL_BCC);

        Boolean priority = (Boolean) params.get(NOTIFY_PRIORITY);
        String dueDateString = (String) params.get(NOTIFY_EXPIRE);

        List<String> attachments = null;

        if (params.get(MAIL_ATTACHMENTS) instanceof List)
            attachments = (List<String>) params.get(MAIL_ATTACHMENTS);
        else if (params.get(MAIL_ATTACHMENTS)!=null)
            attachments = org.apache.tools.ant.util.StringUtils.split(params.get(MAIL_ATTACHMENTS).toString(), ',');

        if (attachments != null) {
            //TODO gestire i file://
            for (String attachment : attachments) {
                body += "\n\n" + attachment;
            }
        }

        subject = (String) TemplateRuntime.eval(subject, params);
        body = (String) TemplateRuntime.eval(body, params);

        List<String> toAddresses = new ArrayList<>();

        if (!Strings.isNullOrEmpty(mail_to))
            toAddresses.addAll(org.apache.tools.ant.util.StringUtils.split(mail_to, ','));
        else
            throw new RuntimeException("mail_to is mandatory");

        if (!Strings.isNullOrEmpty(mail_cc))
            toAddresses.addAll(org.apache.tools.ant.util.StringUtils.split(mail_cc, ','));

        if (!Strings.isNullOrEmpty(mail_bcc))
            toAddresses.addAll(org.apache.tools.ant.util.StringUtils.split(mail_bcc, ','));

        /***************************************************************/

        EntityManager em = (EntityManager) params.get("EntityManager");

        if (em==null)
            throw new RuntimeException("em is mandatory for desktop notify");

        CustomNotification customNotification=new CustomNotification();

        customNotification.setBody(body);

        customNotification.setPriority(priority);
        customNotification.setOggetto(subject);
        customNotification.setTypeNotification(0);
        customNotification.setProcessInstanceId(-1L);

        //customNotification.setProcessInstanceId(workItem.getProcessInstanceId());

        Long processInstanceId = (Long) params.get(PROCESS_INSTANCE_ID);
        Long taskId = (Long) params.get(TASK_ID);
        String docNum = (String) params.get(DOC_NUM);

        //TODO nuove colonne taskId e reference

        if (processInstanceId!=null)
            customNotification.setProcessInstanceId(processInstanceId);

        customNotification.setDocNum(docNum);
        customNotification.setTaskId(taskId);

        if (org.drools.core.util.StringUtils.isEmpty(dueDateString)){
            dueDateString = System.getProperty("notify_expire","2099-12-31T00:00:00Z");
        }

        Date dataScadenza=new Date();
        if(DateTimeUtils.isPeriod(dueDateString)){
            Long longDateValue = DateTimeUtils.parseDateAsDuration(dueDateString.substring(1));
            dataScadenza = new Date(System.currentTimeMillis() + longDateValue);
        }else{
            dataScadenza = new Date(DateTimeUtils.parseDateTime(dueDateString));
        }
        customNotification.setDataScadenza(dataScadenza);


        try {

            customNotification = em.merge(customNotification); //TODO SALVARE customnotification
            em.flush();

            for (int i = 0; i < toAddresses.size(); i++) {
                CustomNotificationUser cNU=new CustomNotificationUser();
                cNU.setActor(toAddresses.get(i));
                cNU.setIdCustomNotification(customNotification.getId());
                em.merge(cNU);
                em.flush();
                //TODO iesimo valore di customNotificationUser
            }

            return toAddresses;

        }catch (Exception e) {
            logger.error("error retrieving connection", e);

            //em.getTransaction().rollback();
            throw new RuntimeException(e);
        }finally {
            //em.close();
        }
    }

    public List<String> send(){

        Boolean nMail = (Boolean) this.remove(NOTIFY_MAIL);
        Boolean nDesktop = (Boolean) this.remove(NOTIFY_DESKTOP);

        if (nMail==null)
            nMail = true;

        if (nDesktop==null)
            nDesktop = true;

        return send(nMail,nDesktop);
    }

    public List<String> send(boolean notifyMail){
        return send(notifyMail,true);
    }

    public List<String> send(boolean notifyMail, boolean notifyDesktop){
        List<String> results = new ArrayList<String>();

        try {

            if (notifyMail) {
                List<String> recipients = sendEmailNotification(this);
                results.addAll(recipients);
            }

            if (notifyDesktop) {
                List<String> recipients = sendDesktopNotification(this);
                results.addAll(recipients);
            }

            return results;

        }catch (Exception e) {
            logger.error("error retrieving connection", e);
            //em.getTransaction().rollback();
            throw new RuntimeException(e);
        }finally {
            //em.close();
        }
    }

}
