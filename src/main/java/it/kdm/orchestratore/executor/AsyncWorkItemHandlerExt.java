package it.kdm.orchestratore.executor;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsyncWorkItemHandlerExt extends AsyncWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AsyncWorkItemHandlerExt.class);

    protected ExecutorService executorService;
    String commandClass;

    public AsyncWorkItemHandlerExt(ExecutorService executorService) {
        super(executorService);
        this.executorService = executorService;
    }

    public AsyncWorkItemHandlerExt(ExecutorService executorService, String commandClass) {
        super(executorService, commandClass);
        this.executorService = executorService;
        this.commandClass = commandClass;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        WorkflowProcessInstance pi = (WorkflowProcessInstance) RuntimeManagerRegistry.get()
                .getManager(((WorkItemImpl)workItem).getDeploymentId())
                .getRuntimeEngine(ProcessInstanceIdContext.get(workItem.getProcessInstanceId()))
                .getKieSession().getProcessInstance(workItem.getProcessInstanceId());

        String codEnte = (String) pi.getVariable("INSTANCE_ENTE");
        String codAoo = (String) pi.getVariable("INSTANCE_AOO");
        String processId = pi.getProcessId();

        String callbacks = (String) workItem.getParameter("callbacks");
        if (callbacks == null){
            callbacks = AsyncWorkItemHandlerCmdCallbackExt.class.getName();
        }

        if (executorService == null || !executorService.isActive()) {
            throw new IllegalStateException("Executor is not set or is not active");
        }
        String businessKey = buildBusinessKey(workItem);
        logger.debug("Executing work item {} with built business key {}", workItem, businessKey);
        String cmdClass = (String) workItem.getParameter("CommandClass");
        if (cmdClass == null) {
            cmdClass = this.commandClass;
        }

        logger.debug("Command class for this execution is {}", cmdClass);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", businessKey);
        ctxCMD.setData("workItem", workItem);
        ctxCMD.setData("processInstanceId", getProcessInstanceId(workItem));
        ctxCMD.setData("processId", processId);
        ctxCMD.setData("codEnte", codEnte);
        ctxCMD.setData("codAoo", codAoo);
        ctxCMD.setData("deploymentId", ((WorkItemImpl)workItem).getDeploymentId());
        ctxCMD.setData("callbacks", callbacks);
        if (workItem.getParameter("Retries") != null) {
            ctxCMD.setData("retries", Integer.parseInt(workItem.getParameter("Retries").toString()));
        }
        if (workItem.getParameter("Owner") != null) {
            ctxCMD.setData("owner", workItem.getParameter("Owner"));
        }

        if (RecurringCommand.isRecurringCommand(cmdClass) )
            ctxCMD.setData("recurringRetries", RecurringCommand.getRetries() );

        logger.trace("Command context {}", ctxCMD);
        Long requestId = executorService.scheduleRequest(cmdClass, ctxCMD);
        logger.debug("Request scheduled successfully with id {}", requestId);
    }

}
