package it.kdm.doctoolkit.model;

import javax.activation.DataHandler;

public class DocerFile {

	public DataHandler content = null;

	public long size = 0;
	
	public DataHandler getContent() {
		return content;
	}
	public void setContent(DataHandler content) {
		this.content = content;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	

}
