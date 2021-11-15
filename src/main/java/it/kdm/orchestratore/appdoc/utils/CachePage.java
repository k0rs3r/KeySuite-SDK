package it.kdm.orchestratore.appdoc.utils;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

import java.util.List;
import java.util.UUID;

public class CachePage {

	public static int pageSize = 10;
	public static int firstPage = 1;
	
	public static List<?> get(String guid, String orderBy , int pageNum)throws CacheException {
		
		JCS cache = JCS.getInstance("searches");
		List<?> elements = (List<?>)cache.get(guid+orderBy);
		
		int from = (pageNum - firstPage)*pageSize;
		int to = from + pageSize;
		if(elements == null){
			return null;
		}
	
		int size = elements.size();

		if(to >= size){
			to = size;
		}
		return elements.subList(from, to);
	}
	public static String put(List<?> elements , String orderBy)throws CacheException {
		String guid = UUID.randomUUID().toString();
		JCS cache = JCS.getInstance("searches");
		cache.put(guid+orderBy, elements);
		return guid;
		
	}
	public static int getPageCount(String guid, String orderBy)throws CacheException{ 
		JCS cache = JCS.getInstance("searches");
		List<?> elements = (List<?>)cache.get(guid+orderBy);
		if(elements == null)
			return 0;
		double result = Math.ceil(elements.size()/(double)pageSize);
		return (int)result;
	}
	
}
