import it.kdm.orchestratore.activation.JarClassLoader;
import it.kdm.orchestratore.appBpm.utils.Helper;
import it.kdm.orchestratore.configuration.ConfigurationManager;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by microchip on 10/11/14.
 */
public class TestRepository {



    @Test
    public void test() throws Exception {

        System.setProperty("activation.lib","C:\\Users\\Paolo_2\\bpm-config\\lib");

        Object obj = new JarClassLoader("test").newInstance();

        //obj.getClass().getMethod("test1").invoke(obj);

        Class c = obj.getClass();
        Method m = c.getMethod("test1");
        Object v = m.invoke(obj);

    }

    @Test
    public void importConfig() throws Exception {

        System.setProperty("KEYSUITE_CONFIG","C:\\Users\\Paolo_2\\bpm-config");
        System.setProperty("DOCER_CONFIG","C:\\Users\\Paolo_2\\bpm-config");
        System.setProperty("SOLR_CONFIG","C:\\Users\\Paolo_2\\bpm-config\\solr");

        ConfigurationManager manager = new ConfigurationManager("ANM","ANM_AOO");
        manager.importConfig(true);
    }

    @Test
    public void exportConfig() throws Exception {

        System.setProperty("KEYSUITE_CONFIG","C:\\Users\\Paolo_2\\bpm-config");
        System.setProperty("DOCER_CONFIG","C:\\Users\\Paolo_2\\bpm-config");
        System.setProperty("SOLR_CONFIG","C:\\Users\\Paolo_2\\bpm-config\\solr");

        ConfigurationManager manager = new ConfigurationManager("ANM","ANM_AOO");
        manager.exportConfig(true);
    }

    @Test
    public void complareJson() throws Exception {

        System.setProperty("KEYSUITE_CONFIG","C:\\Users\\Paolo_2\\bpm-config");
        System.setProperty("DOCER_CONFIG","C:\\Users\\Paolo_2\\bpm-config");
        System.setProperty("SOLR_CONFIG","C:\\Users\\Paolo_2\\bpm-config\\solr");

        ConfigurationManager manager = new ConfigurationManager("ANM","ANM_AOO");

        Map<String,Object> map = manager.getConfig();

        String oldFile = Helper.hashMapToJson(map,true);

        //((Map)((Map)map.get("documentale")).get("tipologie")).remove("FATTURA");

        String newFile = Helper.hashMapToJson(map,true);

        String diff = manager.checkDiff(false,oldFile,newFile);

        System.out.print(diff);
    }
}