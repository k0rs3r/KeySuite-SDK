package it.kdm.doctoolkit.model;

public class Ordinamento {

	public enum orderByEnum {ASC,DESC}
	
	 public Ordinamento()
     { 
     }

     public Ordinamento(String NomeCampo, orderByEnum tipo)
     {
         this.NomeCampo = NomeCampo;
         this.tipo = tipo;
     }
     private String NomeCampo = "";

     public String getNomeCampo() {
		return NomeCampo;
	}

	public void setNomeCampo(String nomeCampo) {
		NomeCampo = nomeCampo;
	}

	public orderByEnum getTipo() {
		return tipo;
	}

	public void setTipo(orderByEnum tipo) {
		this.tipo = tipo;
	}
	public orderByEnum tipo = orderByEnum.ASC;
}
