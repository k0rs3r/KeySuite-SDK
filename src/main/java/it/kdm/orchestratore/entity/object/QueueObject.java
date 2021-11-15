package it.kdm.orchestratore.entity.object;

/**
 * Created by maupet on 19/01/16.
 */
public class QueueObject {

    private String name;
    private String origin;
    private int numeroMessaggi;
    private String dataUltimoMessaggio;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumeroMessaggi() {
        return numeroMessaggi;
    }

    public void setNumeroMessaggi(int numeroMessaggi) {
        this.numeroMessaggi = numeroMessaggi;
    }

    public String getDataUltimoMessaggio() {
        return dataUltimoMessaggio;
    }

    public void setDataUltimoMessaggio(String dataUltimoMessaggio) {
        this.dataUltimoMessaggio = dataUltimoMessaggio;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public QueueObject() {}

    public QueueObject(String name, String origin, int numeroMessaggi, String dataUltimoMessaggio) {
        this.name = name;
        this.origin = origin;
        this.numeroMessaggi = numeroMessaggi;
        this.dataUltimoMessaggio = dataUltimoMessaggio;
    }
}
