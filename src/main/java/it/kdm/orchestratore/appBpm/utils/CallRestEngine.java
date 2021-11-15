package it.kdm.orchestratore.appBpm.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import it.kdm.orchestratore.appBpm.constants.RestCalls;
import it.kdm.orchestratore.appBpm.model.CallActivityMap;
import it.kdm.orchestratore.entity.*;
import it.kdm.orchestratore.entity.object.*;
import it.kdm.orchestratore.entity.object.signDocument.TaskDocumentCheckedUncheckedListForm;
import it.kdm.orchestratore.query.QueryResponse;
import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.util.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;

public class CallRestEngine {

    private static final Logger logger = LoggerFactory.getLogger(CallRestEngine.class);
    private static final String defaultOptionalpageSizeHistory = "60";

    private static final String SEPARATOR = "|";
    private static final String KS_AUTH_GROUP = "KS_AUTH_GROUP";

    public static Collection<ProcessConfigurationObject> doCallMenuList(RestTemplate restTemplate)throws Exception {

        logger.info("doCallMenuList");

        setHeaderForAutenticate(restTemplate);
        return Lists.newArrayList(restTemplate.getForObject(RestCalls.NEW_MENU_LIST, ProcessConfigurationObject[].class));
    }

    @SuppressWarnings("unchecked")
    public static Long doCallCountNotificationTask(RestTemplate restTemplate, String id)throws Exception {
        logger.info("doCallTaskDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_COUNT_TASK_NOTIFICATION, id);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, Long.class);
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> doCallTaskDetail(RestTemplate restTemplate, String id) throws Exception{
        logger.info("doCallTaskDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_TASK_DETAILS, id);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, Map.class);
    }

    public static String doCallFtlStartup(RestTemplate restTemplate, String id, String version) throws Exception{
        logger.info("doCallFtlStartup");
        String restCall = MessageFormat.format(RestCalls.NEW_FTL_STARTUP, id, version, "startup.ftl");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, String.class);
    }

    public static String doCallFtlHumanTask(RestTemplate restTemplate, String id, String version) throws Exception{
        logger.info("doCallFtlHumanTask");
        String restCall = MessageFormat.format(RestCalls.NEW_FTL_HUMAN_TASK, id, version);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, String.class);
    }


    public static TasksObject doCallAllTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAllTaskList");

        String restCall = MessageFormat.format(RestCalls.NEW_TASK_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, TasksObject.class);

    }

    public static TasksObject doSearchTasks(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doSearchTasks");

        String restCall = MessageFormat.format(RestCalls.SEARCH_TASKS, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, TasksObject.class);

    }

    public static TasksObject doCallTaskNotitications(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAllTaskList");

        String restCall = MessageFormat.format(RestCalls.TASK_NOTIFICATION, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, TasksObject.class);

    }




    public static TasksObject doCallAssignedTaskList(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallAssignedTaskList");
        String restCall = MessageFormat.format(RestCalls.NEW_TASK_ASSIGNED_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, TasksObject.class);

    }

    public static Task doCallTaskById(RestTemplate restTemplate, String querystring, String id) throws Exception {

        logger.info("doCallTaskById");
        String restCall = MessageFormat.format(RestCalls.NEW_TASK_BY_ID, id, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, Task.class);

    }


    public static List<NodeInstanceExtendedLog> doCallGetExtendedNodeInstanceLog(RestTemplate restTemplate, String processId) throws Exception {
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(RestCalls.GET_EXTENDED_NODE_INSTANCE_LOG, processId);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return Lists.newArrayList(restTemplate.getForObject(restCall, NodeInstanceExtendedLog[].class));
    }

    public static InstancesObject doCallInstancesProcess(RestTemplate restTemplate, String processName, String querystring, String codiceEnte) throws Exception {
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_LIST, processName, codiceEnte, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, InstancesObject.class);
    }


    public static InstancesObject doCallSearchInstancesProcess(RestTemplate restTemplate, Map<String, String> req, String queryString) throws Exception {
        logger.info("doCallSearchInstancesProcess");
        String restCall = MessageFormat.format(RestCalls.SEARCH_INSTANCE_LIST, queryString);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, req, InstancesObject.class);
    }


    @SuppressWarnings("unchecked")
    public static List<Comments> doCallComments(RestTemplate restTemplate, String id) throws Exception{
        logger.info("doCallComments");
        String restCall = MessageFormat.format(RestCalls.NEW_COMMENTS, id);
        setHeaderForAutenticate(restTemplate);
        return Lists.newArrayList(restTemplate.getForObject(restCall, Comments[].class));
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


        String restCall = MessageFormat.format(RestCalls.NEW_TASK_ACTIONS, "delegate", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
//		return restTemplate.getForObject(restCall,TaskRet.class);
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

        String restCall = MessageFormat.format(RestCalls.NEW_TASK_ACTIONS, "forward", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
//		return restTemplate.getForObject(restCall,TaskRet.class);
    }

    public static TaskRet doCallClaim(RestTemplate restTemplate, String id, String user) throws Exception {
        String token = null;
        logger.info("doCallClaim");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);

        String restCall = MessageFormat.format(RestCalls.NEW_TASK_ACTIONS, "claimTask", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
//		return restTemplate.getForObject(restCall,TaskRet.class);
    }

    public static TaskRet doCallRelease(RestTemplate restTemplate, String id, String user) throws Exception {
        logger.info("doCallRelease");
        String token = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        token = Session.getUserInfo().getJwtToken();
        map.add("userToken", token);

        String restCall = MessageFormat.format(RestCalls.NEW_TASK_ACTIONS, "releaseTask", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        setHeaderForAutenticate(restTemplate);
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
        String restCall = MessageFormat.format(RestCalls.NEW_TASK_ACTIONS, "skipTask", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, requestEntity, TaskRet.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> doCallInstancesDataCompleted(RestTemplate restTemplate, String id, Object a) {
        logger.info("doCallTaskDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_DATA_COMPLETED, id);
        return restTemplate.getForObject(restCall, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> doCallInstancesData(RestTemplate restTemplate, String id, Object a) throws Exception{
        logger.info("doCallTaskDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_DATA, id);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, Map.class);
    }

    /* @SuppressWarnings("unchecked")
     public static Map<String, List<Lane>> doCallInstancesDataActor(RestTemplate restTemplate, String id, String version) {
         logger.info("doCallTaskDetail");
         setMessageConverterJson(restTemplate);
         String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_DATA_ACTOR, id, version);
         return restTemplate.getForObject(restCall, Map.class);
     }
 */
    public static Data doCallInstancesData(RestTemplate restTemplate, String id)throws Exception {
        logger.info("doCallInstancesData");
        String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_DATA, id);
        Data data = restTemplate.getForObject(restCall, Data.class);
        setHeaderForAutenticate(restTemplate);
        return data;
    }

    public static InstancesObject doCallGetAllChildProcessInstances(RestTemplate restTemplate, String id, String querystring) throws Exception {
        logger.info("doCallParentProcessInstances");
        String restCall = MessageFormat.format(RestCalls.NEW_ALL_CHILD_INSTANCE, id, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
        setHeaderForAutenticate(restTemplate);
        return detail;
    }

    public static InstancesObject doCallParentProcessInstances(RestTemplate restTemplate, String id, String querystring) throws Exception {
        logger.info("doCallParentProcessInstances");
        String restCall = MessageFormat.format(RestCalls.NEW_PARENT_PROCESS_INSTANCE, id, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
        setHeaderForAutenticate(restTemplate);
        return detail;
    }

    public static InstancesObject doCallInstancesProcessDetail(RestTemplate restTemplate, String id) throws Exception{
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_PROCESS_INSTANCE_DETAIL, id);
        InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
        setHeaderForAutenticate(restTemplate);
        return detail;
    }

    public static InstancesObject doCallInstancesProcessToken(RestTemplate restTemplate, String id, String querystring) throws Exception {
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_TOKEN, id, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        InstancesObject detail = restTemplate.getForObject(restCall, InstancesObject.class);
        setHeaderForAutenticate(restTemplate);
        return detail;
    }

    public static List<Task> doCallGetAllTaskByInstances(RestTemplate restTemplate, List<Long> idIstance) throws Exception {
        logger.info("doCallGetAllTaskByInstances");

        UriTemplate template = new UriTemplate(RestCalls.NEW_INSTANCE_TOKEN_ALL_TASK);

        URI uri = template.expand(idIstance);
        ArrayList detail = Lists.newArrayList( restTemplate.postForObject(uri, idIstance, Task[].class) );
        setHeaderForAutenticate(restTemplate);
        return detail;

    }

    @SuppressWarnings("unchecked")
    public static String doCallNewInstanceComplete(RestTemplate restTemplate, HttpServletRequest request) throws Exception {
        String token = null;
        String returnType = null;
        logger.info("doCallComplete");
        String processName = new String();
        String ID = new String();

//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.setContentType(new MediaType("multipart", "form-data", Charset.forName("ISO-8859-1")));


        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        //HashMap<String, String> map = new HashMap<String, String>();
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
        String restCall = MessageFormat.format(RestCalls.NEW_INSTANCE_COMPLETE, ID, processName);


        logger.info("initConfig post for call startprocess:" + map.toString());
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
        setHeaderForAutenticate(restTemplate);
        return returnType;
    }

    @SuppressWarnings("unchecked")
    public static String doCallNewInstanceComplete(RestTemplate restTemplate, HttpServletRequest request,Map mapClone) throws Exception {
       request.getParameterMap().putAll(mapClone);
        setHeaderForAutenticate(restTemplate);
        return doCallNewInstanceComplete(restTemplate,request);

    }



    @SuppressWarnings("unchecked")
    public static String doCallTaskComplete(RestTemplate restTemplate, HttpServletRequest request, String user) throws Exception {

        logger.info("doCallTaskComplete");
        String token = null;
        String ID = new String();
       /* HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));*/
        //MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> parmameters = request.getParameterNames();

        while (parmameters.hasMoreElements()) {
            String attName = (String) parmameters.nextElement();

            int idx = attName.indexOf("[]");

            logger.info(attName + " - " + request.getParameter(attName));
            if ("PID".equals(attName) || "TID".equals(attName)) {
                ID = request.getParameter(attName);
            } else if (idx!=-1){

                List<String> vals = Arrays.asList( request.getParameterValues(attName) );
                String key = attName.substring(0,idx);
                String value = "[" + StrUtils.join(vals,',') + "]";

                map.put(key, value);

            } else {
                map.put(attName, request.getParameter(attName));
            }
        }

        //modifica token Antonio


        token = Session.getUserInfo().getJwtToken();
        map.put("userToken", token);

       String restCall = MessageFormat.format(RestCalls.NEW_TASK_ACTIONS, "completeTask", ID);
 /*        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
*/
        /*HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
*/
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, map, String.class);
    }


    public static List<CorrelationKeyInstanceObject> doCallCorrelationKeyInstance(RestTemplate restTemplate, String processId, String codiceEnte) throws Exception{

        logger.info("doCallCorrelationKeyInstance");
        String restCall = MessageFormat.format(RestCalls.GET_PROCESS_INSTANCECK, processId, codiceEnte);
        setHeaderForAutenticate(restTemplate);
        return Lists.newArrayList(restTemplate.getForObject(restCall, CorrelationKeyInstanceObject[].class));

    }

    @SuppressWarnings("unchecked")
    public static String doCallAddComment(RestTemplate restTemplate, HttpServletRequest request) throws RestClientException, Exception {

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

        restCall = MessageFormat.format(RestCalls.NEW_COMMENTS_ADD, id);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, map, String.class);
    }

    @SuppressWarnings("unchecked")
    public static String doCallRemoveComment(RestTemplate restTemplate, HttpServletRequest request) throws Exception{

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
        restCall = MessageFormat.format(RestCalls.NEW_COMMENTS_REMOVE, id, commentId);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, map, String.class);
    }


    public static InstancesObject doCallInstanceHistoryWithParams(RestTemplate restTemplate, Long id) throws Exception {
        logger.info("doCallInstanceHistoryWithParams");

        String idParameter = id != null ? String.valueOf(id) : null;

        String restCall = MessageFormat.format(RestCalls.INSTANCE_HISTORY_WITH_PARAMS, idParameter);

        restCall = URLDecoder.decode(restCall, "UTF-8");

        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, InstancesObject.class);
    }


    public static List<NodeInstanceLogResultObject> doCallInstanceLogResult(RestTemplate restTemplate, Long id) throws Exception {
        logger.info("doCallInstanceLogResult");


        String idParameter = id != null ? String.valueOf(id) : null;

        String restCall = MessageFormat.format(RestCalls.INSTANCE_LOG_RESULT, idParameter);

        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        Map response = restTemplate.getForObject(restCall, Map.class);
        List<NodeInstanceLogResultObject> ret = new ArrayList<>();

        for (Object o : (List) response.get("data")) {
            NodeInstanceLogResultObject n = new NodeInstanceLogResultObject();

            BeanUtils.populate(n, (Map) o);

            ret.add(n);
        }


        return ret;
    }

    /*public static List<TaskInstance> doCallGetTaskInstance(RestTemplate restTemplate, Long id) throws Exception {
        logger.info("doCallInstanceLogResult");

        String idParameter = id != null ? String.valueOf(id) : null;

        String restCall = MessageFormat.format(RestCalls.GET_TASK_INSTANCE, idParameter);

        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        TaskInstance[] response = restTemplate.getForObject(restCall, TaskInstance[].class);

        return Lists.newArrayList(response);
    }*/

    public static Map<String, List<NodeInstanceLogResultObject>> doCallInstanceLogResultByNode(RestTemplate restTemplate, Long id) throws Exception {
        logger.info("doCallInstanceLogResult");

        String idParameter = id != null ? String.valueOf(id) : null;

        String restCall = MessageFormat.format(RestCalls.INSTANCE_LOG_RESULT_BY_NODE, idParameter);

        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        Map response = restTemplate.getForObject(restCall, Map.class);
        Map<String, List<NodeInstanceLogResultObject>> ret = new HashMap<>();

        Map data = (Map) response.get("data");
        for (Object key : data.keySet()) {
            List l = (List) data.get(key);

            if (l != null) {
                for (Object o : l) {
                    NodeInstanceLogResultObject n = new NodeInstanceLogResultObject();
                    BeanUtils.populate(n, (Map) o);

                    List lo = ret.get(key);
                    if (lo == null) {
                        lo = new ArrayList();
                        ret.put(key.toString(), lo);
                    }
                    lo.add(n);
                }
            }
        }

        return ret;
    }


    public static List<CallActivityMap> doCallProcessInstanceCallActivity(RestTemplate restTemplate, Long id) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception {

        logger.info("doCallProcessInstanceCallActivity");
        if (id == null) {
            return new ArrayList<>();
        }

        String query = MessageFormat.format(RestCalls.PROCESS_INSTANCE_CALL_ACTIVITY, id.toString());

        setHeaderForAutenticate(restTemplate);
        List<Map<String, Object>> maps = restTemplate.getForObject(query, List.class);

        List<CallActivityMap> ret = new ArrayList<>();

        for (Map<String, Object> m : maps) {
            CallActivityMap c = new CallActivityMap();

            Integer intParent = (Integer) m.get("parentProcessInstanceId");
            Long parent = intParent != null ? Long.parseLong(intParent.toString()) : null;

            Integer intProcess = (Integer) m.get("processInstanceId");
            Long process = intProcess != null ? Long.parseLong(intProcess.toString()) : null;

            c.setParentProcessInstanceId(parent);
            c.setNodeName((String) m.get("nodeName"));
            c.setProcessInstanceId(process);

            String name = BeanUtils.getNestedProperty(m, "detail.name");
            String context = BeanUtils.getNestedProperty(m, "context");
            String version = BeanUtils.getNestedProperty(m, "detail.version");
            String processId = BeanUtils.getNestedProperty(m, "detail.processId");
            String description = BeanUtils.getNestedProperty(m, "detail.description");
            String state = BeanUtils.getNestedProperty(m, "detail.state");
            String outcome = BeanUtils.getNestedProperty(m, "detail.outcome");
            String startDate = BeanUtils.getNestedProperty(m, "detail.startDate");
            String endDate = BeanUtils.getNestedProperty(m, "detail.endDate");
            String lastModificationDate = BeanUtils.getNestedProperty(m, "detail.lastModificationDate");
            Long parentProcessInstanceid = Long.parseLong(BeanUtils.getNestedProperty(m, "detail.parentProcessInstanceid"));
            String parentProcessName = BeanUtils.getNestedProperty(m, "detail.parentProcessName");
            String parentProcessVersion = BeanUtils.getNestedProperty(m, "detail.parentProcessVersion");
            String parentProcessStatus = BeanUtils.getNestedProperty(m, "detail.parentProcessStatus");

            c.setName(name);
            c.setContextId(context);
            c.setVersion(version);
            c.setProcessId(processId);
            c.setDescription(description);
            c.setState(state);
            c.setOutcome(outcome);
            c.setStartDate(startDate);
            c.setEndDate(endDate);
            c.setLastModificationDate(lastModificationDate);
            c.setParentProcessInstanceId(parentProcessInstanceid);
            c.setParentProcessName(parentProcessName);
            c.setParentProcessVersion(parentProcessVersion);
            c.setParentProcessStatus(parentProcessStatus);

            ret.add(c);
        }

        return ret;

    }


    public static LogWorkitemNode doCallWorkitemLogInfo(RestTemplate restTemplate, String processInstanceId, String workitemId) throws Exception {

        logger.info("doCallProcessList");
        String restCall = MessageFormat.format(RestCalls.WORKITEM_LOG_INFO, processInstanceId, workitemId);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, LogWorkitemNode.class);

    }

    @SuppressWarnings("unchecked")
    public static String doCallretryWorkItem(RestTemplate restTemplate, String parameters, String workItemId, String processInstanceId, String operation) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        String restCall = MessageFormat.format(RestCalls.RETRY_WORKITEM, processInstanceId, workItemId, operation);
//		map.add("parameters", parameters);
        HttpEntity request = new HttpEntity(parameters, headers);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.postForObject(restCall, request, String.class);
    }


    public static String doCallGetFileMailPreview(RestTemplate restTemplate, String name, String version) throws Exception {
        logger.info("doCallGetFileMailPreview");
        String restCall = MessageFormat.format(RestCalls.PREVIEW, name, version);
        restCall = URLDecoder.decode(restCall, "UTF-8");

        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, String.class);
    }

    public static String doCallGetFileMailPreview(RestTemplate restTemplate, String processId, String version, String block) throws Exception {
        logger.info("doCallGetFileMailPreview");
        String resourceName = "";
        String restCall;

        if (block != null && !"".equals(block)) {
            resourceName = String.format("main_%s.html", block);
        } else {
            resourceName = "main.html";
        }

        restCall = MessageFormat.format(RestCalls.GET_IN_MEMORY_PROCESS_DEFINITION, processId, version, resourceName);
        DeployedProcessObject deployedProcessObject = restTemplate.getForObject(restCall, DeployedProcessObject.class);

        String resourceValue = new String(deployedProcessObject.getResourceValue());

        setHeaderForAutenticate(restTemplate);
        return resourceValue;
    }

    public static InstancesObject doCallGetProcessInstanceInfo(RestTemplate restTemplate, Long id) throws Exception {
        logger.info("doCallGetProcessInstanceInfo");

        String idParameter = id != null ? String.valueOf(id) : null;

        String restCall = MessageFormat.format(RestCalls.PROCESS_INSTANCE_INFO, idParameter);

        restCall = URLDecoder.decode(restCall, "UTF-8");

        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, InstancesObject.class);
    }


    public static ResponseValidateBpmn doCallRestart(RestTemplate restTemplate) throws Exception{

        logger.info("doCallRestart");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(RestCalls.RESTART, ResponseValidateBpmn.class);

    }

    public static List<ProcessConfigurationObject> getProcessDefinitionsConfigurationList(RestTemplate restTemplate) throws Exception{

        logger.info("doCallProcessList");
        setHeaderForAutenticate(restTemplate);
        return Lists.newArrayList(restTemplate.getForObject(RestCalls.PROCESS_DEFINITION_CONFIGURATION_LIST, ProcessConfigurationObject[].class));

    }

    public static List<DeployedProcessObject> doGetProcessDeployList(RestTemplate restTemplate) throws Exception{

        logger.info("doCallDeploymnetList");
        setHeaderForAutenticate(restTemplate);
        return Lists.newArrayList(restTemplate.getForObject(RestCalls.GET_PROCESS_DEPLOY_LIST, DeployedProcessObject[].class));

    }


    public static DeployedProcessObject getInMemoryProcessConfiguration(RestTemplate restTemplate, String processId, String version, String resourceName) throws Exception {

        logger.info("getInMemoryProcessConfiguration");
        String restCall = MessageFormat.format(RestCalls.GET_IN_MEMORY_PROCESS_DEFINITION, processId, version, resourceName);
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, DeployedProcessObject.class);

    }


    public static void saveConfigurationProcessDefinition(RestTemplate restTemplate, ProcessConfigurationObject processConfigurationObject) throws Exception{


        logger.info("saveConfigurationProcessDefinition");
        setHeaderForAutenticate(restTemplate);
        restTemplate.postForObject(RestCalls.SAVE_PROCESS_DEFINITION_CONFIGURATION, processConfigurationObject, ProcessConfigurationObject.class);
        logger.info("end saveConfigurationProcessDefinition");

    }


    public static ProcessConfigurationObject doCallSettings(RestTemplate restTemplate, ProcessConfigurationObject processConfigurationObject) throws Exception {
        logger.info("doCallSettings");
        String restCall = MessageFormat.format(RestCalls.SETTINGS, processConfigurationObject.getProcessId(), processConfigurationObject.getEnte(), processConfigurationObject.getAoo());
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, ProcessConfigurationObject.class);
    }

    public static List<ProcessConfigurationObject> doCallGetAllProcessType(RestTemplate restTemplate)throws Exception {
        logger.info("doCallSettings");
        //String restCall = MessageFormat.format(RestCalls.GET_ALL_PROCESS_TYPE, processConfigurationObject.getEnte(),processConfigurationObject.getAoo());
        setHeaderForAutenticate(restTemplate);

        return Lists.newArrayList(restTemplate.getForObject(RestCalls.GET_ALL_PROCESS_TYPE, ProcessConfigurationObject[].class));
    }

    public static void doCallSaveSettings(RestTemplate restTemplate, ProcessConfigurationObject processConfigurationObject) throws Exception{
        logger.info("doCallSaveSettings");
        setHeaderForAutenticate(restTemplate);

        restTemplate.postForObject(RestCalls.SAVE_SETTINGS, processConfigurationObject, ProcessConfigurationObject.class);
    }

    public static String doCallSetProcessProperties(RestTemplate restTemplate, String process, String version, String roles, String description, String category)throws Exception {
        logger.info("doCallSetProcessProperties");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("roles", roles);
        map.add("description", description);
        map.add("category", category);

        String restCall = MessageFormat.format(RestCalls.SET_PROCESS_PROPERTIES, process, version);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(restCall, map, String.class);
    }


    public static ProcessItem doCallGetProcessProperties(RestTemplate restTemplate, String process, String version) throws Exception{
        logger.info("doCallGetProcessProperties");
        String restCall = MessageFormat.format(RestCalls.GET_PROCESS_PROPERTIES, process, version);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, ProcessItem.class);
    }


    public static String doCallPubblishProcess(RestTemplate restTemplate, String id, String version) throws Exception {

        logger.info("doCallProcessList");
        String restCall = MessageFormat.format(RestCalls.PUBLISH_PROCESS, id, version);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, String.class);

    }

    public static void doCallRemoveProcessDefinition(RestTemplate restTemplate, ProcessConfigurationObject processConfigurationObject)throws Exception {

        logger.info("doCallRemoveProcessDefinition");

        setHeaderForAutenticate(restTemplate);

        restTemplate.postForObject(RestCalls.REMOVE_PROCESS_DEFINITION, processConfigurationObject, ProcessConfigurationObject.class);

    }

    public static String doCallRemoveDeployedProcess(RestTemplate restTemplate, DeployedProcessObject deployedProcessObject) throws Exception{

        logger.info("doCallRemoveDeployedProcess");

        //restTemplate.postForObject(RestCalls.REMOVE_PROCESS_DEPLOY, deployedProcessObject, DeployedProcessObject.class );
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(RestCalls.REMOVE_PROCESS_DEPLOY, deployedProcessObject, String.class);

    }


    public static ProcessConfigurationObject getProcessConfiguration(RestTemplate restTemplate, ProcessConfigurationObject processConfigurationObject) throws Exception{

        logger.info("doCallRemoveProcessDefinition");
        String restCall = MessageFormat.format(RestCalls.GET_PROCESS_DEFINITION_CONFIGURATION, processConfigurationObject.getProcessId(), processConfigurationObject.getEnte(), processConfigurationObject.getAoo());
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, ProcessConfigurationObject.class);

    }


    public static String doCallCompleteProcessInstance(RestTemplate restTemplate, String id, String outcome) throws Exception {
        logger.info("doCallCompleteProcessInstance");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("outCome", outcome);
        String restCall = MessageFormat.format(RestCalls.COMPLETE_PROCESS_INSTANCE, id);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(restCall, map, String.class);
//		return restTemplate.getForObject(restCall,TaskRet.class);
    }

    public static ConversationObject doCallGetMessageInstance(RestTemplate restTemplate, String processInstanceId) throws Exception {
        logger.info("doCallGetMessageInstance");

        String restCall = MessageFormat.format(RestCalls.GET_MESSAGE_INSTANCE, processInstanceId);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, ConversationObject.class);
    }

    public static void doCallAddMessageInstance(RestTemplate restTemplate, MessageConversationObject messageConversationObject,
                                                String processInstanceId) throws Exception {
        logger.info("doCallAddMessageInstance");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MessageConversationObject> requestEntity =
                new HttpEntity<MessageConversationObject>(messageConversationObject, headers);
        String restCall = MessageFormat.format(RestCalls.ADD_MESSAGE_INSTANCE, processInstanceId);
        setHeaderForAutenticate(restTemplate);

        restTemplate.postForObject(restCall, messageConversationObject, String.class);
    }

    public static void doCallSignedConversation(RestTemplate restTemplate, MessageConversationObject messageConversationObject
    ) throws Exception {
        logger.info("doCallSignedConversation");
        setHeaderForAutenticate(restTemplate);

        restTemplate.postForObject(RestCalls.SIGNED_CONVERSATION, messageConversationObject, String.class);
    }

    public static ResponseValidateBpmn doCallNewProcessDefinition(RestTemplate restTemplate, String id, HttpEntity file, String sovrascrivi) throws Exception{

        logger.info("doCallNewProcessDefinition");
        String restCall = MessageFormat.format(RestCalls.DEPLOY, sovrascrivi);
        setHeaderForAutenticate(restTemplate);

        ResponseValidateBpmn resp = restTemplate.postForObject(restCall, file, ResponseValidateBpmn.class);
        logger.info("end doCallNewProcessDefinition");
        return resp;
    }

    public static ResponseValidateBpmn doCallSaveProcess(RestTemplate restTemplate, HttpEntity file, String ente, String aoo, String sovrascrivi, String deploy, String configura) throws Exception{

        logger.info("doCallSaveProcess");
        String restCall = MessageFormat.format(RestCalls.SAVE_PROCESS, deploy,sovrascrivi,configura,ente,aoo);
        setHeaderForAutenticate(restTemplate);

        ResponseValidateBpmn resp = restTemplate.postForObject(restCall, file, ResponseValidateBpmn.class);
        logger.info("end doCallSaveProcess");
        return resp;
    }

    public static ResponseValidateBpmn doCallDeployProcess(RestTemplate restTemplate, String processId, Boolean overwrite) throws Exception{

        logger.info("doCallSaveProcess");
        String restCall = MessageFormat.format(RestCalls.DEPLOY_PROCESS, processId, overwrite);
        setHeaderForAutenticate(restTemplate);

        ResponseValidateBpmn resp = restTemplate.getForObject(restCall, ResponseValidateBpmn.class);
        logger.info("end doCallSaveProcess");
        return resp;
    }

    //Rest per monitoring

    public static InstanceMonitoringNodeObject doCallMonitoringObjectInstance(RestTemplate restTemplate, String processInstanceId) throws Exception {
        logger.info("doCallMonitoringObjectInstance");
        String restCall = MessageFormat.format(RestCalls.GET_MONITORING_OBJECT_INSTANCE, processInstanceId);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, InstanceMonitoringNodeObject.class);
    }

    public static RequestInfoObject doCallAsyncWorkItemDetails(RestTemplate restTemplate, String processInstanceId, String workItemId) throws Exception {
        logger.info("doCallAsyncWorkItemDetails");
        String restCall = MessageFormat.format(RestCalls.GET_REQUEST_INFO, processInstanceId, workItemId);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, RequestInfoObject.class);
    }

    public static RequestInfoObject doCallTaskWorkItemDetails(RestTemplate restTemplate, String taskId) throws Exception {
        logger.info("doCallAsyncWorkItemDetails");
        String restCall = MessageFormat.format(RestCalls.GET_TASK_INFO, taskId);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, RequestInfoObject.class);
    }

    public static String doCallTaskWorkItemSaveDetails(RestTemplate restTemplate, String parameters, String taskId) throws Exception {
        logger.info("doCallretryAsyncWorkItem");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        String restCall = MessageFormat.format(RestCalls.SAVE_TASK_INFO, taskId);
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        HashMap<String, Object> o = objectMapper.readValue(parameters, typeRef);
        HttpEntity request = new HttpEntity(o, headers);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(restCall, request, String.class);
    }

    public static String doCallretryAsyncWorkItem(RestTemplate restTemplate, String parameters, String workItemId, String processInstanceId) throws Exception {
        logger.info("doCallretryAsyncWorkItem");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        String restCall = MessageFormat.format(RestCalls.RETRY_ASYNC_WORK_ITEM, processInstanceId, workItemId);
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        HashMap<String, Object> o = objectMapper.readValue(parameters, typeRef);
        HttpEntity request = new HttpEntity(o, headers);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(restCall, request, String.class);
    }

    public static MonitoringNodeObject doCallgetNodeInfo(RestTemplate restTemplate, String workItemId) throws Exception {
        logger.info("doCallgetNodeInfo");
        String restCall = MessageFormat.format(RestCalls.GET_NODE_INFO, workItemId);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, MonitoringNodeObject.class);
    }

    //Rest per la gestione delle code DLQ

    public static List<QueueObject> doCallGetInfoDlqQueues(RestTemplate restTemplate) throws Exception {
        logger.info("doCallGetInfoDlqQueues");
        String restCall = URLDecoder.decode(RestCalls.GET_INFO_DLQ_QUEUE, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return Lists.newArrayList(restTemplate.getForObject(restCall, QueueObject[].class));
    }

    public static JmsMessageObject doCallReadMessageFromJMS(RestTemplate restTemplate, String queue) throws Exception {
        logger.info("doCallReadMessageFromJMS");
        String restCall = MessageFormat.format(RestCalls.READ_MESSAGE_FROM_JMS, queue);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, JmsMessageObject.class);
    }

    public static void doCallDeleteMessageFromJMS(RestTemplate restTemplate, String queue) throws Exception {
        logger.info("doCallDeleteMessageFromJMS");
        String restCall = MessageFormat.format(RestCalls.DELETE_MESSAGE_FROM_JMS, queue);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        restTemplate.delete(restCall);
    }

    public static void doCallReQueue(RestTemplate restTemplate, String queue, String messaggio) throws Exception {
        logger.info("doCallReadMessageFromJMS");



        String restCall = MessageFormat.format(RestCalls.RE_QUEUE, queue);
        restCall = URLDecoder.decode(restCall, "UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        HashMap<String, Object> o = objectMapper.readValue(messaggio, typeRef);

        setHeaderForAutenticate(restTemplate);

        restTemplate.postForObject(restCall, o, String.class);
    }





    public static Boolean doCallsetPreferredStatus(RestTemplate restTemplate, String processInstanceId) throws Exception {

        logger.info("doCallsetPreferredStatus");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", String.valueOf(MediaType.TEXT_PLAIN));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("multipart", "form-data"));

        String username = Session.getUserInfo().getUsername();
        String token = Session.getUserInfo().getJwtToken();
       Map<String,String> postParameters= new HashMap<String,String>();
        postParameters.put("userId",username);
        postParameters.put("token",token);
        postParameters.put("processInstanceId",processInstanceId);
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(RestCalls.PREFERRED_STATUS, postParameters, Boolean.class);

    }





    public static InstancesObject doCallgetPreferredByStatus(RestTemplate restTemplate, String userId, String querystring, String codiceEnte) throws Exception {
        logger.info("doCallGetPreferredStatus");
        String restCall = MessageFormat.format(RestCalls.GET_PREFERRED_PROCESS_BYSTATUS, userId, codiceEnte, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, InstancesObject.class);
    }

    public static InstancesObject doCallgetPreferred(RestTemplate restTemplate, String userId, String querystring, String codiceEnte) throws Exception {
        logger.info("doCallGetPreferredStatus");
        String restCall = MessageFormat.format(RestCalls.GET_PREFERRED_PROCESS, userId, codiceEnte, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, InstancesObject.class);
    }



    public static Map doCallgetPreferredById(RestTemplate restTemplate, String userId, String querystring, String codiceEnte) throws Exception {
        logger.info("doCallGetPreferredStatus");
        String restCall = MessageFormat.format(RestCalls.GET_PREFERRED_PROCESS_BYID, userId, codiceEnte, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall, Map.class);
    }

    public static List<GroupByPotOwners> doCallgetPotentialOwners(RestTemplate restTemplate, String querystring) throws Exception {
        logger.info("doCallgetPotentialOwners");
        String restCall = MessageFormat.format(RestCalls.GET_POTENTIAL_OWNERS, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");

        setHeaderForAutenticate(restTemplate);

        GroupByPotOwnersWrap groupByPotOwnersWrap= restTemplate.getForObject(restCall, GroupByPotOwnersWrap.class);
        return groupByPotOwnersWrap.getGroupByPotOwnersList();

    }

    public static List<GroupByPotOwners> doCallgetAssignedOwners(RestTemplate restTemplate, String querystring) throws Exception {
        logger.info("doCallgetAssignedOwners");
        String restCall = MessageFormat.format(RestCalls.GET_ASSIGNED_OWNERS, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        GroupByPotOwnersWrap groupByPotOwnersWrap= restTemplate.getForObject(restCall, GroupByPotOwnersWrap.class);
        return groupByPotOwnersWrap.getGroupByPotOwnersList();
    }


    public static List<TaskDocument> doCallGetTaskFirmaRemotaByUser(RestTemplate restTemplate, String querystring) throws Exception {

        logger.info("doCallGetTaskFirmaRemotaByUser");
        String restCall = MessageFormat.format(RestCalls.NEW_GET_ALL_TASK_BY_USER_TASKNAME, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return Lists.newArrayList(restTemplate.getForObject(restCall, TaskDocument[].class));



    }


    public static List<TaskDocument> getAllTaskByIds(RestTemplate restTemplate, List<Integer> listIds) throws Exception {

        logger.info("doCallSearchInstancesProcess");
        setHeaderForAutenticate(restTemplate);

        return Lists.newArrayList(restTemplate.postForObject(RestCalls.GET_TASK_BY_IDS, listIds, TaskDocument[].class));

    }


    public static ProcessVariable doCallGetInstanceVariable(RestTemplate restTemplate, ProcessVariable ProcessVariable) throws Exception {
        logger.info("doCallGetInstanceVariable");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(RestCalls.GET_PROCESS_VARIABLE, ProcessVariable, ProcessVariable.class);
    }


    public static ProcessVariable doCallSetInstanceVariable(RestTemplate restTemplate, ProcessVariable ProcessVariable) throws Exception {
        logger.info("doCallGetInstanceVariable");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.postForObject(RestCalls.SET_PROCESS_VARIABLE, ProcessVariable, ProcessVariable.class);
    }


    public static List<Integer> doCallCompleteSignTask(RestTemplate restTemplate, TaskDocumentCheckedUncheckedListForm taskDocumentCheckedUncheckedListForm) throws Exception {
        logger.info("doCallGetInstanceVariable");
        setHeaderForAutenticate(restTemplate);

        Integer[] tasks = restTemplate.postForObject(RestCalls.COMPLETE_SIGN_TASK, taskDocumentCheckedUncheckedListForm, Integer[].class);
        return Arrays.asList(tasks);
    }

    public static MessageConversationObjectResponse getMessageConversationObject(RestTemplate restTemplate, String querystring)  throws Exception{
        logger.info("doCallAllTaskList");

        String restCall = MessageFormat.format(RestCalls.MESSAGE_CONVERSATION, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);

        return restTemplate.getForObject(restCall,MessageConversationObjectResponse.class);
    }


    public static void setHeaderForAutenticate(RestTemplate restTemplate) throws Exception{
        //Leggo le ingo utente dalla Session
        if(restTemplate.getInterceptors() ==  null || (restTemplate.getInterceptors() != null && restTemplate.getInterceptors().size()==0)) {
            UserInfo ui = Session.getUserInfo();

            //Leggo i ruoli e costruisco una stringa che li concatena con il SEPARATOR
            /*HashMap<String, String> rolesHm = ui.getRole();
            StringBuffer sb = new StringBuffer();
            for (String kr: rolesHm.keySet()) {
                sb.append(kr + SEPARATOR);
            }
            String roles = sb.toString();
            roles = roles.endsWith(SEPARATOR) ? roles.substring(0,roles.length()-1) : roles;


            //Leggo i gruppi e costruisco una stringa che li concatena con il SEPARATOR
            final HashMap<String, HashMap<String, String>> groupsHm = ui.getStructureGroup();
            sb = new StringBuffer();
            for (String kg : groupsHm.keySet()){
                sb.append(kg + SEPARATOR);
            }

            String groups = sb.toString();
            groups = groups.endsWith(SEPARATOR) ? groups.substring(0,groups.length()-1) : groups;

            //Costruisco la Stringa settare nell'Header
            final String ksAuthGroup = ui.getUsername() + SEPARATOR + ui.getCodEnte() + SEPARATOR + ui.getCodAoo() + SEPARATOR + roles + SEPARATOR + groups;
            */

            final String ksAuthGroup = StringUtils.join( ui.getActors(), "|" );


            //Setto il custom header per l'autenticazione
            restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
                @Override
                public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                    request.getHeaders().set(KS_AUTH_GROUP, ksAuthGroup);
                    return execution.execute(request, body);
                }
            });
        }

    }
    public static List<Map<String,Object>> doCallReportList(RestTemplate restTemplate, String querystring) throws Exception {
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(RestCalls.REPORT_LIST, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, List.class);
    }

    public static QueryResponse doCallExecuteReport(RestTemplate restTemplate, String querystring) throws Exception {
        logger.info("doCallInstancesProcessDetail");
        String restCall = MessageFormat.format(RestCalls.EXECUTE_REPORT, querystring);
        restCall = URLDecoder.decode(restCall, "UTF-8");
        setHeaderForAutenticate(restTemplate);
        return restTemplate.getForObject(restCall, QueryResponse.class);
    }

    public static Boolean doCallCompleteInstanceForced(RestTemplate restTemplate, Long instanceId) throws Exception {
        logger.info("doCallGetInstanceVariable");
        setHeaderForAutenticate(restTemplate);
        String query = "instanceId="+instanceId;
        String url = MessageFormat.format(RestCalls.COMPLETE_INSTANCE_FORCED, query);
        Boolean res = restTemplate.postForObject(url, instanceId, Boolean.class);

        return res;
    }
}
