package g10.entities;

public enum BicicletaStatus {
	DISPONIVEL("Disponível"), 
	EM_USO("Em uso"), 
	REPARO_SOLICITADO("Reparo solicitado"),
	EM_REPARO("Em reparo"), 
	APOSENTADA("Aposentada"),
	NOVA("Nova");

	private String status;

	BicicletaStatus(String string) {
		this.status = string;
	}

	public String getStatus() {
		return status;
	}
}
