package it.kdm.orchestratore.entity.object.protocollo;

import it.kdm.orchestratore.entity.object.TaskDocumentProto;

import java.util.Map;

/**
 * Created by antsic on 13/03/17.
 */
public class TaskContents {

    private TaskDocumentProto taskDocumentProto;
    private Map<String,Object> input;
    private Map<String,Object> output;
    private Map<String,Object> fault;

    public TaskDocumentProto getTaskDocumentProto() {
        return taskDocumentProto;
    }

    public void setTaskDocumentProto(TaskDocumentProto taskDocumentProto) {
        this.taskDocumentProto = taskDocumentProto;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }

    public Map<String, Object> getFault() {
        return fault;
    }

    public void setFault(Map<String, Object> fault) {
        this.fault = fault;
    }
}
