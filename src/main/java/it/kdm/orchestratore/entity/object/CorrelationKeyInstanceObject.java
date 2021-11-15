package it.kdm.orchestratore.entity.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antsic on 05/06/15.
 */
public class CorrelationKeyInstanceObject {


    private List<String> ckValue = new ArrayList<String>();
    private Long processInstanceId;
    private String processId;
    private String ente;
    private String aoo;
    private String variableValue;

    public List<String> getCkValue() {
        return ckValue;
    }

    public void setCkValue(List<String> ckValue) {
        this.ckValue = ckValue;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public String getAoo() {
        return aoo;
    }

    public void setAoo(String aoo) {
        this.aoo = aoo;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }
}
