package it.kdm.orchestratore.entity.object;


import com.google.common.base.Strings;
import it.kdm.orchestratore.session.Session;

public class ProcessConfigurationObject {

    private String  processId;
    private String  ente;
    private String  aoo;
    private String  category;
    private String  viewRole;
    private String  startRole;
    private String  name;
    private String  version;
    private String  cloneRole;
    private String  cloneFtl;
    private String  statusFtl;
    private String  settings;
    private Boolean prefer=false;


    public Boolean getRunnable() {
        return runnable;
    }

    public void setRunnable(Boolean runnable) {
        this.runnable = runnable;
    }

    private Boolean runnable=false;
    private String  configRole;
    private String  startFtl;
    private String  configFtl;
    private Boolean deleteTask=false;
    private Integer retentionDay=-1;
    public ProcessConfigurationObject(){

    }

    public ProcessConfigurationObject(
            String processId, String ente, String aoo, String category, String viewRole,
            String startRole, String name, String version, String cloneRole, String cloneFtl,
            String statusFtl, String deleteTask, String retentionDay) {
        this.processId = processId;
        this.ente = ente;
        this.aoo = aoo;
        this.category = category;
        this.viewRole = viewRole;
        this.startRole = startRole;
        this.name = name;
        this.version = version;
        this.cloneRole=cloneRole;
        this.cloneFtl=cloneFtl;
        this.statusFtl=statusFtl;
        this.deleteTask = deleteTask != null ? Integer.parseInt(deleteTask) > 0 ? true : false : false;
        this.retentionDay = retentionDay != null ? Integer.parseInt(deleteTask) : null;

    }

    public String getStatusFtl() {
        return statusFtl;
    }

    public void setStatusFtl(String statusFtl) {
        this.statusFtl = statusFtl;
    }

    public String getCloneFtl() {
        return cloneFtl;
    }

    public void setCloneFtl(String cloneFtl) {
        this.cloneFtl = cloneFtl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getDeploymentId() {
        return "default-per-instance";
        //return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public String getAoo() {
        return aoo;
    }

    public void setAoo(String aoo) {
        this.aoo = aoo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getViewRole() {
        return viewRole;
    }

    public void setViewRole(String viewRole) {
        this.viewRole = viewRole;
    }

    public String getStartRole() {
        return startRole;
    }

    public void setStartRole(String startRole) {
        this.startRole = startRole;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getPrefer() {
        return prefer;
    }

    public void setPrefer(Boolean prefer) {
        this.prefer = prefer;
    }

    public String getCloneRole() {
        return cloneRole;
    }

    public void setCloneRole(String cloneRole) {
        this.cloneRole = cloneRole;
    }

    public String getConfigRole() {
        return configRole;
    }

    public void setConfigRole(String configRole) {
        this.configRole = configRole;
    }

    public String getStartFtl() {
        return startFtl;
    }

    public void setStartFtl(String startFtl) {
        this.startFtl = startFtl;
    }

    public String getConfigFtl() {
        return configFtl;
    }

    public void setConfigFtl(String configFtl) {
        this.configFtl = configFtl;
    }

    public boolean canStart(){
        return Session.getUserInfoNoExc().hasGroup(startRole);
    }

    public boolean canClone(){
        return Session.getUserInfoNoExc().hasGroup(cloneRole);
    }

    public boolean canConfig(){
        return Session.getUserInfoNoExc().hasGroup(configRole);
    }

    public boolean canView(){
        if (Strings.isNullOrEmpty(viewRole))
            return true;
        return Session.getUserInfoNoExc().hasGroup(viewRole);
    }

    public boolean canDelete(){
        return Session.getUserInfoNoExc().isAdmin();
    }
    public Boolean getDeleteTask() {
        return deleteTask;
    }

    public void setDeleteTask(Boolean deleteTask) {
        this.deleteTask = deleteTask;
    }

    public Integer getRetentionDay() {
        return retentionDay;
    }

    public void setRetentionDay(Integer retentionDay) {
        this.retentionDay = retentionDay;
    }
}
