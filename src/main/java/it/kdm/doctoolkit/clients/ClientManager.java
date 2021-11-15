package it.kdm.doctoolkit.clients;

import it.kdm.doctoolkit.clients.firma.WSFirmaStub;
import it.kdm.doctoolkit.services.ToolkitConnector;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public enum ClientManager {

    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private HttpClient httpClient = null;

    private DocerServicesStub docerServicesClient = null;
    private String docerEpr;
    private boolean docerRequiresChanges = false;

    private AuthenticationServiceStub authenticationServicesClient = null;
    private String authenticationEpr;
    private boolean authenticationRequiresChanges = false;

    // Clients Configuration
    private long timeout = 120000;
    private boolean cacheAttachments = false;
    private File cacheDir = null;
    private String fileSizeThreshold = Integer.toString(5 * 1024 * 1024);
	
	private WSFascicolazioneStub fascicolazioneClient;
	private String fascicolazioneEpr;
	private boolean fascicolazioneRequiresChanges;
	
	private WSRegistrazioneStub registrazioneClient;
	private String registrazioneEpr;
	private boolean registrazioneRequiresChanges;
	
	private WSProtocollazioneStub protocollazioneClient;
	private String protocollazioneEpr;
	private boolean protocollazioneRequiresChanges;

    private TracerServiceStub tracerClient;
    private String tracerEpr;
    private boolean tracerRequiresChanges;

    private WSFirmaStub firmaClient;
    private WSTimbroDigitaleStub timbroDigitaleClient;
    private WSPECStub wspecStub;
    private String timbroDigitaleEpr;
    private boolean timbroDigitaleRequiresChanges;

    private WSInvioDocumentoStub invioDocumentoClient;
    private String invioDocumentoEpr;
    private boolean invioDocumentoRequiresChanges;


    public interface ClientInstantiationStrategy {
        public AuthenticationServiceStub getAuthClient(String sede, String epr) throws AxisFault;
        public DocerServicesStub getDocerClient(String sede, String epr) throws AxisFault;
    }

    private ClientInstantiationStrategy defaultStrategy = new ClientInstantiationStrategy() {
        @Override
        public AuthenticationServiceStub getAuthClient(String sede, String epr) throws AxisFault {
            return new AuthenticationServiceStub(epr);
        }

        @Override
        public DocerServicesStub getDocerClient(String sede, String epr) throws AxisFault {
            return new DocerServicesStub(epr);
        }
    };

    public void setInstantiationStrategy(ClientInstantiationStrategy strategy) {
        defaultStrategy = strategy;
    }

    private ClientManager() {
        try {
            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/toolkit.properties"));

            long timeout = Long.parseLong(props.getProperty("timeout"));
            this.setTimeout(timeout);

            String attachmentDir = props.getProperty("attachments.dir");
            File f = new File(attachmentDir);
            if (!f.exists()) {
                f.mkdirs();
            }

            this.setCacheAttachments(true);
            this.setCacheDir(f);

//            this.setDocerServicesEpr(props.getProperty("remote.addr"));
//            this.setAuthenticationEpr(props.getProperty("core.addr"));
//
//            this.setProtocollazioneEpr(props.getProperty("protocollazione.epr"));
//            this.setRegistrazioneEpr(props.getProperty("registrazione.epr"));
//            this.setFascicolazioneEpr(props.getProperty("fascicolazione.epr"));
//            this.setTracerEpr(props.getProperty("tracer.epr"));

            MultiThreadedHttpConnectionManager connectionManager =
                    new MultiThreadedHttpConnectionManager();
            Integer maxConnection = new Integer(System.getProperty("http.maxConnections", "50"));
            connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnection);
            connectionManager.getParams().setMaxTotalConnections(maxConnection);

            httpClient = new HttpClient();
            httpClient.setHttpConnectionManager(connectionManager);
            

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String getDocerServicesEpr(){
        return this.docerEpr;
    }
    
    public ClientManager setDocerServicesEpr(String epr) {
        this.docerEpr = epr;
        docerRequiresChanges = true;
        return this;
    }

    public DocerServicesStub getDocerServicesClient(String sede) throws AxisFault {

        try{
            String epr = ToolkitConnector.getServiceAddress(sede, "docer");
            docerServicesClient = defaultStrategy.getDocerClient(sede, epr); //this.docerEpr);
            configure(docerServicesClient._getServiceClient(), epr); //docerEpr);

    //        if (docerServicesClient == null) {
    //            docerServicesClient = new DocerServicesStub(epr); //this.docerEpr);
    //        }

    //        if (docerRequiresChanges) {
    //            configure(docerServicesClient._getServiceClient(), epr); //docerEpr);
    //            docerRequiresChanges = false;
    //        }
    //        docerServicesClient._getServiceClient().cleanupTransport();
            return docerServicesClient;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }

    private void configure(ServiceClient serviceClient, String epr) {
    	configure(serviceClient, epr, true);
    }
    
    private void configure(ServiceClient serviceClient, String epr, 
    		boolean mtom) {

    	Options options = serviceClient.getOptions();

        options.setTo(new EndpointReference(epr));
        options.setTimeOutInMilliSeconds(this.timeout);
        int timeOutInMilliSeconds = 3 * 60 * 1000; 
        if (mtom) {
	        options.setProperty(Constants.Configuration.ENABLE_MTOM,
	                Constants.VALUE_TRUE);
	        options.setProperty(Constants.Configuration.CACHE_ATTACHMENTS,
	                this.cacheAttachments ? Constants.VALUE_TRUE : Constants.VALUE_FALSE);
	        options.setProperty(Constants.Configuration.ATTACHMENT_TEMP_DIR,
	                this.cacheDir.getAbsolutePath());
	        options.setProperty(Constants.Configuration.FILE_SIZE_THRESHOLD,
	                this.fileSizeThreshold);
	        options.setProperty(HTTPConstants.SO_TIMEOUT, timeOutInMilliSeconds);
        }

        /*options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Constants.VALUE_TRUE);
        options.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, this.httpClient);
        options.setProperty(HTTPConstants.AUTO_RELEASE_CONNECTION, Constants.VALUE_TRUE);*/

        /* Questo blocco e' stato commentato in quanto il ConfigurationContext 
         * sovrascrive le impostazioni dei servizi che usano il ClientManager.
         * Ad esempio il docersystem lavora in MTOM indipendentemente dalle
         * impostazioni locali
         */
        /*ConfigurationContext context = serviceClient.getServiceContext().getConfigurationContext();
        context.setProperty(Constants.Configuration.ENABLE_MTOM,
                Constants.VALUE_TRUE);
        context.setProperty(Constants.Configuration.CACHE_ATTACHMENTS,
                this.cacheAttachments ? Constants.VALUE_TRUE : Constants.VALUE_FALSE);
        context.setProperty(Constants.Configuration.ATTACHMENT_TEMP_DIR,
                this.cacheDir.getAbsolutePath());
        context.setProperty(Constants.Configuration.FILE_SIZE_THRESHOLD,
                this.fileSizeThreshold);


        context.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Constants.VALUE_TRUE);
        context.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, this.httpClient);
        context.setProperty(HTTPConstants.AUTO_RELEASE_CONNECTION, Constants.VALUE_TRUE);*/
    }

    public ClientManager setAuthenticationEpr(String epr) {
        this.authenticationEpr = epr;
        authenticationRequiresChanges = true;
        return this;
    }

    public AuthenticationServiceStub getAuthenticationClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "auth");
            authenticationServicesClient = defaultStrategy.getAuthClient(sede, epr);
            configure(authenticationServicesClient._getServiceClient(),	epr, false);


    //        if (authenticationServicesClient == null) {
    //        	authenticationServicesClient = new AuthenticationServiceStub(epr); //authenticationEpr);
    //        } else if (authenticationRequiresChanges) {
    //            //configure(authenticationServicesClient._getServiceClient(),	authenticationEpr, false);
    //            configure(authenticationServicesClient._getServiceClient(),	epr, false);
    //
    //            authenticationRequiresChanges = false;
    //        }

            authenticationServicesClient._getServiceClient().cleanupTransport();
            return authenticationServicesClient;
        } catch (Exception e) {throw new AxisFault(e.getMessage());}
    }

	public ClientManager setFascicolazioneEpr(String epr) {
		this.fascicolazioneEpr = epr;
		fascicolazioneRequiresChanges = true;
        return this;
	}



    public WSInvioDocumentoStub getInvioDocumentoClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "invioDoc");
            invioDocumentoClient = new WSInvioDocumentoStub(epr); //fascicolazioneEpr);
            configure(invioDocumentoClient._getServiceClient(), epr); //fascicolazioneEpr);

            //        if (fascicolazioneClient == null) {
            //        	fascicolazioneClient = new WSFascicolazioneStub(epr); //fascicolazioneEpr);
            //        } else if (fascicolazioneRequiresChanges) {
            //            configure(fascicolazioneClient._getServiceClient(), epr); //fascicolazioneEpr);
            //
            //            fascicolazioneRequiresChanges = false;
            //        }

            invioDocumentoClient._getServiceClient().cleanupTransport();
            return invioDocumentoClient;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }


	
	public WSFascicolazioneStub getFascicolazioneClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "fasc");
            fascicolazioneClient = new WSFascicolazioneStub(epr); //fascicolazioneEpr);
            configure(fascicolazioneClient._getServiceClient(), epr); //fascicolazioneEpr);

    //        if (fascicolazioneClient == null) {
    //        	fascicolazioneClient = new WSFascicolazioneStub(epr); //fascicolazioneEpr);
    //        } else if (fascicolazioneRequiresChanges) {
    //            configure(fascicolazioneClient._getServiceClient(), epr); //fascicolazioneEpr);
    //
    //            fascicolazioneRequiresChanges = false;
    //        }

            fascicolazioneClient._getServiceClient().cleanupTransport();
            return fascicolazioneClient;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }


    public WSTimbroDigitaleStub getTimbroDigitaleClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "timbro");
            timbroDigitaleClient = new WSTimbroDigitaleStub(epr); //fascicolazioneEpr);
            configure(timbroDigitaleClient._getServiceClient(), epr); //fascicolazioneEpr);


            timbroDigitaleClient._getServiceClient().cleanupTransport();
            return timbroDigitaleClient;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }


    public WSFirmaStub getFirmaClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "firma");
            firmaClient = new WSFirmaStub(epr); //fascicolazioneEpr);
            configure(firmaClient._getServiceClient(), epr); //fascicolazioneEpr);


            firmaClient._getServiceClient().cleanupTransport();
            return firmaClient;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }

    public WSPECStub getPecClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "pec");
            wspecStub = new WSPECStub(epr); //fascicolazioneEpr);
            configure(wspecStub._getServiceClient(), epr); //fascicolazioneEpr);

            wspecStub._getServiceClient().cleanupTransport();
            return wspecStub;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }

	public ClientManager setRegistrazioneEpr(String epr) {
		this.registrazioneEpr = epr;
        registrazioneRequiresChanges = true;
        return this;
	}
	
	public WSRegistrazioneStub getRegistrazioneClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "regis");
            registrazioneClient = new WSRegistrazioneStub(epr);//registrazioneEpr);
            configure(registrazioneClient._getServiceClient(), epr); //registrazioneEpr);

    //        if (registrazioneClient == null) {
    //        	registrazioneClient = new WSRegistrazioneStub(epr);//registrazioneEpr);
    //        } else if (registrazioneRequiresChanges) {
    //            configure(registrazioneClient._getServiceClient(), epr); //registrazioneEpr);
    //
    //            registrazioneRequiresChanges = false;
    //        }

            return registrazioneClient;
        } catch (Exception e) {throw new AxisFault(e.getMessage());}
    }

	public ClientManager setProtocollazioneEpr(String epr) {
		this.protocollazioneEpr = epr;
        protocollazioneRequiresChanges = true;
        return this;
	}
	
	public WSProtocollazioneStub getProtocollazioneClient(String sede) throws AxisFault {

        try {
            String epr = ToolkitConnector.getServiceAddress(sede, "prot");
            protocollazioneClient = new WSProtocollazioneStub(epr); //protocollazioneEpr);
            configure(protocollazioneClient._getServiceClient(), epr); //protocollazioneEpr);

    //        if (protocollazioneClient == null) {
    //        	protocollazioneClient = new WSProtocollazioneStub(epr); //protocollazioneEpr);
    //        } else if (protocollazioneRequiresChanges) {
    //            configure(protocollazioneClient._getServiceClient(), epr); //protocollazioneEpr);
    //
    //            protocollazioneRequiresChanges = false;
    //        }

            return protocollazioneClient;
        } catch (Exception e) {throw new AxisFault(e.getMessage());}
    }

    public ClientManager setTracerEpr(String epr) {
        this.tracerEpr = epr;
        tracerRequiresChanges = true;
        return this;
    }

    public TracerServiceStub getTracerClient(String sede) throws AxisFault {

        try {
        String epr = ToolkitConnector.getServiceAddress(sede, "tracer");
        tracerClient = new TracerServiceStub(epr); //tracerEpr);
        configure(tracerClient._getServiceClient(), epr); //tracerEpr);

//        if (tracerClient == null) {
//            tracerClient = new TracerServiceStub(epr); //tracerEpr);
//        } else if (tracerRequiresChanges) {
//            configure(tracerClient._getServiceClient(), epr); //tracerEpr);
//
//            tracerRequiresChanges = false;
//        }

        return tracerClient;
        } catch(Exception e) {throw new AxisFault(e.getMessage());}
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        resetConf();
        this.timeout = timeout;
    }

    public boolean isCacheAttachments() {
        return cacheAttachments;
    }

    public void setCacheAttachments(boolean cacheAttachments) {
        resetConf();
        this.cacheAttachments = cacheAttachments;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(File cacheDir) {
        resetConf();
        this.cacheDir = cacheDir;
    }

    public long getFileSizeThreshold() {
        return Long.parseLong(fileSizeThreshold);
    }

    public void setFileSizeThreshold(long fileSizeThreshold) {
        resetConf();
        this.fileSizeThreshold = Long.toString(fileSizeThreshold);
    }

    private void resetConf() {
        docerRequiresChanges = true;
        authenticationRequiresChanges = true;
        fascicolazioneRequiresChanges = true;
        registrazioneRequiresChanges = true;
        protocollazioneRequiresChanges = true;
        tracerRequiresChanges = true;

    }
}
