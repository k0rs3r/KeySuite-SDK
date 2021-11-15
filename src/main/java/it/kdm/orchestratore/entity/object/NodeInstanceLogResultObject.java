package it.kdm.orchestratore.entity.object;

import java.util.Map;

/**
 * Created by Łukasz Kwasek on 09/07/14.
 */
public class NodeInstanceLogResultObject {

    private Long workItemId;
    private String dateEnter;
    private String dateOut;
    private Map input;
    private Map output;
    private String processId;
    private Long pocessInstanceId;
    private Integer state;

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public String getDateEnter() {
        return dateEnter;
    }

    public void setDateEnter(String dateEnter) {
        this.dateEnter = dateEnter;
    }

    public String getDateOut() {
        return dateOut;
    }

    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    public Map getInput() {
        return input;
    }

    public void setInput(Map input) {
        this.input = input;
    }

    public Map getOutput() {
        return output;
    }

    public void setOutput(Map output) {
        this.output = output;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Long getPocessInstanceId() {
        return pocessInstanceId;
    }

    public void setPocessInstanceId(Long pocessInstanceId) {
        this.pocessInstanceId = pocessInstanceId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
