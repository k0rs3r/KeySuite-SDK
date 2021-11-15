package it.kdm.orchestratore.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="NodeInstanceStates")
public class NodeInstanceStates implements Serializable {
    @Id
    private Long processInstanceId;

    @Column(name="stato")
    private Integer stato;

    @Column(name="aggiornamento")
    private Date aggiornamento;

    @Column(name="processEndDate")
    private Date processEndDate;

    public NodeInstanceStates(){
        super();
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Integer getStato() {
        return stato;
    }

    public void setStato(Integer stato) {
        this.stato = stato;
    }

    public Date getAggiornamento() {
        return aggiornamento;
    }

    public void setAggiornamento(Date aggiornamento) {
        this.aggiornamento = aggiornamento;
    }

    public Date getProcessEndDate() {
        return processEndDate;
    }

    public void setProcessEndDate(Date processEndDate) {
        this.processEndDate = processEndDate;
    }
}
