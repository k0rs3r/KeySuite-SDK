package it.kdm.doctoolkit.zookeeper;

import org.apache.zookeeper.KeeperException;
//import org.json.JSONArray;
//import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public interface ZKManager {

    boolean exists(String path, boolean watch)throws KeeperException, InterruptedException;

    void writeString(String path, String value) throws KeeperException, InterruptedException,UnsupportedEncodingException;

    void writeProperties(String path, Properties properties)throws KeeperException, InterruptedException,UnsupportedEncodingException;

    void writeObject(String path, Object object)throws KeeperException, InterruptedException,UnsupportedEncodingException;

//    void writeJSONObject(String path, JSONObject jsonObject)throws KeeperException, InterruptedException,UnsupportedEncodingException;
//
//    void writeJSONArray(String path, JSONArray jsonArray)throws KeeperException, InterruptedException,UnsupportedEncodingException;

    Properties getPropertiesNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException;

    String getStringNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException;

//    JSONObject getJSONObjectNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException;
//
//    JSONArray getJSONArrayNode(String path, boolean watchFlag)throws IOException, InterruptedException, KeeperException;

    <T> T getObjectNode(String path, boolean watchFlag, Class<T> classOfT)throws IOException, InterruptedException, KeeperException;

}
