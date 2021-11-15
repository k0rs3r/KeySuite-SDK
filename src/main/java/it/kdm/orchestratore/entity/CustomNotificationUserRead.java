package it.kdm.orchestratore.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="CustomNotificationUserRead")
@SequenceGenerator(name="customNotificationUserReadIdSeq", sequenceName="CUS_NOT_US_R_ID_SEQ", allocationSize=1)
public class CustomNotificationUserRead implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="customNotificationUserReadIdSeq")
    private Long id;


    @Column(name="idCustomNotification")
    private Long idCustomNotification;

    @Column(name="\"user\"", length=100)
    private String user;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dataLettura;

    @Column(name="priority")
    private Boolean priority=false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCustomNotification() {
        return idCustomNotification;
    }

    public void setIdCustomNotification(Long idCustomNotification) {
        this.idCustomNotification = idCustomNotification;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDataLettura() {
        return dataLettura;
    }

    public void setDataLettura(Date dataLettura) {
        this.dataLettura = dataLettura;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }
}
