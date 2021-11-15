package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;

public class Titolario extends ICIFSObject
{

    public static final String TYPE = "titolario";

    public String getCodiceTitolario()
	{
		return this.getProperty("COD_TITOLARIO");
	}

	public void setCodiceTitolario(String codiceTitolario)
	{
		this.setProperty("COD_TITOLARIO", codiceTitolario);
	}

	public boolean getEnabled()
	{
		if (this.getProperty("ENABLED").equalsIgnoreCase("true"))
		{
			return true;
		} else
		{
			return false;
		}
	}

	public void setEnabled(boolean enabled)
	{
		this.setProperty("ENABLED", enabled ? "true" : "false");
	}

	public String getDescrizione()
	{
		return this.getProperty("DES_TITOLARIO");
	}

	public void setDescrizione(String descrizione)
	{
		this.setProperty("DES_TITOLARIO", descrizione);
	}

	public String getEnte()
	{
		return this.getProperty("COD_ENTE");
	}

	public void setEnte(String ente)
	{
		this.setProperty("COD_ENTE", ente);
	}

	public String getAoo()
	{
		return this.getProperty("COD_AOO");
	}

	public void setAoo(String aoo)
	{
		this.setProperty("COD_AOO", aoo);
	}

	public String getParentClassifica()
	{
		return this.getProperty("PARENT_CLASSIFICA");
	}

	public void setParentClassifica(String parentClassifica)
	{
		this.setProperty("PARENT_CLASSIFICA", parentClassifica);
	}

	public String getClassifica()
	{
		return this.getProperty("CLASSIFICA");
	}

	public void setClassifica(String classifica)
	{
		this.setProperty("CLASSIFICA", classifica);
	}

	public String getPiano()
	{
		return this.getProperty("PIANO_CLASS");
	}

	public void setPiano(String piano)
	{
		this.setProperty("PIANO_CLASS", piano);
	}
/*
	@Override
	public String getFEName()
	{
		return this.getDescrizione();
	}
*/

	@Override
	public String getFEId()
	{
		return this.getClassifica();
	}

	

	@Override
	public String getFEDate()
	{
		return this.getProperty("MODIFIED");
	}

    @Override
    public String getFEAuthor() {
        return getProperty("CREATOR");
    }
	
	@Override
	protected void initProperties()
	{
		this.setProperty("COD_TITOLARIO", "");
		this.setProperty("ENABLED", "true");
		this.setProperty("DES_TITOLARIO", "");
		this.setProperty("COD_ENTE", "");
		this.setProperty("COD_AOO", "");
		this.setProperty("PARENT_CLASSIFICA", null);
		this.setProperty("CLASSIFICA", "");

	}

    @Override
    protected String getComputedName() {
        return getDescrizione();
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
        return getClassifica();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getVersionID() {
        //TODO: Da implementare
        return null;
    }

    @Override
    public String getAbstract() {
        //TODO: E' giusto?
        return getDescrizione();
    }
    @Override
    public String getMimeTypeCSS(){
    	return "glyphicon glyphicon-folder-close";
    }
}
