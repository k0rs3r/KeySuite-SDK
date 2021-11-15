package it.kdm.orchestratore.activation;

import com.google.common.base.Strings;
import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.doctoolkit.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JarClassLoader extends MultiClassLoader
{
    private static final Logger log = LoggerFactory.getLogger(JarClassLoader.class);

    protected Map<String,byte[]> resources;
    protected String className;

    public static void setLibPath(String path){
        System.setProperty("activation.lib",path);
    }

    public static String getLibPath(){
        String jarsPath = System.getProperty("activation.lib");
        if (Strings.isNullOrEmpty(jarsPath)){
            jarsPath = ToolkitConnector.getGlobalProperty("activation.lib", Utils.getConfigHome()+"/lib");
        }
        return jarsPath;
    }

    public JarClassLoader (String className) throws ClassNotFoundException {

        try{
            Class.forName(className);
            this.className = className;
            return;
        } catch (ClassNotFoundException cnfe) {
            //lib
        }
        String jarPath = System.getProperty("activation."+className+".jar");
        String fullname = System.getProperty("activation."+className+".fullname");

        if (Strings.isNullOrEmpty(jarPath) || Strings.isNullOrEmpty(fullname)){
            String jarsPath = getLibPath();

            File folder = new File(jarsPath);

            File[] jars = folder.listFiles();

            if (jars==null){
                String message = "class '"+className+"' not found in "+jarsPath+ " (folder doesn't exists or is empty)";
                throw new ClassNotFoundException(message);
            }

            main:
            for( File jar : jars ){

                if (!jar.getName().toLowerCase().endsWith(".jar"))
                    continue;

                JarResources jarResources = new JarResources(jar.toString());

                if (className.contains(".")){
                    String searchName = formatClassName (className);
                    if (jarResources.getResource (searchName) != null){
                        jarPath = jar.toString();
                        fullname = className;
                        this.resources = jarResources.getResources();
                        break;
                    }
                } else {
                    Collection<String> resources = jarResources.getResources().keySet();
                    for ( String resource : resources ){
                        String searchName = formatClassName("."+className);
                        if (resource.endsWith(searchName)){
                            jarPath = jar.toString();
                            fullname = unformatClassName(resource);
                            this.resources = jarResources.getResources();
                            break main;
                        }
                    }
                }
            }

            if (Strings.isNullOrEmpty(fullname)){
                String message = "class '"+className+"' not found in "+jarsPath;
                throw new ClassNotFoundException(message);
            }
            System.setProperty("activation."+className+".jar",jarPath);
            System.setProperty("activation."+className+".fullname",fullname);
        } else {
            this.resources = new JarResources(jarPath).getResources();
        }

        this.className = fullname;
    }

    protected byte[] loadClassBytes (String className)
    {
        // Support the MultiClassLoader's class name munging facility.
        className = formatClassName (className);
        // Attempt to get the class data from the JarResource.
        return (resources.get(className));
    }

    public Class get(){
        try {
            return this.loadClass(className,true);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Object newInstance(Object... args) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class c = this.loadClass(className,true);
        Constructor[] constructors = c.getDeclaredConstructors();

        if (constructors.length==0)
            throw new NoSuchMethodException();

        if (constructors.length==1)
            return constructors[0].newInstance(args);

        List<Class> argsC = new ArrayList<>();
        for( Object o : args ){
            argsC.add(o.getClass());
        }

        Constructor constructor = c.getDeclaredConstructor(argsC.toArray(new Class[0]));

        return constructor.newInstance(args);
    }
}