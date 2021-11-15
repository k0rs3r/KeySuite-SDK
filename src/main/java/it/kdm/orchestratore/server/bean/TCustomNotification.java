package it.kdm.orchestratore.server.bean;

import it.kdm.orchestratore.entity.CustomNotification;

import java.text.SimpleDateFormat;

public class TCustomNotification {
    String id;
    String body;
    String dataNotifica;
    String dataScadenza;
    String oggetto;
    String priority;
    String processInstanceId;
    String riferimento;
    String typeNotification;
    String docNum;
    String takId;
    String actors;
    String priorityUserRead;
    String isRead;

    public String getRiferimento() {
        return riferimento;
    }

    public void setRiferimento(String riferimento) {
        this.riferimento = riferimento;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getTakId() {
        return takId;
    }

    public void setTakId(String takId) {
        this.takId = takId;
    }





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

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public TCustomNotification(){

    }

    public TCustomNotification(CustomNotification customNotification, String actors){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        id=Long.toString(customNotification.getId());
        body=customNotification.getBody();
        try{
            this.dataNotifica= formatter.format(customNotification.getDataNotifica());
        }
        catch (Exception e){

        }
        try{
            this.dataScadenza= formatter.format(customNotification.getDataScadenza());
        }
        catch (Exception e){

        }
        oggetto=customNotification.getOggetto();
        priority=Boolean.toString(customNotification.getPriority());
        processInstanceId=Long.toString(customNotification.getProcessInstanceId());
        typeNotification=Long.toString(customNotification.getTypeNotification());
        this.actors=actors;
    }

    public void loadPojo (Object[] arrayValue){
        if(arrayValue != null){
            try {
                this.id = arrayValue[0] != null ? arrayValue[0].toString() : null;
                this.body = arrayValue[1] != null ? arrayValue[1].toString() : null;

                this.oggetto = arrayValue[4] != null ? arrayValue[4].toString() : null;
                this.priority = arrayValue[5] != null ? arrayValue[5].toString() : null;
                this.processInstanceId = arrayValue[6] != null ? arrayValue[6].toString() : null;
                this.riferimento=arrayValue[7] != null ? arrayValue[7].toString() : null;
                this.typeNotification = arrayValue[8] != null ? arrayValue[8].toString() : null;
                this.docNum = arrayValue[9] != null ? arrayValue[9].toString() : "";

                this.actors = arrayValue[11] != null ? arrayValue[11].toString() : null;
                this.priorityUserRead= arrayValue[12] != null ? arrayValue[12].toString() : null;
                this.isRead = arrayValue[13] != null ? "true" : null;
                if (this.dataScadenza!=null){
                    if (this.dataScadenza.length()>=10){
                        this.dataScadenza=this.dataScadenza.substring(0,10);
                    }
                }
                SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    if (arrayValue[3]!=null){
                        this.dataScadenza = dataFormat.format(dataFormat.parse(arrayValue[3].toString()));
                    }
                }
                catch (Exception e){

                }
                try
                {
                    if (arrayValue[2]!=null){
                        this.dataNotifica = dataFormat.format(dataFormat.parse(arrayValue[2].toString()));
                    }
                }
                catch (Exception e){

                }
                if (this.dataNotifica!=null){
                    if (this.dataNotifica.length()>=10){
                        this.dataNotifica=this.dataNotifica.substring(0,10);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
