package g10.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import g10.entities.Bicicleta;
import g10.entities.BicicletaStatus;
import g10.entities.Totem;
import g10.entities.Tranca;
import g10.entities.TrancaStatus;

public class BicicletaService {
	
	private BicicletaService() {}

	private static List<Bicicleta> bicicletas = new ArrayList<Bicicleta>();
	private static String regexUuid = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static Pattern p = Pattern.compile(regexUuid);
	private static Pattern numeros = Pattern.compile("-?\\d+(\\.\\d+)?");
	
	public static String getAllBicicletas() {
		return bicicletas.toString();
	}

	public static Bicicleta validarPostBicicleta(String body) {
		Gson gson = new Gson();
		Bicicleta bicicleta = gson.fromJson(body, Bicicleta.class);
		if (numeros.matcher(bicicleta.getAno()).matches()) {
			Bicicleta BikeNova = new Bicicleta(bicicleta.getMarca(), bicicleta.getModelo(), bicicleta.getAno(),
					bicicleta.getNumero());
			bicicletas.add(BikeNova);
			return BikeNova ;

		} else {
			return null;
		}
	}

	public static Bicicleta acharBicicletaPorId(String id) {
		Bicicleta temp = null;
		if (p.matcher(id).matches() == true) {
			temp = bicicletas.stream().filter(bicicleta -> id.equals(bicicleta.getId())).findAny().orElse(null);
		}
		return temp;
	}

	public static Bicicleta atualizarBicicleta(String body, Bicicleta bicicleta) {
		Gson gson = new Gson();
		Bicicleta bicicletaAtualizada = gson.fromJson(body, Bicicleta.class);
		if (bicicletaAtualizada.getMarca() == null)
			bicicletaAtualizada.setMarca(bicicleta.getMarca());
		if (bicicletaAtualizada.getModelo() == null)
			bicicletaAtualizada.setModelo(bicicleta.getModelo());
		if (bicicletaAtualizada.getAno() == null)
			bicicletaAtualizada.setAno(bicicleta.getAno());
		if (bicicletaAtualizada.getNumero() == null)
			bicicletaAtualizada.setNumero(bicicleta.getNumero());
		if (bicicletaAtualizada.getStatus() == null)
			bicicletaAtualizada.setStatus(bicicleta.getStatus());
		if (bicicletaAtualizada.getId() == null)
			bicicletaAtualizada.setId(bicicleta.getId());

		return bicicletaAtualizada;
	}

	public static void atualizarListaBicicletas(Bicicleta bicicletaAtualizada) {
		for (int i = 0; i < bicicletas.size(); i++) {
			if (bicicletas.get(i).getId().equals(bicicletaAtualizada.getId()))
				bicicletas.set(i, bicicletaAtualizada);
		}
	}

	public static void deletarBicicleta(Bicicleta bicicleta) {
		for (int i = 0; i < bicicletas.size(); i++) {
			if (bicicletas.get(i).getId().equals(bicicleta.getId()))
				bicicletas.get(i).setStatus("excluída");
		}
	}

	public static Bicicleta getTrancaBicicleta(String idTranca) {
		return bicicletas.stream().filter(bicicleta -> idTranca.equals(bicicleta.getId()))
				.findFirst().orElse(null);
	}
	
	public static void cleanBicicletas() {
		bicicletas.clear();
	}
	
	public static int sizeBicicletas() {
		return bicicletas.size();
	}
	
	public static Bicicleta bicicletaUnicaMock() {
		bicicletas.clear();
		Bicicleta bicicletaT = new Bicicleta("teste", "teste2", "2000", 3);
		bicicletaT.setStatus(BicicletaStatus.APOSENTADA.getStatus());
		bicicletas.add(bicicletaT);
		return bicicletaT;
	}
	
	public static Object addMock(String objeto) {
		bicicletas.clear();
		TrancaService.clearTrancas();
		TotemService.clearTotems();
		Bicicleta bicicletaT = new Bicicleta("teste3", "teste4", "2000", 3);
		bicicletas.add(bicicletaT);
		Tranca trancaT = new Tranca(1, "Esquina1", "2000", "n sei");
		trancaT.setBicicleta(bicicletaT.getId());
		TrancaService.addTranca(trancaT);
		Totem totemT = new Totem("Esquina1");
		totemT.addBicicleta(bicicletaT);
		totemT.addTranca(trancaT);
		TotemService.addTotem(totemT);
		if (objeto.equals("bicicleta")) {
			return bicicletas.get(bicicletas.size() - 1);
		} else if (objeto.equals("tranca")) {
			return TrancaService.getTranca(TrancaService.sizeTrancas() - 1);
		} else if (objeto.equals("totem")) {
			return TotemService.getTotem(TotemService.sizeTotems() - 1);
		} else {
			return null;
		}
	}

	public static String bicicletaRedeMock() {
		Bicicleta bicicletaT = new Bicicleta("test5e", "teste6", "2000", 3);
		bicicletas.add(bicicletaT);
		Tranca trancaT = new Tranca(1, "Esquina2", "2000", "n sei");
		trancaT.setStatus(TrancaStatus.LIVRE.getStatus());
		TrancaService.addTranca(trancaT);
		Totem totemT = new Totem("Esquina2");
		totemT.addTranca(trancaT);
		TotemService.addTotem(totemT);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> items = new LinkedHashMap<>();

		items.put("idTranca", trancaT.getId());
		items.put("idBicicleta", bicicletaT.getId());

		return gson.toJson(items);
	}



}
