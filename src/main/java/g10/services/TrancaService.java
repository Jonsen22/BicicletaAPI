package g10.services;

import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import g10.entities.Tranca;

public class TrancaService {
	
	private TrancaService() {}

	private static String regexUuid = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static Pattern p = Pattern.compile(regexUuid);
	private static Pattern numeros = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static Tranca validarPostTranca(String body) {
		Gson gson = new Gson();
		Tranca tranca = gson.fromJson(body, Tranca.class);
		if (numeros.matcher(tranca.getAnoDeFabricacao()).matches()) {
			return tranca;

		} else {
			return null;
		}
	}

	public static Tranca acharTrancaPorId(String id, List<Tranca> trancas) {
		Tranca temp = null;
		if (p.matcher(id).matches() == true) {
			temp = trancas.stream().filter(tranca -> id.equals(tranca.getId())).findAny().orElse(null);
		}

		return temp;
	}

	public static Tranca atualizarTranca(String body, Tranca tranca) {
		Gson gson = new Gson();
		Tranca trancaAtualizada = gson.fromJson(body, Tranca.class);
		if (trancaAtualizada.getNumero() == null)
			trancaAtualizada.setNumero(tranca.getNumero());
		if (trancaAtualizada.getLocalizacao() == null)
			trancaAtualizada.setLocalizacao(tranca.getLocalizacao());
		if (trancaAtualizada.getAnoDeFabricacao() == null)
			trancaAtualizada.setAnoDeFabricacao(tranca.getAnoDeFabricacao());
		if (trancaAtualizada.getModelo() == null)
			trancaAtualizada.setModelo(tranca.getModelo());
		if (trancaAtualizada.getStatus() == null)
			trancaAtualizada.setStatus(tranca.getStatus());
		trancaAtualizada.setId(tranca.getId());

		return trancaAtualizada;
	}

	public static void atualizarListaTrancas(Tranca trancaAtualizada, List<Tranca> trancas) {
		for (int i = 0; i < trancas.size(); i++) {
			if (trancas.get(i).getId().equals(trancaAtualizada.getId()))
				trancas.set(i, trancaAtualizada);
		}
	}

	public static void deletarTranca(Tranca tranca, List<Tranca> trancas) {
		for (int i = 0; i < trancas.size(); i++) {
			if (trancas.get(i).getId().equals(tranca.getId()))
				trancas.get(i).setStatus("excluída");
		}
	}

	public static boolean trancaComBicicleta(List<Tranca> trancas, String id) {
		for (int i = 0; i < trancas.size(); i++) {
			if (trancas.get(i).getBicicleta().equals(id))
				return true;
		}
		return false;
	}

}
