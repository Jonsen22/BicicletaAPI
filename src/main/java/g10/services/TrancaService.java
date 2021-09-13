package g10.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import g10.entities.Tranca;

public class TrancaService {

	private TrancaService() {
	}

	private static List<Tranca> trancas = new ArrayList<Tranca>();
	private static String regexUuid = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static Pattern p = Pattern.compile(regexUuid);
	private static Pattern numeros = Pattern.compile("-?\\d+(\\.\\d+)?");
	private final static String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

	public static String getAllTrancas() {
		return trancas.toString();
	}

	public static Tranca validarPostTranca(String body) {
		Gson gson = new Gson();
		Tranca tranca = gson.fromJson(body, Tranca.class);
		if (numeros.matcher(tranca.getAnoDeFabricacao()).matches()) {
			Tranca trancaNova = new Tranca(tranca.getNumero(), tranca.getLocalizacao(), tranca.getAnoDeFabricacao(),
					tranca.getModelo());

			trancas.add(trancaNova);
			return trancaNova;

		} else {
			return null;
		}
	}

	public static Tranca acharTrancaPorId(String id) {
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

	public static void atualizarListaTrancas(Tranca trancaAtualizada) {
		for (int i = 0; i < trancas.size(); i++) {
			if (trancas.get(i).getId().equals(trancaAtualizada.getId()))
				trancas.set(i, trancaAtualizada);
		}
	}

	public static void deletarTranca(Tranca tranca) {
		for (int i = 0; i < trancas.size(); i++) {
			if (trancas.get(i).getId().equals(tranca.getId()))
				trancas.get(i).setStatus("excluída");
		}
	}

	public static boolean trancaComBicicleta(String id) {
		for (int i = 0; i < trancas.size(); i++) {
			if (trancas.get(i).getBicicleta().equals(id))
				return true;
		}
		return false;
	}

	public static void addTranca(Tranca tranca) {
		trancas.add(tranca);
	}

	public static Tranca getTranca(int i) {
		return trancas.get(i);
	}

	public static void clearTrancas() {
		trancas.clear();
	}

	public static int sizeTrancas() {
		return trancas.size();
	}

	public static Tranca trancaUnicaMock() {
		trancas.clear();
		Tranca trancaT = new Tranca(4, "teste323", "5000", "teste5122");
		trancas.add(trancaT);
		return trancaT;
	}

	public static String emailBody(String id, String localizacao, String tipoDeAcao) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		return "{\"email\":\"gabriel.jansen222@gmail.com\",\"mensagem\":\"" + tipoDeAcao
				+ " de Tranca na rede de Totems \\nTranca: " + id + " \\nHoras: " + formatter.format(date)
				+ " \\nLocal: " + localizacao + "\"}";
	}

}
