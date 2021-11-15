package it.kdm.orchestratore.entity.object.signDocument;

import java.util.List;

/**
 * Created by antsic on 25/10/16.
 */
public class TaskDocumentCheckedUncheckedListForm {

    private List<TaskDocumentCheckedUncheckedForm> taskDocumentCheckedUncheckedForms;
    private String token;
    private String alias;
    private String pin;
    private String tipo;
    private String otp;

    public List<TaskDocumentCheckedUncheckedForm> getTaskDocumentCheckedUncheckedForms() {
        return taskDocumentCheckedUncheckedForms;
    }

    public void setTaskDocumentCheckedUncheckedForms(List<TaskDocumentCheckedUncheckedForm> taskDocumentCheckedUncheckedForms) {
        this.taskDocumentCheckedUncheckedForms = taskDocumentCheckedUncheckedForms;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
