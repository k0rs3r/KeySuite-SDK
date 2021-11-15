package it.kdm.orchestratore.entity;

import com.google.common.base.Strings;

import java.io.Serializable;

public class CacheActor implements Serializable {

    private String id;
    private String email;
    private String name;
    private String language;
    private String country;
    private String prefix = "";
    private String type;
    private String codEnte;
    private String codAOO;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnte(){
        return "ente".equals(type);
    }

    public boolean isAOO(){
        return "aoo".equals(type);
    }

    public String getCodEnte() {
        return codEnte;
    }

    public void setCodEnte(String codEnte) {
        this.codEnte = codEnte;
    }

    public String getCodAOO() {
        return codAOO;
    }

    public void setCodAOO(String codAOO) {
        this.codAOO = codAOO;
    }

    public CacheActor(){
        super();
    }

    public String getId() {
        return id;
    }

    public String getPrefixedId() {
        return prefix+id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        if (Strings.isNullOrEmpty(name) || name.equals(id))
            return id;
        else
            return String.format("%s (%s)",name,id);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPrefix(){
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
