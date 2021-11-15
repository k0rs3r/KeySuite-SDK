package it.kdm.orchestratore.appBpm.utils;

import it.kdm.orchestratore.appBpm.constants.RestCalls;
import it.kdm.orchestratore.appBpm.properties.PropertiesReader;
import it.kdm.orchestratore.entity.Comments;
import it.kdm.orchestratore.entity.Data;
import it.kdm.orchestratore.entity.Definition;
import it.kdm.orchestratore.entity.object.DefinitionsObject;
import it.kdm.orchestratore.entity.object.InstancesObject;
import it.kdm.orchestratore.entity.object.MenuObject;
import it.kdm.orchestratore.entity.object.TasksObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class CallOldRestEngine {

	private static final Logger logger = LoggerFactory.getLogger(CallOldRestEngine.class);
	
	// GET 
	public static String doCallNewInstance(RestTemplate restTemplate, String id) {

		logger.info("doCallNewInstance");
		setMessageConverterStringHttp(restTemplate);
		String restCall = MessageFormat.format( RestCalls.NEW_INSTANCE_BY_PROCESS,id);
		return  restTemplate.getForObject(restCall, String.class);
	}
	
	public static MenuObject doCallMenuList(RestTemplate restTemplate) {
		
		logger.info("doCallMenuList");
		setMessageConverterJson(restTemplate);
		return restTemplate.getForObject(RestCalls.MENU_LIST, MenuObject.class);

	}
	

	public static TasksObject doCallTaskListByQS(RestTemplate restTemplate,  String querystring) {
		
		logger.info("doCallTaskListByQS");
		setMessageConverterJson(restTemplate);
		String restCall  	= MessageFormat.format(RestCalls.TASK_LIST_BY_EXAMPLE,querystring);
		return restTemplate.getForObject(restCall, TasksObject.class);
		
	}
	


	
	public static TasksObject doCallTaskList(RestTemplate restTemplate) {
		
		logger.info("doCallTaskList");
		setMessageConverterJson(restTemplate);
		return restTemplate.getForObject(RestCalls.TASK_LIST, TasksObject.class);
		
	}
	public static String doCallHumanTaskRenderer(RestTemplate restTemplate,  String id) {
		
		logger.info("doCallHumanTaskRenderer");
		setMessageConverterStringHttp(restTemplate);
		String restCall  	= MessageFormat.format(RestCalls.HUMAN_TASK_BY_ID,id);
		return restTemplate.getForObject(restCall, String.class);
		
	}
	
	public static HashMap<String, Object> doCallSettingLabels(RestTemplate restTemplate,String instanceId){
		logger.info("doCallSettingLabels");
		DefinitionsObject definitionsObj = restTemplate.getForObject(RestCalls.INSTANCE_PROCESS_DEFINITION, DefinitionsObject.class);
		ArrayList<Definition> definitions = definitionsObj.getDefinitions();
		String packageName = "";
		for (Definition definition : definitions) {
			if(instanceId.equals(definition.getId())){
				packageName = definition.getPackageName();
				break;
			}
		}
		String restCall = MessageFormat.format(RestCalls.SETTING,packageName,instanceId);
		HashMap<String, Object> hash = restTemplate.getForObject(restCall, HashMap.class);
		
		return hash;
	}
	public static HashMap<String, Object> doCallInstancesData(RestTemplate restTemplate,  String id , Object a){
		logger.info("doCallInstancesDataHash");
		String restCall = MessageFormat.format(RestCalls.INSTANCE_DATA,id);
		HashMap<String, Object> data = restTemplate.getForObject(restCall, HashMap.class);
		return data;
	}
	public static Data doCallInstancesData(RestTemplate restTemplate,  String id){
		logger.info("doCallInstancesData");
		String restCall  	= MessageFormat.format(RestCalls.INSTANCE_DATA,id);
		Data data = restTemplate.getForObject(restCall, Data.class);
		return data;
	}
	public static InstancesObject doCallInstancesProcessDetail(RestTemplate restTemplate,  String id){
		logger.info("doCallInstancesProcessDetail");
		String restCall  	= MessageFormat.format(RestCalls.INSTANCE_PROCESS_DETAIL,id);
		InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
		return detail;
	}
	public static Comments doCallComments(RestTemplate restTemplate,  String id) {
		logger.info("doCallComments");
		setMessageConverterJson(restTemplate);
		String restCall  	= MessageFormat.format(RestCalls.COMMENTS,id);
		return restTemplate.getForObject(restCall, Comments.class);
	}
	public static InstancesObject doCallParentProcessInstances(RestTemplate restTemplate, String id){
		logger.info("doCallParentProcessInstances");
		String restCall  	= MessageFormat.format(RestCalls.PARENT_PROCESS_INSTANCE,id);
		InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
		return detail;
	}
	
	
	// POST
	public static String doCallClaim(RestTemplate restTemplate,  HttpServletRequest request, String id) {
		
		logger.info("doCallClaim");
		setMessageConverterMixed(restTemplate);
		String restCall  	= MessageFormat.format(RestCalls.TASK_CLAIM,id,request.getHeader(PropertiesReader.getInstance().getSSOUSER()));
		return restTemplate.postForObject(restCall,null, String.class);
	}
	public static String doCallRelease(RestTemplate restTemplate,  HttpServletRequest request, String id) {
		
		logger.info("doCallRelease");
		setMessageConverterMixed(restTemplate);
		String restCall  	= MessageFormat.format(RestCalls.TASK_RELEASE,id);
		return restTemplate.postForObject(restCall,null,String.class);
	}
	public static String doCallClose(RestTemplate restTemplate,  HttpServletRequest request, String id) {
		
		logger.info("doCallClose");
		setMessageConverterMixed(restTemplate);
		String restCall  	= MessageFormat.format(RestCalls.CLOSE_INSTANCE,id);
		return restTemplate.postForObject(restCall, null ,String.class);
	}
	public static String doCallAddComment(RestTemplate restTemplate,  HttpServletRequest request) {
		
		logger.info("doCallAddComment");
		String id=null;
		String restCall = null;
		setMessageConverterMixed(restTemplate);
		HttpHeaders requestHeaders = new HttpHeaders();	
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		Enumeration<String> parmameters =  request.getParameterNames();
		while(parmameters.hasMoreElements()) {
			String attName = (String) parmameters.nextElement();
			map.add(attName, request.getParameter(attName));
			if("id".equals(attName)) {
				id = request.getParameter(attName);
			}
		} 
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, requestHeaders);
		restCall  	= MessageFormat.format(RestCalls.COMMENTS_ADD,id);
		return restTemplate.postForObject(restCall,entity, String.class);
	}
	public static String doCallRemoveComment(RestTemplate restTemplate,  HttpServletRequest request) {
		
		logger.info("doCallRemoveComment");
		String id=null;
		String commentId=null;
		String restCall = null;
		setMessageConverterMixed(restTemplate );
		HttpHeaders requestHeaders = new HttpHeaders();	
		requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		Enumeration<String> parmameters =  request.getParameterNames();
		while(parmameters.hasMoreElements()) {
			String attName = (String) parmameters.nextElement();
			map.add(attName, request.getParameter(attName));
			if("id".equals(attName)) id = request.getParameter(attName);
			if("commentId".equals(attName)) commentId = request.getParameter(attName);
		} 
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, requestHeaders);
		restCall  	= MessageFormat.format(RestCalls.COMMENTS_REMOVE,id,commentId);
		return restTemplate.postForObject(restCall,entity, String.class);
	}	
	

	public static String doCallComplete(RestTemplate restTemplate,  HttpServletRequest request) {
		
		logger.info("doCallComplete");
		
		String PID = null;
		String TID = null;
		String restCall = null;
		
		setMessageConverterMixed(restTemplate );
		
		HttpHeaders requestHeaders = new HttpHeaders();	
		//requestHeaders.set("SSO_USER", request.getHeader("SSO_USER"));
		requestHeaders.setContentType(new MediaType("multipart", "form-data"));
	
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		
		Enumeration<String> parmameters =  request.getParameterNames();
		while(parmameters.hasMoreElements()) {
			String attName = (String) parmameters.nextElement();
			logger.info(attName + " - " +  request.getParameter(attName));
			
			if("PID".equals(attName)) {
				PID = request.getParameter(attName);
			} else if("TID".equals(attName)) {
				TID = request.getParameter(attName);
			} else {
				map.add(attName, request.getParameter(attName));
			}
		} 
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, requestHeaders);
		
		if (TID != null){
			
			restCall  	= MessageFormat.format(RestCalls.TASK_COMPLETE,TID);
		
		} else {
			
			restCall  	= MessageFormat.format(RestCalls.INSTANCE_COMPLETE,PID);
		}
		
		//return restTemplate.exchange(restCall, HttpMethod.POST, entity, String.class);
		return restTemplate.postForObject(restCall,entity, String.class);
	}


	
	
	// CONVERTERS
	private static void setMessageConverterStringHttp(RestTemplate restTemplate){

		List<HttpMessageConverter<?>> converter = new ArrayList<HttpMessageConverter<?>>();
		converter.add(new StringHttpMessageConverter());
		restTemplate.setMessageConverters(converter);

	}
	private static void setMessageConverterJson(RestTemplate restTemplate){

		List<HttpMessageConverter<?>> converter = new ArrayList<HttpMessageConverter<?>>();
		converter.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converter);

	}	

	@SuppressWarnings("unused")
	private static void setMessageConverterFormHttp(RestTemplate restTemplate){

		List<HttpMessageConverter<?>> converter = new ArrayList<HttpMessageConverter<?>>();
		converter.add(new FormHttpMessageConverter());
		restTemplate.setMessageConverters(converter);

	}	
	
	private static void setMessageConverterMixed(RestTemplate restTemplate){
		
		List<HttpMessageConverter<?>> converter = new ArrayList<HttpMessageConverter<?>>();
		converter.add(new FormHttpMessageConverter());
		converter.add(new StringHttpMessageConverter());
		converter.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converter);

		
	}
	
	
	
}
