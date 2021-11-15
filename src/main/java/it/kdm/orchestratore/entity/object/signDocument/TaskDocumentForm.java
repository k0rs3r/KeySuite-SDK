package it.kdm.orchestratore.entity.object.signDocument;

import it.kdm.orchestratore.entity.object.TaskDocument;

import java.util.List;

/**
 * Created by antsic on 25/10/16.
 */
public class TaskDocumentForm {

    private List<TaskDocument> taskDocuments;

    public List<TaskDocument> getTaskDocuments() {
        return taskDocuments;
    }

    public void setTaskDocuments(List<TaskDocument> taskDocuments) {
        this.taskDocuments = taskDocuments;
    }
}
