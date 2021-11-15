package it.kdm.orchestratore.appBpm.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExportDataService  {

    private static final Logger logger = LoggerFactory.getLogger(ExportDataService.class);

 //if you have other defined JdbcTemplates
    private JdbcTemplate exportJdbcTemplate;

    public JdbcTemplate getExportJdbcTemplate() {
        return exportJdbcTemplate;
    }

    public void setExportJdbcTemplate(JdbcTemplate exportJdbcTemplate) {
        this.exportJdbcTemplate = exportJdbcTemplate;
    }

    public List<String> getExportConfiguration() throws Exception {
        return getAllQueryNameExportOrm();
    }


    public HSSFWorkbook exportDataExcel(Map<String, String> req) throws Exception{
        int listSize =0;
        String orderBy = "";
        String orderByReturn = "";
        Boolean trueFalseDataFine = true;




        String descrizione="%";
        Integer status = -1;
        Date dataInizioA=null;
        String processId="%";
        Date dataInizioDa=null;
        Date dataFineA=null;
        Date dataFineDa=null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String exportConfiguration = "";
        String controlEndDate="";
        String controlStatus="";

        if(req.containsKey("descrizione")){
            descrizione =  "%"+req.get("descrizione")+"%";
        }

        if(req.containsKey("dataInizioA")){
            dataInizioA =  formatter.parse(req.get("dataInizioA"));
        }else{
            dataInizioA=formatter.parse("2200-01-01");
        }
        if(req.containsKey("dataInizioDa")){
            dataInizioDa =  formatter.parse(req.get("dataInizioDa"));
        }else{
            dataInizioDa=formatter.parse("1900-01-01");
        }
        if(req.containsKey("dataFineA")){
            dataFineA =  formatter.parse(req.get("dataFineA"));
        }else{
            dataFineA=formatter.parse("2200-01-01");
        }
        if(req.containsKey("dataFineDa")){
            dataFineDa =  formatter.parse(req.get("dataFineDa"));
        }else{
            dataFineDa=formatter.parse("1900-01-01");
        }
        if(!req.containsKey("dataFineA") && !req.containsKey("dataFineDa")){
            controlEndDate = null;
        }
        if(req.containsKey("exportConfiguration")){
            exportConfiguration =  req.get("exportConfiguration");
        }else{
            throw new Exception("parameter exportConfiguration not found" );
        }
        if(req.containsKey("processId")){
            processId =  req.get("processId");
        }
        if(req.containsKey("status")){
            status = Integer.parseInt(req.get("status"));
        }else{
            controlStatus = null;
        }

        //server ad evitare di scomporre la query per la valorizzazione o meno del campo data-fine
        if(!req.containsKey("dataFineA") && !req.containsKey("dataFineDa")){
            trueFalseDataFine=true;
        }else{
            trueFalseDataFine=false;
        }


        String codiceEnte = req.get("codiceEnte");

        String  query = getExportQueryByName(exportConfiguration);

        SqlRowSet sqlRowSet = exportJdbcTemplate.queryForRowSet(
                query,
                descrizione,
                processId,
                new java.sql.Date(dataInizioDa.getTime()),
                new java.sql.Date(dataInizioA.getTime()),
                controlEndDate,
                controlEndDate,
                new java.sql.Date(dataFineDa.getTime()),
                new java.sql.Date(dataFineA.getTime()),
                controlStatus,
                controlStatus,
                status
        );

        String [] columnNames =  new String[sqlRowSet.getMetaData().getColumnCount()];

        int columnCount = sqlRowSet.getMetaData().getColumnCount();

        // The column count starts from 1
        for (int i = 1; i <= columnCount; i++ ) {
            columnNames[i-1] = sqlRowSet.getMetaData().getColumnLabel(i);
        }

        // gestione nessun risultato
        if (!sqlRowSet.next()) {
            return null;
        }


        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("lawix10");
        HSSFRow rowhead = sheet.createRow((short) 0);

        for(int i = 0 ; i < columnNames.length;i++) {
            rowhead.createCell(i).setCellValue(columnNames[i]);
        }

        int i=1;
        while (sqlRowSet.next()) {
            HSSFRow row = sheet.createRow((short) i);
            for(int j = 0; j < columnNames.length; j++) {
                row.createCell(j).setCellValue(sqlRowSet.getString(columnNames[j]));
            }
            i++;
        }

      return workbook;
    }


    public static List<String> getAllQueryNameExportOrm() throws Exception{
        List<String> queryConf=new ArrayList<>();
        InputStream schemaIS = new FileInputStream(getFileExport());
        if(schemaIS==null)
            return queryConf;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder =  builderFactory.newDocumentBuilder();
        org.w3c.dom.Document xmlDocument = builder.parse(schemaIS);
        javax.xml.xpath.XPath xPath =  XPathFactory.newInstance().newXPath();
        NodeList ns = (NodeList)xPath.compile("/entity-mappings/named-native-query/@name").evaluate(xmlDocument, XPathConstants.NODESET);
        for(int i = 0; i < ns.getLength(); i++)
            queryConf.add(ns.item(i).getTextContent());

        return queryConf;

    }





    public static String getExportQueryByName(String queryName) throws Exception{
        String query="";

        InputStream schemaIS = new FileInputStream(getFileExport());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder =  builderFactory.newDocumentBuilder();
        org.w3c.dom.Document xmlDocument = builder.parse(schemaIS);
        javax.xml.xpath.XPath xPath =  XPathFactory.newInstance().newXPath();
        query = xPath.compile("/entity-mappings/named-native-query[@name='"+queryName+"']/query").evaluate(xmlDocument);

        return query;

    }

private static File getFileExport(){
    File homeDir = new File(System.getProperty("user.home"));
    File configDir = new File(homeDir, "bpm-config");
   return new File(configDir,"ExportORM.xml");
}


}
