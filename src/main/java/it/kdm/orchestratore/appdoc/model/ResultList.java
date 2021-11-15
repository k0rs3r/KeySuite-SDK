package it.kdm.orchestratore.appdoc.model;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.params.ModifiableSolrParams;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by microchip on 03/05/17.
 */
public class ResultList<T> extends ArrayList<T> {
    private long numFound = 0L;
    private double qtime = 0;
    private ICIFSObject parentObject;
    private List<FacetField> facets = new ArrayList<FacetField>();
    private List<RangeFacet> ranges = new ArrayList<RangeFacet>();

    public ResultList(List<T> items) {
        super(items);
    }

    public String getPageUrl(HttpServletRequest request, int start, int rows) {

        int defaultRows = 10;

        ModifiableSolrParams params = new ModifiableSolrParams(request.getParameterMap());
        String currentStart = params.get("start",null);
        String currentRows = params.get("rows",null);

        params.set("start", start);

        if (currentStart==null && currentRows==null)
            params.set("rows", rows);

        if (currentStart!=null && currentRows==null)
            params.set("rows", defaultRows);

        return "?" + params.toString();

//        return ((UserRoleRequestWrapper) request).getRequestPath() + "?" + new ModifiableSolrParams(request.getParameterMap())
//                .set("start", start)
//                .set("rows", rows)
//                .toString();
    }

    public String getCurrentStart(HttpServletRequest request) {
        String start = new ModifiableSolrParams(request.getParameterMap()).get("start");
        if (Strings.isNullOrEmpty(start))
            start = "0";

        return start;
    }

    public long getNumFound() {
        return this.numFound;
    }

    public List<FacetField> getFacets() {
        return facets;
    }
    public List<RangeFacet> getRanges() {
        return ranges;
    }

    public void setNumFound(long numFound) {
        this.numFound = numFound;
    }

    public int getPageSize(HttpServletRequest request) {
        int defaultRows = 10;

        ModifiableSolrParams params = new ModifiableSolrParams(request.getParameterMap());
        String currentStart = params.get("start",null);
        String currentRows = params.get("rows",null);

        int rows = defaultRows;
        if (currentStart==null && currentRows==null)
            rows = this.size();

        return rows;

//        return this.size();
    }

    public double getQtime() {
        return qtime;
    }

    public void setQtime(double qtime) {
        this.qtime = qtime;
    }

    public ICIFSObject getParentObject() {
        return parentObject;
    }

    public void setParentObject(ICIFSObject parentObject) {
        this.parentObject = parentObject;
    }

    public void setFacets(List<FacetField> facets) {
        this.facets = facets;
    }
    public void setRanges(List<RangeFacet> ranges) {
        this.ranges = ranges;
    }
//    public String toString() {
//        return "{numFound=" + this.numFound + ",start=" + this.start + (this.maxScore != null?",maxScore=" + this.maxScore:"") + ",docs=" + super.toString() + "}";
//    }
}
