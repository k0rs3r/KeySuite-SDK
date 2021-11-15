package it.kdm.orchestratore.beans;

import java.io.Serializable;

public class ImapMailItemList implements Serializable {

    private String mailbox;

    private String msgid;

    private String date;

    private Integer state;

    private String creationDate;

    private String subject;

    private String mailTo;

    private String mailCc;

    private String mailBcc;

    private String mailFrom;

    private Integer hasAttachment;

    public ImapMailItemList(){
        super();
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailCc() {
        return mailCc;
    }

    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    public String getMailBcc() {
        return mailBcc;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public Integer getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(Integer hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
}
