package it.kdm.orchestratore.query;

import it.kdm.orchestratore.RestResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryResponse<T> extends RestResponse {

    public Map<String, Object> getBuffers() {
        return buffers;
    }

    public void setBuffers(Map<String, Object> buffers) {
        this.buffers = buffers;
    }

    private Map<String,Object> buffers;

    public Object getCollector() {
        return collector;
    }

    public static String normalizeSortSpec(String orderBy){
        if (orderBy==null || orderBy.trim().length()==0)
            return "";
        String[] parts = orderBy.split(",");
        for( int i=0; i<parts.length; i++){
            String[] subparts = parts[i].trim().split("\\s+");
            String spec = "asc";

            if (subparts.length>1 && subparts[1].trim().equalsIgnoreCase("desc"))
                spec = "desc";

            parts[i] = subparts[0].trim()+" "+spec ;
        }
        return StringUtils.join(parts,",");
    }

    public List<String> getColGroups() {
        return colGroups;
    }

    public void setColGroups( List<String> colGroups) {
        this.colGroups = colGroups;
    }

    List<String> colGroups;

    List<Integer> colGroupsSpan;

    Map<String,String> sortSpecs;

    /*public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    Map<String,String> params;*/

    public List<Integer> getColGroupsSpan() {
        return colGroupsSpan;
    }

    public void setColGroupsSpan(List<Integer> colGroupsSpan) {
        this.colGroupsSpan = colGroupsSpan;
    }

    public Map<String,String> getSortSpecs() {
        return sortSpecs;
    }

    public void setSortSpecs(Map<String,String> sortSpecs) {
        this.sortSpecs = sortSpecs;
    }

    public void setCollector(Object collector) {
        this.collector = collector;
    }

    Object collector;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    @Override
    public List<T> getData(){
        return (List) super.getData();
    }

    public void setData(List<T> list){
        super.setData(list);
    }

    public QueryResponse(){
        this.setSeries(new LinkedHashMap<String, List<Object>>());
        this.setData(new ArrayList<>());
        this.setOrderBy("");
        this.setPageCount(0);
        this.setPageNumber(1);
        this.setPageSize(0);
        this.setParameters(new LinkedHashMap<String, String>());
        this.setRecordCount(-1);
        this.setElapsed(0);
        this.setFacets(new LinkedHashMap<String, Map<String, Integer>>());
        this.setColumns(new ArrayList<String>());
        this.setCollector(null);
        this.setColGroups(new ArrayList<String>());
        this.setTitle("");
        this.setColGroupsSpan(new ArrayList<Integer>());
        this.setSortSpecs(new LinkedHashMap<String, String>());
        //this.setParams(new LinkedHashMap<String, String>());
        this.setBuffers(new LinkedHashMap<String,Object>());
    }
}
