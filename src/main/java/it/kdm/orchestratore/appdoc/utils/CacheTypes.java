package it.kdm.orchestratore.appdoc.utils;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

import java.util.Map;

public class CacheTypes {

	public static int pageSize = 10;
	public static int firstPage = 1;
	
	public static Map<String, String> get(String ... args)throws CacheException {

		String cacheName = getCacheName(args);

		JCS cache = JCS.getInstance("types");
		Map<String, String> typesMap = (Map<String, String>)cache.get( cacheName );

		return typesMap;
	}
	public static void put(Map<String, String> typesMap, String ... args) throws CacheException {

		String cacheName = getCacheName(args);
		JCS cache = JCS.getInstance("types");
		cache.put(cacheName, typesMap);

	}

	private static String getCacheName(String ... args){
		String cacheName = "";
		for(String arg : args){
			cacheName+= arg + "~";
		}
		return cacheName;
	}
	
}
