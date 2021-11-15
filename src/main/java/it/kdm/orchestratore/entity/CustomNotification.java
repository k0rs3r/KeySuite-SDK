package it.kdm.orchestratore.entity;


import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name="CustomNotification")
@SequenceGenerator(name="customNotificationIdSeq", sequenceName="CUS_NOT_ID_SEQ", allocationSize=1)
public class CustomNotification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="customNotificationIdSeq")
    private Long id;

    @Column
    private Long processInstanceId;

    @Column
    private Long taskId;

    @Column(name="docNum", length=20)
    private String docNum;

    @Column(name = "typeNotification")
    private Integer typeNotification =0;

    @Column(name="priority")
    private Boolean priority= false;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dataScadenza;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dataNotifica=new Date();

    @Column(name="oggetto")
    private String oggetto;

    @Column(length = 2147483647)
    @Length(max = 2147483647)
    private String body;

    public CustomNotification(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Integer getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(Integer typeNotification) {
        this.typeNotification = typeNotification;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public Date getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDataNotifica() {
        return dataNotifica;
    }

    public void setDataNotifica(Date dataNotifica) {
        this.dataNotifica = dataNotifica;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }
}

