package it.kdm.orchestratore.entity.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maupet on 23/11/15.
 */
public class MessageConversationObjectResponse {

    //record per pagina
    private Integer pageSize;

    //numero pagina attuale
    private Integer pageNumber;

    //numero di pagine
    private Integer pageCount;

    //numero di pagine
    private Integer recordCount;

    private String orderBy;

    private List<MessageConversationObject> messageConversationObjects = new ArrayList<>();


    public MessageConversationObjectResponse() {
    }

    public MessageConversationObjectResponse(Integer pageSize, Integer pageNumber, Integer pageCount, Integer recordCount, String orderBy, List<MessageConversationObject> messageConversationObjects) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.pageCount = pageCount;
        this.recordCount = recordCount;
        this.orderBy = orderBy;
        this.messageConversationObjects = messageConversationObjects;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<MessageConversationObject> getMessageConversationObjects() {
        return messageConversationObjects;
    }

    public void setMessageConversationObjects(List<MessageConversationObject> messageConversationObjects) {
        this.messageConversationObjects = messageConversationObjects;
    }
}
