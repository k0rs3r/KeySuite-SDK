package it.kdm.orchestratore.entity.object;

/**
 * Created by antsic on 28/02/18.
 */
public class ConversationSearch {

    private String descriptionInstanceProcess;
    private Boolean isRead;
    private String idUser;
    private String idOperator;
    private String token;
    private int pageSize;
    private int pageNumber;
    private String[] orders;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDescriptionInstanceProcess() {
        return descriptionInstanceProcess;
    }

    public void setDescriptionInstanceProcess(String descriptionInstanceProcess) {
        this.descriptionInstanceProcess = descriptionInstanceProcess;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }


    public String getIdOperator() {
        return idOperator;
    }

    public void setIdOperator(String idOperator) {
        this.idOperator = idOperator;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String[] getOrders() {
        return orders;
    }

    public void setOrders(String[] orders) {
        this.orders = orders;
    }
}
