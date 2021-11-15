package it.kdm.orchestratore.executor;

import it.kdm.orchestratore.utils.SendExceptionNotification;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandlerCmdCallback;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.executor.CommandContext;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.command.Context;
import org.kie.api.executor.ExecutionResults;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncWorkItemHandlerCmdCallbackExt extends AsyncWorkItemHandlerCmdCallback {

    private static final Logger logger = LoggerFactory.getLogger(AsyncWorkItemHandlerCmdCallbackExt.class);
    public static final String HANDLED_EXCEPTION = "handled_exception";
    @Override
    public void onCommandDone(CommandContext ctx, ExecutionResults results) {
        RecurringCommand.setIndex(null);
        super.onCommandDone(ctx,results);
    }


    @Override
    public void onCommandError(CommandContext ctx, final Throwable exception) {

        try {
            super.onCommandError(ctx,exception);

            Map<String,Object> params = ctx.getData();

            final WorkItem workItem = (WorkItem) params.get("workItem");
            Long _processInstanceId = (Long) params.get(SendExceptionNotification.NT_INSTANCE_ID);
            if (_processInstanceId==null)
                _processInstanceId = (Long) params.get("processInstanceId");
            Long workItemId = workItem != null ? workItem.getId() : null;

            final Long processInstanceId = _processInstanceId;

            String processId = null;

            // find the right runtime to do the complete
            RuntimeManager manager = getRuntimeManager(ctx);
            RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            try {

                processId = engine.getKieSession().execute(new GenericCommand<String>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public String execute(Context context) {
                        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
                        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId);
                        NodeInstance nodeInstance = getNodeInstance(workItem, processInstance);

                        String exceptionName = exception.getClass().getName();
                        ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
                                ((org.jbpm.workflow.instance.NodeInstance)nodeInstance).resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);

                        if (exceptionScopeInstance != null) {
                            exceptionScopeInstance.handleException(exceptionName, exception);
                            return HANDLED_EXCEPTION;
                        } else {
                            return processInstance.getProcessId();
                        }
                    }
                });

            } catch(Exception e) {
                logger.error("Error when handling callback from executor", e);
            } finally {
                manager.disposeRuntimeEngine(engine);
            }

            // il flusso ha seguito un ramo alternativo
            if (HANDLED_EXCEPTION.equals(processId))
                return;

            logger.error("onCommandError executor: instanceId:{} workItemid:{}\n{}", processInstanceId,workItemId,exception);

            Integer recurringIndex = (Integer) ctx.getData().get("recurringIndex");
            List<Integer> recurringRetries = (List<Integer>) ctx.getData().get("recurringRetries");

            if (recurringRetries!=null && recurringRetries.size() > 0){
                if (recurringIndex==null)
                    recurringIndex = 0;
                else
                    recurringIndex++;

                ctx.getData().put("retries",0);
                ctx.getData().put("recurringIndex",recurringIndex);
                RecurringCommand.setIndex(recurringIndex);

                if (recurringIndex<recurringRetries.size()){
                    logger.info("Recurring workItemId:{} retries:{} index:{}",workItemId,recurringRetries,recurringIndex);
                    /* non mando la mail */
                    return;
                } else {
                    logger.info("Stopping workItemId:{} retries:{} index:{}",workItemId,recurringRetries,recurringIndex);
                }
            }

            String errorMessage = exception != null ? exception.getMessage() : null;
            String stackTrace = exception != null ? ExceptionUtils.getStackTrace(exception) : null;

            /*String processId = (String) params.get("processId");
            if (processId==null && processInstanceId!=null){
                try {
                    processId = RuntimeManagerRegistry.get()
                            .getManager(((WorkItemImpl) workItem).getDeploymentId())
                            .getRuntimeEngine(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()))
                            .getKieSession().getProcessInstance(workItem.getProcessInstanceId())
                            .getProcessId();
                } catch (Exception e){
                    logger.error("Errore getting processId", e);
                }
            }*/

            params = new HashMap<>(params);

            params.put(SendExceptionNotification.NT_INSTANCE_ID,processInstanceId);
            params.put(SendExceptionNotification.NT_PROCESS_ID,processId);
            params.put(SendExceptionNotification.NT_WORKITEM_ID,workItemId);
            params.put(SendExceptionNotification.NT_ERROR_MESSAGE,errorMessage);
            params.put(SendExceptionNotification.NT_STACK_TRACE,stackTrace);

            SendExceptionNotification sen = new SendExceptionNotification();
            sen.sendNotification(params);

            //if (RecurringCommand.getRetries()==0)
            //    return;

        } catch(Exception e) {
            logger.error("Errore onCommandError executor:"+ctx, e);
        }

        //throw new RuntimeException("Recurring retries are over");
    }
}
