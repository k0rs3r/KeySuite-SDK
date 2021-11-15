package it.kdm.orchestratore.security;

import it.kdm.doctoolkit.exception.DocerApiException;
import org.springframework.core.env.PropertyResolver;

import java.util.Map;

public class BaseEffectiveRights implements IEffectiveRights {

    PropertyResolver properties = null;

    public PropertyResolver getProperties() {
		return properties;
	}

	public void setProperties(PropertyResolver properties) {
		this.properties = properties;
	}

    protected long getRoleMask(String roleName) {
        try {
            long value = Long.parseLong(properties.getProperty("Security.roleMask."+roleName));
            return value;
        }
        catch(Exception e) {return 0;}
    }

    @Override
	public long getEffectiveRights(Object target, String token, String user)
			throws DocerApiException {
		
		return 0;
	}

    @Override
    public Map<String,String> getRules() {
        throw new UnsupportedOperationException();
    }


}
