package it.kdm.orchestratore.appdoc.controllers.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.orchestratore.appdoc.model.protocollo.DataProtocolloPage;
import it.kdm.orchestratore.appdoc.properties.PropertiesReader;
import it.kdm.orchestratore.appdoc.utils.CallDocumentMgt;
import it.kdm.orchestratore.entity.*;
import it.kdm.orchestratore.entity.object.DeployedProcessObject;
import it.kdm.orchestratore.entity.object.InstancesObject;
import it.kdm.orchestratore.entity.object.RestResponseTaskDocumentProto;
import it.kdm.orchestratore.entity.object.protocollo.TaskContents;
import it.kdm.orchestratore.server.bean.TCustomNotification;
import it.kdm.orchestratore.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;

public class BpmRestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BpmRestEngine.class);
    private static final String defaultOptionalpageSizeHistory = "60";
    private static String NEW_HOST_REST = PropertiesReader.getInstance().getServerRestPath();

    public static final String ASSEGNAZIONI_IN_CORSO="AssegnazioniInCorso";

    public static final String NEW_TASK_LIST 				= NEW_HOST_REST + "taskdocument/taskDocumentByPotentialOwner?{0}";
    public static final String NEW_TASK_LIST_STAKEHOLDER	= NEW_HOST_REST + "/taskdocument/getTasksAssignedAsTaskStakeholder?{0}";
    public static final String NEW_TASK_ASSIGNED_LIST 		= NEW_HOST_REST + "/taskdocument/taskDocumentAssign?{0}";
    public static final String NEW_TASK_BY_ID				= NEW_HOST_REST + "tasks/taskById/{0}?{1}";
    public static final String NEW_TASK_DETAILS				= NEW_HOST_REST + "tasks/getTaskContentData/{0}";
    public static final String NEW_FTL_HUMAN_TASK		 	= NEW_HOST_REST + "tasks/getTaskForm/{0}/{1}";
    public static final String NEW_INSTANCE_DATA			= NEW_HOST_REST + "process/getProcessBuffer/{0}";
    public static final String NEW_COMMENTS 				= NEW_HOST_REST + "tasks/taskCommentByIdTask/{0}";
    public static final String NEW_PROCESS_INSTANCE_DETAIL	= NEW_HOST_REST + "process/getProcessInstanceInfo/{0}";
    public static final String NEW_TASK_ACTIONS	 			= NEW_HOST_REST + "tasks/{0}/{1}";
    public static final String NEW_COMMENTS_ADD 			= NEW_HOST_REST + "tasks/taskAddCommentByIdTask/{0}";
    public static final String NEW_COMMENTS_REMOVE			= NEW_HOST_REST + "tasks/taskDelCommentByIdTask/{0}";
    public static final String NEW_INSTANCE_COMPLETE 		= NEW_HOST_REST + "process/startProcess/{0}/{1}";
    public static final String NEW_GET_TASK_BY_DOCNUM 		= NEW_HOST_REST + "taskdocument/taskDocumentByDocNum?{0}";
    public static final String NEW_GET_TASK_ASSIGN_WITH_CONTENTS 				= NEW_HOST_REST + "/taskdocument/getAllTaskDocumentProtoByDocNumAndTaskName?docNum={0}&taskName={1}";
    public static final String START_BPM_PROCESS 		    = NEW_HOST_REST + "/process/startProcessAppDoc/{0}/{1}";
    public static final String IS_ASSEGNAZIONI_IN_CORSO		    = NEW_HOST_REST + "/taskdocument/isAssignanzioneInCorso?variableName={0}&docNum={1}";
    public static final String COUNT_TASK_LIST 				= NEW_HOST_REST + "taskdocument/countTaskDocumentByPotentialOwner?{0}";
    public static final String COUNT_TASK_ASSIGNED_LIST 		= NEW_HOST_REST + "/taskdocument/countTaskDocumentAssign?{0}";
    public static final String START_FORM_PROCESS 		= NEW_HOST_REST + "process/getInMemoryProcessConfiguration?processId={0}&version={1}&resourceName=startup.ftl";
    public static final String PROCESS_CONF 		= NEW_HOST_REST + "process/getInMemoryProcessConfiguration?processId={0}&version={1}";
    public static final String PROCESS_INFO 		= NEW_HOST_REST + "process/getProcessInstanceInfo/{0}";
    public static final String MONITOR_PROCESS_INSTANCE 		= NEW_HOST_REST + "monitoring/getMonitoringObjectInstance?processInstanceId={0}";
    public static final String GET_SEND_MAILS_ACCOUNTS_BY_USERNAME = NEW_HOST_REST + "process/getSendMailsAccountsByUsername?{0}";
    public static final String CUSTOM_NOTIFICATION_GET_BY_ID 		= NEW_HOST_REST + "customnotification/getById?{0}";
    public static final String CUSTOM_NOTIFICATION_GET_BY_ACTOR 		= NEW_HOST_REST + "customnotification/getByActor?{0}";
    public static final String CUSTOM_NOTIFICATION_GET_COUNT_BY_ACTOR 		= NEW_HOST_REST + "customnotification/getCountByActor?{0}";
    public static final String CUSTOM_NOTIFICATION_USER_READ_SAVE 		= NEW_HOST_REST + "saveCustomNotificationUserRead";
    public static final String CUSTOM_NOTIFICATION_GET_COUNT_BY_ACTOR_AND_ID 		= NEW_HOST_REST + "customnotification/getByActorAndId?{0}";
    public static final String CUSTOM_NOTIFICATION_GET_BY_REFERENCE 		= NEW_HOST_REST + "customnotification/getByReference?{0}";
    public static final String CUSTOM_NOTIFICATION_GET_ALL_COUNT_BY_REFERENCE 		= NEW_HOST_REST + "customnotification/getCountAllCustomNotificationByReference?{0}";

    public static RestResponseTaskDocumentProto doCallAllTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAllTaskList");

        String restCall = MessageFormat.format(NEW_TASK_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, RestResponseTaskDocumentProto.class);

    }


    public static RestResponseTaskDocumentProto doCallStakeHolderTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAssignedTaskList");
        String restCall = MessageFormat.format(NEW_TASK_LIST_STAKEHOLDER, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, RestResponseTaskDocumentProto.class);

    }


    public static RestResponseTaskDocumentProto doCallAssignedTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAssignedTaskList");
        String restCall = MessageFormat.format(NEW_TASK_ASSIGNED_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, RestResponseTaskDocumentProto.class);

    }

    public static RestResponseTaskDocumentProto doCallGetTaskByDocNum(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAssignedTaskList");
        String restCall = MessageFormat.format(NEW_GET_TASK_BY_DOCNUM, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, RestResponseTaskDocumentProto.class);

    }

    public static String doCallGetMonitoringObjectInstanceData(RestTemplate restTemplate, String instanceId)  {

        logger.info("doCallGetMonitoringObjectInstanceData");
        String restCall = MessageFormat.format(MONITOR_PROCESS_INSTANCE, instanceId);
        String out = null;
        Gson gson = new Gson();
        out = gson.toJson(  restTemplate.getForObject(restCall, HashMap.class) );
        return out;
    }

    public static String doCallFtlStartFormProc(RestTemplate restTemplate, String id, String version) {
        logger.info("doCallFtlStartFormProc");
        String restCall = MessageFormat.format(START_FORM_PROCESS, id, version);
        String out = null;
        Gson gson = new Gson();
        out = gson.toJson(  restTemplate.getForObject(restCall, DeployedProcessObject.class) );
        return out;
    }

    public static DeployedProcessObject doCallProcessConfiguration(RestTemplate restTemplate, String id, String version, String resourceName) {
        logger.info("doCallProcessConfiguration");
        String restCall = MessageFormat.format(PROCESS_CONF, id, version);
        restCall += "&resourceName=" + resourceName;
        return restTemplate.getForObject(restCall, DeployedProcessObject.class);
    }
    public static Task doCallTaskById(RestTemplate restTemplate, String querystring, String id) throws Exception {

        logger.info("doCallTaskById");
        String restCall = MessageFormat.format(NEW_TASK_BY_ID, id, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, Task.class);

    }

    public static Map<String, Object> doCallTaskDetail(RestTemplate restTemplate, String id) {
        logger.info("doCallTaskDetail");
        String restCall = MessageFormat.format(NEW_TASK_DETAILS, id);
        return restTemplate.getForObject(restCall, Map.class);
    }

    public static String doCallFtlHumanTask(RestTemplate restTemplate, String id, String version) {
        logger.info("doCallFtlHumanTask");
        String restCall = MessageFormat.format(NEW_FTL_HUMAN_TASK, id, version);
        return restTemplate.getForObject(restCall, String.class);
    }

    public static Map<String, Object> doCallInstancesData(RestTemplate restTemplate, String id, Object a) {
        logger.info("doCallTaskDetail");
        String restCall = MessageFormat.format(NEW_INSTANCE_DATA, id);
        return restTemplate.getForObject(restCall, Map.class);
    }

    public static List<Comments> doCallComments(RestTemplate restTemplate, String id) {
        logger.info("doCallComments");
        String restCall = MessageFormat.format(NEW_COMMENTS, id);
        return restTemplate.getForObject(restCall, ArrayList.class);
    }

    public static InstancesObject doCallInstancesProcessDetail(RestTemplate restTemplate, String id) {
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(NEW_PROCESS_INSTANCE_DETAIL, id);
        InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
        return detail;
    }

    public static String doCallInstancesProcessDetailData(RestTemplate restTemplate, String instanceId)  {

        logger.info("doCallInstancesProcessDetailData");
        String restCall = MessageFormat.format(PROCESS_INFO, instanceId);
        String out = null;
        Gson gson = new Gson();
        out = gson.toJson(  restTemplate.getForObject(restCall, HashMap.class) );
        return out;
    }

    public static TaskRet doCallForward(RestTemplate restTemplate, String id, String user, String targetEntity, String comment) throws Exception {
        String token = null;
        logger.info("doCallForward");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);
        map.add("targetEntity", targetEntity);
        map.add("comment", comment);

        String restCall = MessageFormat.format(NEW_TASK_ACTIONS, "forward", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
    }

    public static TaskRet doCallDelegate(RestTemplate restTemplate, String id, String user, String targetUser, String comment) throws Exception {
        String token = null;
        logger.info("doCallDelegate");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);
        map.add("targetUser", targetUser);
        map.add("comment", comment);

        String restCall = MessageFormat.format(NEW_TASK_ACTIONS, "delegate", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
    }

    public static TaskRet doCallClaim(RestTemplate restTemplate, String id, String user) throws Exception {
        String token = null;
        logger.info("doCallClaim");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);

        String restCall = MessageFormat.format(NEW_TASK_ACTIONS, "claimTask", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
    }

    public static TaskRet doCallRelease(RestTemplate restTemplate, String id, String user) throws Exception {
        logger.info("doCallRelease");
        String token = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);

        String restCall = MessageFormat.format(NEW_TASK_ACTIONS, "releaseTask", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
    }

    public static TaskRet doCallSkip(RestTemplate restTemplate, String id, String user) throws Exception {
        logger.info("doCallSkip");
        String token = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);
        String restCall = MessageFormat.format(NEW_TASK_ACTIONS, "skipTask", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
    }

    public static String doCallAddComment(RestTemplate restTemplate, HttpServletRequest request) throws RestClientException {

        logger.info("doCallAddComment");
        String id = null;
        String restCall = null;

        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> parmameters = request.getParameterNames();
        while (parmameters.hasMoreElements()) {
            String attName = (String) parmameters.nextElement();
            map.put(attName, request.getParameter(attName));
            if ("id".equals(attName)) {
                id = request.getParameter(attName);
            }
        }

        restCall = MessageFormat.format(NEW_COMMENTS_ADD, id);
        return restTemplate.postForObject(restCall, map, String.class);
    }

    @SuppressWarnings("unchecked")
    public static String doCallRemoveComment(RestTemplate restTemplate, HttpServletRequest request) {

        logger.info("doCallRemoveComment");
        String id = null;
        String commentId = null;
        String restCall = null;

        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> parmameters = request.getParameterNames();
        while (parmameters.hasMoreElements()) {
            String attName = (String) parmameters.nextElement();
            map.put(attName, request.getParameter(attName));
            if ("id".equals(attName)) id = request.getParameter(attName);
            if ("commentId".equals(attName)) commentId = request.getParameter(attName);
            logger.info("doCallRemoveComment: {}={}", attName, request.getParameter(attName));
        }
        restCall = MessageFormat.format(NEW_COMMENTS_REMOVE, id, commentId);
        return restTemplate.postForObject(restCall, map, String.class);
    }

    public static String doCallNewInstanceComplete(RestTemplate restTemplate, HttpServletRequest request) throws Exception {
        String token = null;
        String returnType = null;
        logger.info("doCallComplete");
        String processName = new String();
        String ID = new String();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        Enumeration<String> parmameters = request.getParameterNames();
        while (parmameters.hasMoreElements()) {
            String attName = (String) parmameters.nextElement();
            logger.info(attName + " - " + request.getParameter(attName));
            if ("PID".equals(attName) || "TID".equals(attName)) {
                ID = request.getParameter(attName);
            } else if ("PROCESS_NAME".equals(attName)) {
                processName = request.getParameter(attName);
            } else {
                map.add(attName, request.getParameter(attName));
            }
        }
        //modifica token Antonio
        token = Session.getUserInfo().getJwtToken();
        if (token == null || token.equals("")) {
            logger.error("error token not flound in start instance");
        }
        map.add("userToken", token);
        String restCall = MessageFormat.format(NEW_INSTANCE_COMPLETE, ID, processName);


        logger.error("initConfig post for call startprocess:" + map.toString());
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, String>> requestEntity =
                    new HttpEntity<MultiValueMap<String, String>>(map, headers);


            returnType = restTemplate.postForObject(restCall, requestEntity, String.class);
        } catch (Exception e) {
            logger.error("Exception parameters input:" + map.toString());
            logger.error("Exceptuion :" + e);
            e.printStackTrace();

            throw e;
        }

        logger.info("end post for call startprocess");
        return returnType;
    }

    public static String doCallTaskComplete(RestTemplate restTemplate, HttpServletRequest request, String user) throws Exception {

        logger.info("doCallTaskComplete");
        String token = null;
        String ID = new String();
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> parmameters = request.getParameterNames();
        while (parmameters.hasMoreElements()) {
            String attName = (String) parmameters.nextElement();
            logger.info(attName + " - " + request.getParameter(attName));
            if ("PID".equals(attName) || "TID".equals(attName)) {
                ID = request.getParameter(attName);
            } else {
                map.put(attName, request.getParameter(attName));
            }
        }

        //modifica token Antonio


        token = Session.getUserInfo().getJwtToken();
        map.put("userToken", token);

        String restCall = MessageFormat.format(NEW_TASK_ACTIONS, "completeTask", ID);

        return restTemplate.postForObject(restCall, map, String.class);
    }





    public static List<TaskContents> doCallAssignTaskWithContentsByDocNum(RestTemplate restTemplate, String docNum) throws Exception {

        logger.info("doCallAssignTaskWithContentsByDocNum");
        String TASK_NAME_ASSIGN = PropertiesReader.getInstance().getProperty("bpm.assegnazione.htName");

        String restCall = MessageFormat.format(NEW_GET_TASK_ASSIGN_WITH_CONTENTS, docNum, TASK_NAME_ASSIGN);
        restCall = URLDecoder.decode(restCall, "UTF-8");

        List<TaskContents> taskContentsList = Lists.newArrayList(restTemplate.getForObject(restCall, TaskContents[].class));
        return taskContentsList;

    }


    public static Long doCallAssegnazioneStart(RestTemplate restTemplate,  HttpServletRequest request, Map<String,Object> parameters) throws Exception{

        logger.info("doCallComplete");

        String PID = null;
        String TID = null;



        String processId= PropertiesReader.getInstance().getProperty("bpm.assegnazione.start.processId");
        String processName= PropertiesReader.getInstance().getProperty("bpm.assegnazione.start.processName");

        String currentUser = CallDocumentMgt.getUtenteCorrente();
        String fullName=Session.getUserInfo().getFullname();
        Map<String,String> smistatoreM = new HashMap<>(0);
        smistatoreM.put("identity",currentUser);
        smistatoreM.put("name",fullName);
        smistatoreM.put("type","USER");

        parameters.put("smistatore",smistatoreM);
        String docNumPrincipale = (String)parameters.remove("docNum");


        Map<String,String> docNumM = new HashMap<>();
        docNumM.put("DOCNUM",docNumPrincipale);
        parameters.put("principale",docNumM);

        String restCall = MessageFormat.format(START_BPM_PROCESS,processId,processName);
        //return restTemplate.exchange(restCall, HttpMethod.POST, entity, String.class);
        return restTemplate.postForObject(restCall,parameters , Long.class);
    }



    public static Long doCallAssegnazioneFascicoloStart(RestTemplate restTemplate,  HttpServletRequest request, Map<String,Object> parameters) throws Exception{

        logger.info("doCallComplete");

        String PID = null;
        String TID = null;



        String processId= PropertiesReader.getInstance().getProperty("bpm.assegnazione.fascicolo.start.processId");
        String processName= PropertiesReader.getInstance().getProperty("bpm.assegnazione.fascicolo.start.processName");


        String currentUser = CallDocumentMgt.getUtenteCorrente();
        String fullName=Session.getUserInfo().getFullname();
        Map<String,String> smistatoreM = new HashMap<>(0);
        smistatoreM.put("identity",currentUser);
        smistatoreM.put("name",fullName);
        smistatoreM.put("type","USER");

        parameters.put("smistatore",smistatoreM);
        String docNumPrincipale = (String)parameters.remove("docNum");


        Map<String,String> docNumM = new HashMap<>();

        //come sequence inserisco il docnum per retrocompatibilità con la vecchia chiamata
        docNumM.put("DOCNUM",docNumPrincipale);
        parameters.put("principale",docNumM);

        String restCall = MessageFormat.format(START_BPM_PROCESS,processId,processName);
        //return restTemplate.exchange(restCall, HttpMethod.POST, entity, String.class);
        return restTemplate.postForObject(restCall,parameters , Long.class);
    }


    public static Long doCallInoltro(RestTemplate restTemplate, HttpServletRequest request, DataProtocolloPage datiProtocollo) throws Exception {
        logger.info("doCallInoltroComplete");

        String PID = null;
        String TID = null;



        String processId= PropertiesReader.getInstance().getProperty("bpm.inoltroProto.start.processId");
        String processName= PropertiesReader.getInstance().getProperty("bpm.inoltroProto.start.processName");

        ObjectMapper mapper = new ObjectMapper();
        String parametersString = null;

        try {
             parametersString = mapper.writeValueAsString(datiProtocollo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HashMap<String,Object> parameters =
                new ObjectMapper().readValue(parametersString, HashMap.class);

        String currentUser = CallDocumentMgt.getUtenteCorrente();
        String token = Session.getUserInfo().getJwtToken();
        String fullName = Session.getUserInfo().getFullname();

        Map<String,String> proponente = new HashMap<>(0);
        proponente.put("identity",currentUser);
        proponente.put("name", fullName);
        proponente.put("type", "USER");

        parameters.put("Proponente", proponente);

        parameters.put("userToken", token);
        parameters.put("AOO",  request.getAttribute("currAoo"));
        parameters.put("ENTE", Session.getUserInfo().getEnte().getCod());

        String docNumPrincipale = (String) parameters.remove("docNum");

        Map<String,String> docNumM = new HashMap<>();
        docNumM.put("DOCNUM",docNumPrincipale);
        parameters.put("Documento", docNumM);

        String restCall = MessageFormat.format(START_BPM_PROCESS, processId, processName);
        return restTemplate.postForObject(restCall, parameters , Long.class);

    }



    public static Boolean doCallIsAssegnazioniInCorso(RestTemplate restTemplate, HttpServletRequest request, String docnum, String variable) throws Exception {
        logger.info("doCallIsAssegnazioniInCorso");


        String restCall = MessageFormat.format(IS_ASSEGNAZIONI_IN_CORSO, variable, docnum);
        restCall = URLDecoder.decode(restCall, "UTF-8");


        return restTemplate.getForObject(restCall, Boolean.class);

    }

    public static Long doCallAnnullaProtocollazione(RestTemplate restTemplate,  HttpServletRequest request, Map<String,Object> parameters) throws Exception{

        logger.info("doCallAnnullaProtocollazione");

        String processId= PropertiesReader.getInstance().getProperty("bpm.annullaProto.start.processId");
        String processName= PropertiesReader.getInstance().getProperty("bpm.annullaProto.start.processName");


        String currentUser = CallDocumentMgt.getUtenteCorrente();
        parameters.put("utenteAnnullatore",currentUser);

        String token = Session.getUserInfo().getJwtToken();
        parameters.put("userToken", token);

        parameters.put("AOO",  Session.getUserInfo().getCurrentAoo().getCod());
        parameters.put("ENTE", Session.getUserInfo().getEnte().getCod());

        String restCall = MessageFormat.format(START_BPM_PROCESS,processId,processName);
        return restTemplate.postForObject(restCall,parameters , Long.class);
    }

    public static Map<String,Map<String,String>> getCustomProcsProperties(ICIFSObject object, Map<String,Object> parameters) throws Exception {

        Map<String,Map<String,String>> properties = new HashMap<>();

        List<String> processes = getCustomProcs( object, parameters);

        Iterator iter = processes.iterator();
        while (iter.hasNext()) {
            String procAlias = ""+iter.next();

            String key = String.format("bpm.%s.start.key", procAlias);
            key = key!=null ? PropertiesReader.getInstance().getProperty(key) : "";

            String args = String.format("bpm.%s.start.args", procAlias);
            args = args!=null ? PropertiesReader.getInstance().getProperty(args) : "";

            String desc = String.format("bpm.%s.start.desc", procAlias);
            desc = desc!=null ? PropertiesReader.getInstance().getProperty(desc) : "";

            Map<String,String> procProps = new HashMap<>();
            procProps.put("alias", procAlias);
            procProps.put("key", key);
            procProps.put("args", args);
            procProps.put("desc", desc);

            properties.put(procAlias, procProps);
        }

        return properties;
    }
    public static List<String> getCustomProcs(ICIFSObject object, Map<String,Object> parameters) throws Exception {

        logger.info("getCustomProcesses");

        List<String> processes = new ArrayList();

        String businessType = "" + object.getBusinessType();  //parameters.get("businessType");

        String bpmCustomProcs = PropertiesReader.getInstance().getProperty("bpm.customProcs." + businessType);

        if(!Strings.isNullOrEmpty(bpmCustomProcs)) {
            String[] list = bpmCustomProcs.split(",");
            processes = Arrays.asList(list);
        }

        return processes;
    }

    public static Long doCallCustomProc(RestTemplate restTemplate,  HttpServletRequest request, Map<String,Object> parameters) throws Exception{

        logger.info("doCallCustomProcByIndex");

        String procNum = ""+parameters.get("proc");

        String processId= String.format("bpm.customProc%s.start.processId", procNum);
        processId = PropertiesReader.getInstance().getProperty(processId);

        String processName= String.format("bpm.customProc%s.start.processName", procNum);
        processName= PropertiesReader.getInstance().getProperty(processName);

        String paramName= String.format("bpm.customProc%s.start.paramName", procNum);
        paramName= PropertiesReader.getInstance().getProperty(paramName);

        if(paramName!=null) {
            String target = (String) parameters.get("target");
            parameters.put(paramName, target);
        }

        String token = Session.getUserInfo().getJwtToken();
        parameters.put("userToken", token);

        parameters.put("AOO",  Session.getUserInfo().getCurrentAoo().getCod());
        parameters.put("ENTE", Session.getUserInfo().getEnte().getCod());

        String restCall = MessageFormat.format(START_BPM_PROCESS,processId,processName);
        return restTemplate.postForObject(restCall,parameters , Long.class);
    }

    public static List<String> doCallCountTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallCountTaskList");

        String restCall = MessageFormat.format(COUNT_TASK_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        String [] docnums =  restTemplate.getForObject(restCall, String[].class);
        return Arrays.asList(docnums);
    }

    public static List<String> doCallCountAssignedTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallCountAssignedTaskList");
        String restCall = MessageFormat.format(COUNT_TASK_ASSIGNED_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        String [] docnums =  restTemplate.getForObject(restCall, String[].class);
        return Arrays.asList(docnums);

    }

    public static HashMap<String, String> doCalGetSendMailsAccountsByUsername(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallGetSendMailsAccountsByUsername");
        String restCall = MessageFormat.format(GET_SEND_MAILS_ACCOUNTS_BY_USERNAME, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        HashMap<String, String> result =  restTemplate.getForObject(restCall, HashMap.class);
        return result;

        }

    public static TCustomNotification doCallgetCustomNotificationById(RestTemplate restTemplate, String querystring) throws Exception {
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_GET_BY_ID, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, TCustomNotification.class);
    }

    public static List<LinkedHashMap<String, Object>>  doCallgetCustomNotificationsByActor(RestTemplate restTemplate,  String querystring) throws Exception {
        logger.info("doCallgetCustomNotificationsByActor");
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_GET_BY_ACTOR,querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall,  List.class);
    }

    public static BigInteger doCallgetCountCustomNotificationsByActor(RestTemplate restTemplate, String querystring) throws Exception {
        logger.info("doCallgetCountCustomNotificationsByActor");
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_GET_COUNT_BY_ACTOR ,querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, BigInteger.class);
    }

    public static CustomNotificationUserRead doCallSaveCustomNotificationUserRead(RestTemplate restTemplate,String idCustomNotification,String priority,String isRead) throws Exception {
        logger.info("doCallSaveCustomNotificationUserRead");
        String token = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);
        map.add("idCustomNotification",idCustomNotification);
        map.add("priority",priority);
        map.add("isRead",isRead);
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_USER_READ_SAVE,null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForObject(restCall, requestEntity, CustomNotificationUserRead.class);
    }

    public static List<LinkedHashMap<String, Object>>  doCallgetCustomNotificationsByActorAndId(RestTemplate restTemplate,  String querystring) throws Exception {
        logger.info("doCallgetCustomNotificationsByActorAndId");
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_GET_COUNT_BY_ACTOR_AND_ID,querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return  restTemplate.getForObject(restCall,  List.class);
    }
    public static List<LinkedHashMap<String, Object>>  doCallgetCustomNotificationsByReference(RestTemplate restTemplate,  String querystring) throws Exception {
        logger.info("doCallgetCustomNotificationsByActor");
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_GET_BY_REFERENCE,querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall,  List.class);
    }

    public static BigInteger doCallgetCountAllCustomNotificationsByReference(RestTemplate restTemplate, String querystring) throws Exception {
        logger.info("doCallgetCountAllCustomNotificationsByReference");
        String restCall = MessageFormat.format(CUSTOM_NOTIFICATION_GET_ALL_COUNT_BY_REFERENCE ,querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        return restTemplate.getForObject(restCall, BigInteger.class);
    }

    }
