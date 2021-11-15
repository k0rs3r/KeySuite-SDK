package it.kdm.orchestratore.entity;

import java.util.HashMap;
import java.util.Map;

public class Feed {
	private String guid="";
	private String id="";
	private String name="";
	private String html="";
	private Integer num=-1;
	private String url="";
    private String title="";
    private String type="";
    private String cssClass = "";
    private String annoFascicolo;
    private String progressivoFascicolo;
    private String classifica;
    private String descClassifica;
	private String[] roles;
	public Map<String,String> props;


    /*
	 * "id": "7",
        "text": "Grapes",
        "html": "This is <b>Grapes</b> html!",
        "num": 22,
        "url": "http://en.wikipedia.org/wiki/Grape",
        "title": "This is test title and link"
	 * */
	public Feed(){
		this.guid = java.util.UUID.randomUUID().toString();
		this.props = new HashMap<>();
	}
	
	
	public Feed(String id, String text, String html, Integer num, String url, String title) {
		this.id = id;
		this.name = text;
		this.html = html;
		this.num = num;
		this.url = url;
		this.title = title;
	}


	

	public String getDescClassifica() {
		return descClassifica;
	}


	public void setDescClassifica(String descClassifica) {
		this.descClassifica = descClassifica;
	}


	public String getAnnoFascicolo() {
		return annoFascicolo;
	}


	public void setAnnoFascicolo(String annoFascicolo) {
		this.annoFascicolo = annoFascicolo;
	}


	public String getProgressivoFascicolo() {
		return progressivoFascicolo;
	}


	public void setProgressivoFascicolo(String progressivoFascicolo) {
		this.progressivoFascicolo = progressivoFascicolo;
	}


	public String getClassifica() {
		return classifica;
	}


	public void setClassifica(String classifica) {
		this.classifica = classifica;
	}


	public String getHtml() {
		return html;
	}



	public void setHtml(String html) {
		this.html = html;
	}



	public Integer getNum() {
		return num;
	}



	public void setNum(Integer num) {
		this.num = num;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}
	

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String text) {
		this.name = text;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}


	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
}
