package it.kdm.orchestratore.entity.object;

import com.fasterxml.jackson.databind.ObjectMapper;


public class LogWorkitemNode {
	
	public static final String SEPARATOR_FILE = "-------";
	public static final String INPUT = "INPUT";
	public static final String ERROR = "ERROR";
	public static final String OUTPUT = "OUTPUT";
	public static final String INIT = "INIT";
	public static final String END = "END";
	
	
	private String key;
	private String output;
	private String input;
	private String error;
	private String init;
	private String end;
	private String action;
	
	
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOutput() {
			return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getInput() {
			return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getInit() {
		return init;
	}
	public void setInit(String init) {
		this.init = init;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	
	
}
