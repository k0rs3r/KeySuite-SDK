package it.kdm.orchestratore.entity.object;

import java.util.Date;

/**
 * Created by maupet on 23/11/15.
 */
public class MessageConversationObject {

    private long id;
    private Date dateMessage;
    private String idOperator;
    private String idUser;
    private String message;
    private int type;
    private String descriptionIdIstance;
    private Long idProcessInstance;
    public static final int messagein=1;
    public static final int messageout=0;
    private boolean isRead;


    public MessageConversationObject() {
    }

    public MessageConversationObject(long id, Date dateMessage, String idOperator, String message, int type) {
        this.id = id;
        this.dateMessage = dateMessage;
        this.idOperator = idOperator;
        this.message = message;
        this.type=type;
    }


    public MessageConversationObject(long id, Date dateMessage, String idOperator, String message, int type,Long idProcessInstance, boolean isRead,String idUser) {
        this.id = id;
        this.dateMessage = dateMessage;
        this.idOperator = idOperator;
        this.message = message;
        this.type=type;
        this.idProcessInstance=idProcessInstance;
        this.isRead=isRead;
        this.idUser=idUser;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(Date dateMessage) {
        this.dateMessage = dateMessage;
    }

    public String getIdOperator() {
        return idOperator;
    }

    public void setIdOperator(String idOperator) {
        this.idOperator = idOperator;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescriptionIdIstance() {
        return descriptionIdIstance;
    }

    public void setDescriptionIdIstance(String descriptionIdIstance) {
        this.descriptionIdIstance = descriptionIdIstance;
    }

    public Long getIdProcessInstance() {
        return idProcessInstance;
    }

    public void setIdProcessInstance(Long idProcessInstance) {
        this.idProcessInstance = idProcessInstance;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

}
