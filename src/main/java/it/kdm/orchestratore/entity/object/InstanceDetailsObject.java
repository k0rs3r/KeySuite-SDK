package it.kdm.orchestratore.entity.object;

import it.kdm.orchestratore.entity.InstanceDetails;

import java.util.ArrayList;
import java.util.List;

public class InstanceDetailsObject {

	private String key = "instances";
	private ArrayList<InstanceDetails> instances;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<InstanceDetails> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<InstanceDetails> instances) {
		this.instances = instances;
	}
	
	
}
