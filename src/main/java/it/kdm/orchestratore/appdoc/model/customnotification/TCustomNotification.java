package it.kdm.orchestratore.appdoc.model.customnotification;

public class TCustomNotification {
    String id;
    String body;
    String dataNotifica;
    String dataScadenza;
    String oggetto;
    String priority;
    String processInstanceId;
    String typeNotification;
    String actors;
    String priorityUserRead;

    public String getPriorityUserRead() {
        return priorityUserRead;
    }

    public void setPriorityUserRead(String priorityUserRead) {
        this.priorityUserRead = priorityUserRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDataNotifica() {
        return dataNotifica;
    }

    public void setDataNotifica(String dataNotifica) {
        this.dataNotifica = dataNotifica;
    }

    public String getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(String typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }
}
