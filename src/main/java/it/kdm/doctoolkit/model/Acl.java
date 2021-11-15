package it.kdm.doctoolkit.model;

import it.kdm.doctoolkit.model.path.ICIFSObject;

import java.util.ArrayList;
import java.util.List;

public class Acl extends GenericObject {

    private String parentNodeId = "";
	private String parentDescription = "";

	public static enum aclRights {

		FullAccess(0), NormalAccess(1), ReadOnly(2), ViewProfile(3), CreateOnly(4), CreateReadAccess(5);

		private final int value;

		private aclRights(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	private List<String> profileList = new ArrayList<>();

	public Acl() {

	}

	public Acl(String utenteGruppo, aclRights diritti) {
		setUtenteGruppo(utenteGruppo);
		int val = diritti.getValue();
		this.setProperty("RIGHTS", String.valueOf(val));
	}

	public String getProfilo(){

		if( this.profileList.size()>0 )
		{
			return this.profileList.get(0);
		}

		return null;
	}
	public List<String> getProfileList() {
		return profileList;
	}

	public void setProfileList(List<String> profileList) {
		this.profileList = profileList;
	}


	public String getUtenteGruppo() {
		return this.getProperty("USERORGROUP");
	}
	public void setUtenteGruppo(String utenteGruppo) {
		this.setProperty("USERORGROUP", utenteGruppo);
	}

	public String getActorType() {
		return this.getProperty("ACTOR_TYPE");
	}
	public void setActorType(String actorType) {
		this.setProperty("ACTOR_TYPE", actorType);
	}

	public String getDescription() {
		return this.getProperty("ACTOR_DESCRIPTION");
	}
	public void setDescription(String actorDescription) {
		this.setProperty("ACTOR_DESCRIPTION", actorDescription);
	}

	public String getParentDescription() {
		return parentDescription;
	}
	public void setParentDescription(String description) {
		parentDescription = description;
	}

	public String getParentNodeId() {
		return parentNodeId;
	}
	public void setParentNodeId(String id) {
		parentNodeId = id;
	}
	public int getDiritti() throws NumberFormatException {

		if( this.getProperty("RIGHTS") ==null ) {

			String prof = this.getProfileList().get(0);
			if("fullAccess".equals(prof)){
				return 0;
			}else if("normalAccess".equals(prof)){
				return 1;
			}else{
				return 2;
			}
		}
		return Integer.parseInt(this.getProperty("RIGHTS"));
	}
	public void setDiritti(String diritti) {
		int val = Integer.parseInt(diritti);
		this.setProperty("RIGHTS", String.valueOf(val));
	}
	
	
	
 @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Acl))return false;
        Acl otherMyClass = (Acl)other;
        if(this.getUtenteGruppo().equals(otherMyClass.getUtenteGruppo())) return true;
        else return false;
    }
	
	
	

	@Override
	protected void initProperties() {
		

	}

}
