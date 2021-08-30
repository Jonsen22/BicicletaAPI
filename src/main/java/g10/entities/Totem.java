package g10.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

public class Totem {
	private String id;
	private String localizacao;
	private List<Bicicleta> bicicletas;
	private List<Tranca> trancas;

	public Totem(String localizacao) {
		generateId();
		this.localizacao = localizacao;
		this.bicicletas = new ArrayList<Bicicleta>();
		this.trancas = new ArrayList<Tranca>();
	}

	public List<Bicicleta> getBicicletas() {
		return bicicletas;
	}
	
	public void addBicicleta(Bicicleta bicicleta) {
		this.bicicletas.add(bicicleta);
	}

	public void setBicicletas(List<Bicicleta> bicicletas) {
		this.bicicletas = bicicletas;
	}

	public List<Tranca> getTrancas() {
		return trancas;
	}
	
	public void addTranca(Tranca tranca) {
		this.trancas.add(tranca);
	}

	public void setTrancas(List<Tranca> trancas) {
		this.trancas = trancas;
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

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
