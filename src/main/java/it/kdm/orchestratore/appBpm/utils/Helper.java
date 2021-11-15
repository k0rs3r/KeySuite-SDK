package it.kdm.orchestratore.appBpm.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by antsic on 31/03/15.
 */
public class Helper {

    final static ObjectMapper yamlMapper;
    final static ObjectMapper jsonMapper;
    final static ObjectMapper jsonIndentedMapper;

    static{
        yamlMapper = new ObjectMapper(new YAMLFactory());
        jsonMapper = new ObjectMapper();
        jsonIndentedMapper = new ObjectMapper();
        jsonIndentedMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static Map<String,Object> jsonToHashMap(String strSettings) throws IOException{
        //ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> settings = null;
        try {
            settings = jsonMapper.readValue(strSettings, new TypeReference<Map<String,Object>>() { });

        } catch (JsonParseException e) {
            e.printStackTrace();
            throw e;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return settings;
    }

    public static String hashMapToJson(Map<String,Object> mapSetting) throws IOException{
        return hashMapToJson(mapSetting,false);
    }

    public static String hashMapToJson(Map<String,Object> mapSetting, boolean indent) throws IOException{
        ObjectMapper mapper;
        if (indent)
            mapper = jsonIndentedMapper;
        else
            mapper = jsonMapper;
        String json = "";

        //convert map to JSON string
        try {
            json = mapper.writeValueAsString(mapSetting);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String toJson(Object bean,boolean indent) throws IOException {
        return indent? jsonIndentedMapper.writeValueAsString(bean) : jsonMapper.writeValueAsString(bean);
    }

    public static <T> T parseJson(String json) throws IOException{
        return (T) jsonMapper.readValue(json, Object.class);
    }

    public static <T> T parseJson(InputStream json) throws IOException{
        return (T) jsonMapper.readValue(json, Object.class);
    }

    public static String toYAML(Object bean) throws IOException {
        return yamlMapper.writeValueAsString(bean);
    }

    public static Object parseYAML(String yaml) throws IOException{
        return yamlMapper.readValue(yaml, Object.class);
    }

    public static <T> T parseYAML(InputStream yaml) throws IOException{
        return (T) yamlMapper.readValue(yaml, Object.class);
    }

}
