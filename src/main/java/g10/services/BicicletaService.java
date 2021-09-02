package g10.services;

import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import g10.entities.Bicicleta;

public class BicicletaService {
	
	private BicicletaService() {}

	private static String regexUuid = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
	private static Pattern p = Pattern.compile(regexUuid);
	private static Pattern numeros = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static Bicicleta validarPostBicicleta(String body) {
		Gson gson = new Gson();
		Bicicleta bicicleta = gson.fromJson(body, Bicicleta.class);
		if (numeros.matcher(bicicleta.getAno()).matches()) {
			Bicicleta BikeNova = new Bicicleta(bicicleta.getMarca(), bicicleta.getModelo(), bicicleta.getAno(),
					bicicleta.getNumero());

			return BikeNova ;

		} else {
			return null;
		}
	}

	public static Bicicleta acharBicicletaPorId(String id, List<Bicicleta> bicicletas) {
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

	public static void atualizarListaBicicletas(Bicicleta bicicletaAtualizada, List<Bicicleta> bicicletas) {
		for (int i = 0; i < bicicletas.size(); i++) {
			if (bicicletas.get(i).getId().equals(bicicletaAtualizada.getId()))
				bicicletas.set(i, bicicletaAtualizada);
		}
	}

	public static void deletarBicicleta(Bicicleta bicicleta, List<Bicicleta> bicicletas) {
		for (int i = 0; i < bicicletas.size(); i++) {
			if (bicicletas.get(i).getId().equals(bicicleta.getId()))
				bicicletas.get(i).setStatus("excluída");
		}
	}

}
