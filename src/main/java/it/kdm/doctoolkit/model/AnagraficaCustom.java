package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;

public class AnagraficaCustom extends ICIFSObject {

	
	public String getEnte() {
		return this.getProperty("COD_ENTE");
	}
	public void setEnte(String ente) {
		this.setProperty("COD_ENTE", ente);
	}

	public String getAoo() {
		return this.getProperty("COD_AOO"); 
	}
	public void setAoo(String aoo) {
		this.setProperty("COD_AOO", aoo); 
	}

    public boolean getEnabled() {
		if (this.getProperty("ENABLED").equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}
	public void setEnabled(boolean enabled) {
		this.setProperty("ENABLED", enabled ? "true" : "false");
	}


	@Override
	protected void initProperties() {
		this.setProperty("COD_ENTE", "");
		this.setProperty("COD_AOO", "");
		this.setProperty("ENABLED", "true");
	}

	@Override
	protected String getComputedName() {
		return getProperty("name");
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public String getID() {
		return getProperty("id");
	}

	@Override
	public String getType() {
		return getProperty("id").split("@")[1];
	}

	@Override
	public String getVersionID() {
		//TODO: Da implementare
		return null;
	}

	@Override
	public String getAbstract() {
		//TODO: E' giusto?
		return getProperty("name");
	}
}
