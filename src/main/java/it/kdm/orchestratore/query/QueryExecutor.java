package it.kdm.orchestratore.query;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.kdm.orchestratore.RestCall;
import it.kdm.orchestratore.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class QueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

    public static QueryResponse query(QueryParams params){
        return RestCall.GET("query",params,QueryResponse.class);
    }

    /*public static ModelAndView executeQuery(String qt, QueryParams qparams, Map<String,Object> buffers, HttpServletRequest request, HttpServletResponse response, MessageSource messageSource) throws Exception {
        qparams.set("qt",qt);
        qparams.set("echoProperties","true");

        it.kdm.orchestratore.query.QueryResponse retCall = RestCall.GET("report",qparams, it.kdm.orchestratore.query.QueryResponse.class);

        ModelAndView model = executeQuery(retCall,qt,qparams,buffers,request,response,messageSource);

        return KDMUtils.finalizeModelAndView(model, request);
    }*/

    public static Map<String,Object> getReportResults(Map<String,Object> response, QueryParams qparams,HttpServletRequest request) throws Exception {
        QueryResponse qresponse = new QueryResponse();

        qresponse.setBuffers( (Map<String,Object>) response.get("buffers"));
        qresponse.setColumns( (List<String>) response.get("columns"));
        qresponse.setData(response.get("data"));
        qresponse.setFacets( (Map<String,Map<String,Integer>>) response.get("facets"));
        qresponse.setTitle( (String) response.get("title"));
        qresponse.setRecordCount( (Integer) response.get("recordCount"));
        qresponse.setElapsed( (Integer) response.get("elapsed") );
        qresponse.setPageCount( (Integer) response.get("pageCount") );
        qresponse.setPageNumber( (Integer) response.get("pageNumber") );
        qresponse.setPageSize( (Integer) response.get("pageSize") );
        qresponse.setOrderBy( (String) response.get("orderBy") );
        qresponse.setSortSpecs( (Map<String,String>) response.get("sortSpecs") );
        qresponse.setSeries( (Map<String,List<Object>>) response.get("series") );
        qresponse.setColGroups( (List<String>) response.get("colGroups") );
        qresponse.setColGroupsSpan( (List<Integer>) response.get("colGroupsSpan") );
        qresponse.setParameters( (Map<String,String>) response.get("parameters") );

        ModelAndView mv = getReportModel(qresponse,qparams,request);

        Map<String,Object> ret = mv.getModel();

        for( String key : ret.keySet().toArray(new String[0]) ){
            Object val = ret.get(key);
            if (val==null){
                ret.remove(key);
                continue;
            }
        }

        return ret;
    }

    public static ModelAndView getReportModel(it.kdm.orchestratore.query.QueryResponse queryResponse, QueryParams qparams, HttpServletRequest request) throws Exception {

        String qt = qparams.get("qt");
        //qparams.set("echoProperties","true");

        it.kdm.orchestratore.query.QueryResponse retCall = queryResponse;

        //Set<String> pars = retCall.getParameters().keySet();

        //pars.removeAll(Lists.newArrayList("actors","actorId","codiceEnte","codiceAoo"));

        //InputStream ftlStream = ResourceUtils.getResourceAsStream(String.format("reports/%s.properties", qt));

        //Map<String,String> properties = (Map) KDMUtils.getMultiLinePropertiesFile(ftlStream);
        Map<String,String> properties = null;

        if (retCall.getBuffers()!=null)
            properties = (Map<String,String>) retCall.getBuffers().get("properties");

        if (properties==null)
            properties = new HashMap<>();

        QueryParams changeParams = new QueryParams(qparams);
        changeParams.remove("orderBy");
        changeParams.remove("sort");
        changeParams.remove("pageNumber");
        changeParams.remove("echoProperties");

        List<String> pars = new ArrayList<>(properties.keySet());

        //pars.removeAll(QueryParams.knownPars);

        for( String p : pars ){

            if (QueryParams.isKnown(p))
                continue;

            String v = properties.get(p);
            if (v!=null && !qparams.contains(p)){
                if ("ACTOR".equals(v) || "CURRENT".equals(v)){
                    v = Session.getUserInfoNoExc().getUsername();
                }
                qparams.setSplitted(p,v);
                //changeParams.setSplitted(p,v);
            }
        }

        Set<String> facets = new LinkedHashSet<>();
        Map<String,Boolean> combos = new LinkedHashMap<>();

        //String facetConfig = ToolkitConnector.getGlobalProperty(String.format("report.%s.facets.inline",qt) ,"");

        //List<String> facetlist = Arrays.asList( facetConfig.split(",") );

        Map<String,Map<String,Integer> > allFacetsNums = new LinkedHashMap<>();

        for( String echo : properties.keySet() ){
            if (echo.startsWith("facet.") && echo.endsWith(".options") && properties.get(echo).contains("inline")){
                String facet = echo.split("\\.")[1];
                allFacetsNums.put(facet,new HashMap<String,Integer>() );
            }
        }

        allFacetsNums.putAll(retCall.getFacets());

        String businessState = properties.get("facet.businessState");
        String processName=null;

        if (Strings.isNullOrEmpty(businessState))
            businessState = "businessState:processName";

        String[] parts = businessState.split(":");

        businessState = parts[0];
        if (parts.length>1){
            String[] x = qparams.getParams(parts[1]);
            if (x!=null && x.length==1){
                processName = x[0];
            }
        }

        List<String> actors = Session.getUserInfoNoExc().getGroups();
        actors.add(Session.getUserInfoNoExc().getUsername());

        String actor = Session.getUserInfoNoExc().getUsername();

        //List<String> hide = Arrays.asList(resplit(qparams.getParams("hide")));

        boolean reset = qparams.getParameterNames().size()>0;

        for( String facet : allFacetsNums.keySet() ){

            if (facet.equals(businessState) && Strings.isNullOrEmpty(processName))
                continue;

            //if (qparams.contains(facet))
            //    reset = true;

            //if (hide.contains(facet))
            //	continue;


            //String valS = ToolkitConnector.getGlobalProperty( String.format("report.%s.%s.values",qt,facet) );

            String optionS = properties.get( String.format("facet.%s.options",facet) );

            if (optionS==null)
                optionS = "";

            List<String> optionL = Arrays.asList(optionS.toLowerCase().split(","));

            boolean inline = optionL.contains("inline");
            boolean multivalue = optionL.contains("multivalue");
            boolean hidden = optionL.contains("hidden");
            //boolean actor = optionL.contains("actor");

            String valS = properties.get( String.format("facet.%s.values",facet) );

            Map<String,Integer> counts = allFacetsNums.get(facet);

            if (!Strings.isNullOrEmpty(valS)){

                Map<String,Integer> counts0 = counts;
                counts = new LinkedHashMap<>();

                allFacetsNums.put(facet, counts);

                String[] vals = valS.split(",");
                for( int i=0; i<vals.length; i++){
                    counts.put(vals[i], counts0.get(vals[i]) );
                }

                for ( String key : counts0.keySet() ){
                    if (!counts.containsKey(key))
                        counts.put(key,counts0.get(key));
                        //counts.put(ActorsCache.getDisplayName(key),counts0.get(key));
                }
            } /*else {
                counts =  allFacetsNums.get(facet);
                for ( String key : counts0.keySet() ){
                    counts.put(key,counts0.get(key));
                    //counts.put(ActorsCache.getDisplayName(key),counts0.get(key));
                }
            }*/

            String facetValue = qparams.get(facet);

            if ( hidden || counts.size() == 0 || (counts.size() == 1 && Strings.isNullOrEmpty(facetValue)) )
                continue;

            if ("CURRENT".equals(facetValue) || "ACTOR".equals(facetValue) ){
                String v = counts.keySet().iterator().next();
                qparams.set(facet,v);
                changeParams.set(facet,v);
            } else if ("ACTORS".equals(facetValue)){

                List<String> vals = new ArrayList<>(actors);
                vals.retainAll(counts.keySet());

                qparams.set(facet, vals.toArray(new String[0]) );
                changeParams.set(facet, vals.toArray(new String[0]) );
            }

            String[] facetValues = qparams.getParams(facet);

            if (!inline && multivalue && facetValues!=null && facetValues.length>0 ){

                Map<String,Integer> counts0 = counts;
                counts = new LinkedHashMap<>();

                allFacetsNums.put(facet, counts);

                for( int i=0; i<facetValues.length; i++){
                    counts.put(facetValues[i], counts0.get(facetValues[i]) );
                }

                for ( String key : counts0.keySet() ){
                    if (!counts.containsKey(key))
                        counts.put(key,counts0.get(key));
                }
            }

            //allFacetsNums.put(facet, counts);

            if (inline)
                facets.add(facet);
            else
                combos.put(facet,multivalue);
        }

        String joinClause = qparams.get("join.field",properties.get("join.field"));

        String arrayField = null;

        if (!Strings.isNullOrEmpty(joinClause) && joinClause.contains(":")){
            if (joinClause.endsWith("-*") || joinClause.toLowerCase().endsWith("-n"))
                arrayField = joinClause.split(":")[0];
        }

        String wt = qparams.get("wt",properties.get("wt"));

        ModelAndView model = new ModelAndView();

        if ("json".equals(wt) || "data".equals(wt) || "csv".equals(wt) ){

            //String content;
            //Writer writer = response.getWriter();

            if ("csv".equals(wt)) {
                //response.setContentType("text/plain; charset=utf-8");

                model.addObject("columns", retCall.getColumns());
                model.addObject("data", retCall.getData() );
                model.addObject("arrayField", arrayField);
                model.addObject("content-type", "text/csv; charset=utf-8");

                model.addObject("page", "csv" );

                //content = KDMUtils.ftlHandler("csv", model.getModel() );
                model.setViewName("csv");
            } else {
                //response.setContentType("application/json; charset=utf-8");
                String content;

                if ("json".equals(wt)){
                    model.addObject("content",retCall);
                } else {
                    model.addObject("content",retCall.getData());
                    //content = new ObjectMapper().writeValueAsString(retCall.getData());
                }

                model.addObject("content-type", "application/json; charset=utf-8");

                model.addObject("page", "json" );

                model.setViewName("json");

                //content = new ObjectMapper().writeValueAsString( "json".equals(wt)  ? retCall : retCall.getData() );
            }

            //writer.write(content);
            //writer.flush();

            return model;
        }

        List<String> renderers;

        String resultFtl = qparams.get("ftl",properties.get("ftl"));

        String eRenderers = properties.get("ftls");

        if (Strings.isNullOrEmpty(eRenderers))
            renderers = Lists.newArrayList();
        else
            renderers = Lists.newArrayList(eRenderers.split(","));

        if (Strings.isNullOrEmpty(resultFtl)){
            if (renderers.isEmpty())
                resultFtl = "results";
            else
                resultFtl = renderers.get(0);
        }

        if (!renderers.contains(resultFtl))
            renderers.add(0,resultFtl);

        Map buffers = new HashMap<>();
        if (retCall.getBuffers()!=null)
            buffers.putAll(retCall.getBuffers());

        Map<String,Integer> idxs = new LinkedHashMap<>();

        if (retCall.getColumns()!=null) {
            for (int i = 0; i < retCall.getColumns().size(); i++) {
                idxs.put(retCall.getColumns().get(i), i);
            }
        }

        boolean showStats = !"false".equals(properties.get("showStats"));

        String changeQuerystring = "&"+changeParams.toString();

        model.addObject("action",properties.get("action"));
        //model.addObject("response",retCall);
        model.addObject("qt", qt);
        model.addObject("title", retCall.getTitle());
        model.addObject("subtitle", properties.get("subtitle") );
        model.addObject("form", properties.get("form") );
        model.addObject("altftls",new LinkedHashSet<>(renderers));
        model.addObject("ftls",new LinkedHashSet<>(renderers));

        model.addObject("ftl",resultFtl);

        model.addObject("actorId",actor);
        model.addObject("actors",actors);
        model.addObject("isAdmin", Session.getUserInfo().isAdmin());
        //model.addObject("context",request.getContextPath());
        model.addObject("params", properties);
        model.addObject("properties", properties);
        //model.addObject("req",qparams.toMap());
        model.addObject("lreq",qparams.toListMap());
        model.addObject("req",qparams.toMap());
        model.addObject("querystringParams",changeQuerystring);

        model.addObject("showStats",showStats);
        model.addObject("totResults",retCall.getRecordCount());
        model.addObject("recordCount",retCall.getRecordCount());
        model.addObject("elapsed",retCall.getElapsed());
        model.addObject("totPage",retCall.getPageCount());
        model.addObject("pageCount",retCall.getPageCount());
        model.addObject("pageNumber",retCall.getPageNumber());
        model.addObject("orderBy",retCall.getOrderBy());

        model.addObject("moreItems", "true".equals(properties.get("facet.moreItems")) );
        model.addObject("facets",facets);
        model.addObject("combos",combos);
        model.addObject("counts",allFacetsNums);
        model.addObject("reset", reset);
        model.addObject("businessState",businessState);
        model.addObject("processName",processName);

        model.addObject("buffers",buffers);
        model.addObject("sortSpecs",retCall.getSortSpecs());
        model.addObject("data",retCall.getData());
        //model.addObject("records",retCall.getData());
        model.addObject("series",retCall.getSeries());
        model.addObject("columns",retCall.getColumns());
        model.addObject("groups",retCall.getColGroups());
        model.addObject("colGroups",retCall.getColGroups());
        model.addObject("spans",retCall.getColGroupsSpan());
        model.addObject("colGroupsSpan",retCall.getColGroupsSpan());
        model.addObject("colIndexes",idxs);
        model.addObject("arrayField",arrayField);
        model.addObject("joinField",arrayField);

        model.addObject("pathIdx", Session.getUserInfo().getCurrentTreeviewProfile().length()+1);
        //model.addObject("utils",new TemplateUtils());

        model.addObject("parameters", retCall.getParameters());

        String page = qparams.get("page",properties.get("page"));

        if (Strings.isNullOrEmpty(page))
            page = "report";

        model.addObject("view",resultFtl);
        model.addObject("page",page);
        model.setViewName(page);

        if (request!=null){

            if (request.getServletPath().endsWith("/"+qt))
                changeParams.remove("qt");

            request.setAttribute("properties",properties);

            //model.addObject("baseUrl",request.getServletPath());

            String xref = request.getHeader("X-baseurl");

            //retrocompatibilità vecchi template ftl
            if (!Strings.isNullOrEmpty(xref)) {
                String[] xparts = xref.split("\\?");
                //model.addObject("baseUrl", xparts[0]);
                //dai qs params rimuovi quelli di base
                if (xparts.length>1) {
                    //changeQuerystring
                    String querystringParams = changeQuerystring; // (String) model.get("querystringParams");
                    if (!Strings.isNullOrEmpty(querystringParams)) {
                        querystringParams = querystringParams.replace(xparts[1],"").replaceAll("&+","&");
                        model.addObject("querystringParams", querystringParams);
                    }
                }
            }
        }

        return model;


        /*try {

            InputStream ftlStream = KDMUtils.findResource("reports/"+resultFtl+".ftl"));

            if (ftlStream==null)
                throw new FileNotFoundException("not found reports/"+resultFtl+".ftl");

            String template = org.apache.commons.io.IOUtils.toString(ftlStream);

            String ftl = KDMUtils.ftlHandler(template, model.getModel());
            ftl = KDMUtils.translateMessages(ftl, null, messageSource, request.getLocale());
            model.addObject("ftl",ftl);

        } catch (TemplateException e) {
            throw new IOException(e);
        }*/

        //if ("print".equals(wt))
        //    model.addObject("print",true);
            //request.setAttribute("wt","print");



        //model.setViewName("report");
        //return model;
    }

}
