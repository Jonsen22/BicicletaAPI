package g10.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import g10.entities.Bicicleta;
import g10.entities.Totem;
import g10.entities.Tranca;

public class TotemService {

	private TotemService() {
	}

	private static List<Totem> redeDeTotems = new ArrayList<Totem>();
	private static String regexUuid = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static Pattern p = Pattern.compile(regexUuid);

	public static String getAllTotems() {
		return redeDeTotems.toString();
	}

	public static Totem validarPostTotem(String body) {
		Gson gson = new Gson();
		Totem totem = gson.fromJson(body, Totem.class);
		Totem totemNovo = new Totem(totem.getLocalizacao());

		return totemNovo;
	}

	public static void addTotem(Totem totem) {
		redeDeTotems.add(totem);
	}

	public static String imprimirTotem(Totem totem) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> localizacao = new LinkedHashMap<>();
		localizacao.put("localizacao", totem.getLocalizacao());
		return gson.toJson(localizacao);
	}

	public static Totem acharTotemPorId(String id) {
		Totem temp = null;
		if (p.matcher(id).matches() == true) {
			temp = redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null);
			redeDeTotems.add(temp);
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

	public static void atualizarListaTotems(Totem totemAtualizado) {
		for (int i = 0; i < redeDeTotems.size(); i++) {
			if (redeDeTotems.get(i).getId().equals(totemAtualizado.getId()))
				redeDeTotems.set(i, totemAtualizado);
		}
	}

	public static List<Totem> deletarTotem(String id) {
		redeDeTotems.removeIf(totem -> totem.getId().equals(id));
		return redeDeTotems;
	}

	public static void addBicicletaRede(String idTranca, Bicicleta bicicletaProcurada) {
		redeDeTotems.stream().filter(totem -> totem.getTrancas().stream().map(Tranca::getId).anyMatch(idTranca::equals))
				.findFirst().ifPresent(totem -> {

					totem.addBicicleta(bicicletaProcurada);
				});

	}

	public static void excluirBicicletaRede(Bicicleta bicicletaProcurada) {
		redeDeTotems.forEach(totem -> totem.getBicicletas().remove(bicicletaProcurada));
	}

	public static void atualizarBicicletaRede(Bicicleta atualizada) {
		redeDeTotems.stream().filter(
				totem -> totem.getBicicletas().stream().map(Bicicleta::getId).anyMatch(atualizada.getId()::equals))
				.findFirst().ifPresent(totem -> {
					for (int i = 0; i < totem.getTrancas().size(); i++) {
						if (totem.getBicicletas().get(i).getId().equals(atualizada.getId()))
							totem.getBicicletas().set(i, atualizada);
					}
				});

	}

	public static void addTrancaRede(String idTotem, Tranca trancaProcurada) {
		redeDeTotems.stream().filter(totem -> idTotem.equals(totem.getId())).findFirst()
				.ifPresent(totem -> totem.addTranca(trancaProcurada));
	}

	public static void deleteTrancaRede(Tranca trancaProcurada) {
		redeDeTotems.forEach(totem -> totem.getTrancas().remove(trancaProcurada));
	}

	public static void atualizarTrancaRede(Tranca atualizada) {
		redeDeTotems.stream()
				.filter(totem -> totem.getTrancas().stream().map(Tranca::getId).anyMatch(atualizada.getId()::equals))
				.findFirst().ifPresent(totem -> {
					for (int i = 0; i < totem.getTrancas().size(); i++) {
						if (totem.getTrancas().get(i).getId().equals(atualizada.getId()))
							totem.getTrancas().set(i, atualizada);
					}
				});
	}

	public static Totem getBicicletas(String id) {
		return redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null);
	}

	public static Totem getTrancas(String id) {
		return redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null);
	}

}
