package it.kdm.orchestratore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ColFasc {
	private String custom_fields;
	@JsonProperty("abstract")
	private String abstr;
	private Boolean enabled;
	@JsonProperty("@type")
	private String type;
	private String id_immobile;
	private String rights;
	@JsonProperty("COD_FISC_PG")
	private String codFiscPg;
	private String cf_persona;
	@JsonProperty("COD_FISC_PF")
	private String codFiscPf;
	private String progressivo_padre;
	private String classifica;
	@JsonProperty("DES_TITOLARIO")
	private String desTitolario;
	@JsonProperty("COD_ENTE")
	private String codEnte;
	private String anno;
	private String num_fascicolo;
	private String progr_fascicolo;
	@JsonProperty("COD_AOO")
	private String codAoo;
	private String cf_azienda;
	private String nome;
	private String progressivo;
	public String getCustom_fields() {
		return custom_fields;
	}
	public void setCustom_fields(String custom_fields) {
		this.custom_fields = custom_fields;
	}
	public String getAbstr() {
		return abstr;
	}
	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId_immobile() {
		return id_immobile;
	}
	public void setId_immobile(String id_immobile) {
		this.id_immobile = id_immobile;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getCodFiscPg() {
		return codFiscPg;
	}
	public void setCodFiscPg(String codFiscPg) {
		this.codFiscPg = codFiscPg;
	}
	public String getCf_persona() {
		return cf_persona;
	}
	public void setCf_persona(String cf_persona) {
		this.cf_persona = cf_persona;
	}
	public String getCodFiscPf() {
		return codFiscPf;
	}
	public void setCodFiscPf(String codFiscPf) {
		this.codFiscPf = codFiscPf;
	}
	public String getProgressivo_padre() {
		return progressivo_padre;
	}
	public void setProgressivo_padre(String progressivo_padre) {
		this.progressivo_padre = progressivo_padre;
	}
	public String getClassifica() {
		return classifica;
	}
	public void setClassifica(String classifica) {
		this.classifica = classifica;
	}
	public String getDesTitolario() {
		return desTitolario;
	}
	public void setDesTitolario(String desTitolario) {
		this.desTitolario = desTitolario;
	}
	public String getCodEnte() {
		return codEnte;
	}
	public void setCodEnte(String codEnte) {
		this.codEnte = codEnte;
	}
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
	public String getNum_fascicolo() {
		return num_fascicolo;
	}
	public void setNum_fascicolo(String num_fascicolo) {
		this.num_fascicolo = num_fascicolo;
	}
	public String getProgr_fascicolo() {
		return progr_fascicolo;
	}
	public void setProgr_fascicolo(String progr_fascicolo) {
		this.progr_fascicolo = progr_fascicolo;
	}
	public String getCodAoo() {
		return codAoo;
	}
	public void setCodAoo(String codAoo) {
		this.codAoo = codAoo;
	}
	public String getCf_azienda() {
		return cf_azienda;
	}
	public void setCf_azienda(String cf_azienda) {
		this.cf_azienda = cf_azienda;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getProgressivo() {
		return progressivo;
	}
	public void setProgressivo(String progressivo) {
		this.progressivo = progressivo;
	}
	
}
