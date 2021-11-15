package it.kdm.orchestratore.entity.object;

import java.util.Date;
import java.util.Map;

/**
 * Created by antsic on 22/04/15.
 */
public class RequestInfoObject {




        private Long id;
        private Date time;
        private String status;
        private String commandName;
        private String message;
        private String key;
        //Number of times that this request must be retried
        private int retries = 0;
        //Number of times that this request has been executed
        private int executions = 0;

        private String requestData;
        private String responseData;
        private String errorMessage;
        private String errorStack;



        public RequestInfoObject() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCommandName() {
            return commandName;
        }

        public void setCommandName(String commandName) {
            this.commandName = commandName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getRetries() {
            return retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }

        public int getExecutions() {
            return executions;
        }

        public void setExecutions(int executions) {
            this.executions = executions;
        }

        public String getRequestData() {
            return requestData;
        }

        public void setRequestData(String requestData) {
            this.requestData = requestData;
        }

        public String getResponseData() {
            return responseData;
        }

        public void setResponseData(String responseData) {
            this.responseData = responseData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorStack() {
            return errorStack;
        }

        public void setErrorStack(String errorStack) {
            this.errorStack = errorStack;
        }
}
