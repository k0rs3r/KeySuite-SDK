package it.kdm.orchestratore.entity.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.kdm.orchestratore.entity.Instances;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author antsic
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstancesObject {

	private String key = "instances";
	
	private int pageSize;
	private int pageNumber;
	private int pageCount;
	private int recordCount;
	private String orderBy;
	
	private String id;
    private String nodeRawId;
	private String processId;
	private String processInstanceId;
	private String version;
	private String startDate;
	private String endDate;
	private String lastReadDate;
	private String lastModificationDate;
	private Long parentProcessInstanceid;
	private String parentProcessName;
	private String parentProcessVersion;
	private String parentProcessStatus;
	private String parentProcessDescrizione;
	private String parentProcessDescription;

	public String getPrimaryProcessId() {
		return primaryProcessId;
	}

	public void setPrimaryProcessId(String primaryProcessId) {
		this.primaryProcessId = primaryProcessId;
	}

	public String getPrimaryProcessInstanceId() {
		return primaryProcessInstanceId;
	}

	public void setPrimaryProcessInstanceId(String primaryProcessInstanceId) {
		this.primaryProcessInstanceId = primaryProcessInstanceId;
	}

	public String getPrimaryProcessInstanceDesc() {
		return primaryProcessInstanceDesc;
	}

	public void setPrimaryProcessInstanceDesc(String primaryProcessInstanceDesc) {
		this.primaryProcessInstanceDesc = primaryProcessInstanceDesc;
	}

	private String primaryProcessId;
	private String primaryProcessInstanceId;
	private String primaryProcessInstanceDesc;

	private String name;
	private String description;
	private String outcome;
	private String processInstanceByteArray;
	private ArrayList<?> eventTypes;
	private String env;
	private String state;
	private String ente;
	private String aoo;
	private String deploymentId;
	private Boolean conversation;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	private String creator;

	public Map<String, Map<String, Integer>> getFacets() {
		return facets;
	}

	public void setFacets(Map<String, Map<String, Integer>> facets) {
		this.facets = facets;
	}

	private Map<String,Map<String,Integer>> facets;

	public String getBusinessState() {
		return businessState;
	}

	public void setBusinessState(String businessState) {
		this.businessState = businessState;
	}

	private String businessState;

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	private String businessKey;


	public String getParentProcessDescription() {
		return parentProcessDescription;
	}

	public void setParentProcessDescription(String parentProcessDescription) {
		this.parentProcessDescription = parentProcessDescription;
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

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	private ArrayList<Instances> data;
	private ArrayList<Instances> instances;

    public String getNodeRawId() {
        return nodeRawId;
    }

    public void setNodeRawId(String nodeRawId) {
        this.nodeRawId = nodeRawId;
    }

    public String getParentProcessDescrizione() {
		return parentProcessDescrizione;
	}
	public void setParentProcessDescrizione(String parentProcessDescrizione) {
		this.parentProcessDescrizione = parentProcessDescrizione;
	}
	public String getParentProcessName() {
		return parentProcessName;
	}
	public void setParentProcessName(String parentProcessName) {
		this.parentProcessName = parentProcessName;
	}
	public String getParentProcessVersion() {
		return parentProcessVersion;
	}
	public void setParentProcessVersion(String parentProcessVersion) {
		this.parentProcessVersion = parentProcessVersion;
	}
	public String getParentProcessStatus() {
		return parentProcessStatus;
	}
	public void setParentProcessStatus(String parentProcessStatus) {
		this.parentProcessStatus = parentProcessStatus;
	}
	public ArrayList<Instances> getInstances() {
		return instances;
	}
	public void setInstances(ArrayList<Instances> instances) {
		this.instances = instances;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = "instances";
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public ArrayList<Instances> getData() {
		return data;
	}
	public void setData(ArrayList<Instances> data) {
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getLastReadDate() {
		return lastReadDate;
	}
	public void setLastReadDate(String lastReadDate) {
		this.lastReadDate = lastReadDate;
	}
	public String getLastModificationDate() {
		return lastModificationDate;
	}
	public void setLastModificationDate(String lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}
	public String getProcessInstanceByteArray() {
		return processInstanceByteArray;
	}
	public void setProcessInstanceByteArray(String processInstanceByteArray) {
		this.processInstanceByteArray = processInstanceByteArray;
	}
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	public String getProcessId() {
		return processId;
	}
	public Long getParentProcessInstanceid() {
		return parentProcessInstanceid;
	}
	public void setParentProcessInstanceid(Long parentProcessInstanceid) {
		this.parentProcessInstanceid = parentProcessInstanceid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public ArrayList<?> getEventTypes() {
		return eventTypes;
	}
	public void setEventTypes(ArrayList<?> eventTypes) {
		this.eventTypes = eventTypes;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Boolean getConversation() {
		return conversation;
	}

	public void setConversation(Boolean conversation) {
		this.conversation = conversation;
	}
}
