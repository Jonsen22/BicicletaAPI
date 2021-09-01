package g10.entities;

import java.util.UUID;

import com.google.gson.Gson;

public class Bicicleta {
	private String id; // UUID
	private String marca;
	private String modelo;
	private String ano;
	private Integer numero;
	private String status;

	public Bicicleta(String marca, String modelo, String ano, Integer numero) {
		generateId();
		this.marca = marca;
		this.modelo = modelo;
		this.ano = ano;
		this.numero = numero;
		this.status = "Nova";
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void generateId() {
		if (id == null) {
			UUID uuid = UUID.randomUUID();
			this.id = uuid.toString();
		}
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
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
