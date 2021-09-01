package g10.entities;

import java.util.UUID;

import com.google.gson.Gson;

public class Tranca {
	private String id;
	private String bicicleta;
	private Integer numero;
	private String localizacao;
	private String anoDeFabricacao;
	private String modelo;
	private String status;

	public Tranca(Integer numero, String localizacao, String anoDeFabricacao, String modelo) {
		generateId();
		this.numero = numero;
		this.localizacao = localizacao;
		this.anoDeFabricacao = anoDeFabricacao;
		this.modelo = modelo;
		this.status = "Nova";
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void generateId() {
		if (id == null) {
			UUID uuid = UUID.randomUUID();
			this.id = uuid.toString();
		}
	}

	public String getBicicleta() {
		return bicicleta;
	}

	public void setBicicleta(String bicicleta) {
		this.bicicleta = bicicleta;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getAnoDeFabricacao() {
		return anoDeFabricacao;
	}

	public void setAnoDeFabricacao(String anoDeFabricacao) {
		this.anoDeFabricacao = anoDeFabricacao;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
