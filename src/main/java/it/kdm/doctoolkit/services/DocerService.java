package it.kdm.doctoolkit.services;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import it.kdm.docer.commons.configuration.ConfigurationUtils;
import it.kdm.doctoolkit.clients.ClientManager;
import it.kdm.doctoolkit.clients.DocerServicesStub;
import it.kdm.doctoolkit.clients.DocerServicesStub.*;
import it.kdm.doctoolkit.exception.DocerApiException;
import it.kdm.doctoolkit.helper.InputStreamDataSource;
import it.kdm.doctoolkit.model.LockStatus;
import it.kdm.doctoolkit.model.*;
import it.kdm.doctoolkit.model.path.ICIFSObject;
import it.kdm.doctoolkit.model.protocollazione.AnnullaProtocolloOBJ;
import it.kdm.doctoolkit.utils.CacheManager;
import it.kdm.doctoolkit.utils.Utils;
import it.kdm.firma.SignatureValidator;
import it.kdm.firma.bean.EsitoVerifiche;
import it.kdm.firma.bean.Firmatario;
import it.kdm.orchestratore.appdoc.utils.CallDocumentMgt;
import it.kdm.orchestratore.session.ActorsCache;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jcs.access.exception.CacheException;
import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class DocerService {
    private static final Logger log = LoggerFactory.getLogger(DocerService.class);
    public static final String DELETED_FORMAT = "DELETED-%s-%s";
//    public static void setWSURL(String url) {
//        ClientManager.INSTANCE.setDocerServicesEpr(url);
//    }

    public static void setAttachDir(String path) {
        ClientManager.INSTANCE.setCacheDir(new File(path));
    }

    public static Documento createFile(String token, Documento file, String parentPath) throws DocerApiException, IOException {
        SolrPathInterface solrInterface = new SolrPathInterface();
        String ente;
        String aoo;

        InputStream in = file.getFileStream();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        org.apache.commons.io.IOUtils.copy(in, baos);
        byte[] bytes = baos.toByteArray();


        if (file.getEstraiFirmatari()) {
            try {

                List<PersonaFisica> firmatari = getFirmatari(new ByteArrayInputStream(bytes));

                file.setFirmatari((List<Corrispondente>) (List<? extends Corrispondente>) firmatari);
            } catch (Exception exc) {
                throw new RuntimeException("Impossibile estrarre i firmatari.", exc);
            }
        }


        ICIFSObject object = solrInterface.openByPath(token, parentPath);
        ente = object.getEnte();
        aoo = object.getAOO();

        if (!(object instanceof Cartella) &&
                !(object instanceof Fascicolo) &&
                !(object instanceof AOO) &&
                !(object instanceof Titolario)) {
            throw new AccessDeniedException(parentPath);
        }

//        try {
//            file.setParentPath(parentPath);
//            solrInterface.openByPath(token, file.getFullPath());
//            throw new FileAlreadyExistsException("FileAlreadyExists: " + file.getFullPath());
//        } catch (FileNotFoundException e) {}

        file.setEnte(ente);
        file.setAoo(aoo);

        if (object instanceof Cartella) {
            Cartella parent = (Cartella) object;

            file.setProperty("PARENT_FOLDER_ID", parent.getID());
        } else if (object instanceof Fascicolo) {
            Fascicolo parent = (Fascicolo) object;

            if (!Strings.isNullOrEmpty(parent.getProperty("PIANO_CLASS"))) {
                file.setProperty("PARENT_FASCICOLO_ID", String.format("%s$%s|%s|%s",
                        parent.getClassifica(), parent.getProperty("PIANO_CLASS"), parent.getAnno(), parent.getProgressivo()));
            } else {
                file.setProperty("PARENT_FASCICOLO_ID", String.format("%s|%s|%s",
                        parent.getClassifica(), parent.getAnno(), parent.getProgressivo()));
            }
        }

        file.setFile(new ByteArrayInputStream(bytes), file.getDocName());
        file = DocerService.creaDocumentoCIFS(token, file);

        try {
            file = (Documento) solrInterface.reopenObject(token, file);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    public static User recuperaUtente(String token, String userId) throws DocerApiException {
        DocerServicesStub.GetUserResponse userResp;
        DocerServicesStub.GetUser req = new DocerServicesStub.GetUser();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            req.setToken(token);
            req.setUserId(userId);

            userResp = serv.getUser(req);
            KeyValuePair[] meta = userResp.get_return();

            HashMap<String, String> props = convertMetadati(meta);
            if (props.isEmpty()) {
                throw new DocerApiException("User not found", 404);
            }

            User u = new User();
            u.copyFrom(props);

            return u;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    @Deprecated
    public static Group recuperaGruppo(String token, String groupId) throws DocerApiException {
        DocerServicesStub.GetGroupResponse resp;
        DocerServicesStub.GetGroup req = new DocerServicesStub.GetGroup();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            req.setToken(token);
            req.setGroupId(groupId);

            resp = serv.getGroup(req);
            KeyValuePair[] meta = resp.get_return();

            HashMap<String, String> props = convertMetadati(meta);
            if (props.isEmpty()) {
                throw new DocerApiException("Group not found", 404);
            }

            Group g = new Group();
            g.copyFrom(props);

            return g;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static User creaUtente(String token, User user) throws DocerApiException {
        DocerServicesStub.CreateUserResponse userResp;
        DocerServicesStub.CreateUser createUser = new DocerServicesStub.CreateUser();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            createUser.setToken(token);

            KeyValuePair[] metadati = convertMetadati(user.properties);

            createUser.setUserInfo(metadati);
            serv.createUser(createUser);

            return recuperaUtente(token, user.getUserId());

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static Group creaGruppo(String token, Group group) throws DocerApiException {
        DocerServicesStub.CreateGroupResponse resp;
        DocerServicesStub.CreateGroup createGroup = new DocerServicesStub.CreateGroup();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            createGroup.setToken(token);

            KeyValuePair[] metadati = convertMetadati(group.properties);

            createGroup.setGroupInfo(metadati);
            serv.createGroup(createGroup);

            return recuperaGruppo(token, group.getGroupId());

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static User aggiornaUtente(String token, User user) throws DocerApiException {
        DocerServicesStub.UpdateUserResponse userResp;
        DocerServicesStub.UpdateUser req = new DocerServicesStub.UpdateUser();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            req.setToken(token);
            req.setUserId(user.getUserId());

            KeyValuePair[] metadati = convertMetadati(user.properties);

            req.setUserInfo(metadati);
            serv.updateUser(req);

            return recuperaUtente(token, user.getUserId());

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static Group aggiornaGruppo(String token, Group group) throws DocerApiException {
        DocerServicesStub.UpdateGroupResponse resp;
        DocerServicesStub.UpdateGroup req = new DocerServicesStub.UpdateGroup();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            String groupId = group.getGroupId();

            req.setToken(token);
            req.setGroupId(groupId);

            //workaround perchè viene controllato il parametro GROUP_ID in update ed è vietato
            group.properties.remove("GROUP_ID");

            KeyValuePair[] metadati = convertMetadati(group.properties);

            req.setGroupInfo(metadati);
            serv.updateGroup(req);

            return recuperaGruppo(token, groupId);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static void settaGruppiUtente(String token, List<Group> groupList, String userId) throws DocerApiException {

        List<String> listaGruppi = new ArrayList<String>();

        for (Group g : groupList)
            listaGruppi.add(g.getGroupId());

        setGroupsOfUser(token, listaGruppi.toArray(new String[0]), userId);
    }

    public static void setGroupsOfUser(String token, String[] groupList, String userId) throws DocerApiException {
        DocerServicesStub.SetGroupsOfUserResponse resp;
        DocerServicesStub.SetGroupsOfUser req = new DocerServicesStub.SetGroupsOfUser();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            req.setToken(token);

            req.setUserId(userId);
            req.setGroups(groupList);

            serv.setGroupsOfUser(req);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static Documento classificaDocumento(String token, Documento doc, String classifica) throws DocerApiException {

        DocerServicesStub.ClassificaDocumento classificaDocumento = new DocerServicesStub.ClassificaDocumento();
        DocerServicesStub.ClassificaDocumentoResponse classificaDocumentoResponse = new DocerServicesStub.ClassificaDocumentoResponse();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            Documento clone = new Documento();
            clone.copyFrom(doc);

            clone.setProperty("CLASSIFICA", classifica);
            clone.properties.remove("TIPO_COMPONENTE");
            clone.properties.remove("FILE_NAME");
            clone.properties.remove("ABSTRACT");
            clone.properties.remove("TYPE_ID");
            clone.properties.remove("DOCNAME");
            clone.properties.remove("DOCNUM");

            classificaDocumento.setDocId(doc.getDocNum());
            classificaDocumento.setToken(token);

            KeyValuePair[] metadati = convertMetadati(clone.properties);
            classificaDocumento.setMetadata(metadati);

            classificaDocumentoResponse = serv.classificaDocumento(classificaDocumento);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }

        return doc;

    }

    public static String[] recuperaVersione(String token, String docId) throws DocerApiException {

        DocerServicesStub.GetVersionsResponse gur = null;
        DocerServicesStub.GetVersions gui = new DocerServicesStub.GetVersions();

        try {

            if (true){
                SolrPathInterface solrInterface = new SolrPathInterface();

                Documento currDoc = solrInterface.openByDocnum(CallDocumentMgt.getToken(), docId, Collections.singletonList("DOC_VERSION"));
                String DOC_VERSION = currDoc.getDocumentVersion();
                int v = ((Double)Double.parseDouble(DOC_VERSION)).intValue();
                List<String> versions = new ArrayList<>();
                for( int i=v; i>0; i--){
                    versions.add(i+".0");
                }
                return versions.toArray(new String[0]);
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setDocId(docId);
            gui.setToken(token);

            gur = serv.getVersions(gui);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return();

    }

    public static Document readConfigDOM(String token) throws DocerApiException, IOException {

        Document doc = null;

        try {
            Optional obj = CacheManager.confCache().get("docerConfigDOM");

             if(obj.isPresent() && obj.get() instanceof Document)
                 return (Document) obj.get();

        } catch (CacheException e) {
            throw new IOException(e);
        }

        String xmlStr;

        if (true){
            String ente = Utils.extractOptionalTokenKey(token,"ente","");
            File f = ConfigurationUtils.getFile(ente,"WSDocer/configuration.xml");
            xmlStr = FileUtils.readFileToString(f);
        } else {
            xmlStr = DocerService.readConfig(token);
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlStr)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CacheManager.confCache().put("docerConfigDOM", doc);

        } catch (CacheException e) {
            log.warn("readConfigDOM: errore gestione cache della configurazione xml di DocER.");
        }
        return doc;

    }

    public static String readConfig(String token) throws DocerApiException {

        DocerServicesStub.ReadConfigResponse gur = null;
        DocerServicesStub.ReadConfig gui = new DocerServicesStub.ReadConfig();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setToken(token);

            gur = serv.readConfig(gui);

            String config = gur.get_return();
            return config;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static boolean pubblicaDocumento(String token, String docNum, PubblicaDocumentoSdk pubblicaDocumentoSdk) throws DocerApiException {

        DocerServicesStub.PubblicaDocumentoResponse gur = null;
        DocerServicesStub.PubblicaDocumento gui = new DocerServicesStub.PubblicaDocumento();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setDocId(docNum);
            gui.setToken(token);

            KeyValuePair[] key = new KeyValuePair[5];
            key[0] = new KeyValuePair();
            key[1] = new KeyValuePair();
            key[2] = new KeyValuePair();
            key[3] = new KeyValuePair();
            key[4] = new KeyValuePair();
            key[0].setKey("DATA_INIZIO_PUB");
            key[0].setValue(pubblicaDocumentoSdk.getDataInizio());
            key[1].setKey("DATA_FINE_PUB");
            key[1].setValue(pubblicaDocumentoSdk.getDataFine());
            key[2].setKey("OGGETTO_PUB");
            key[2].setValue(pubblicaDocumentoSdk.getOggetto());
            key[3].setKey("REGISTRO_PUB");
            key[3].setValue(pubblicaDocumentoSdk.getRegistro());
            key[4].setKey("NUMERO_PUB");
            key[4].setValue(pubblicaDocumentoSdk.getNumero());

            gui.setMetadata(key);

            gur = serv.pubblicaDocumento(gui);

            boolean b = gur.get_return();
            return b;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static List<String> recuperaTipiAnagraficheCustom(String token) throws DocerApiException {

        List<String> listTypes = new ArrayList<String>();

        try {

            Document doc = DocerService.readConfigDOM(token);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile("/configuration/group[@name='impianto']/section[@name='anagrafiche_types']/type[@custom='true']");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); i++) {
                Node firstNode = nl.item(i);

                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstElement = (Element) firstNode;
                    String name = firstElement.getAttribute("name");
                    listTypes.add(name);
                }
            }
            return listTypes;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    /*
    public static List<String> recuperaTipiAnagraficheCustom2(String token) throws DocerApiException {

        DocerServicesStub.ReadConfigResponse gur = null;
        DocerServicesStub.ReadConfig gui = new DocerServicesStub.ReadConfig();
        List<String> listTypes = new ArrayList<String>();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setToken(token);
            gur = serv.readConfig(gui);
            String config = gur.get_return();

            ByteArrayInputStream stream = new ByteArrayInputStream(config.getBytes());

            Reader reader = new InputStreamReader(stream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/configuration/group[@name='impianto']/section[@name='anagrafiche_types']/type[@custom='true']");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); i++) {
                Node firstNode = nl.item(i);

                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstElement = (Element) firstNode;
                    String name = firstElement.getAttribute("name");
                    listTypes.add(name);
                }
            }
            return listTypes;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }
*/

    public static String recuperaCodiceAnagraficheCustom(String token, String typeAnag) throws DocerApiException {

        DocerServicesStub.ReadConfigResponse gur = null;
        DocerServicesStub.ReadConfig gui = new DocerServicesStub.ReadConfig();
        String name = "";

        try {
            /*String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setToken(token);
            gur = serv.readConfig(gui);
            String config = gur.get_return();

            ByteArrayInputStream stream = new ByteArrayInputStream(config.getBytes());

            Reader reader = new InputStreamReader(stream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);*/

            Document doc = DocerService.readConfigDOM(token);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/configuration/group[@name='impianto']/section[@name='anagrafiche_types']/type[@name='" + typeAnag.toLowerCase() + "']");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); i++) {
                Node firstNode = nl.item(i);

                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstElement = (Element) firstNode;
                    NodeList list = firstElement.getChildNodes();
                    for (int x = 0; x < list.getLength(); x++) {
                        Node node = list.item(x);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String role = element.getAttribute("role");
                            if ("codice".equals(role)) {
                                name = element.getNodeName();
                                break;
                            }
                        }
                    }
                }
            }
            return name.toUpperCase();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

/*
    public static String recuperaDescrAnagraficheCustom2(String token, String typeAnag) throws DocerApiException {

        DocerServicesStub.ReadConfigResponse gur = null;
        DocerServicesStub.ReadConfig gui = new DocerServicesStub.ReadConfig();
        String name = "";

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setToken(token);
            gur = serv.readConfig(gui);
            String config = gur.get_return();

            ByteArrayInputStream stream = new ByteArrayInputStream(config.getBytes());

            Reader reader = new InputStreamReader(stream,"UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/configuration/group[@name='impianto']/section[@name='anagrafiche_types']/type[@name='" + typeAnag + "']");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nl.getLength(); i++) {
                Node firstNode = nl.item(i);

                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstElement = (Element) firstNode;
                    NodeList list = firstElement.getChildNodes();
                    for (int x = 0; x < list.getLength(); x++) {
                        Node node = list.item(x);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String role = element.getAttribute("role");
                            if ("descrizione".equals(role)) {
                                name = element.getNodeName();
                                break;
                            }
                        }
                    }
                }
            }
            return name.toUpperCase();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }
*/

    public static String recuperaDescrAnagraficheCustom(String token, String typeAnag) throws DocerApiException {

        String name = "";
        try {
            Document doc = DocerService.readConfigDOM(token);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile("/configuration/group[@name='impianto']/section[@name='anagrafiche_types']/type[@name='" + typeAnag.toLowerCase() + "']");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            Node n = null;
            Element eElement = null;

            for (int i = 0; i < nl.getLength(); i++) {
                Node firstNode = nl.item(i);

                if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstElement = (Element) firstNode;
                    NodeList list = firstElement.getChildNodes();
                    for (int x = 0; x < list.getLength(); x++) {
                        Node node = list.item(x);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String role = element.getAttribute("role");
                            if ("descrizione".equals(role)) {
                                name = element.getNodeName();
                                break;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return name.toUpperCase();
    }


    public static boolean verifyDocerToken(String token) throws DocerApiException {

        String expiration = Utils.extractOptionalTokenKey(token,"expiration",null);

        if (!Strings.isNullOrEmpty(expiration)){
            int expiresAt = Integer.parseInt(expiration);
            return expiresAt > (System.currentTimeMillis()/1000);
        }

        DocerServicesStub.GetEnte gui = new DocerServicesStub.GetEnte();

        try {

            String codEnte = Utils.extractTokenKey(token,"ente");

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setCodiceEnte(codEnte);
            gui.setToken(token);

            serv.getEnte(gui);
            return true;

        } catch (Exception e) {
            return false;
        }

    }


/*
    public static String recuperaUserLock(String token, String docId) throws DocerApiException {

        DocerServicesStub.GetLockStatusResponse gur = null;
        DocerServicesStub.GetLockStatus gui = new DocerServicesStub.GetLockStatus();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            gui.setDocId(docId);
            gui.setToken(token);

            gur = serv.getLockStatus(gui);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return().getUserId();

    }
*/

    public static int recuperaDirittiEffettivi(String token, String docId, String userId) throws DocerApiException {

        DocerServicesStub.GetUserRightsResponse gur = null;
        DocerServicesStub.GetUserRights gui = new DocerServicesStub.GetUserRights();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gui.setDocId(docId);
            gui.setToken(token);
            gui.setUserId(userId);
            gur = serv.getUserRights(gui);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return();

    }




    public static boolean annullamentoProtocollazione(String token, AnnullaProtocolloOBJ annullaProtocolloOBJ) throws DocerApiException {

        boolean isOkAnnull =false;

        if(token == null){
            throw new DocerApiException(new Exception("token not defined for method annullamentoProtocollazione"));
        }

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.ProtocollaDocumento proto= new DocerServicesStub.ProtocollaDocumento();
            proto.setDocId(annullaProtocolloOBJ.getDocNum());
            proto.setToken(token);

            KeyValuePair annullatoPg = new KeyValuePair();
            annullatoPg.setKey("annullato_pg");
            annullatoPg.setValue(annullaProtocolloOBJ.getAnnullatoPg());

            KeyValuePair motivoAnnullamento = new KeyValuePair();
            motivoAnnullamento.setKey("m_annull_pg");
            motivoAnnullamento.setValue(annullaProtocolloOBJ.getMotivoAnnullaMentoPG());

            KeyValuePair provvedimentoAnnullaPg = new KeyValuePair();
            provvedimentoAnnullaPg.setKey("p_annull_pg");
            provvedimentoAnnullaPg.setValue(annullaProtocolloOBJ.getProvvedimentoAnnullaPG());

            //imposto la data di annullamento
            Date dataR = new Date();
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            DateTime dateTime = new DateTime(dataR);

            KeyValuePair dataAnnullamento = new KeyValuePair();
            dataAnnullamento.setKey("d_annull_pg");
            dataAnnullamento.setValue(fmt.print(dateTime.toDateTime(DateTimeZone.UTC)));


            KeyValuePair[] protoKey = new KeyValuePair[4];
            protoKey[0] = annullatoPg;
            protoKey[1] = motivoAnnullamento;
            protoKey[2] = provvedimentoAnnullaPg;
            protoKey[3] = dataAnnullamento;

            proto.setMetadata(protoKey);

            DocerServicesStub.ProtocollaDocumentoResponse resp =  serv.protocollaDocumento(proto);


            isOkAnnull = resp.get_return();
            if(!isOkAnnull){
                throw new DocerApiException(new Exception("Impossibile effettuare l'annullamento della protocollazione"));
            }

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return isOkAnnull;
    }




    public static boolean protocollaDocumentoDocerServices(String token, String docNum, String tipoProtocollazione, ProtoDocerServicesObj obj  ) throws DocerApiException {



        if(token == null){
            throw new DocerApiException(new Exception("token not defined for method annullamentoProtocollazione"));
        }

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.ProtocollaDocumento proto= new DocerServicesStub.ProtocollaDocumento();
            proto.setDocId(docNum);
            proto.setToken(token);

            KeyValuePair codEnte = new KeyValuePair();
            codEnte.setKey("COD_ENTE");
            codEnte.setValue(obj.getCodEnte());

            KeyValuePair codAOO = new KeyValuePair();
            codAOO.setKey("COD_AOO");
            codAOO.setValue(obj.getCodAoo());

            KeyValuePair numPG = new KeyValuePair();
            numPG.setKey("NUM_PG");
            numPG.setValue(obj.getNumPG());

            KeyValuePair oggettoPG = new KeyValuePair();
            oggettoPG.setKey("OGGETTO_PG");
            oggettoPG.setValue(obj.getOggettoPG());

            KeyValuePair registrPG = new KeyValuePair();
            registrPG.setKey("REGISTRO_PG");
            registrPG.setValue(obj.getRegistroPG());

            KeyValuePair dataPg = new KeyValuePair();
            dataPg.setKey("DATA_PG");
            dataPg.setValue(obj.getDataPG());

            KeyValuePair tipoProt = new KeyValuePair();
            tipoProt.setKey("TIPO_PROTOCOLLAZIONE");
            tipoProt.setValue(tipoProtocollazione);

            KeyValuePair tipFirma = new KeyValuePair();
            tipFirma.setKey("TIPO_FIRMA");
            tipFirma.setValue(obj.getTipoFirma());

            KeyValuePair destProt = new KeyValuePair();
            destProt.setKey("DESTINATARI");
            destProt.setValue("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Destinatari/>");

            KeyValuePair mitProt = new KeyValuePair();
            mitProt.setKey("MITTENTI");
            mitProt.setValue("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Mittenti/>");

            KeyValuePair[] protoKey = new KeyValuePair[9];
            protoKey[0] = codEnte;
            protoKey[1] = codAOO;
            protoKey[2] = numPG;
            protoKey[3] = oggettoPG;
            protoKey[4] = registrPG;
            protoKey[5] = dataPg;
            protoKey[6] = tipFirma;
            protoKey[7] = destProt;
            protoKey[8] = tipoProt;


            proto.setMetadata(protoKey);

            DocerServicesStub.ProtocollaDocumentoResponse resp =  serv.protocollaDocumento(proto);


            return resp.get_return();


        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }



    public static int recuperaDirittiEffettivi(String token, Fascicolo fascicolo, String userId) throws DocerApiException {

        DocerServicesStub.GetUserRightsAnagraficheResponse gur = null;
        DocerServicesStub.GetUserRightsAnagrafiche gui = new DocerServicesStub.GetUserRightsAnagrafiche();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            KeyValuePair codEnte = new KeyValuePair();
            codEnte.setKey("COD_ENTE");
            codEnte.setValue(fascicolo.getProperty("COD_ENTE"));

            KeyValuePair codAoo = new KeyValuePair();
            codAoo.setKey("COD_AOO");
            codAoo.setValue(fascicolo.getProperty("COD_AOO"));

            KeyValuePair classifica = new KeyValuePair();
            classifica.setKey("CLASSIFICA");
            classifica.setValue(fascicolo.getProperty("CLASSIFICA"));

            KeyValuePair anno = new KeyValuePair();
            anno.setKey("ANNO_FASCICOLO");
            anno.setValue(fascicolo.getProperty("ANNO_FASCICOLO"));

            KeyValuePair progrFasc = new KeyValuePair();
            progrFasc.setKey("PROGR_FASCICOLO");
            progrFasc.setValue(fascicolo.getProperty("PROGR_FASCICOLO"));

            KeyValuePair[] fascId = new KeyValuePair[5];
            fascId[0] = codEnte;
            fascId[1] = codAoo;
            fascId[2] = classifica;
            fascId[3] = anno;
            fascId[4] = progrFasc;

            gui.setToken(token);
            gui.setId(fascId);
            gui.setType("FASCICOLO");
            gui.setUserId(userId);

            gur = serv.getUserRightsAnagrafiche(gui);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return();

    }

    public static int recuperaDirittiEffettivi(String token, Titolario titolario, String userId) throws DocerApiException {

        DocerServicesStub.GetUserRightsAnagraficheResponse gur = null;
        DocerServicesStub.GetUserRightsAnagrafiche gui = new DocerServicesStub.GetUserRightsAnagrafiche();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            KeyValuePair codEnte = new KeyValuePair();
            codEnte.setKey("COD_ENTE");
            codEnte.setValue(titolario.getProperty("COD_ENTE"));

            KeyValuePair codAoo = new KeyValuePair();
            codAoo.setKey("COD_AOO");
            codAoo.setValue(titolario.getProperty("COD_AOO"));

            KeyValuePair classifica = new KeyValuePair();
            codEnte.setKey("CLASSIFICA");
            codEnte.setValue(titolario.getProperty("CLASSIFICA"));

            KeyValuePair[] titoId = new KeyValuePair[3];
            titoId[0] = codEnte;
            titoId[1] = codAoo;
            titoId[2] = classifica;

            gui.setToken(token);
            gui.setId(titoId);
            gui.setType("TITOLARIO");
            gui.setUserId(userId);

            gur = serv.getUserRightsAnagrafiche(gui);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return();

    }

    public static int recuperaDirittiEffettivi(String token, Cartella folder, String userId) throws DocerApiException {

        DocerServicesStub.GetUserRightsFolderResponse gur = null;
        DocerServicesStub.GetUserRightsFolder gui = new DocerServicesStub.GetUserRightsFolder();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            gui.setToken(token);
            gui.setFolderId(folder.getID());
            gui.setToken(token);
            gui.setUserId(userId);

            gur = serv.getUserRightsFolder(gui);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return();

    }

    public static int recuperaDirittiEffettivi(String token, AnagraficaCustom anag, String userId) throws DocerApiException {

        DocerServicesStub.GetUserRightsAnagraficheResponse gur = null;
        DocerServicesStub.GetUserRightsAnagrafiche gui = new DocerServicesStub.GetUserRightsAnagrafiche();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            KeyValuePair codEnte = new KeyValuePair();
            codEnte.setKey("COD_ENTE");
            codEnte.setValue(anag.getProperty("COD_ENTE"));

            KeyValuePair codAoo = new KeyValuePair();
            codAoo.setKey("COD_AOO");
            codAoo.setValue(anag.getProperty("COD_AOO"));

            String codice_name = recuperaCodiceAnagraficheCustom(token, anag.getFEName());
            KeyValuePair codice = new KeyValuePair();
            codice.setKey(codice_name);
            codice.setValue(anag.getProperty(codice_name));

            KeyValuePair[] anagId = new KeyValuePair[5];
            anagId[0] = codEnte;
            anagId[1] = codAoo;
            anagId[2] = codice;

            gui.setToken(token);
            gui.setId(anagId);
            gui.setType(anag.getProperty("TYPE_ID"));
            gui.setUserId(userId);

            gur = serv.getUserRightsAnagrafiche(gui);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur.get_return();

    }

/*
    public static DocerServicesStub.GetUserResponse getUserInfo(String token, String userId) throws DocerApiException {

        DocerServicesStub.GetUserResponse gur = null;
        DocerServicesStub.GetUser gui = new DocerServicesStub.GetUser();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            gui.setToken(token);
            gui.setUserId(userId);
            gur = serv.getUser(gui);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gur;
    }
*/

/*
    public static String[] recuperaUtentiDaGruppo(String token, String groupId) throws DocerApiException {
        DocerServicesStub.GetUsersOfGroup gug = new DocerServicesStub.GetUsersOfGroup();
        DocerServicesStub.GetUsersOfGroupResponse gugr = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gug.setToken(token);
            gug.setGroupId(groupId);

            gugr = serv.getUsersOfGroup(gug);


        } catch (Exception e) {
            throw new DocerApiException(e);
        }

        return gugr.get_return();
    }
*/

    @Deprecated
    public static DocerServicesStub.GetUsersOfGroupResponse getUsersOfGroup(String token, String groupId) throws DocerApiException {

        DocerServicesStub.GetUsersOfGroup gug = new DocerServicesStub.GetUsersOfGroup();
        DocerServicesStub.GetUsersOfGroupResponse gugr = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            gug.setToken(token);
            gug.setGroupId(groupId);

            gugr = serv.getUsersOfGroup(gug);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return gugr;
    }

/*
    public static String[] recuperaGruppiUtente(String token, String userId) throws DocerApiException {

        DocerServicesStub.GetGroupsOfUser ggu = new DocerServicesStub.GetGroupsOfUser();
        DocerServicesStub.GetGroupsOfUserResponse ggur = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            ggu.setToken(token);
            ggu.setUserId(userId);

            ggur = serv.getGroupsOfUser(ggu);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return ggur.get_return();
    }
*/

    @Deprecated
    public static DocerServicesStub.GetGroupsOfUserResponse getGroupsOfUser(String token, String userId) throws DocerApiException {

        DocerServicesStub.GetGroupsOfUser ggu = new DocerServicesStub.GetGroupsOfUser();
        DocerServicesStub.GetGroupsOfUserResponse ggur = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            ggu.setToken(token);
            ggu.setUserId(userId);

            ggur = serv.getGroupsOfUser(ggu);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return ggur;
    }

/*
    public static List<Group> ricercaGruppi(String token, GroupCriteria criteria) throws DocerApiException {

        List<Group> lista;
        DocerServicesStub.SearchGroups sg = new DocerServicesStub.SearchGroups();
        DocerServicesStub.SearchGroupsResponse sgr = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            sg.setToken(token);
            KeyValuePair[] meta = convertMetadati(criteria.properties, true);
            sg.setSearchCriteria(meta);

            sgr = serv.searchGroups(sg);

            lista = new ArrayList<Group>();

            if (sgr.get_return() == null)
                return lista;

            for (SearchItem item : sgr.get_return()) {
                Group g = new Group();
                HashMap<String, String> props = convertMetadati(item.getMetadata());
                g.properties = props;
                lista.add(g);
            }
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return lista;
    }
*/

    @Deprecated
    public static DocerServicesStub.SearchGroupsResponse searchGroups(String token, KeyValuePair[] criteria) throws DocerApiException {

        DocerServicesStub.SearchGroups sg = new DocerServicesStub.SearchGroups();
        DocerServicesStub.SearchGroupsResponse sgr = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            sg.setToken(token);
            sg.setSearchCriteria(criteria);

            sgr = serv.searchGroups(sg);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return sgr;
    }

/*
    public static List<User> ricercaUtenti(String token, UserCriteria criteria) throws DocerApiException {
        List<User> lista;
        DocerServicesStub.SearchUsers su = new DocerServicesStub.SearchUsers();
        DocerServicesStub.SearchUsersResponse sur = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            su.setToken(token);
            KeyValuePair[] meta = convertMetadati(criteria.properties, true);
            su.setSearchCriteria(meta);

            sur = serv.searchUsers(su);
            lista = new ArrayList<User>();

            if (sur.get_return() == null)
                return lista;

            for (SearchItem item : sur.get_return()) {
                User u = new User();
                HashMap<String, String> props = convertMetadati(item.getMetadata());
                u.properties = props;
                lista.add(u);
            }

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return lista;
    }
*/

    @Deprecated
    public static DocerServicesStub.SearchUsersResponse searchUsers(String token, KeyValuePair[] criteria) throws DocerApiException {

        DocerServicesStub.SearchUsers su = new DocerServicesStub.SearchUsers();
        DocerServicesStub.SearchUsersResponse sur = null;
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            su.setToken(token);
            su.setSearchCriteria(criteria);

            sur = serv.searchUsers(su);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return sur;
    }

    public static AnagraficaCustom creaAnagraficaCustom(String token, String tipoAnagrafica, AnagraficaCustom anagrafica) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            CreateAnagraficaCustom anag = new CreateAnagraficaCustom();
            GetAnagraficaCustom getAnag = new GetAnagraficaCustom();

            GetAnagraficaCustomResponse getResp = null;

            anagrafica.setProperty("TYPE_ID", tipoAnagrafica);

            KeyValuePair[] metadati = convertMetadati(anagrafica.properties);

            anag.setCustomInfo(metadati);
            anag.setToken(token);

            serv.createAnagraficaCustom(anag);

            getAnag.setCustomId(metadati);
            getAnag.setToken(token);

            getResp = serv.getAnagraficaCustom(getAnag);

            KeyValuePair[] metaAnagrafica = getResp.get_return();

            HashMap<String, String> properties = convertMetadati(metaAnagrafica);

            AnagraficaCustom anaCustom = new AnagraficaCustom();
            anaCustom.properties = new HashMap<String, String>(properties);

            return anaCustom;
        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

/*
    public static void versiona(String token) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }
*/

    public static AnagraficaCustom aggiornaAnagraficaCustom(String token, String tipoAnagrafica, String nome_campo_codice, AnagraficaCustom anagrafica) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            UpdateAnagraficaCustom update = new UpdateAnagraficaCustom();

            GetAnagraficaCustom get = new GetAnagraficaCustom();
            GetAnagraficaCustomResponse getResp = null;

            anagrafica.setProperty("TYPE_ID", tipoAnagrafica);

            KeyValuePair[] metadati = convertMetadati(anagrafica.properties);

            HashMap<String, String> metaid = new HashMap<String, String>();
            metaid.put("COD_ENTE", anagrafica.getEnte());
            metaid.put("COD_AOO", anagrafica.getAoo());
            metaid.put("TYPE_ID", tipoAnagrafica);
            metaid.put(nome_campo_codice, anagrafica.getProperty(nome_campo_codice));

            KeyValuePair[] metadati_id = convertMetadati(metaid);

            update.setCustomInfo(metadati);
            update.setCustomId(metadati_id);
            update.setToken(token);

            update.setCustomInfo(metadati);
            update.setCustomId(metadati_id);
            update.setToken(token);

            get.setCustomId(metadati);
            get.setToken(token);

            serv.updateAnagraficaCustom(update);

            getResp = serv.getAnagraficaCustom(get);

            KeyValuePair[] metaAnagrafica = getResp.get_return();

            HashMap<String, String> properties = convertMetadati(metaAnagrafica);

            AnagraficaCustom anagraficaRet = new AnagraficaCustom();
            anagraficaRet.properties = new HashMap<String, String>(properties);

            return anagraficaRet;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }
    }

    public static List<AnagraficaCustom> ricercaAnagraficaCustom(String token, AnagraficaCustomCriteria parametri_ricerca) throws DocerApiException {
        AnagraficaCustom a = new AnagraficaCustom();
        a.properties = parametri_ricerca.properties;
        return ricercaAnagraficaCustom(token, parametri_ricerca.getTipoAnagrafica(), a);
    }

    @Deprecated
    public static List<AnagraficaCustom> ricercaAnagraficaCustom(String token, String tipoAnagrafica, AnagraficaCustom parametri_ricerca) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            HashMap<String, String> clone = new HashMap<String, String>(parametri_ricerca.properties);
            for (Entry<String, String> pair : clone.entrySet()) {
                // skip dei parametri "" (vuoti)
                if (StringUtils.isEmpty(parametri_ricerca.getProperty(pair.getKey()))) {
                    parametri_ricerca.properties.remove(pair.getKey());

                }
            }

            clone.clear();

            // for(String key : clone.Keys)

            KeyValuePair[] metasearch = convertMetadati(parametri_ricerca.properties, true);

            SearchAnagrafiche search = new SearchAnagrafiche();
            SearchAnagraficheResponse resp = null;

            search.setSearchCriteria(metasearch);
            search.setToken(token);
            search.setType(tipoAnagrafica);

            resp = serv.searchAnagrafiche(search);

            SearchItem[] items = resp.get_return();

            List<AnagraficaCustom> lista = new ArrayList<AnagraficaCustom>();

            if (items == null) {
                return lista;
            }

            AnagraficaCustom anagrafica = null;

            for (SearchItem item : items) {
                anagrafica = new AnagraficaCustom();
                anagrafica.properties = convertMetadati(item.getMetadata());
                lista.add(anagrafica);
            }

            return lista;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static Titolario creaTitolario(String token, Titolario titolario, List<Acl> diritti) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            if (titolario.getParentClassifica() == null) {
                throw new DocerApiException("Valore 'null' non ammesso per il campo: 'ParentClassifica'", 560);
            }

            //Utils.validateFolderName(titolario.getDescrizione());

            KeyValuePair[] metadati = convertMetadati(titolario.properties);

            if (diritti != null) {
                KeyValuePair[] acls = convertACL(diritti);

                SetACLTitolario paramAcl = new SetACLTitolario();
                paramAcl.setAcls(acls);
                paramAcl.setTitolarioId(metadati);
                paramAcl.setToken(token);
                serv.setACLTitolario(paramAcl);
            }

            CreateTitolario param = new CreateTitolario();
            GetTitolario paramTit = new GetTitolario();
            GetTitolarioResponse resp = null;

            param.setTitolarioInfo(metadati);
            param.setToken(token);

            paramTit.setTitolarioId(metadati);
            paramTit.setToken(token);

            serv.createTitolario(param);

            resp = serv.getTitolario(paramTit);
            KeyValuePair[] metaTitolario = resp.get_return();

            HashMap<String, String> properties = convertMetadati(metaTitolario);
            Titolario tit = new Titolario();
            tit.properties = new HashMap<String, String>(properties);

            return tit;
        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static Titolario aggiornaTitolario(String token, Titolario titolario, List<Acl> diritti) throws DocerApiException {
        try {

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

//            if (titolario.getParentClassifica() == null) {
//                throw new DocerApiException("Valore 'null' non ammesso per il campo: 'ParentClassifica'", 560);
//            }

            /*if (!Strings.isNullOrEmpty(titolario.getDescrizione())) {
                Utils.validateFolderName(titolario.getDescrizione());
            }*/

            titolario.cleanFields(false);
            KeyValuePair[] metadati = convertMetadati(titolario.properties);

            if (diritti != null) {
                KeyValuePair[] acls = convertACL(diritti);

                SetACLTitolario paramsACL = new SetACLTitolario();
                paramsACL.setAcls(acls);
                paramsACL.setTitolarioId(metadati);
                paramsACL.setToken(token);
                serv.setACLTitolario(paramsACL);
            }

            HashMap<String, String> metaid = new HashMap<String, String>();
            metaid.put("COD_ENTE", titolario.getEnte());
            metaid.put("COD_AOO", titolario.getAoo());
            metaid.put("CLASSIFICA", titolario.getClassifica());
            metaid.put("COD_TITOLARIO", titolario.getCodiceTitolario());

            KeyValuePair[] metadati_id = convertMetadati(metaid);

            UpdateTitolario params = new UpdateTitolario();

            GetTitolario paramsGet = new GetTitolario();
            GetTitolarioResponse resp = null;

            params.setTitolarioId(metadati_id);
            params.setTitolarioInfo(metadati);
            params.setToken(token);

            paramsGet.setTitolarioId(metadati);
            paramsGet.setToken(token);

            serv.updateTitolario(params);

            resp = serv.getTitolario(paramsGet);

            KeyValuePair[] metaTitolario = resp.get_return();

            HashMap<String, String> properties = convertMetadati(metaTitolario);
            Titolario tit = new Titolario();
            tit.properties = new HashMap<String, String>(properties);

            return tit;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }
    }

    public static List<AOO> ricercaAOO(String token, AOOCriteria parametri_ricerca) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            HashMap<String, String> clone = new HashMap<String, String>(parametri_ricerca.properties);

            /*
            for (Entry<String, String> pair : parametri_ricerca.properties.entrySet()) {
                if (pair.getKey().equalsIgnoreCase("PARENT_CLASSIFICA"))
                    continue;

                if (StringUtils.isEmpty(clone.get(pair.getKey())))
                    clone.remove(pair.getKey());
            }


            if (parametri_ricerca.getParentClassifica() == null) {
                clone.remove("PARENT_CLASSIFICA");
            }
            */
            KeyValuePair[] metasearch = convertMetadati(clone, true);

            SearchAnagrafiche search = new SearchAnagrafiche();
            SearchAnagraficheResponse resp = null;

            search.setSearchCriteria(metasearch);
            search.setToken(token);
            search.setType("AOO");

            resp = serv.searchAnagrafiche(search);

            SearchItem[] items = resp.get_return();

            List<AOO> listaAOO = new ArrayList<AOO>();

            if (items == null) {
                return listaAOO;
            }

            AOO tit = null;

            for (SearchItem item : items) {
                tit = new AOO();
                tit.properties = convertMetadati(item.getMetadata());
                listaAOO.add(tit);
            }

            return listaAOO;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static List<Titolario> ricercaTitolari(String token, TitolarioCriteria parametri_ricerca) throws DocerApiException {
        Titolario tit = new Titolario();
        tit.properties = parametri_ricerca.properties;
        return ricercaTitolari(token, tit);
    }

    @Deprecated
    public static List<Titolario> ricercaTitolari(String token, Titolario parametri_ricerca) throws DocerApiException {

        long start = System.nanoTime();

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            HashMap<String, String> clone = new HashMap<String, String>(parametri_ricerca.properties);

            for (Entry<String, String> pair : parametri_ricerca.properties.entrySet()) {
                if (pair.getKey().equalsIgnoreCase("PARENT_CLASSIFICA"))
                    continue;

                if (StringUtils.isEmpty(clone.get(pair.getKey())))
                    clone.remove(pair.getKey());
            }

            if (parametri_ricerca.getParentClassifica() == null) {
                clone.remove("PARENT_CLASSIFICA");
            }

            KeyValuePair[] metasearch = convertMetadati(clone, true);

            SearchAnagrafiche search = new SearchAnagrafiche();
            SearchAnagraficheResponse resp = null;

            search.setSearchCriteria(metasearch);
            search.setToken(token);
            search.setType("TITOLARIO");

            resp = serv.searchAnagrafiche(search);

            SearchItem[] items = resp.get_return();

            List<Titolario> listaTitolari = new ArrayList<Titolario>();

            if (items == null) {
                return listaTitolari;
            }

            Titolario tit = null;

            for (SearchItem item : items) {
                tit = new Titolario();
                tit.properties = convertMetadati(item.getMetadata());
                listaTitolari.add(tit);
            }

            log.debug("ricercaTitolari: {} ms",
                    TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

            return listaTitolari;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static boolean verificaFascicolo(String token, Fascicolo fascicolo) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetFascicolo param = new GetFascicolo();
            GetFascicoloResponse resp = new GetFascicoloResponse();

            KeyValuePair[] metadati = convertMetadati(fascicolo.properties, true);

            param.setFascicoloId(metadati);
            param.setToken(token);

            resp = serv.getFascicolo(param);

            KeyValuePair[] datiFascicolo = resp.get_return();
        } catch (Exception e) {
            // fascicolo non trovato
            if (e.getMessage().contains("[475]"))
                return false;

            throw new DocerApiException(e);
        }

        return true;

    }

    public static Fascicolo recuperaFascicolo(String token, FascicoloCriteria fascicolo) throws DocerApiException {
        Fascicolo fa = new Fascicolo();
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            KeyValuePair[] metadati = convertMetadati(fascicolo.properties);
            GetFascicolo fasc = new GetFascicolo();
            GetFascicoloResponse resp = new GetFascicoloResponse();
            fasc.setFascicoloId(metadati);
            fasc.setToken(token);
            resp = serv.getFascicolo(fasc);
            KeyValuePair[] metaFascicolo = resp.get_return();
            HashMap<String, String> properties = convertMetadati(metaFascicolo);

            fa.properties = new HashMap<String, String>(properties);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
        return fa;
    }

    public static boolean rimuoviFascicolo(String token, Fascicolo fascicolo) throws DocerApiException {
        Fascicolo req = new Fascicolo();
        req.properties.clear();
        req.setEnte(fascicolo.getEnte());
        req.setAoo(fascicolo.getAoo());
        req.setClassifica(fascicolo.getClassifica());
        req.setProgressivoPadre(fascicolo.getProgressivoPadre());
        req.setProgressivo(fascicolo.getProgressivo());
        req.setAnno(fascicolo.getAnno());
//        req.setDescrizione(String.format(DELETED_FORMAT, fascicolo.getProgressivo().replace('/', '$'),
//                fascicolo.getDescrizione()));
        req.setEnabled(false);

        aggiornaFascicolo(token, req, null);

        return true;
    }

    public static Fascicolo aggiornaFascicolo(String token, Fascicolo fascicolo, List<Acl> diritti) throws DocerApiException {
        return aggiornaFascicolo(token, fascicolo, diritti, true);
    }
    public static Fascicolo aggiornaFascicolo(String token, Fascicolo fascicolo, List<Acl> diritti, Boolean onlyUpdate) throws DocerApiException {


        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            fascicolo.cleanFields(false);
            KeyValuePair[] metadati = convertMetadati(fascicolo.properties);

            if (diritti != null) {
                KeyValuePair[] acls = convertACL(diritti);

                SetACLFascicolo acl = new SetACLFascicolo();
                acl.setAcls(acls);
                acl.setFascicoloId(metadati);
                acl.setToken(token);
                serv.setACLFascicolo(acl);
            }

            HashMap<String, String> metaid = new HashMap<String, String>();
            metaid.put("COD_ENTE", fascicolo.getEnte());
            metaid.put("COD_AOO", fascicolo.getAoo());
            metaid.put("CLASSIFICA", fascicolo.getClassifica());
            metaid.put("PROGR_FASCICOLO", fascicolo.getProgressivo());
            metaid.put("ANNO_FASCICOLO", fascicolo.getAnno());

            KeyValuePair[] metadati_id = convertMetadati(metaid);

            UpdateFascicolo update = new UpdateFascicolo();

            GetFascicolo fasc = new GetFascicolo();
            GetFascicoloResponse resp = new GetFascicoloResponse();

            update.setFascicoloInfo(metadati);
            update.setFascicoloId(metadati_id);
            update.setToken(token);

            fasc.setFascicoloId(metadati);
            fasc.setToken(token);

            serv.updateFascicolo(update);

            resp = serv.getFascicolo(fasc);

            KeyValuePair[] metaFascicolo = resp.get_return();

            HashMap<String, String> properties = convertMetadati(metaFascicolo);
            Fascicolo fa = new Fascicolo();
            fa.properties = new HashMap<String, String>(properties);

            return fa;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static List<Fascicolo> ricercaFascicoli(String token, FascicoloCriteria parametri_ricerca) throws DocerApiException {
        Fascicolo f = new Fascicolo();
        f.properties = parametri_ricerca.properties;

        return ricercaFascicoli(token, f);
    }


    @Deprecated
    public static List<Fascicolo> ricercaFascicoli(String token, String ente, String aoo, String descFasc, String parentProgFascicolo, String... classifiche) throws DocerApiException {
        long start = System.nanoTime();
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);


            SearchAnagrafiche search = new SearchAnagrafiche();

            KeyValuePair kvp = new KeyValuePair();
            kvp.setKey("COD_ENTE");
            kvp.setValue(ente);
            search.addSearchCriteria(kvp);

            kvp = new KeyValuePair();
            kvp.setKey("COD_AOO");
            kvp.setValue(aoo);
            search.addSearchCriteria(kvp);
            
            kvp = new KeyValuePair();
            kvp.setKey("DES_FASCICOLO");
            kvp.setValue(descFasc);
            search.addSearchCriteria(kvp);
          
            kvp = new KeyValuePair();
            kvp.setKey("PARENT_PROGR_FASCICOLO");
            kvp.setValue(parentProgFascicolo);
            search.addSearchCriteria(kvp);

            if(classifiche!=null){
	            for (String classifica : classifiche) {
	                kvp = new KeyValuePair();
	                kvp.setKey("CLASSIFICA");
	                kvp.setValue(classifica);
	                search.addSearchCriteria(kvp);
	            }
            }

            SearchAnagraficheResponse resp = new SearchAnagraficheResponse();

            search.setToken(token);
            search.setType("FASCICOLO");

            resp = serv.searchAnagrafiche(search);

            SearchItem[] items = resp.get_return();

            List<Fascicolo> lista = new ArrayList<Fascicolo>();

            if (items == null)
                return lista;

            Fascicolo tit = null;

            for (SearchItem item : items) {
                tit = new Fascicolo();
                tit.properties = convertMetadati(item.getMetadata());
                lista.add(tit);
            }

            log.debug("ricercaFascicoli: {} ms",
                    TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

            return lista;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }


    @Deprecated
    public static List<Fascicolo> ricercaFascicoli(String token, Fascicolo parametri_ricerca) throws DocerApiException {
        long start = System.nanoTime();
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            HashMap<String, String> clone = new HashMap<String, String>(parametri_ricerca.properties);

            for (Entry<String, String> pair : parametri_ricerca.properties.entrySet()) {
                if (pair.getKey().equalsIgnoreCase("PARENT_PROGR_FASCICOLO"))
                    continue;

                if (StringUtils.isEmpty(clone.get(pair.getKey())))
                    clone.remove(pair.getKey());
            }

            if (parametri_ricerca.getProgressivoPadre() == null) {
                clone.remove("PARENT_PROGR_FASCICOLO");
            }

            KeyValuePair[] metasearch = convertMetadati(clone, true);

            SearchAnagrafiche search = new SearchAnagrafiche();
            SearchAnagraficheResponse resp = new SearchAnagraficheResponse();
            search.setSearchCriteria(metasearch);
            search.setToken(token);
            search.setType("FASCICOLO");

            resp = serv.searchAnagrafiche(search);

            SearchItem[] items = resp.get_return();

            List<Fascicolo> lista = new ArrayList<Fascicolo>();

            if (items == null)
                return lista;

            Fascicolo tit = null;

            for (SearchItem item : items) {
                tit = new Fascicolo();
                tit.properties = convertMetadati(item.getMetadata());
                lista.add(tit);
            }

            log.debug("ricercaFascicoli: {} ms",
                    TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

            return lista;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static Documento aggiornaDocumento(String token, Documento documento) throws DocerApiException {
        return aggiornaDocumento(token, documento, null, false);
    }
    public static Documento aggiornaDocumento(String token, Documento documento, List<Acl> diritti) throws DocerApiException {
        return aggiornaDocumento(token, documento, diritti, false);
    }
    public static Documento aggiornaDocumento(String token, Documento documento, List<Acl> diritti, Boolean onlyUpdate) throws DocerApiException {
        try {

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            UpdateProfileDocument upd = new UpdateProfileDocument();
            SetACLDocument acl = new SetACLDocument();
            GetProfileDocument params = new GetProfileDocument();
            GetProfileDocumentResponse resp = new GetProfileDocumentResponse();
            if(documento.properties.containsKey("NUM_PG")){
            	documento.properties.remove("NUM_PG");
            }
            if(documento.properties.containsKey("ANNO_PG")){
            	documento.properties.remove("ANNO_PG");
            }
            documento.cleanFields(false);
            KeyValuePair[] metadati = convertMetadati(documento.properties);

            KeyValuePair[] acls = null;

            if (diritti != null) {
                acls = convertACL(diritti);
            }

            upd.setDocId(documento.getDocNum());
            upd.setMetadata(metadati);
            upd.setToken(token);

            acl.setAcls(acls);
            acl.setDocId(documento.getDocNum());
            acl.setToken(token);

            params.setDocId(documento.getDocNum());
            params.setToken(token);

            serv.updateProfileDocument(upd);

            if(onlyUpdate) {
                return null;
            }

            if (diritti != null) {
                serv.setACLDocument(acl);
            }

            resp = serv.getProfileDocument(params);
            KeyValuePair[] metaDoc = resp.get_return();

            HashMap<String, String> properties = convertMetadati(metaDoc);
            Documento doc = new Documento();
            doc.properties = new HashMap<String, String>(properties);

            return doc;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }





    public static boolean rimuoviDocumentoDefinitivamente(String token, Documento documento) throws DocerApiException {

        try {
        String sede = ToolkitConnector.extractSedeFromToken(token);
        DocerServicesStub client = ClientManager.INSTANCE.getDocerServicesClient(sede);
        DeleteDocument deleteDocuemnt = new DeleteDocument();
            deleteDocuemnt.setDocId(documento.getDocNum());
            deleteDocuemnt.setToken(token);
            DeleteDocumentResponse ddr = client.deleteDocument(deleteDocuemnt);
            return ddr.get_return();
        } catch (Exception ex) {
            throw new DocerApiException(ex);
        }
    }




    public static boolean rimuoviDocumento(String token, Documento documento) throws DocerApiException {
        return rimuoviDocumento(token, documento.getDocNum());
    }

    public static boolean rimuoviDocumento(String token, String id_documento) throws DocerApiException {
        try {
            //controllo se il documento ha allegati
            //se si cancellazione non ammessa
            UnitaDocumentaria uni = recuperaUnitaDocumentaria(token, id_documento);
            if(uni!=null && uni.getDocumentoPrincipale()!=null){
                Documento principale = uni.getDocumentoPrincipale();
                String stato_archivistico = principale.getStatoArchivistico();
//                boolean isAnnesso = false;
//                for(Documento dc: uni.getAnnessi()){
//                    if(dc.getDocNum().equals(id_documento)){
//                        isAnnesso = true;
//                        break;
//                    }
//                }
                //VALUTARE SE AGGIUNGERE UNA CONDIZIONE CHE PERMETTA L'ELIMINAZIONE DEGLI ANNESSI
                //PER IL MOMENTO è STATA INIBITA PERCHè TRA GLI ANNESSI VI SONO LE RICEVUTE PEC

                if(!Strings.isNullOrEmpty(stato_archivistico) && StringUtils.isNumeric(stato_archivistico) && Integer.parseInt(stato_archivistico)>1){
                    throw new DocerApiException("Docoumento non eliminabile stato archivistico: record",450+Integer.parseInt(stato_archivistico));
                }
            }
            Documento documentoPrincipale = uni.getDocumentoPrincipale();
            if (id_documento.equals(documentoPrincipale.getDocNum()))
                if (uni.getAllegati().size() > 0 || uni.getAnnessi().size() > 0 || uni.getAnnotazioni().size() > 0)
                    throw new DocerApiException("Cancellazione documento non ammessa! Controllare la presenza di eventuali allegati, annessi o annotazione.", 547);

            documentoPrincipale = recuperaProfiloDocumento(token, id_documento);

            //Cartella trash = getCartellaCestino(token, documentoPrincipale.getEnte(), documentoPrincipale.getAoo());

            //aggiungiDocumentiACartella(token, trash.getID(), id_documento);

            Documento editDoc = new Documento();
            editDoc.properties.clear();
            editDoc.setDocNum(id_documento);
            //editDoc.setDocName(String.format(DELETED_FORMAT, id_documento, documentoPrincipale.getDocName()));
            editDoc.setProperty("ENABLED", "false");

            aggiornaDocumento(token, editDoc, null, true);

            return true;

        } catch (Exception ex) {
            throw new DocerApiException(ex);
        }
    }

    private static Cartella getCartellaCestino(String token, String codEnte, String codAoo) throws DocerApiException, SolrServerException {
        String cestinoName = "#CESTINO#";

        String sede = ToolkitConnector.extractSedeFromToken(token);

        CartellaCriteria cc = new CartellaCriteria();
        cc.setEnte(codEnte);
        cc.setAoo(codAoo);
        cc.setProperty("location", sede);
        cc.setProperty("depth", "3");
        cc.setProperty("name", cestinoName);

        SOLRClient solrClient = new SOLRClient();
        List<Cartella> results = solrClient.search(token, cc, Cartella.class);
        if (results.size() > 1) {
            throw new DocerApiException("Risultati di ricerca inconsistenti durante la lookup del cestino", 500);
        }

        Cartella cestino;
        if (results.size() > 0) {
            cestino = results.get(0);
        } else {
            cc.setProperty("name", "Temporanei");
            cc.setProperty("depth", "2");

            results = solrClient.search(token, cc, Cartella.class);
            if (results.size() == 0) {
                throw new DocerApiException("Cartella dei Temporanei non trovata", 500);
            }

            if (results.size() > 1) {
                throw new DocerApiException("Risultati di ricerca inconsistenti durante la lookup dei temporanei", 500);
            }

            Cartella temps = results.get(0);

            cestino = new Cartella();
            cestino.setCartellaSuperiore(temps.getID());
            cestino.setNome(cestinoName);
            cestino.setEnte(codEnte);
            cestino.setAoo(codAoo);

            //TODO: Va in errore di permessi
            cestino = creaCartella(token, cestino);
        }

        return cestino;
    }

    public static boolean sovrascriviUltimaVersione(String token, Documento documento, File file) throws DocerApiException {
        return sovrascriviUltimaVersione(token, documento.getDocNum(), file);
    }

    public static boolean sovrascriviUltimaVersione(String token, String id_documento, File file) throws DocerApiException {
        try {
            return sovrascriviUltimaVersione(token, id_documento, FileUtils.openInputStream(file));
        } catch (IOException ex) {
            throw new DocerApiException(ex, 540);
        }
    }

    public static boolean sovrascriviUltimaVersione(String token, Documento documento, InputStream file) throws DocerApiException {
        return sovrascriviUltimaVersione(token, documento.getDocNum(), file);
    }

    public static boolean sovrascriviUltimaVersione(String token, String id_documento, InputStream file) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub client = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.ReplaceLastVersion req = new DocerServicesStub.ReplaceLastVersion();
            req.setToken(token);
            req.setDocId(id_documento);

            DataSource source = new InputStreamDataSource(file);
            DataHandler handler = new DataHandler(source);
            req.setFile(handler);

            DocerServicesStub.ReplaceLastVersionResponse resp = client.replaceLastVersion(req);
            return resp.get_return();

        } catch (Exception ex) {
            throw new DocerApiException(ex);
        }

    }

    public static String aggiungiNuovaVersione(String token, Documento documento, File file) throws DocerApiException {
        return aggiungiNuovaVersione(token, documento.getDocNum(), file);
    }

    public static String aggiungiNuovaVersione(String token, String id_documento, File file) throws DocerApiException {
        try {
            return aggiungiNuovaVersione(token, id_documento, FileUtils.openInputStream(file));
        } catch (IOException ex) {
            throw new DocerApiException(ex, 540);
        }
    }

    public static String aggiungiNuovaVersione(String token, Documento documento, InputStream file) throws DocerApiException {
        return aggiungiNuovaVersione(token, documento.getDocNum(), file);
    }

    public static String aggiungiNuovaVersione(String token, String id_documento, InputStream file) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub client = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.AddNewVersion req = new DocerServicesStub.AddNewVersion();
            req.setToken(token);
            req.setDocId(id_documento);

            DataSource source = new InputStreamDataSource(file);
            DataHandler handler = new DataHandler(source);
            req.setFile(handler);

            DocerServicesStub.AddNewVersionResponse resp = client.addNewVersion(req);
            return resp.get_return();

        } catch (Exception ex) {
            throw new DocerApiException(ex);
        }

    }

    public static boolean aggiungiNuovaVersioneAvanzata(String token, Documento documento1, Documento documento2) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub client = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.AddNewAdvancedVersion req = new AddNewAdvancedVersion();
            req.setToken(token);
            req.setDocIdLastVersion(documento1.getDocNum());
            req.setDocIdNewVersion(documento2.getDocNum());

            AddNewAdvancedVersionResponse resp = client.addNewAdvancedVersion(req);

            return resp.get_return();

        } catch (Exception ex) {
            throw new DocerApiException(ex);
        }

    }

    public static List<Documento> recuperaDocumentiFascicolo(String token, Fascicolo fascicolo) throws DocerApiException {
        FascicoloCriteria fc = new FascicoloCriteria();
        fc.properties = fascicolo.properties;
        return recuperaDocumentiFascicolo(token, fc);
    }

    public static List<Documento> recuperaDocumentiFascicolo(String token, FascicoloCriteria fascicolo) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            Fascicolo clone = new Fascicolo();
            clone.copyFrom(fascicolo);

            for (String key : fascicolo.properties.keySet()) {
                if (key.equals("PARENT_PROGR_FASCICOLO") && clone.properties.get("PARENT_PROGR_FASCICOLO") != null) {
                    continue;
                }

                if (key.equals("ENABLED") || StringUtils.isEmpty(clone.properties.get(key))) {
                    clone.properties.remove(key);
                }
            }

            clone.properties.remove("PARENT_PROGR_FASCICOLO");

            clone.properties.remove("DES_FASCICOLO");

            SearchDocuments req = new SearchDocuments();
            req.setToken(token);
            req.setMaxRows(100);
            req.setSearchCriteria(convertMetadati(clone.properties, true));
            req.addKeywords("*");

            SearchDocumentsResponse resp = serv.searchDocuments(req);
            SearchItem[] results = resp.get_return();

            ArrayList<Documento> ret = new ArrayList<Documento>();
            if (results != null) {
                for (SearchItem item : results) {
                    Documento doc = new Documento();
                    doc.properties = convertMetadati(item.getMetadata());
                    ret.add(doc);
                }
            }

            return ret;

        } catch (Exception ex) {
            throw new DocerApiException(ex);
        }

    }

    /*public static List<Documento> ricercaUnitaDocumentarie(String token, DocumentoCriteria parametri_ricerca, Ordinamento... ordinamenti) throws DocerApiException {
        return ricercaUnitaDocumentarie(token, parametri_ricerca, Arrays.asList(ordinamenti));
    }*/

    public static List<Documento> ricercaUnitaDocumentarie(String token, DocumentoCriteria parametri_ricerca) throws DocerApiException {
        Documento d = new Documento();
        d.properties = parametri_ricerca.properties;

        List<Ordinamento> ordinamenti = new ArrayList<>();
        if (parametri_ricerca.getOrdinamento() != null) {
            ordinamenti.add(parametri_ricerca.getOrdinamento());
        }

        return ricercaUnitaDocumentarie(token, d, parametri_ricerca.getKeywords(), parametri_ricerca.getMaxElementi(), ordinamenti);
    }

    public static List<Documento> ricercaUnitaDocumentarie(String token, DocumentoCriteria parametri_ricerca, List<Ordinamento> ordinamento) throws DocerApiException {
        Documento d = new Documento();
        d.properties = parametri_ricerca.properties;
        return ricercaUnitaDocumentarie(token, d, parametri_ricerca.getKeywords(), parametri_ricerca.getMaxElementi(), ordinamento);
    }

    @Deprecated
    public static List<Documento> ricercaUnitaDocumentarie(String token, Documento parametri_ricerca, String keywords, int maxElementi, List<Ordinamento> ordinamento) throws DocerApiException {

        long start = System.nanoTime();

        parametri_ricerca.properties.remove(GenericCriteria.ORDER_BY);

        if (keywords == null)
            keywords = "";

        String[] keywordsArray = keywords.split(" ");

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            KeyValuePair[] metasearch = null;

            if (parametri_ricerca != null) {
                metasearch = convertMetadati(parametri_ricerca.properties, true);
            }

            SearchDocuments search = new SearchDocuments();
            SearchDocumentsResponse resp = null;

            search.setKeywords(keywordsArray);
            search.setMaxRows(maxElementi);
            search.setSearchCriteria(metasearch);
            search.setToken(token);

            KeyValuePair[] orderBy = convertOrderBy(ordinamento);
            search.setOrderby(orderBy);

            resp = serv.searchDocuments(search);
            SearchItem[] items = resp.get_return();

            List<Documento> lista = new ArrayList<Documento>();

            if (items == null)
                return lista;

            Documento doc = null;

            for (SearchItem item : items) {
                doc = new Documento();
                doc.properties = convertMetadati(item.getMetadata());
                //((ICIFSObject)doc).setParentPath(path);
                lista.add(doc);
            }

            log.debug("ricercaUnitaDocumentarie: {} ms",
                    TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

            return lista;

        } catch (Exception e) {

            throw new DocerApiException(e);
        }
    }

    public static Documento creaDocumento(String token, Documento doc, List<Acl> diritti) throws DocerApiException {
        return creaDocumento(token, doc, null, diritti);
    }

    public static Documento creaDocumento(String token, Documento doc, String createAsUser, List<Acl> diritti) throws DocerApiException {

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);


            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            CreateDocument createDocument = new CreateDocument();
            CreateDocumentResponse resp = null;

            
            if(doc.properties.containsKey("NUM_PG")){
            	doc.properties.remove("NUM_PG");
            }
            if(doc.properties.containsKey("ANNO_PG")){
            	doc.properties.remove("ANNO_PG");
            }


            KeyValuePair[] metadati = convertMetadati(doc.properties);

            if( !"true".equals( doc.getProperty("NO_FILE"))
                || !"PAPER".equals( doc.getProperty("ARCHIVE_TYPE")) ) {
                InputStream stream = doc.getFileStream();

                DataHandler ds = new DataHandler(new InputStreamDataSource(stream));

                createDocument.setFile(ds);
            }
            createDocument.setToken(token);
            createDocument.setMetadata(metadati);

            if (createAsUser == null) {
                createAsUser = Utils.extractTokenKey(token, "uid");
            }

            //TODO nella versione del trunk va trovata una soluzione più elegante
            //GetUserResponse userResponse = getUserInfo(token, createAsUser);
            //KeyValuePair[] kvps = userResponse.get_return();
            String fullNameUsername = ActorsCache.getDisplayName(createAsUser);
            /*for (KeyValuePair kvp : kvps) {
                if (kvp.getKey().equals("FULL_NAME")) {
                    fullNameUsername = kvp.getValue() + " (" + createAsUser + ")";
                    break;
                }

            }*/

            KeyValuePair pair = new KeyValuePair();
            pair.setKey("AUTHOR_ID");
            pair.setValue(fullNameUsername);
            createDocument.addMetadata(pair);

            resp = serv.createDocument(createDocument);

            doc = recuperaProfiloDocumento(token, resp.get_return());

            if (diritti != null && diritti.size()>0) {
                KeyValuePair[] acls = convertACL(diritti);

                SetACLDocument acl = new SetACLDocument();

                acl.setAcls(acls);
                acl.setDocId(doc.getDocNum());
                acl.setToken(token);

                serv.setACLDocument(acl);
            }

            return doc;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }

    }

    public static Documento creaDocumentoCIFS(String token, Documento doc) throws DocerApiException {

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            CreateDocument createDocument = new CreateDocument();
            CreateDocumentResponse resp = null;


            if(doc.properties.containsKey("NUM_PG")){
                doc.properties.remove("NUM_PG");
            }
            if(doc.properties.containsKey("ANNO_PG")){
                doc.properties.remove("ANNO_PG");
            }

            KeyValuePair[] metadati = convertMetadati(doc.properties);

            InputStream stream = doc.getFileStream();

            DataHandler ds = new DataHandler(new InputStreamDataSource(stream));

            createDocument.setToken(token);
            createDocument.setMetadata(metadati);
            createDocument.setFile(ds);


            //TODO nella versione del trunk va trovata una soluzione più elegante
            String username = Utils.extractTokenKey(token, "uid");
            String fullName = Utils.extractTokenKey(token, "fullName");

            KeyValuePair pair = new KeyValuePair();
            pair.setKey("AUTHOR_ID");
            pair.setValue(String.format("%s (%s)", fullName, username));
            createDocument.addMetadata(pair);

            resp = serv.createDocument(createDocument);

            SOLRClient client = new SOLRClient();
            doc = client.openByDocnum(token, resp.get_return());

            return doc;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            throw new DocerApiException(e);
        }

    }

    public static void correlaDocumenti(String token, String docNum, List<String> related) throws DocerApiException {
        String[] change = new String[related.size()];
        change = related.toArray(change);

        correlaDocumenti(token, docNum, change);

    }

    public static LockStatus recuperaLock(String token, Documento doc) throws DocerApiException {
        return recuperaLock(token, doc.getDocNum());
    }

    public static LockStatus recuperaLock(String token, String docNum) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetLockStatus req = new GetLockStatus();
            req.setToken(token);
            req.setDocId(docNum);

            GetLockStatusResponse resp = serv.getLockStatus(req);

            DocerServicesStub.LockStatus lockStatus = resp.get_return();

            return new LockStatus(lockStatus.getLocked(), lockStatus.getUserId());

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static boolean lock(String token, Documento doc) throws DocerApiException {
        return lock(token, doc.getDocNum());
    }

    public static boolean lock(String token, String docNum) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            LockDocument req = new LockDocument();
            req.setToken(token);
            req.setDocId(docNum);

            LockDocumentResponse resp = serv.lockDocument(req);
            return resp.get_return();
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static boolean unlock(String token, Documento doc) throws DocerApiException {
        return unlock(token, doc.getDocNum());
    }

    public static boolean unlock(String token, String docNum) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            UnlockDocument req = new UnlockDocument();
            req.setToken(token);
            req.setDocId(docNum);

            UnlockDocumentResponse resp = serv.unlockDocument(req);
            return resp.get_return();
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static Documento recuperaProfiloDocumento(String token, String docNum) throws DocerApiException {
        try {
            if (docNum.startsWith("\\")) {
                DocumentoCriteria dc = new DocumentoCriteria();
                dc.setProperty("PATH", docNum);
                List<Documento> ret = ricercaUnitaDocumentarie(token, dc);
                if (ret.isEmpty()) {
                    throw new DocerApiException("Not Found", 404);
                } else {
                    return ret.get(0);
                }
            } else {
                String sede = ToolkitConnector.extractSedeFromToken(token);
                DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
                GetProfileDocument parms = new GetProfileDocument();
                GetProfileDocumentResponse resp;
                parms.setDocId(docNum);
                parms.setToken(token);

                resp = serv.getProfileDocument(parms);

                KeyValuePair[] metadati = resp.get_return();

                Documento doc = new Documento();
                doc.properties = new HashMap<String, String>(convertMetadati(metadati));

                return doc;
            }
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }


    public static String recuperaXmlProfiloDocumento(String token, String docNum) throws DocerApiException {
            try{
                String sede = ToolkitConnector.extractSedeFromToken(token);
                DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
                GetProfileDocument parms = new GetProfileDocument();
                GetProfileDocumentResponse resp;
                parms.setDocId(docNum);
                parms.setToken(token);

                resp = serv.getProfileDocument(parms);
                OMElement element = resp.getOMElement(GetProfileDocumentResponse.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
               // KeyValuePair[] metadati = resp.get_return();

               return element.toString();
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static Map<String, String> recuperaTipiDocumento(String token) throws DocerApiException {

        return recuperaTipiDocumentoPerComponente(token,CallDocumentMgt.getEnteCorrente(),CallDocumentMgt.getAOOCorrente(),"*");

        /*try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            GetDocumentTypes parms = new GetDocumentTypes();
            GetDocumentTypesResponse resp = new GetDocumentTypesResponse();
            parms.setToken(token);

            resp = serv.getDocumentTypes(parms);

            KeyValuePair[] metadati = resp.get_return();

            return convertMetadati(metadati);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }*/
    }

    public static String recuperaParametroDiConfigurazione(String token, String nomeSezione, String nomeNodo, String nomeAttributo ){

        String path = String.format("//section[@name='%s']/%s/@%s", nomeSezione,nomeNodo,nomeAttributo);
        return recuperaParametroDiConfigurazione(token, path );
    }
    public static String recuperaParametroDiConfigurazione(String token, String nomeSezione, String nomeNodo ){

        String path = String.format("//section[@name='%s']/%s", nomeSezione, nomeNodo);
        return recuperaParametroDiConfigurazione(token, path );
    }
    public static String recuperaParametroDiConfigurazione(String token, String path ){

        String val = null;

        try
        {
            Document doc = DocerService.readConfigDOM(token);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile(path);
            NodeList nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            if(nList.getLength()>0) {
                return nList.item(0).getNodeValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;

    }

    public static List<String> recuperaParametriDiConfigurazione(String token, String nomeSezione, String nomeNodo ){

        String path = String.format("//section[@name='%s']/%s", nomeSezione, nomeNodo);

        List<String> list = new ArrayList<>();

        try
        {
            Document doc = DocerService.readConfigDOM(token);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile(path);
            NodeList nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            Node n=null;

            for (int i = 0; i < nList.getLength(); i++) {
                System.out.println(nList.getLength());
                n = nList.item(i);
                list.add(n.getNodeName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    public static Map<String, String> recuperaTipiDocumentoPerComponente(String token, String ente, String codiceAOO, String tipoComponente) throws DocerApiException {

        Map<String, String> map = new HashMap<String,String>();

        //Cache
        String cacheKey = "docer_documenttypes_" + tipoComponente;

        try {
            Optional obj = CacheManager.confCache().get(cacheKey);

            if(obj.isPresent() && obj.get() instanceof Map) {
                map = (Map<String, String>) obj.get();
                return map;
            }

        } catch (CacheException e) {
            throw new RuntimeException(e);
        }


        try
        {
            Document doc = DocerService.readConfigDOM(token);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            XPathExpression expr = xpath.compile("//section[@name='documenti']/ente[@id='"+ente+"']/aoo[@id='"+codiceAOO+"']/documento");
            NodeList nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            Node n=null;
            Element eElement=null;

            for (int i = 0; i < nList.getLength(); i++) {
                System.out.println(nList.getLength());
                n= nList.item(i);
                System.out.println("\nCurrent Element :" + n.getNodeName());


                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    eElement = (Element) n.getChildNodes();
                    System.out.println("\nCurrent Element :" + n.getNodeName());
//					name = eElement.getElementsByTagName("name").item(i).getTextContent();
                    String type_id = eElement.getAttribute("name");
                    String type_desc = eElement.getAttribute("description");
                    String cTypes = eElement.getAttribute("componentTypes");

                    if( "".equals(cTypes) || cTypes.contains(tipoComponente) || "*".equals(tipoComponente))
                        map.put(type_id,type_desc);
                }
                n.getNextSibling();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            CacheManager.confCache().put(cacheKey, map);
        } catch (CacheException e) {
            throw new RuntimeException(e);
        }
        return map;

    }
    public static Map<String, String> recuperaTipiDocumento(String token, String ente, String codiceAOO) throws DocerApiException {
        return recuperaTipiDocumentoPerComponente(token,ente,codiceAOO,"*");
        /*try {
            KeyValuePair[] metadati = null;
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            if (codiceAOO == null || codiceAOO.equals("") || codiceAOO.equals(ente) || codiceAOO.equals("*")) {
                GetDocumentTypesResponse resp = new GetDocumentTypesResponse();
                GetDocumentTypes getDocumentTypes = new GetDocumentTypes();
                getDocumentTypes.setToken(token);
                resp = serv.getDocumentTypes(getDocumentTypes);
                metadati = resp.get_return();
            } else {
                GetDocumentTypesByAOOResponse respAoo = new GetDocumentTypesByAOOResponse();
                GetDocumentTypesByAOO parms = new GetDocumentTypesByAOO();
                parms.setToken(token);
                parms.setCodiceAOO(codiceAOO);
                parms.setCodiceEnte(ente);
                respAoo = serv.getDocumentTypesByAOO(parms);
                metadati = respAoo.get_return();
            }
            if (metadati != null)
                return convertMetadati(metadati);

            return new HashMap<String, String>();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }*/
    }

    public static List<Documento> recuperaCorrelati(String token, String docNum) throws DocerApiException {
        try {

            List<Documento> returnList = new ArrayList<Documento>();
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            GetRelatedDocuments params2 = new GetRelatedDocuments();
            GetRelatedDocumentsResponse resp2 = new GetRelatedDocumentsResponse();
            params2.setDocId(docNum);
            params2.setToken(token);

            resp2 = serv.getRelatedDocuments(params2);
            String[] docNums = resp2.get_return();

            List<String> allDocs = new ArrayList<String>();

            if (docNums != null) {
                allDocs.addAll(Arrays.asList(docNums));
            }

            for (String dnum : allDocs) {

                GetProfileDocument params3 = new GetProfileDocument();
                GetProfileDocumentResponse resp3 = new GetProfileDocumentResponse();
                params3.setDocId(dnum);
                params3.setToken(token);

                resp3 = serv.getProfileDocument(params3);

                KeyValuePair[] relatedMetadati = resp3.get_return();

                Documento relatedDoc = new Documento();
                relatedDoc.properties = new HashMap<String, String>(convertMetadati(relatedMetadati));
                returnList.add(relatedDoc);
            }

            return returnList;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }


    public static UnitaDocumentaria recuperaUnitaDocumentaria(String token, String docNum) throws DocerApiException {
        try {
            UnitaDocumentaria unitaDocumentaria = new UnitaDocumentaria();

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            // GetProfileDocument params = new GetProfileDocument();
            // GetProfileDocumentResponse resp = new
            // GetProfileDocumentResponse();
            // params.setDocId(docNum);
            // params.setToken(token);
            // resp = serv.getProfileDocument(params);
            //
            // KeyValuePair[] metadati = resp.get_return();
            //
            // Documento doc = new Documento();
            // doc.properties = new
            // HashMap<String,String>(convertMetadati(metadati));
            // unitaDocumentaria.setDocumentoPrincipale(doc);

            GetRelatedDocuments params2 = new GetRelatedDocuments();
            GetRelatedDocumentsResponse resp2 = new GetRelatedDocumentsResponse();
            params2.setDocId(docNum);
            params2.setToken(token);

            resp2 = serv.getRelatedDocuments(params2);
            String[] docNums = resp2.get_return();

            List<String> allDocs = new ArrayList<String>();

            if (docNums != null) {
                allDocs.addAll(Arrays.asList(docNums));
            }

            allDocs.add(0, docNum);

            for (String dnum : allDocs) {

                GetProfileDocument params3 = new GetProfileDocument();
                GetProfileDocumentResponse resp3 = new GetProfileDocumentResponse();
                params3.setDocId(dnum);
                params3.setToken(token);

                resp3 = serv.getProfileDocument(params3);

                KeyValuePair[] relatedMetadati = resp3.get_return();

                Documento relatedDoc = new Documento();
                relatedDoc.properties = new HashMap<String, String>(convertMetadati(relatedMetadati));

                if (relatedDoc.getTipoComponente().equalsIgnoreCase("ALLEGATO")) {
                    unitaDocumentaria.getAllegati().add(relatedDoc);
                } else if (relatedDoc.getTipoComponente().equalsIgnoreCase("ANNESSO")) {
                    unitaDocumentaria.getAnnessi().add(relatedDoc);
                } else if (relatedDoc.getTipoComponente().equalsIgnoreCase("ANNOTAZIONE")) {
                    unitaDocumentaria.getAnnotazioni().add(relatedDoc);
                } else if (relatedDoc.getTipoComponente().equalsIgnoreCase("PRINCIPALE")) {
                    unitaDocumentaria.setDocumentoPrincipale(relatedDoc);
                }
//                else {
//                    unitaDocumentaria.getAllegati().add(relatedDoc);
//                }

            }

            return unitaDocumentaria;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static List<Acl> recuperaACLDocumento(String token, String docNum) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetACLDocument params = new GetACLDocument();
            GetACLDocumentResponse resp = new GetACLDocumentResponse();

            params.setDocId(docNum);
            params.setToken(token);

            resp = serv.getACLDocument(params);

            KeyValuePair[] metadati = resp.get_return();

            List<Acl> listaACL = convertACL(metadati);

            return listaACL;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static List<Acl> recuperaACLFolder(String token, String folderId) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetACLFolder params = new GetACLFolder();
            GetACLFolderResponse resp = null;

            params.setFolderId(folderId);
            params.setToken(token);

            resp = serv.getACLFolder(params);

            KeyValuePair[] metadati = resp.get_return();

            List<Acl> listaACL = convertACL(metadati);

            return listaACL;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }


    public static List<Acl> recuperaACLFascicolo(String token, String codEnte, String codAoo, String progressivo, String anno, String classif) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetACLFascicolo params = new GetACLFascicolo();
            GetACLFascicoloResponse resp = new GetACLFascicoloResponse();

            KeyValuePair cod_ente = new KeyValuePair();
            cod_ente.setKey("COD_ENTE");
            cod_ente.setValue(codEnte);

            KeyValuePair cod_aoo = new KeyValuePair();
            cod_aoo.setKey("COD_AOO");
            cod_aoo.setValue(codAoo);

            KeyValuePair progr_fascicolo = new KeyValuePair();
            progr_fascicolo.setKey("PROGR_FASCICOLO");
            progr_fascicolo.setValue(progressivo);

            KeyValuePair anno_fascicolo = new KeyValuePair();
            anno_fascicolo.setKey("ANNO_FASCICOLO");
            anno_fascicolo.setValue(anno);

            KeyValuePair classifica = new KeyValuePair();
            classifica.setKey("CLASSIFICA");
            classifica.setValue(classif);

            KeyValuePair[] param =
                    {
                            cod_ente, cod_aoo, progr_fascicolo, anno_fascicolo, classifica
                    };

            params.setFascicoloId(param);
            params.setToken(token);

            resp = serv.getACLFascicolo(params);

            KeyValuePair[] metadati = resp.get_return();

            List<Acl> listaACL = convertACL(metadati);

            return listaACL;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static List<Acl> recuperaUtentiFascicolo(String token, String codEnte, String codAoo, String progressivo, String anno, String classif) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetACLFascicolo params = new GetACLFascicolo();
            GetACLFascicoloResponse resp = new GetACLFascicoloResponse();

            KeyValuePair cod_ente = new KeyValuePair();
            cod_ente.setKey("COD_ENTE");
            cod_ente.setValue(codEnte);

            KeyValuePair cod_aoo = new KeyValuePair();
            cod_aoo.setKey("COD_AOO");
            cod_aoo.setValue(codAoo);

            KeyValuePair progr_fascicolo = new KeyValuePair();
            progr_fascicolo.setKey("PROGR_FASCICOLO");
            progr_fascicolo.setValue(progressivo);

            KeyValuePair anno_fascicolo = new KeyValuePair();
            anno_fascicolo.setKey("ANNO_FASCICOLO");
            anno_fascicolo.setValue(anno);

            KeyValuePair classifica = new KeyValuePair();
            classifica.setKey("CLASSIFICA");
            classifica.setValue(classif);

            KeyValuePair[] param =
                    {
                            cod_ente, cod_aoo, progr_fascicolo, anno_fascicolo, classifica
                    };

            params.setFascicoloId(param);
            params.setToken(token);

            resp = serv.getACLFascicolo(params);

            KeyValuePair[] metadati = resp.get_return();

            List<Acl> listaACL = convertACL(metadati);

            return listaACL;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static List<Acl> recuperaACLTitolario(String token, String codEnte, String codAoo, String classif) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            GetACLTitolario params = new GetACLTitolario();
            GetACLTitolarioResponse resp = new GetACLTitolarioResponse();

            KeyValuePair cod_ente = new KeyValuePair();
            cod_ente.setKey("COD_ENTE");
            cod_ente.setValue(codEnte);

            KeyValuePair cod_aoo = new KeyValuePair();
            cod_aoo.setKey("COD_AOO");
            cod_aoo.setValue(codAoo);

            KeyValuePair classifica = new KeyValuePair();
            classifica.setKey("CLASSIFICA");
            classifica.setValue(classif);

            KeyValuePair[] param =
                    {
                            cod_ente, cod_aoo, classifica
                    };

            params.setTitolarioId(param);
            params.setToken(token);

            resp = serv.getACLTitolario(params);

            KeyValuePair[] metadati = resp.get_return();

            List<Acl> listaACL = convertACL(metadati);

            return listaACL;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void correlaDocumenti(String token, String docNum, String[] related) throws DocerApiException {
        try {
            if (related == null) {
                throw new DocerApiException("Argomento 'related' non valido.", 560);
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            AddRelated parms = new AddRelated();
            parms.setDocId(docNum);
            parms.setRelated(related);
            parms.setToken(token);

            serv.addRelated(parms);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void scorrelaDocumenti(String token, String docNum, List<String> related) throws DocerApiException {
        String[] change = new String[related.size()];
        change = related.toArray(change);

        scorrelaDocumenti(token, docNum, change);

    }


    public static void scorrelaDocumenti(String token, String docNum, String[] related) throws DocerApiException {
        try {
            if (related == null) {
                throw new DocerApiException("Argomento 'related' non valido.", 560);
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            RemoveRelated parms = new RemoveRelated();
            parms.setDocId(docNum);
            parms.setRelated(related);
            parms.setToken(token);

            serv.removeRelated(parms);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void impostaDirittiDocumento(String token, String docNum, List<Acl> diritti) throws DocerApiException {
        try {

            if (diritti == null) {
                throw new DocerApiException("Argomento 'diritti' non valido.", 560);
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            KeyValuePair[] acls = convertACL(diritti);

            SetACLDocument acl = new SetACLDocument();
            acl.setAcls(acls);
            acl.setDocId(docNum);
            acl.setToken(token);

            serv.setACLDocument(acl);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void impostaDirittiFascicolo(String token, Fascicolo fascicolo, List<Acl> diritti) throws DocerApiException {
        try {
            if (diritti == null) {
                throw new DocerApiException("Argomento 'diritti' non valido.", 560);
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            KeyValuePair[] acls = convertACL(diritti);
            KeyValuePair[] metadati = convertMetadati(fascicolo.properties);

            SetACLFascicolo acl = new SetACLFascicolo();
            acl.setAcls(acls);
            acl.setFascicoloId(metadati);
            acl.setToken(token);

            serv.setACLFascicolo(acl);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void impostaDirittiCartella(String token, String folderId, List<Acl> diritti) throws DocerApiException {
        try {
            if (diritti == null) {
                throw new DocerApiException("Argomento 'diritti' non valido.", 560);
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            KeyValuePair[] acls = convertACL(diritti);

            SetACLFolder acl = new SetACLFolder();
            acl.setAcls(acls);
            acl.setFolderId(folderId);
            acl.setToken(token);

            serv.setACLFolder(acl);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void addNewAdvancedVersion(String token, String lastDocNum, String newDocnum) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            AddNewAdvancedVersion advVersion = new AddNewAdvancedVersion();
            advVersion.setDocIdLastVersion(lastDocNum);
            advVersion.setDocIdNewVersion(newDocnum);
            advVersion.setToken(token);

            serv.addNewAdvancedVersion(advVersion);
        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }
    public static void impostaDirittiTitolario(String token, Titolario titolario, List<Acl> diritti) throws DocerApiException {
        try {
            if (titolario.properties.containsKey("COD_TITOLARIO"))
                titolario.properties.remove("COD_TITOLARIO");
            if (titolario.properties.containsKey("DES_TITOLARIO"))
                titolario.properties.remove("DES_TITOLARIO");

            if (diritti == null) {
                throw new DocerApiException("Argomento 'diritti' non valido.", 560);
            }
            if (titolario.getParentClassifica() == null) {
                titolario.properties.remove("PARENT_CLASSIFICA");
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            KeyValuePair[] acls = convertACL(diritti);
            KeyValuePair[] metadati = convertMetadati(titolario.properties);

            SetACLTitolario acl = new SetACLTitolario();
            acl.setAcls(acls);
            acl.setTitolarioId(metadati);
            acl.setToken(token);

            serv.setACLTitolario(acl);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void aggiungiAllegatiAnnessiAnnotazioni(String token, String docNum, List<Documento> allegati, List<Documento> annessi, List<Documento> annotazioni, List<Acl> diritti) throws DocerApiException {
        try {
            if (allegati != null) {
                for (Documento doc : allegati) {
                    Documento docToUpdate = new Documento();
                    docToUpdate.properties.clear();
                    docToUpdate.properties.put("DOCNUM", doc.getDocNum());
                    docToUpdate.setTipoComponente("ALLEGATO");
                    DocerService.aggiornaDocumento(token, docToUpdate);
                }
            }
            if (annessi != null) {
                for (Documento doc : annessi) {
                    Documento docToUpdate = new Documento();
                    docToUpdate.properties.clear();
                    docToUpdate.properties.put("DOCNUM", doc.getDocNum());
                    docToUpdate.setTipoComponente("ANNESSO");
                    DocerService.aggiornaDocumento(token, docToUpdate);
                }
            }
            if (annotazioni != null) {
                for (Documento doc : annotazioni) {
                    Documento docToUpdate = new Documento();
                    docToUpdate.properties.clear();
                    docToUpdate.properties.put("DOCNUM", doc.getDocNum());
                    docToUpdate.setTipoComponente("ANNOTAZIONE");
                    DocerService.aggiornaDocumento(token, docToUpdate);
                }
            }

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
            List<String> related = new ArrayList<String>();
            List<Documento> allDocs = new ArrayList<Documento>();

            if (allegati != null)
                allDocs.addAll(allegati);

            if (annessi != null)
                allDocs.addAll(annessi);

            if (annotazioni != null)
                allDocs.addAll(annotazioni);

            for (Documento d : allDocs) {
                if (d.getDocNum() == null || "".equals(d.getDocNum())) {
                    d = creaDocumento(token, d, diritti);
                }
                related.add(d.getDocNum());
            }

            if (related.size() > 0) {

                String[] change = new String[related.size()];
                change = related.toArray(change);

                AddRelated parms = new AddRelated();
                parms.setDocId(docNum);
                parms.setRelated(change);
                parms.setToken(token);

                serv.addRelated(parms);
            }
        } catch (Exception e) {
            throw new DocerApiException(e);
        }

    }

    public static UnitaDocumentaria creaUnitaDocumentaria(String token, Documento documento_principale) throws DocerApiException {
        return creaUnitaDocumentaria(token, documento_principale, new ArrayList<Documento>(), new ArrayList<Documento>(), new ArrayList<Documento>());
    }

    public static UnitaDocumentaria creaUnitaDocumentaria(String token, Documento documento_principale, List<Documento> allegati, List<Documento> annessi, List<Documento> annotazioni) throws DocerApiException {
        return creaUnitaDocumentaria(token, documento_principale, allegati, annessi, annotazioni, new ArrayList<Acl>());
    }

    public static UnitaDocumentaria creaUnitaDocumentaria(String token, Documento documento_principale, List<Documento> allegati, List<Documento> annessi, List<Documento> annotazioni, List<Acl> diritti) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            UnitaDocumentaria unitaDocumentaria = new UnitaDocumentaria();

            List<String> related = new ArrayList<String>();

            documento_principale.setTipoComponente("PRINCIPALE");
            documento_principale = creaDocumento(token, documento_principale, diritti);

            unitaDocumentaria.setDocumentoPrincipale(documento_principale);

            if (allegati != null) {
                // creazione allegati
                for (Documento allegato : allegati) {
                    allegato.setTipoComponente("ALLEGATO");
                    Documento allegatoA = creaDocumento(token, allegato, diritti);
                    related.add(allegatoA.getDocNum());
                    unitaDocumentaria.getAllegati().add(allegatoA);
                }
            }

            if (annessi != null) {
                // creazione annessi
                for (Documento annesso : annessi) {
                    annesso.setTipoComponente("ANNESSO");
                    Documento annessoA = creaDocumento(token, annesso, diritti);
                    related.add(annessoA.getDocNum());
                    unitaDocumentaria.getAnnessi().add(annessoA);
                }
            }

            if (annotazioni != null) {
                // creazione annotazioni
                for (Documento annotazione : annotazioni) {
                    annotazione.setTipoComponente("ANNOTAZIONE");
                    Documento annotazioneA = creaDocumento(token, annotazione, diritti);

//                    if (diritti != null) {
//                        KeyValuePair[] acls = convertACL(diritti);
//
//                        SetACLDocument acl = new SetACLDocument();
//                        acl.setAcls(acls);
//                        acl.setDocId(annotazioneA.getDocNum());
//                        acl.setToken(token);
//
//                        serv.setACLDocument(acl);
//                    }

                    related.add(annotazioneA.getDocNum());
                    unitaDocumentaria.getAnnotazioni().add(annotazioneA);
                }
            }

            if (related.size() > 0) {
                correlaDocumenti(token, documento_principale.getDocNum(), related);
            }

            return unitaDocumentaria;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    private static KeyValuePair[] convertACL(List<Acl> acls) throws NumberFormatException, DocerApiException {


        Map<String,Integer> facl = new HashMap<>();

        List<KeyValuePair> listaAcl = new ArrayList<KeyValuePair>();
        KeyValuePair docerPair = null;

        for (Acl a : acls) {

            if (facl.containsKey(a.getUtenteGruppo()) && facl.get(a.getUtenteGruppo()) < a.getDiritti())
                continue;

            facl.put(a.getUtenteGruppo(),a.getDiritti());
        }

        for (String key : facl.keySet()) {

            docerPair = new DocerServicesStub.KeyValuePair();
            docerPair.setKey(key);
            docerPair.setValue(String.valueOf(facl.get(key)));
            listaAcl.add(docerPair);
        }

        KeyValuePair[] ret = listaAcl.toArray(new KeyValuePair[listaAcl.size()]);

        return ret;

    }

    private static List<Acl> convertACL(KeyValuePair[] acls) {
        List<Acl> listaAcl = new ArrayList<Acl>();

        if (acls == null)
            return listaAcl;

        for (KeyValuePair a : acls) {
            Acl acl = new Acl();
            acl.setUtenteGruppo(a.getKey());
            acl.setDiritti(a.getValue());
            listaAcl.add(acl);
        }

        return listaAcl;
    }

    private static KeyValuePair[] convertOrderBy(List<Ordinamento> orderby) {
        List<KeyValuePair> listaMetadati = new ArrayList<KeyValuePair>();
        KeyValuePair docerPair = null;

        if (orderby != null) {
            for (Ordinamento pair : orderby) {
                docerPair = new KeyValuePair();
                docerPair.setKey(pair.getNomeCampo());
                docerPair.setValue(pair.getTipo().toString());
                listaMetadati.add(docerPair);
            }
        }

        KeyValuePair[] arr = listaMetadati.toArray(new KeyValuePair[listaMetadati.size()]);
        return arr;
    }


    private static KeyValuePair[] convertMetadati(HashMap<String, String> metadati) {
        return convertMetadati(metadati, false);
    }


    private static KeyValuePair[] convertMetadati(HashMap<String, String> metadati, boolean multivalue) {

        List<KeyValuePair> listaMetadati = new ArrayList<KeyValuePair>();
        KeyValuePair docerPair = null;
        final String split = "||";

        for (Entry<String, String> pair : metadati.entrySet()) {
            if (pair.getValue() != null) {


                if (!pair.getValue().contains(split) || !multivalue) {
                    docerPair = new KeyValuePair();
                    docerPair.setKey(pair.getKey());
                    docerPair.setValue(pair.getValue());
                    listaMetadati.add(docerPair);
                } else {
                    String[] parts = StringUtils.split(pair.getValue(), "||");
                    for (int i = 0; i < parts.length; i++) {
                        docerPair = new KeyValuePair();
                        docerPair.setKey(pair.getKey());
                        docerPair.setValue(parts[i]);
                        listaMetadati.add(docerPair);
                    }
                }


            }
        }

        KeyValuePair[] arr = listaMetadati.toArray(new KeyValuePair[listaMetadati.size()]);
        return arr;
    }

    private static HashMap<String, String> convertMetadati(KeyValuePair[] metadati) {
        HashMap<String, String> listaMetadati = new HashMap<String, String>();
        if (metadati == null)
            return listaMetadati;

        for (KeyValuePair pair : metadati)
            if (pair.getValue() != null) {
                listaMetadati.put(pair.getKey(), pair.getValue());
            }
        return listaMetadati;
    }

    public static DocerFile downloadDocument(String token, Documento doc) throws DocerApiException {
        return downloadDocument(token, doc.getDocNum());
    }

    public static DocerFile downloadDocument(String token, String docNum) throws DocerApiException {
        try {

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DownloadDocument docFile = new DownloadDocument();
            DownloadDocumentResponse resp = new DownloadDocumentResponse();
            docFile.setDocId(docNum);
            docFile.setToken(token);

            resp = serv.downloadDocument(docFile);
            StreamDescriptor desc = resp.get_return();

            DocerFile dFile = new DocerFile();
            dFile.setSize(desc.getByteSize());
            dFile.setContent(desc.getHandler());
            return dFile;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DocerApiException(e);

        }

    }

    public static DocerFile downloadVersion(String token, String docNum, String version) throws DocerApiException {
        try {

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DownloadVersion docFile = new DownloadVersion();
            DownloadVersionResponse resp = new DownloadVersionResponse();
            docFile.setDocId(docNum);
            docFile.setVersionNumber(version);
            docFile.setToken(token);

            resp = serv.downloadVersion(docFile);
            StreamDescriptor desc = resp.get_return();

            DocerFile dFile = new DocerFile();
            dFile.setSize(desc.getByteSize());
            dFile.setContent(desc.getHandler());
            return dFile;

        } catch (Exception e) {
            e.printStackTrace();
            throw new DocerApiException(e);

        }

    }

    public static List<Cartella> ricercaCartella(String token, CartellaCriteria parametri_ricerca) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.SearchFolders req = new DocerServicesStub.SearchFolders();
            req.setToken(token);
            req.setSearchCriteria(convertMetadati(parametri_ricerca.properties, true));

            if (parametri_ricerca.getOrdinamento() != null) {
                KeyValuePair pair = new KeyValuePair();
                pair.setKey(parametri_ricerca.getOrdinamento().getNomeCampo());
                pair.setValue(parametri_ricerca.getOrdinamento().getTipo().toString());
                req.addOrderby(pair);
            }

            req.setMaxRows(parametri_ricerca.getMaxElementi());

            DocerServicesStub.SearchFoldersResponse resp = serv.searchFolders(req);
            SearchItem[] results = resp.get_return();
            List<Cartella> folders = new ArrayList<Cartella>();

            if (results != null) {
                for (SearchItem item : results) {

                    Cartella folder = new Cartella();

                    KeyValuePair[] metadata = item.getMetadata();
                    for (KeyValuePair meta : metadata) {
                        folder.properties.put(meta.getKey(), meta.getValue());
                    }
                    folders.add(folder);
                }
            }

            return folders;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    @Deprecated
    public static List<Cartella> ricercaCartella(String token, Cartella parametri_ricerca) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.SearchFolders req = new DocerServicesStub.SearchFolders();
            req.setToken(token);
            req.setMaxRows(100);
            req.setSearchCriteria(convertMetadati(parametri_ricerca.properties, true));

            DocerServicesStub.SearchFoldersResponse resp = serv.searchFolders(req);
            SearchItem[] results = resp.get_return();
            List<Cartella> folders = new ArrayList<Cartella>();

            if (results != null) {
                for (SearchItem item : results) {

                    Cartella folder = new Cartella();

                    KeyValuePair[] metadata = item.getMetadata();
                    for (KeyValuePair meta : metadata) {
                        folder.properties.put(meta.getKey(), meta.getValue());
                    }
                    folders.add(folder);
                }
            }

            return folders;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static Cartella creaCartella(String token, Cartella cartella) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            //Utils.validateFolderName(cartella.getNome());

            cartella.setProperty("FOLDER_OWNER", Utils.extractTokenKey(token, "uid"));

            DocerServicesStub.CreateFolder req = new DocerServicesStub.CreateFolder();
            req.setToken(token);
            req.setFolderInfo(convertMetadati(cartella.properties));

            DocerServicesStub.CreateFolderResponse resp = serv.createFolder(req);
            cartella.setID(resp.get_return());

//            List<Acl> diritti = new ArrayList<Acl>();
//            DocerService.impostaDirittiCartella(token, resp.get_return(), diritti);

            Cartella newCartella = new Cartella();
            newCartella.setID(resp.get_return());
            newCartella.setEnte(cartella.getEnte());
            newCartella.setAoo(cartella.getAoo());

            List<Cartella> listCartella = ricercaCartella(token, newCartella);
            if (listCartella == null || listCartella.size() == 0) {
                throw new DocerApiException("Folder non trovato" + newCartella.getID(), 404);
            } else {
                return listCartella.get(0);
            }


        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static Cartella creaCartellaCifs(String token, Cartella cartella) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            //Utils.validateFolderName(cartella.getNome());

            cartella.setProperty("FOLDER_OWNER", Utils.extractTokenKey(token, "uid"));

            DocerServicesStub.CreateFolder req = new DocerServicesStub.CreateFolder();
            req.setToken(token);
            req.setFolderInfo(convertMetadati(cartella.properties));

            DocerServicesStub.CreateFolderResponse resp = serv.createFolder(req);
            String folderId = resp.get_return();

            SOLRClient client = new SOLRClient();

//            List<Acl> diritti = new ArrayList<Acl>();
//            DocerService.impostaDirittiCartella(token, folderId, diritti);

            CartellaCriteria cc = new CartellaCriteria();
            cc.properties.clear();
            cc.setProperty("FOLDER_ID", folderId);

            Optional<Cartella> ret = client.openBySearch(token, cc, Cartella.class, true);
            if (!ret.isPresent()) {
                throw new DocerApiException("Cartella non trovata dopo la creazione", 500);
            }

            return ret.get();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static boolean rimuoviCartella(String token, Cartella cartella) throws DocerApiException {
//        String newName = String.format(DELETED_FORMAT, cartella.getID(), cartella.getNome());
        Cartella newCartella = new Cartella()
                .setEnte(cartella.getEnte())
                .setAoo(cartella.getAoo())
                .setID(cartella.getID());

        newCartella .setProperty("ENABLED", "false");
        return aggiornaCartella(token, newCartella);
    }

    private static boolean rimuoviCartella(String token, String id) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            List<Cartella> cartelle = ricercaCartella(token, new Cartella().setID(id));
            if (cartelle.size() == 0) {
                throw new DocerApiException("Cartella non trovata: " + id, 404);
            }

            return rimuoviCartella(token, cartelle.get(0));

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static List<Documento> recuperaDocumentiCartella(String token, Cartella cartella, Ordinamento... ordinamenti) throws DocerApiException {
        return recuperaDocumentiCartella(token, cartella.getID(), ordinamenti);
    }

    public static List<Documento> recuperaDocumentiCartella(String token, String idCartella, Ordinamento... ordinamenti) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.GetFolderDocuments getFolderDocumentsReq = new DocerServicesStub.GetFolderDocuments();
            getFolderDocumentsReq.setToken(token);
            getFolderDocumentsReq.setFolderId(idCartella);

            DocerServicesStub.GetFolderDocumentsResponse getFolderDocumentsResp = serv.getFolderDocuments(getFolderDocumentsReq);
            String[] docs = getFolderDocumentsResp.get_return();

            List<Documento> documents = new ArrayList<Documento>();

            if (docs != null && docs.length > 0) {

                SearchDocuments searchDocumentsReq = new SearchDocuments();
                searchDocumentsReq.setToken(token);
                searchDocumentsReq.setMaxRows(100);

                for (String doc : docs) {
                    KeyValuePair pair = new KeyValuePair();
                    pair.setKey("DOCNUM");
                    pair.setValue(doc);
                    searchDocumentsReq.addSearchCriteria(pair);
                }

                searchDocumentsReq.addKeywords("");

                if (ordinamenti != null) {
                    for (Ordinamento ordinamento : ordinamenti) {
                        KeyValuePair pair = new KeyValuePair();
                        pair.setKey(ordinamento.getNomeCampo());
                        pair.setValue(ordinamento.getTipo().toString());

                        searchDocumentsReq.addOrderby(pair);
                    }
                }

                SearchDocumentsResponse searchDocumentsResp = serv.searchDocuments(searchDocumentsReq);
                SearchItem[] results = searchDocumentsResp.get_return();

                if (results != null) {
                    PathInterface pi;
                    for (SearchItem result : results) {
                        Documento doc = new Documento();
                        doc.properties = convertMetadati(result.getMetadata());
                        documents.add(doc);
                    }
                }
            }

            return documents;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    private static String[] getIds(Documento... documenti) {
        String[] idDocumenti = new String[documenti.length];
        for (int i = 0; i < idDocumenti.length; i++) {
            idDocumenti[i] = documenti[i].getDocNum();
        }

        return idDocumenti;
    }

    public static boolean aggiungiDocumentiACartella(String token, String idCartella, Documento... documenti) throws DocerApiException {
        return aggiungiDocumentiACartella(token, idCartella, getIds(documenti));
    }

    public static boolean aggiungiDocumentiACartella(String token, Cartella cartella, String... idDocumenti) throws DocerApiException {
        return aggiungiDocumentiACartella(token, cartella.getID(), idDocumenti);
    }

    public static boolean aggiungiDocumentiACartella(String token, Cartella cartella, Documento... documenti) throws DocerApiException {
        return aggiungiDocumentiACartella(token, cartella.getID(), getIds(documenti));
    }

    public static boolean aggiungiDocumentiACartella(String token, String idCartella, String... idDocumenti) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.AddToFolderDocuments req = new DocerServicesStub.AddToFolderDocuments();
            req.setToken(token);
            req.setFolderId(idCartella);
            req.setDocument(idDocumenti);

            return serv.addToFolderDocuments(req).get_return();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static boolean rimuoviDocumentiDaCartella(String token, Cartella cartella, Documento... documenti) throws DocerApiException {
        return rimuoviDocumentiDaCartella(token, cartella.getID(), getIds(documenti));
    }

    public static boolean rimuoviDocumentiDaCartella(String token, String idCartella, Documento... documenti) throws DocerApiException {
        return rimuoviDocumentiDaCartella(token, idCartella, getIds(documenti));
    }

    public static boolean rimuoviDocumentiDaCartella(String token, Cartella cartella, String... idDocumenti) throws DocerApiException {
        return rimuoviDocumentiDaCartella(token, cartella.getID(), idDocumenti);
    }

    public static boolean rimuoviDocumentiDaCartella(String token, String idCartella, String... idDocumenti) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            DocerServicesStub.RemoveFromFolderDocuments req = new DocerServicesStub.RemoveFromFolderDocuments();
            req.setToken(token);
            req.setFolderId(idCartella);
            req.setDocument(idDocumenti);

            return serv.removeFromFolderDocuments(req).get_return();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static boolean aggiornaCartella(String token, Cartella cartella) throws DocerApiException {

        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            /*if (!Strings.isNullOrEmpty(cartella.getNome())) {
                Utils.validateFolderName(cartella.getNome());
            }*/

            DocerServicesStub.UpdateFolder req = new DocerServicesStub.UpdateFolder();
             req.setToken(token);
            req.setFolderId(cartella.properties.remove("FOLDER_ID"));
            req.setFolderInfo(convertMetadati(cartella.properties));

            return serv.updateFolder(req).get_return();

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void aggiungiRiferimento(String token, Documento documento, List<Documento> riferimenti) throws DocerApiException {
        List<String> docnumbers = new ArrayList<>();
        for (Documento doc : riferimenti) {
            docnumbers.add(doc.getDocNum());
        }

        aggiungiRiferimento(token, documento.getDocNum(), docnumbers);
    }

    public static void aggiungiRiferimento(String token, String documento, List<String> riferimenti) throws DocerApiException {
        try {
            AddRiferimentiDocuments addRiferimentiDocuments = new AddRiferimentiDocuments();
            addRiferimentiDocuments.setToken(token);
            addRiferimentiDocuments.setDocId(documento);
            addRiferimentiDocuments.setRiferimenti(riferimenti.toArray(new String[riferimenti.size()]));

            String sede = ToolkitConnector.extractSedeFromToken(token);
            DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);

            serv.addRiferimentiDocuments(addRiferimentiDocuments);

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }

    public static void aggiungiRiferimento(String token, String documento, String riferimento) throws DocerApiException {
        List<String> riferimentoDocNum = new ArrayList<>();
        riferimentoDocNum.add(riferimento);
        aggiungiRiferimento(token, documento, riferimentoDocNum);
    }

	public static void aggiungiRiferimento(String token, Documento documento,Documento riferimento) throws DocerApiException
	{
		
		List<Documento> riferimentoDocNum = new ArrayList<Documento>();
		riferimentoDocNum.add(riferimento);
		aggiungiRiferimento(token, documento, riferimentoDocNum);
	}
	
	
	public static void eliminaRiferimenti(String token, String docNumPrincipale, String[] docNumRiferimento) throws DocerApiException
	{
		if(docNumRiferimento==null || docNumRiferimento.length<1)
			throw new DocerApiException("Nessun riferimento al documento", 404);
		try
		{
            String sede = ToolkitConnector.extractSedeFromToken(token);
			DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
			RemoveRiferimentiDocuments removeRiferimentiDocuments = new RemoveRiferimentiDocuments();
			removeRiferimentiDocuments.setRiferimenti(docNumRiferimento);
			removeRiferimentiDocuments.setDocId(docNumPrincipale);
			removeRiferimentiDocuments.setToken(token);
			serv.removeRiferimentiDocuments(removeRiferimentiDocuments);
		}
		catch (Exception e)
		{
			throw new DocerApiException(e);
		}
	}
	
	public static List<Documento> getRiferimenti(String token, String docNumPrincipale) throws DocerApiException
	{
		GetRiferimentiDocumentsResponse getRiferimentiDocumentsResponse=null;
		List<Documento> ret = new ArrayList<Documento>();
		if(docNumPrincipale==null || docNumPrincipale.equals(""))
			throw new DocerApiException("Campo docNumPrincipale vuoto", 404);
		try
		{
            String sede = ToolkitConnector.extractSedeFromToken(token);
			DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
			GetRiferimentiDocuments getRiferimentiDocuments = new GetRiferimentiDocuments();
			getRiferimentiDocuments.setToken(token);
			getRiferimentiDocuments.setDocId(docNumPrincipale);
			getRiferimentiDocumentsResponse = serv.getRiferimentiDocuments(getRiferimentiDocuments);
			if(getRiferimentiDocumentsResponse!=null && getRiferimentiDocumentsResponse.get_return()!=null && 
					getRiferimentiDocumentsResponse.get_return().length > 0){
			
				String [] docNum = getRiferimentiDocumentsResponse.get_return();
				SearchDocuments searchDocuments = new SearchDocuments();
				KeyValuePair [] keyValuePair = new KeyValuePair[docNum.length];
				int indexKeyValue = 0;
				for(String tmp:docNum){
					keyValuePair[indexKeyValue]=new KeyValuePair();
					keyValuePair[indexKeyValue].setKey("DOCNUM");
					keyValuePair[indexKeyValue].setValue(tmp);
					indexKeyValue++;
					
				}
				searchDocuments.setToken(token);
				String keywordString [] = new String[1];
				searchDocuments.setKeywords(keywordString);
				searchDocuments.setSearchCriteria(keyValuePair);
				searchDocuments.setMaxRows(100);
				SearchDocumentsResponse searchDocumentsResponse = serv.searchDocuments(searchDocuments);
				SearchItem[] results = searchDocumentsResponse.get_return();
				if (results != null) {
			        for (SearchItem item : results) {
			            Documento doc = new Documento();
			            doc.properties = convertMetadati(item.getMetadata());
			            ret.add(doc);
			        }
			    }
			}
			return ret;
		}catch (Exception e){
			throw new DocerApiException(e);
		}
	}
	
	
    
	

    /*public static String getDescription(String token, Titolario titolario) throws DocerApiException {
        try {

            List<Titolario> results = DocerService.ricercaTitolari(token, titolario);
            if (results.size() > 0) {
                return results.get(0).getDescrizione();
            }

            return null;

        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }*/
/*
    public static String getDescription(String token, Fascicolo fascicolo) throws DocerApiException {
        try {
            List<Fascicolo> results = DocerService.ricercaFascicoli(token, fascicolo);
            if (results.size() > 0) {
                return results.get(0).getDescrizione();
            }

            return null;
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }
*/

/*
    public static AOO getAOO(String token, String codiceEnte, String codiceAoo) throws DocerApiException {
        try {
            String sede = ToolkitConnector.extractSedeFromToken(token);
        	DocerServicesStub serv = ClientManager.INSTANCE.getDocerServicesClient(sede);
        	GetAOO getAoo = new GetAOO();
        	KeyValuePair [] key = new KeyValuePair[2];
        	key[1] = new KeyValuePair();
        	key[0] = new KeyValuePair();
        	key[0].setKey("COD_ENTE");
        	key[0].setValue(codiceEnte);
        	key[1].setKey("COD_AOO");
        	key[1].setValue(codiceAoo);
         	getAoo.setAooId(key);
        	getAoo.setToken(token);
        	GetAOOResponse aooResponse = serv.getAOO(getAoo);
           
        	KeyValuePair[] metaAnagrafica = aooResponse.get_return();
			
			HashMap<String, String> properties = convertMetadati(metaAnagrafica);
			AOO aoo = new AOO();
			aoo.properties = properties;
			
			return aoo;
            
        } catch (Exception e) {
            throw new DocerApiException(e);
        }
    }
*/
/*
	public static List<PersonaFisica> getFirmatari(String token, String docNum, boolean verifyCertificates) throws Exception
	{
        List<PersonaFisica> persone;

        DocerFile file = downloadDocument(token, docNum);
        DataHandler handler = file.getContent();

        persone = getFirmatari( handler.getInputStream(), verifyCertificates );

		return persone;
	}
*/

    public static List<PersonaFisica> getFirmatari(String token, String docNum, Date verificationDate) throws Exception
    {
        List<PersonaFisica> persone;

        DocerFile file = downloadDocument(token, docNum);
        DataHandler handler = file.getContent();

        persone = getFirmatari( handler.getInputStream(), verificationDate );

        return persone;
    }

    public static List<PersonaFisica> getFirmatari( InputStream binaryDocument) throws Exception {
        return getFirmatari(binaryDocument,null);
    }

    private static List<PersonaFisica> getFirmatari( InputStream binaryDocument,Date verificationDate) throws Exception
    {

        List<PersonaFisica> lista = new ArrayList<PersonaFisica>();
        SignatureValidator validator = new SignatureValidator(binaryDocument);

        validator.validateDocument(verificationDate);

        List<Firmatario> firmatari = validator.getFirmatari();

        for(Firmatario firmatario : firmatari){
            PersonaFisica persona = new PersonaFisica();

            persona.setProperty( "denominazione", firmatario.getCognomeNome() );
            persona.setCodiceFiscale(firmatario.getCf());
            persona.setDataFirma(firmatario.getRiferimentoTemporaleUsato());

            String controlloConformita=firmatario.getEsitoFirma().getControlloConformita();
            if(!EsitoVerifiche.POSITIVE.equals(controlloConformita)){
                persona.setControlloFirma(firmatario.getEsitoFirma().getVerificaFirma());
            }
//            persona.setWarnings("");
//            persona.setErrors("");
            lista.add(persona);
        }
        return lista;
    }



    private static boolean searchBinary(byte[] Source, byte[] Search) {
        boolean Find = false;
        int i;
        int fromIndex=0;
        for (i = fromIndex;i<Source.length-Search.length;i++){
            if(Source[i]==Search[0]){
                Find = true;
                for (int j = 0;j<Search.length;j++){
                    if (Source[i+j]!=Search[j]){
                        Find = false;
                    }
                }
            }
            if(Find){
                return true;
            }
        }
        return false;
    }


    public static String verifyFileSigned(String binaryDocument) throws IOException {

        if (!binaryDocument.toUpperCase().endsWith(".TMP")  && !binaryDocument.toUpperCase().endsWith(".P7M") )
            return binaryDocument;

        BufferedReader br = null;
        try {
             br = new BufferedReader(new FileReader(binaryDocument));
             String firstLine = br.readLine();
             if(Base64.isBase64(firstLine)){

                 File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
                 File fileOrig = new File(binaryDocument);
                 FileUtils.copyFile(fileOrig,temp);
                 Base64InputStream base64InputStream = new Base64InputStream(new FileInputStream(temp));
                 FileUtils.copyInputStreamToFile(base64InputStream,fileOrig);
                 return binaryDocument;
             }else{
                 return binaryDocument;
             }
        }catch(IOException e){
           throw e;
        }finally{
            br.close();
        }




    }





    public static boolean isFileSigned(String binaryDocument) throws IOException {


        if ( binaryDocument.toUpperCase().endsWith(".P7M") ) {
            verifyFileSigned(binaryDocument);
            return true;
        }

        if ( !binaryDocument.toUpperCase().endsWith(".PDF") )
            return false;



        File target = new File(binaryDocument);
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(target,"r");

            long l = file.length();

            String tag = "SigFlags";

            int chunk = 1024;

            chunk += tag.length()+1;

            long position = target.length()-chunk-1;

            int steps = (int) l/chunk;

            byte[] searchBytes = new String(tag).getBytes();

            boolean found = false;
            long toRead = chunk * 40; //circa 40k
            while(toRead>0)
            {
                if (position<0)
                    break;

                file.seek(position);

                byte[] buffer = new byte[chunk];

                toRead -= file.read(buffer);

                if (searchBinary(buffer, searchBytes))
                {
                    found=true;
                    break;
                }

                position -= chunk;
            }
            return found;
        } catch (IOException e) {
            throw e;
        } finally {
            file.close();
        }
    }





}