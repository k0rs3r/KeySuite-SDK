package it.kdm.orchestratore.appBpm.constants;

import it.kdm.orchestratore.appBpm.properties.PropertiesReader;

public class RestCalls {

	private static String NEW_HOST_REST = PropertiesReader.getInstance().getServerRestPath();
	
	/** OLD REST **/
	public static final String HOST_REST 					= "http://192.168.0.202:8080/gwt-console-server/rs/";
	public static final String SETTING 						= "http://192.168.0.202:8080/OrchestratoreApi/rest/services/jsonfile/{0}/{1}json";
	public static final String TASK_LIST 					= HOST_REST + "tasksData/tasks/list";
	public static final String TASK_LIST_BY_EXAMPLE 		= HOST_REST + "tasksData/tasks/list?{0}";
	public static final String INSTANCE_DEFINITION 			= HOST_REST + "tasksData/definition/{0}/instances";
	public static final String INSTANCE_PROCESS_DEFINITION 	= HOST_REST + "process/definitions";
	public static final String INSTANCE_DATA 	  			= HOST_REST + "tasksData/instance/{0}/data";
	public static final String INSTANCE_PROCESS_DETAIL 		= HOST_REST + "tasksData/process/list?id={0}";
	public static final String PARENT_PROCESS_INSTANCE 		= HOST_REST + "tasksData/process/list?parentProcessInstanceId={0}";
	public static final String TASK_CLAIM 					= HOST_REST + "task/{0}/assign/{1}";
	public static final String TASK_RELEASE 				= HOST_REST + "task/{0}/release";
	public static final String INSTANCE_LIST 				= HOST_REST + "tasksData/process/list";
	public static final String INSTANCE_LIST_BY_EXAMPLE 	= HOST_REST + "tasksData/process/list?{0}";
	public static final String NEW_INSTANCE_BY_PROCESS 		= HOST_REST + "form/process/{0}/render";
	public static final String HUMAN_TASK_BY_ID 			= HOST_REST + "form/task/{0}/render";
	public static final String INSTANCE_COMPLETE			= HOST_REST + "form/process/{0}/complete";
	public static final String TASK_COMPLETE				= HOST_REST + "form/task/{0}/complete";
	public static final String MENU_LIST 					= HOST_REST + "tasksData/definitions";
	public static final String COMMENTS 					= HOST_REST + "tasksData/task/{0}/comment";
	public static final String COMMENTS_ADD 				= HOST_REST + "tasksData/task/{0}/comment/add";
	public static final String COMMENTS_REMOVE				= HOST_REST + "tasksData/task/{0}/comment/remove/{1}";
	public static final String CLOSE_INSTANCE				= HOST_REST + "process/instance/{0}/end/OBSOLETE";

	/** NEW REST **/
	//public static final String NEW_HOST_REST 				= "http://localhost:8080/bpm-server/";
	
	public static final String NEW_MENU_LIST 				= NEW_HOST_REST + "process/getProcessDefinitions";
	public static final String NEW_TASK_LIST 				= NEW_HOST_REST + "tasks/taskByPotentialOwner?{0}";
	public static final String SEARCH_TASKS 				= NEW_HOST_REST + "searchTask/getTasks?{0}";
	public static final String EXECUTE_REPORT 				= NEW_HOST_REST + "query?{0}";
	public static final String REPORT_LIST 					= NEW_HOST_REST + "queryList?{0}";
	public static final String NEW_TASK_ASSIGNED_LIST 		= NEW_HOST_REST + "tasks/taskAssignedAsPotentialOwner?{0}";



	public static final String NEW_INSTANCE_LIST 			= NEW_HOST_REST + "process/getProcessInstances/{0}/{1}?{2}";
	
	//ritorna tutte le instanze figlie di un process
	public static final String NEW_ALL_CHILD_INSTANCE 		= NEW_HOST_REST + "/process/getAllParentProcessInstance/{0}?{1}";	
 
	public static final String NEW_TASK_ACTIONS	 			= NEW_HOST_REST + "tasks/{0}/{1}";
	public static final String NEW_TASK_DETAILS				= NEW_HOST_REST + "tasks/getTaskContentData/{0}";
	public static final String NEW_COUNT_TASK_NOTIFICATION				= NEW_HOST_REST + "tasks/getTasksNotificationCountKDM?{0}";
	public static final String NEW_TASK_BY_ID				= NEW_HOST_REST + "tasks/taskById/{0}?{1}";
	
	public static final String NEW_INSTANCE_COMPLETE 		= NEW_HOST_REST + "process/startProcess/{0}/{1}";
	public static final String NEW_SETTINGS		 			= NEW_HOST_REST + "process/getSettingProcess/{0}/{1}";

	public static final String NEW_FTL_STARTUP		 		= NEW_HOST_REST + "process/getStartupProcessForm/{0}/{1}/{2}";
	public static final String NEW_FTL_HUMAN_TASK		 	= NEW_HOST_REST + "tasks/getTaskForm/{0}/{1}";
	
	public static final String NEW_COMMENTS 				= NEW_HOST_REST + "tasks/taskCommentByIdTask/{0}";
	public static final String NEW_COMMENTS_ADD 			= NEW_HOST_REST + "tasks/taskAddCommentByIdTask/{0}";
	public static final String NEW_COMMENTS_REMOVE			= NEW_HOST_REST + "tasks/taskDelCommentByIdTask/{0}";
	

	public static final String NEW_PARENT_PROCESS_INSTANCE	= NEW_HOST_REST + "process/getProcessInstancesByParent/{0}?{1}";
	public static final String NEW_PROCESS_INSTANCE_DETAIL	= NEW_HOST_REST + "process/getProcessInstanceInfo/{0}";
	public static final String NEW_INSTANCE_DATA			= NEW_HOST_REST + "process/getProcessBuffer/{0}";
	//public static final String NEW_INSTANCE_DATA_ACTOR		= NEW_HOST_REST + "process/getFileSettingProcessLane/{0}/{1}";
	public static final String NEW_INSTANCE_DATA_COMPLETED			= NEW_HOST_REST + "process/getProcessBufferCompleted/{0}";

    public static final String PREVIEW		 	   			   = NEW_HOST_REST + "process/getFileMailPreview/{0}/{1}";
    public static final String PREVIEW_BLOCK 	   			   = NEW_HOST_REST + "process/getFileMailPreview/{0}/{1}/{2}";
    public static final String PROCESS_INSTANCE_CALL_ACTIVITY  = NEW_HOST_REST + "process/getProcessInstanceCallActivity/{0}";
    public static final String GET_EXTENDED_NODE_INSTANCE_LOG  = NEW_HOST_REST + "process/getExtendedNodeInstanceLog/{0}";
    public static final String GET_TASK_INSTANCE			   = NEW_HOST_REST + "process/getTaskInstanceById/{0}";
    public static final String PROCESS_INSTANCE_INFO           = NEW_HOST_REST + "process/getProcessInstanceInfo/{0}";

    public static final String INSTANCE_HISTORY_WITH_PARAMS    = NEW_HOST_REST + "process/instanceHistoryWithParams/{0}";
    public static final String INSTANCE_LOG_RESULT           = NEW_HOST_REST + "process/nodeInstanceLogResult/{0}";
    public static final String INSTANCE_LOG_RESULT_BY_NODE     = NEW_HOST_REST + "process/nodeInstanceLogResultByNode/{0}";

	public static final String NEW_INSTANCE_TOKEN			   = NEW_HOST_REST + "tasks/getTaskByProcessInstanceId/{0}?{1}";
	public static final String NEW_INSTANCE_TOKEN_ALL_TASK	   = NEW_HOST_REST + "tasks/getAllTaskByInstances/";
	public static final String RETRY_WORKITEM 				   = NEW_HOST_REST + "process/retryWorkItem/{0}/{1}/{2}";
    public static final String WORKITEM_LOG_INFO			   = NEW_HOST_REST + "process/logWorkitemNode/{0}/{1}";
	public static final String GET_PROCESS_INSTANCECK		   = NEW_HOST_REST + "process/getProcessInstanceByName/{0}/{1}";
    
    
    
    public static final String RESTART						  = NEW_HOST_REST + "process/restartServer";
    public static final String PROCESS_LIST_ALL				   = NEW_HOST_REST + "process/getProcessDefinitionsAll";

	public static final String SEARCH_INSTANCE_LIST 			= NEW_HOST_REST + "searchInstance/getProcessInstances?{0}";
	public static final String GET_ALL_PROCESS_TYPE		 	   = NEW_HOST_REST + "searchInstance/getAllProcessType";
    public static final String SETTINGS		 				   = NEW_HOST_REST + "process/getSettingProcess/{0}/{1}/{2}";
    public static final String SAVE_SETTINGS		 		   = NEW_HOST_REST + "process/setSettingProcess";

    public static final String REMOVE_PROCESS_DEFINITION	   = NEW_HOST_REST + "process/removeProcessDefinition";
    public static final String PUBLISH_PROCESS				   = NEW_HOST_REST + "process/publishProcess/{0}/{1}";

    public static final String COMPLETE_PROCESS_INSTANCE	   = NEW_HOST_REST + "process/completeProcessInstance/{0}";

	public static final String GET_MESSAGE_INSTANCE	           = NEW_HOST_REST + "conversation/getConversation?id={0}";
    public static final String ADD_MESSAGE_INSTANCE            = NEW_HOST_REST + "conversation/addMessage?id={0}";
	public static final String SIGNED_CONVERSATION            = NEW_HOST_REST + "conversation/signedConversation";
	public static final String MESSAGE_CONVERSATION 				= NEW_HOST_REST + "conversation/getConversationMessages?{0}";



    public static final String DEPLOY 						   = NEW_HOST_REST + "process/deployNewBpm/{0}?deploy={1}";
	public static final String SAVE_PROCESS 				   = NEW_HOST_REST + "process/saveProcess?deploy={0}&sovrascrivi={1}&configura={2}&ente={3}&aoo={4}";
	public static final String DEPLOY_PROCESS 				   = NEW_HOST_REST + "process/deployProcess?processId={0}&overwrite={1}";
	public static final String REMOVE_PROCESS_DEPLOY           = NEW_HOST_REST + "/process/removeProcessDeployed";
    public static final String SET_PROCESS_PROPERTIES		   = NEW_HOST_REST + "/process/setProcessProperties/{0}/{1}";
    public static final String GET_PROCESS_PROPERTIES		   = NEW_HOST_REST + "/process/getProcessProperties/{0}/{1}";


	public static final String GET_PROCESS_DEPLOY_LIST				   	= NEW_HOST_REST + "process/getProcessDeployList";
	public static final String GET_IN_MEMORY_PROCESS_DEFINITION   		= NEW_HOST_REST + "process/getInMemoryProcessConfiguration?processId={0}&version={1}&resourceName={2}";
	public static final String SAVE_PROCESS_DEFINITION_CONFIGURATION   	= NEW_HOST_REST + "process/saveProcessConfiguration/";
	public static final String PROCESS_DEFINITION_CONFIGURATION_LIST	= NEW_HOST_REST + "process/getProcessDefinitionsConfigurationList";
	public static final String GET_PROCESS_DEFINITION_CONFIGURATION		= NEW_HOST_REST + "process/getProcessDefinitionsConfiguration?processId={0}&ente={1}&aoo={2}";


    public static final String GET_MONITORING_OBJECT_INSTANCE           = NEW_HOST_REST + "monitoring/getMonitoringObjectInstance?processInstanceId={0}";
    public static final String GET_REQUEST_INFO                         = NEW_HOST_REST + "monitoring/getRequestInfo?processInstanceId={0}&workItemId={1}";
//    public static final String GET_TASK_INFO                         = NEW_HOST_REST + "monitoring/getTaskInfo?taskId={0}";
	public static final String GET_TASK_INFO                         = NEW_HOST_REST + "monitoring/getTaskInfo?processInstanceId={0}&workItemId={1}";
	public static final String SAVE_TASK_INFO                         = NEW_HOST_REST + "monitoring/saveTaskInfo?taskId={0}";
	public static final String RETRY_ASYNC_WORK_ITEM 				   = NEW_HOST_REST + "monitoring/retryAsynchWorkItem/{0}/{1}";
    public static final String GET_NODE_INFO                         = NEW_HOST_REST + "monitoring/getNodeInfo?workItemId={0}";

    public static final String GET_INFO_QUEUE               = NEW_HOST_REST + "manageDlq/getInfoQueue?queue={0}";
    public static final String GET_INFO_DLQ_QUEUE           = NEW_HOST_REST + "manageDlq/getInfoDlqQueues";
    public static final String READ_MESSAGE_FROM_JMS        = NEW_HOST_REST + "manageDlq/readMessageFromJMS?queue={0}";
    public static final String DELETE_MESSAGE_FROM_JMS      = NEW_HOST_REST + "manageDlq/deleteMessageFromJMS?queue={0}";
    public static final String RE_QUEUE                     = NEW_HOST_REST + "manageDlq/reQueue?queue={0}";


    public static final String PREFERRED_STATUS             = NEW_HOST_REST + "/preferredInstance/Update";
    public static final String GET_PREFERRED_PROCESS 			= NEW_HOST_REST + "/process/getPreferredInstances/{0}/{1}?{2}";

    public static final String GET_PREFERRED_PROCESS_BYSTATUS 	= NEW_HOST_REST + "/process/getPreferredInstancesByStatus/{0}/{1}?{2}";

    public static final String GET_PREFERRED_PROCESS_BYID= NEW_HOST_REST +"/preferredInstance/getPreferredInstancesById/{0}/{1}?{2}";

    public static final String GET_POTENTIAL_OWNERS 		= NEW_HOST_REST + "tasks/groupByListTaskPotential?{0}";
    public static final String GET_ASSIGNED_OWNERS 		= NEW_HOST_REST + "tasks/groupByListTaskAssigned?{0}";
	public static final String NEW_GET_ALL_TASK_BY_USER_TASKNAME 		= NEW_HOST_REST + "tasks/getAllTaskByUserByTaskName?{0}";
	public static final String GET_TASK_BY_IDS 		= NEW_HOST_REST + "tasks/getAllTaskByIds";
	public static final String GET_PROCESS_VARIABLE		 		   = NEW_HOST_REST + "process/getProcessVariable";
	public static final String SET_PROCESS_VARIABLE		 		   = NEW_HOST_REST + "/process/setProcessVariable";
	public static final String COMPLETE_SIGN_TASK					= NEW_HOST_REST + "/process/completeAllSignTask";
	public static final String TASK_NOTIFICATION					= NEW_HOST_REST + "/tasks/getTasksNotificationKDM?{0}";
	public static final String COMPLETE_INSTANCE_FORCED					= NEW_HOST_REST + "/process/completeInstanceForced?{0}";

    public static int NUM_ELEMENT_PER_PAGE = 10;
}
