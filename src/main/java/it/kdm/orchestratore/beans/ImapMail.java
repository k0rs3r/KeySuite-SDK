package it.kdm.orchestratore.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ImapMail implements Serializable {

    private String msgid;
    private Date date;
    private String mailbox;
    private String subject;
    private String from;
    private String cc;
    private String bcc;
    private String body;
    private List<String> attachementsURN;
    private String documentURN;
    private String smimeURN;



    public ImapMail(){
        super();
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getAttachementsURN() {
        return attachementsURN;
    }

    public void setAttachementsURN(List<String> attachementsURN) {
        this.attachementsURN = attachementsURN;
    }

    public String getDocumentURN() {
        return documentURN;
    }

    public void setDocumentURN(String documentURN) {
        this.documentURN = documentURN;
    }

    public String getSmimeURN() {
        return smimeURN;
    }

    public void setSmimeURN(String smimeURN) {
        this.smimeURN = smimeURN;
    }
}
