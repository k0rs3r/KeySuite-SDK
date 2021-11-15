package it.kdm.orchestratore.security;

import it.kdm.doctoolkit.exception.DocerApiException;

import java.util.Map;

public interface IEffectiveRights {
	public long getEffectiveRights(Object target ,String token , String user)throws DocerApiException;
	public Map<String,String> getRules();
}