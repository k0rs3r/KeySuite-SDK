package it.kdm.doctoolkit.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public abstract class GenericObject implements Serializable {

    protected String businessType;
	
	 //internal Dictionary<string, string> properties = new Dictionary<string, string>();
	 public HashMap<String,String> properties = new HashMap<String,String>();
	 
	 
	 public GenericObject()
     {
         initProperties();
     }

     public void importProperties(HashMap<String,Object> map) {
        for (String key : map.keySet()) {
            this.setProperty(key,(String)map.get(key));
        }
     }

	 public void setProperty(String key, String value)
     {
         if (this.properties.containsKey(key))
             this.properties.remove(key);

         this.properties.put(key, value);
     }
	 
     public String getProperty(String key) 
     {
         if (this.properties.containsKey(key))
             return  this.properties.get(key);

         return null;
     }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void copyFrom(GenericObject obj)
     {
         this.properties = new HashMap<String,String>(obj.properties);
     }

    public void getSolrModelFrom(GenericObject obj){

        HashMap<String,String> criteria = new HashMap<String,String>();

        for (Map.Entry<String, String> entry : obj.properties.entrySet()) {

            String key = entry.getKey();
            String val = entry.getValue();

            val = "".equals(val) ? null : val;
            criteria.put(key, val);

        }

        this.properties = criteria;
    }

     public void copyFrom(HashMap<String,String> props)
     {
         this.properties = new HashMap<String,String>(props);
     }

     public HashMap<String,Object> toFlowObject() throws Exception {
         HashMap<String,Object> flowObj = new HashMap<String,Object>();
         //cleanFields(true);
         flowObj.putAll(properties);

         return flowObj;
     }

     public void fromFlowObject(HashMap<String,Object> flowObject) throws Exception {
         for (String key : flowObject.keySet()) {
             properties.put(key, String.valueOf(flowObject.get(key)));
         }
         cleanFields(false);
     }
     
     public String getFEId() {
    	 return "";
     }
    
     public String getFEName() {
    	 return "";
     }
    
     public String getFEAuthor() {
    	 return "";
     }
    
     public String getFEDate() {
    	 return "";
     }
     

	protected abstract void initProperties();

    @Override
    public String toString() {
        return properties.toString();
    }

    public void cleanFields(boolean onlyIfEmpty)throws Exception{
        String className = this.getClass().getSimpleName();

        Properties props = new Properties();
        props.load(this.getClass().getResourceAsStream("/toolkit.properties"));
        String fieldListToSkip = props.getProperty(className+".fieldListToSkip");

        if (fieldListToSkip==null)
            return;

        StringTokenizer st = new StringTokenizer(fieldListToSkip,",");
    	while (st.hasMoreElements()) {
			String token = (String) st.nextElement();
            token = token.trim();
            if (onlyIfEmpty==true)
                if (properties.get(token)!=null && !properties.get(token).equals(""))
                    continue;

			this.properties.remove(token);
		}
    	
    }
}
