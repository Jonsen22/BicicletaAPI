package g10.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import g10.entities.Totem;

public class TotemService {
	
	private TotemService() {}

	private static String regexUuid = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static Pattern p = Pattern.compile(regexUuid);

	public static Totem validarPostTotem(String body) {
		Gson gson = new Gson();
		Totem totem = gson.fromJson(body, Totem.class);
		Totem totemNovo = new Totem(totem.getLocalizacao());

		return totemNovo;
	}

	public static String imprimirTotem(Totem totem) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> localizacao = new LinkedHashMap<>();
		localizacao.put("localizacao", totem.getLocalizacao());
		return gson.toJson(localizacao);
	}

	public static Totem acharTotemPorId(String id, List<Totem> totems) {
		Totem temp = null;
		if (p.matcher(id).matches() == true) {
			temp = totems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null);
		}
		return temp;
	}

	public static Totem atualizarTotem(String body, Totem totem) {
		Gson gson = new Gson();
		Totem totemAtualizado = gson.fromJson(body, Totem.class);
		if (totemAtualizado.getLocalizacao() == null) {
			totemAtualizado.setLocalizacao(totem.getLocalizacao());
		}
		totemAtualizado.setBicicletas(totem.getBicicletas());
		totemAtualizado.setTrancas(totem.getTrancas());
		totemAtualizado.setId(totem.getId());

		return totemAtualizado;
	}

	public static void atualizarListaTotems(Totem totemAtualizado, List<Totem> totems) {
		for (int i = 0; i < totems.size(); i++) {
			if (totems.get(i).getId() == totemAtualizado.getId())
				totems.set(i, totemAtualizado);
		}
	}

	public static List<Totem> deletarTotem(String id, List<Totem> totems) {
		for (int i = 0; i < totems.size(); i++) {
			if (totems.get(i).getId().equals(id)) {
				totems.remove(i);
			}
		}
		return totems;
	}

}
