package it.kdm.orchestratore.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="CustomNotificationUser")
@SequenceGenerator(name="customNotificationUserIdSeq", sequenceName="CUS_NOT_US_ID_SEQ", allocationSize=1)
public class CustomNotificationUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="customNotificationUserIdSeq")
    private Long id;

    @Column(name="actor", length=100)
    private String actor;

    @Column(name= "idCustomNotification")
    private Long  idCustomNotification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Long getIdCustomNotification() {
        return idCustomNotification;
    }

    public void setIdCustomNotification(Long idCustomNotification) {
        this.idCustomNotification = idCustomNotification;
    }

}
