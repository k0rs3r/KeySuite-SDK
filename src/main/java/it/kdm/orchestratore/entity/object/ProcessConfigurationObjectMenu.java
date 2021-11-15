package it.kdm.orchestratore.entity.object;

public class ProcessConfigurationObjectMenu {
	
   private ProcessConfigurationObject processConfigurationObject;
	private boolean hasStartRole;
	private boolean hasViewRole;
	private boolean hasCloneRole;


	public ProcessConfigurationObject getProcessConfigurationObject() {
		return processConfigurationObject;
	}

	public void setProcessConfigurationObject(ProcessConfigurationObject processConfigurationObject) {
		this.processConfigurationObject = processConfigurationObject;
	}

	public boolean isHasStartRole() {
		return hasStartRole;
	}

	public void setHasStartRole(boolean hasStartRole) {
		this.hasStartRole = hasStartRole;
	}

	public boolean isHasViewRole() {
		return hasViewRole;
	}

	public void setHasViewRole(boolean hasViewRole) {
		this.hasViewRole = hasViewRole;
	}

	public boolean isHasCloneRole() {
		return hasCloneRole;
	}

	public void setHasCloneRole(boolean hasCloneRole) {
		this.hasCloneRole = hasCloneRole;
	}
}
