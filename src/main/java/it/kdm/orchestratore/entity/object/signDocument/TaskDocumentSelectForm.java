package it.kdm.orchestratore.entity.object.signDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antsic on 27/10/16.
 */
public class TaskDocumentSelectForm {

    private List<Integer> taskToClaim=new ArrayList<>();
    private List<Integer> taskAlreadyClaim=new ArrayList<>();

    public List<Integer> getTaskToClaim() {
        return taskToClaim;
    }

    public void setTaskToClaim(List<Integer> taskToClaim) {
        this.taskToClaim = taskToClaim;
    }

    public List<Integer> getTaskAlreadyClaim() {
        return taskAlreadyClaim;
    }

    public void setTaskAlreadyClaim(List<Integer> taskAlreadyClaim) {
        this.taskAlreadyClaim = taskAlreadyClaim;
    }
}
