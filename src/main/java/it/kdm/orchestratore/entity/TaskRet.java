package it.kdm.orchestratore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jbpm.services.task.impl.model.TaskDataImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskRet extends TaskDataImpl {
}
