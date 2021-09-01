package g10.entities;

public enum TrancaStatus {
	LIVRE("Livre"), 
	NOVA("Nova"),
	OCUPADA("Ocupada"),
	REPARO_SOLICITADO("Reparo solicitado"),
	EM_REPARO("Em reparo"), 
	APOSENTADA("Aposentada");

	private String status;

	TrancaStatus(String string) {
		this.status = string;
	}

	public String getStatus() {
		return status;
	}
}
