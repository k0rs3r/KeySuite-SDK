package it.kdm.orchestratore.jms.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

@Deprecated
public class BpmnMessage {

	public enum ForwardTypeEnum{
		none,
		ufficio,
		istanza
	}
	
	private String forwardTo;
	
	private ForwardTypeEnum forwardType=ForwardTypeEnum.none;
	
	private HashMap <String,Object> props = new HashMap <String,Object>();

	private String channel;
	
	//identificativo dell'elemento sul message store
	private String id;

	public BpmnMessage(){

	}
	
	public BpmnMessage(HashMap <String,Object> props){
		this.props = props;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannel() {
		String channel = (String) getProperty("Channel");
		return channel;
	}

	public void setChannel(String channel) {
		setProperty("CHANNEL", channel);
	}

	public HashMap<String, Object> getProps() {
		return props;
	}

	public void setProps(HashMap<String, Object> props) {
		this.props = props;
	}

	public Object getProperty(String key){
		
		if(props.containsKey(key)){
			return props.get(key);
		}else{
			return "";
		}
	}

	public void setProperty(String key, Object value) {
		props.put(key, value);
	}
	
	public ForwardTypeEnum getForwardType() {
		return forwardType;
	}

	public void setForwardType(ForwardTypeEnum forwardType) {
		this.forwardType = forwardType;
	}

	public String toJson() throws JsonProcessingException{
    	ObjectMapper mapper = new ObjectMapper();
    	return mapper.writeValueAsString(props);
    }

	public String getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(String forwardTo) {
		this.forwardTo = forwardTo;
	}
	
}
