package it.kdm.orchestratore.entity.object;

import java.util.List;

/**
 * Created by maupet on 23/11/15.
 */
public class ConversationObject {

    private long processInstanceId;
    private String idUser;
    private String descriptionInstanceProcess;
    private long proceedingInstanceId;
    private List<MessageConversationObject> messaggi;

    public ConversationObject() {
    }

    public ConversationObject(long processInstanceId, String idUser, long proceedingInstanceId, List<MessageConversationObject> messaggi) {
        this.processInstanceId = processInstanceId;
        this.idUser = idUser;
        this.proceedingInstanceId = proceedingInstanceId;
        this.messaggi = messaggi;
    }

    public long getProceedingInstanceId() {
        return proceedingInstanceId;
    }

    public void setProceedingInstanceId(long proceedingInstanceId) {
        this.proceedingInstanceId = proceedingInstanceId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public List<MessageConversationObject> getMessaggi() {
        return messaggi;
    }

    public void setMessaggi(List<MessageConversationObject> messaggi) {
        this.messaggi = messaggi;
    }


    public String getDescriptionInstanceProcess() {
        return descriptionInstanceProcess;
    }

    public void setDescriptionInstanceProcess(String descriptionInstanceProcess) {
        this.descriptionInstanceProcess = descriptionInstanceProcess;
    }
}
