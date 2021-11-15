package it.kdm.doctoolkit.utils;

import java.util.HashMap;


public class TreeProfileItemConfig {
	HashMap itemConfig = null;
	
	public TreeProfileItemConfig(HashMap itemConfig) {
		this.itemConfig = itemConfig;
	}
	
	public String getTag() {
		return (String)itemConfig.get("tag");
	}
	
	public String getSearchName() {
		return (String)itemConfig.get("searchName");
	}
	
	public HashMap getArgs() {
		return (HashMap)itemConfig.get("args");
	}
}
