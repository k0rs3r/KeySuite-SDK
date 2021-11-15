package it.kdm.doctoolkit.zookeeper;

import com.google.gson.Gson;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
//import org.json.JSONArray;
//import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class ZKManagerImpl implements ZKManager {
    private static ZooKeeper zkeeper;
    private static ZKConnection zkConnection;


    public ZKManagerImpl(String host) throws IOException, InterruptedException {
        initialize(host);
    }

    private void initialize(String host) throws IOException, InterruptedException {
        zkConnection = new ZKConnection();
        zkeeper = zkConnection.connect(host);
    }

    public void closeConnection() throws InterruptedException {
        zkConnection.close();
    }

    private void createOrUpdate(String path, byte[] data)
            throws KeeperException,
            InterruptedException {

        if(!exists(path, false)) {
            zkeeper.create(
                    path,
                    data,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        }else{
            update(path, data);
        }
    }

    private void update(String path, byte[] data) throws KeeperException,
            InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);
    }

    private Object getZNodeData(String path, boolean watchFlag, boolean asString)
            throws KeeperException,
            InterruptedException, UnsupportedEncodingException {

        byte[] b = null;
        b = zkeeper.getData(path, null, null);
        ByteArrayInputStream bis = new ByteArrayInputStream(b);

        if(asString){
            return new String(b, "UTF-8");
        }
        return bis;
    }

    @Override
    public boolean exists(String path, boolean watch) throws KeeperException, InterruptedException {
        return ((zkeeper.exists(path,watch)) != null);
    }

    @Override
    public void writeString(String path, String value)
            throws KeeperException,
            InterruptedException,UnsupportedEncodingException {

        createOrUpdate(path, value.getBytes("UTF-8"));
    }

    @Override
    public void writeProperties(String path, Properties properties)throws KeeperException,
            InterruptedException,UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        for(String key:properties.stringPropertyNames()){
            sb.append(key).append("=").append(properties.getProperty(key, ""));
            sb.append("\n");
        }
        String s = sb.toString();
        writeString(path, s);

    }

    @Override
    public void writeObject(String path, Object object)throws KeeperException,
            InterruptedException,UnsupportedEncodingException {
        Gson gson = new Gson();

        String s = gson.toJson(object);
        writeString(path, s);

    }

//    @Override
//    public void writeJSONObject(String path, JSONObject jsonObject)throws KeeperException,
//            InterruptedException,UnsupportedEncodingException {
//        Gson gson = new Gson();
//
//        String s = jsonObject.toString();
//
//        writeString(path, s);
//
//    }
//
//    @Override
//    public void writeJSONArray(String path, JSONArray jsonArray)throws KeeperException,
//            InterruptedException,UnsupportedEncodingException {
//        Gson gson = new Gson();
//
//        String s = jsonArray.toString();
//
//        writeString(path, s);
//
//    }

    @Override
    public Properties getPropertiesNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException{
        Object is = getZNodeData(path, watchFlag, false);
        Properties p = null;
        if(is instanceof InputStream) {
            p = new Properties();
            p.load((InputStream) is);
        }
        return p;
    }

    @Override
    public String getStringNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException{
        return (String) getZNodeData(path, watchFlag, true);
    }

//    @Override
//    public JSONObject getJSONObjectNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException{
//        JSONObject obje = new JSONObject((String) getZNodeData(path, watchFlag, true));
//        return obje;
//    }
//
//    @Override
//    public JSONArray getJSONArrayNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException{
//        JSONArray obje = new JSONArray((String) getZNodeData(path, watchFlag, true));
//        return obje;
//    }

    @Override
    public <T> T getObjectNode(String path, boolean watchFlag, Class<T> classOfT)throws IOException, InterruptedException, KeeperException{
        String stringfyNode = getStringNode(path,watchFlag);
        Gson gson = new Gson();
        T obj = gson.fromJson(stringfyNode, classOfT);
        return obj;
    }

}
