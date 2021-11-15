package it.kdm.orchestratore.appdoc.utils;

import it.kdm.doctoolkit.services.ToolkitConnector;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.core.env.PropertyResolver;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private static DataSource     datasource;
    private BasicDataSource ds;

    private DataSource() throws IOException, SQLException, PropertyVetoException {


		PropertyResolver props; // = new Properties();

//		props.load(this.getClass().getResourceAsStream("/docsyncservice.properties"));
		props = ToolkitConnector.loadConfigFile("docsyncservice");

		String db_sync_user_id = props.getProperty("db_sync_user_id");
		String db_sync_user_pwd = props.getProperty("db_sync_user_pwd");
		String db_sync_driver = props.getProperty("db_sync_driver");
		String db_sync_connect = props.getProperty("db_sync_connect");
		String db_sync_location = props.getProperty("db_sync_location");
		String db_sync_file = props.getProperty("db_sync_file");

        ds = new BasicDataSource();
        ds.setDriverClassName(db_sync_driver);
        ds.setUsername(db_sync_user_id);
        ds.setPassword(db_sync_user_pwd);
		String url = db_sync_connect + db_sync_location;
		url+=   (db_sync_file!=null && !"".equals(db_sync_file) ? "/"+ db_sync_file : "" );
        ds.setUrl(url);
       
     // the settings below are optional -- dbcp can work with defaults
        ds.setMinIdle(1);
        ds.setMaxIdle(3);
        ds.setMaxOpenPreparedStatements(180);

    }

    public static DataSource getInstance() throws IOException, SQLException, PropertyVetoException 
    {
        if (datasource == null) {
            datasource = new DataSource();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }
    
    public static String getTableTestStatement(String tablename)
    {
    	String  sql = "SELECT 1 FROM " + tablename ;  
    	return sql;	
    }

    
    public static String getTableCreateStatement(String tablename)
    {
    	String  sql = "CREATE TABLE " + tablename ;  
    			//sql += "(ID bigint auto_increment, ";
    	
    	if (tablename.equalsIgnoreCase("Collaboratore")) 
    	{  
    		sql += "(cod_coll VARCHAR(20)  DEFAULT NULL, " +
			        "cognome VARCHAR(255)  DEFAULT NULL, " +
			        "nome VARCHAR(255) DEFAULT NULL, " +
			        "cod_soc VARCHAR(255) DEFAULT NULL, " +
			        "denominazione_soc VARCHAR(255) DEFAULT NULL, " +
			        "cod_qualifica VARCHAR(255) DEFAULT NULL, " +
			        "desc_qualifica VARCHAR(255) DEFAULT NULL, " +
			        "data_qualifica VARCHAR(255) DEFAULT NULL, " +
			        "cod_gruppo VARCHAR(255) DEFAULT NULL, " +
			        "desc_gruppo VARCHAR(255) DEFAULT NULL, " +
			        "data_gruppo VARCHAR(255) DEFAULT NULL, " +
			        "cod_resp_gruppo VARCHAR(255) DEFAULT NULL, " +
			        "nome_resp_gruppo VARCHAR(255) DEFAULT NULL, " +
			        "cod_settore VARCHAR(255) DEFAULT NULL, " +
			        "desc_settore VARCHAR(255) DEFAULT NULL, " +
			        "stato VARCHAR(255) DEFAULT NULL, " +
			        "indirizzo_posta VARCHAR(255) DEFAULT NULL, " +
			        "autorizzazione VARCHAR(255) DEFAULT NULL, " +
			        "server_login VARCHAR(255) DEFAULT NULL, " +
			        "citta VARCHAR(255) DEFAULT NULL, " +
			        "sede_lav VARCHAR(255) DEFAULT NULL, " +
			        "flag_aggiornamento VARCHAR(255) DEFAULT NULL, " +
			        "data_modifica VARCHAR(255) DEFAULT NULL," ;
    	} 
    	else if (tablename.equalsIgnoreCase("INCARICO")) 
    	{  
    		sql += "(cod_soc VARCHAR(255) DEFAULT NULL, " +
					"denominazione_soc VARCHAR(255) DEFAULT NULL, " +
					"cod_cli VARCHAR(255) DEFAULT NULL, " +
					"denominazione_cli VARCHAR(MAX) DEFAULT NULL, " +
					"tipo_inc VARCHAR(255) DEFAULT NULL, " +
					"desc_inc VARCHAR(255) DEFAULT NULL, " +
					"cod_comm VARCHAR(255) DEFAULT NULL, " +
					"tipo_servizio VARCHAR(MAX) DEFAULT NULL, " +
					"riservato VARCHAR(255) DEFAULT NULL, " +
					"sede_lav VARCHAR(255) DEFAULT NULL, " +
					"gruppo_1R VARCHAR(255) DEFAULT NULL, " +
					"desc_gruppo_1R VARCHAR(1000) DEFAULT NULL, " +
					"cod_1R VARCHAR(255) DEFAULT NULL, " +
					"nom_1R VARCHAR(255) DEFAULT NULL, " +
					"gruppo_2R VARCHAR(255) DEFAULT NULL, " +
					"desc_gruppo_2R VARCHAR(1000) DEFAULT NULL, " +
					"cod_2R VARCHAR(255) DEFAULT NULL, " +
					"nom_2R VARCHAR(255) DEFAULT NULL, " +
					"cod_3R VARCHAR(255) DEFAULT NULL, " +
					"nom_3R VARCHAR(255) DEFAULT NULL, " +
					"cod_provvisorio VARCHAR(255) DEFAULT NULL, " +
					"stato VARCHAR(255) DEFAULT NULL, " +
					"data_apertura VARCHAR(255) DEFAULT NULL, " +
					"cod_incarico VARCHAR(255) DEFAULT NULL, " +
					"data_chiusura VARCHAR(255) DEFAULT NULL, " +
					"flag_aggiornamento VARCHAR(255) DEFAULT NULL, " +
					"data_modifica VARCHAR(255) DEFAULT NULL, " +
					"cod_incarico_padre VARCHAR(255) DEFAULT NULL, " +
					"cod_cliente_rif VARCHAR(255) DEFAULT NULL, " +
					"desc_cliente_rif VARCHAR(255) DEFAULT NULL, " +
					"stato_cliente VARCHAR(255) DEFAULT NULL, " +
					"cod_area_business VARCHAR(255) DEFAULT NULL, " +
					"desc_area_business VARCHAR(255) DEFAULT NULL, ";
    	} 
    	else if (tablename.equalsIgnoreCase("GRUPPO")) 
    	{  
    		sql += "(cod_coll VARCHAR(255) DEFAULT NULL, " +
		            "cognome VARCHAR(255) DEFAULT NULL, " +
		            "nome VARCHAR(255) DEFAULT NULL, " +
		            "citta VARCHAR(255) DEFAULT NULL, " +
		            "cod_qualifica VARCHAR(255) DEFAULT NULL, " +
		            "desc_qualifica VARCHAR(255) DEFAULT NULL, " +
		            "cod_settore VARCHAR(255) DEFAULT NULL, " +
		            "desc_settore VARCHAR(255) DEFAULT NULL, " +
		            "stato VARCHAR(255) DEFAULT NULL, " +
		            "indirizzo_posta VARCHAR(255) DEFAULT NULL, " +
		            "server_login VARCHAR(255) DEFAULT NULL, " +
		            "server_login_segretaria1 VARCHAR(255) DEFAULT NULL, " +
		            "server_login_segretaria2 VARCHAR(255) DEFAULT NULL, " +
		            "flag_gr_primario VARCHAR(255) DEFAULT NULL, " +
		            "cod_gruppo VARCHAR(255) DEFAULT NULL, " +
		            "desc_gruppo VARCHAR(255) DEFAULT NULL, " +
		            "sede_lav_gruppo VARCHAR(255) DEFAULT NULL, " +
		            "server_login_resp_gruppo VARCHAR(255) DEFAULT NULL, " +
		            "nome_resp_gruppo VARCHAR(255) DEFAULT NULL, " +
		            "tipo_gruppo VARCHAR(255) DEFAULT NULL, " +
		            "codice_societa VARCHAR(255) DEFAULT NULL, " +
		            "ragione_sociale_societa VARCHAR(255) DEFAULT NULL, " +
		            "flag_aggiornamento VARCHAR(255) DEFAULT NULL, ";
    	} 
    	else if (tablename.equalsIgnoreCase("Sicurezza")) 
    	{  
    		sql += "(cod_inc VARCHAR(255) DEFAULT NULL," +
	        		"userLogin VARCHAR(255) DEFAULT NULL," +
			        "cod_coll VARCHAR(MAX) DEFAULT NULL," +
			        "autorizzazione VARCHAR(255) DEFAULT NULL," +
			        "flag_aggiornamento VARCHAR(255) DEFAULT NULL," +
			        "sede_lav VARCHAR(255) DEFAULT NULL,";
    	} 
    	else if (tablename.equalsIgnoreCase("Backlog")) 
    	{  
    		sql += "(cod_soc VARCHAR(255) DEFAULT NULL, " +
    				"cod_obj VARCHAR(255) DEFAULT NULL," +
	        		"sede_lav VARCHAR(255) DEFAULT NULL," +
			        "flag_obj VARCHAR(255) DEFAULT NULL," +
			        "exc_msg VARCHAR(MAX) DEFAULT NULL," +
			        "err_desc VARCHAR(MAX) DEFAULT NULL," +
			        "status_code VARCHAR(5) DEFAULT NULL,"+
			        "children VARCHAR(6) DEFAULT NULL,";
    	} 
    	
    	sql += "request_date TIMESTAMP AS CURRENT_TIMESTAMP NOT NULL,";
    	sql += "THREAD_ID VARCHAR(255) DEFAULT NULL,";
    	sql += "REQUEST_GUID VARCHAR(255) DEFAULT NULL)";

    	
    	return sql;
    	
    }
    
    public static String getInsertStatement(String tablename){
    	
    	String  sql = "";
    	
    	if (tablename.equalsIgnoreCase("Collaboratore")) 
    	{  
            
    			sql += "INSERT INTO Collaboratore(";
        		sql += "cod_coll,";
        		sql += "cognome,";
		        sql += "nome,";
		        sql += "cod_soc,";
		        sql += "denominazione_soc,";
		        sql += "cod_qualifica,";
		        sql += "desc_qualifica,";
		        sql += "data_qualifica,";
		        sql += "cod_gruppo,";
		        sql += "desc_gruppo,";
		        sql += "data_gruppo,";
		        sql += "cod_resp_gruppo,";
		        sql += "nome_resp_gruppo,";
		        sql += "cod_settore,";
		        sql += "desc_settore,";
		        sql += "stato,";
		        sql += "indirizzo_posta,";
		        sql += "autorizzazione,";
		        sql += "server_login,";
		        sql += "citta,";
		        sql += "sede_lav,";
		        sql += "flag_aggiornamento,";
		        sql += "data_modifica,";
		        sql += "REQUEST_DATE,";
		        sql += "THREAD_ID,";
		        sql += "REQUEST_GUID)";
		        sql += "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		        
    	} 
    	else if (tablename.equalsIgnoreCase("incarico")) 
		{

				sql += "INSERT INTO INCARICO(";
				sql += "cod_soc,";
                sql += "denominazione_soc,";
                sql += "cod_cli,";
                sql += "denominazione_cli,";
                sql += "tipo_inc,";
                sql += "desc_inc,";
                sql += "cod_comm,";
                sql += "tipo_servizio,";
                sql += "riservato,";
                sql += "sede_lav,";
                sql += "gruppo_1R,";
                sql += "desc_gruppo_1R,";
                sql += "cod_1R,";
                sql += "nom_1R,";
                sql += "gruppo_2R,";
                sql += "desc_gruppo_2R,";
                sql += "cod_2R,";
                sql += "nom_2R,";
                sql += "cod_3R,";
                sql += "nom_3R,";
                sql += "cod_provvisorio,";
                sql += "stato,";
                sql += "data_apertura,";
                sql += "cod_incarico,";
                sql += "data_chiusura,";
                sql += "flag_aggiornamento,";
		        sql += "data_modifica,";
                sql += "cod_incarico_padre,";
            	sql += "cod_cliente_rif,";
            	sql += "desc_cliente_rif,";
            	sql += "stato_cliente,";
            	sql += "cod_area_business,";
            	sql += "desc_area_business,";	
		        sql += "REQUEST_DATE,";
		        sql += "THREAD_ID,";
		        sql += "REQUEST_GUID)";
		        sql += "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";		
		} 
    	else if (tablename.equalsIgnoreCase("Gruppo")) 
		{

				sql += "INSERT INTO GRUPPO(";
				sql += "cod_coll,";
				sql += "cognome,";
				sql += "nome,";
				sql += "citta,";
				sql += "cod_qualifica,";
				sql += "desc_qualifica,";
				sql += "cod_settore,";
				sql += "desc_settore,";
				sql += "stato,";
				sql += "indirizzo_posta,";
				sql += "server_login,";
				sql += "server_login_segretaria1,";
				sql += "server_login_segretaria2,";
				sql += "flag_gr_primario,";
				sql += "cod_gruppo,";
				sql += "desc_gruppo,";
				sql += "sede_lav_gruppo,";
				sql += "server_login_resp_gruppo,";
				sql += "nome_resp_gruppo,";
				sql += "tipo_gruppo,";
				sql += "codice_societa,";
				sql += "ragione_sociale_societa,";
				sql += "flag_aggiornamento,";
				sql += "REQUEST_DATE,";
		        sql += "THREAD_ID,";
		        sql += "REQUEST_GUID)";
		        sql += "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		} 
    	else if (tablename.equalsIgnoreCase("Sicurezza")) 
		{
				sql += "INSERT INTO Sicurezza(";
				sql += "cod_inc,";
	    		sql += "userLogin,";
		        sql += "cod_coll,";
		        sql += "autorizzazione,";
		        sql += "flag_aggiornamento,";
		        sql += "sede_lav,";
		        sql += "REQUEST_DATE,";
		        sql += "THREAD_ID,";
		        sql += "REQUEST_GUID)";
		        sql += "values (?,?,?,?,?,?,?,?,?)";
		}
    	else if (tablename.equalsIgnoreCase("BackLog")) 
    	{  
				sql += "INSERT INTO BackLog(";
				sql += "cod_soc,";
				sql += "cod_obj,";
		        sql += "sede_lav,";
		        sql += "flag_obj,";
		        sql += "exc_msg,";
		        sql += "err_desc,";
		        sql += "status_code,";
		        sql += "children,";
		        sql += "REQUEST_DATE,";
		        sql += "THREAD_ID,";
		        sql += "REQUEST_GUID)";
		        sql += "values (?,?,?,?,?,?,?,?,?,?,?)";
    	} 
    	
		 return sql;
    }

}