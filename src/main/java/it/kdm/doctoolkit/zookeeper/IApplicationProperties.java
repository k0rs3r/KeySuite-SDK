package it.kdm.doctoolkit.zookeeper;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Properties;

public interface IApplicationProperties {

    Properties inizializePropertiesbyPath(String path)throws IOException, InterruptedException, KeeperException;
    String getPropertyByKey(String key, String defaultValue);

}
