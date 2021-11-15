package it.kdm.orchestratore.appdoc.utils;

import freemarker.cache.WebappTemplateLoader;
import it.kdm.orchestratore.utils.KDMUtils;
import it.kdm.orchestratore.utils.ResourceUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

public class MacroTemplatesLoader extends WebappTemplateLoader {
    private String ente;
    private String aoo;


    public MacroTemplatesLoader(ServletContext servletContext) {
        super(servletContext);
    }
    public MacroTemplatesLoader(ServletContext servletContext, String ente, String aoo) {
        super(servletContext);
        this.ente = ente;
        this.aoo = aoo;
    }

    public MacroTemplatesLoader(ServletContext servletContext, String path) {
        super(servletContext, path);
    }


    @Override
    public Object findTemplateSource(String name) throws IOException {
        Object result = null;

        try {
            if(!name.startsWith("/") && name.contains("macros")){
                name = "/"+name;
            }
            if(name.startsWith("/macros")){
                name="/templates"+name;
            }
            InputStream initialStream = ResourceUtils.getResourceAsStream(null, ente, aoo, name);

            if(initialStream != null) {
                result = KDMUtils.stream2file(initialStream,name);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
