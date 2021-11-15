package it.kdm.doctoolkit.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TreeViewProfile
{
    private HashMap confMap;

    private void initialize(InputStream confFile) throws IOException {
        String jsonProfiles = IOUtils.toString(confFile);
        confFile.close();

        confMap = convertFromJsonStringToGeneric(jsonProfiles, HashMap.class);
        //defaultProfile = (String) confMap.get("defaultProfile");
        //mapProfiles = (HashMap) confMap.get("profiles");
    }

	public TreeViewProfile() throws IOException {
		InputStream is = getClass().getResourceAsStream("/default-profiles.json");
        initialize(is);
	}

    public TreeViewProfile(String ente) throws IOException {
        File configFile = new File(Utils.getConfigHome(), String.format("%s-profiles.json", ente.toLowerCase()));

        InputStream is;
        if (configFile.exists()) {
            is = new FileInputStream(configFile);
        } else {
            is = getClass().getResourceAsStream("/default-profiles.json");
        }
        try {
            initialize(is);
        } finally {
            is.close();
        }
    }

    private static <T> T convertFromJsonStringToGeneric(String jsonString, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException
{
    ObjectMapper mapper = new ObjectMapper();

    T jsonObject = null;

    if (mapper.canSerialize(valueType)) {
        jsonObject = mapper.readValue(jsonString, valueType);
    }

    return jsonObject;
}
	
	public HashMap getProfileConfig(String profileName) {
		return (HashMap)((HashMap)confMap.get("profiles")).get(profileName);
	}

    public String getDefaultProfile() {
        return (String) confMap.get("defaultProfile");
    }

    public HashMap getSearches() {
        return (HashMap) confMap.get("searches");
    }

    public java.util.Set<String> listProfiles() {
        return ((HashMap)confMap.get("profiles")).keySet();
    }
}

