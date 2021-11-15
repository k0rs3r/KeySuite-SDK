package it.kdm.orchestratore.appBpm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class DocerTokenUtil {

		public static String createServerToken(String token) throws Exception {
			String uid = "uid";
			String userGroup = "userGroup";
 
		Pattern patternUser = Pattern.compile(String.format("(?:\\||^)%s:([^|]*?)\\|.*", uid));
		Matcher matcherUser = patternUser.matcher(token);
		if(!matcherUser.find()) {
			 throw new Exception("key not found: "+uid);
		}
		
		Pattern patternGroups = Pattern.compile(String.format("(?:\\||^)%s:([^|]*?)\\|.*", userGroup));
		Matcher matcherGroups = patternGroups.matcher(token);
		if(!matcherGroups.find()) {
			 throw new Exception("key not found: "+userGroup);
		}

		return "uid:"+matcherUser.group(1)+"|userGroup:"+matcherGroups.group(1)+"|";
	}


	public static String extractTokenKey(String token, String key) throws Exception {

		if(key == null || key.equals("")) {
			throw new Exception("Empty key cannot be extracted from token");
		}

		Pattern pattern = Pattern.compile(String.format("(?:\\||^)%s:([^|]*?)\\|.*", key));
		Matcher matcher = pattern.matcher(token);
		if(!matcher.find()) {
			throw new Exception("key not found: "+key);
		}

		return matcher.group(1);
	}


}
