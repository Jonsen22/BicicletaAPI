package g10;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import g10.entities.Bicicleta;
import g10.entities.BicicletaStatus;
import g10.entities.Totem;
import g10.entities.Tranca;
import g10.entities.TrancaStatus;
import g10.services.BicicletaService;
import g10.services.TotemService;
import g10.services.TrancaService;
import g10.util.JsonHelper;
import io.javalin.http.Context;

public class Controller {

	private Controller() {

	}

	private static List<Bicicleta> bicicletas = new ArrayList<Bicicleta>();
	private static List<Tranca> trancas = new ArrayList<Tranca>();
	private static List<Totem> redeDeTotems = new ArrayList<Totem>();

	private static final String DADOS_INVALIDOS = "Dados inválidos";
	private static final String DADOS_CADASTRADOS = "Dados cadastrados";
	private static final String NAO_ENCONTRADO = "Não encontrado";

	public static void getBicicleta(Context ctx) {
		ctx.status(200);
		ctx.result(bicicletas.toString());
	}

	public static void postBicicleta(Context ctx) {
		String body = ctx.body();
		Bicicleta validateResponse = BicicletaService.validarPostBicicleta(body);
		if (validateResponse != null) {
			ctx.status(200);
			ctx.json(validateResponse);
			bicicletas.add(validateResponse);
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo("-", "422", DADOS_INVALIDOS));
		}

	}

	public static void postBicicletaNaRede(Context ctx) {

		String body = ctx.body();
		String[] Ids = JsonHelper.jsonParseTrancaBicicleta(body);
		// Procura a Bicicleta e a tranca
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(Ids[0], bicicletas);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
		// Identifica se o status está de acordo com a regra de negócio(Livre/Nova/Em
		// reparo)
		if (trancaProcurada != null && bicicletaProcurada != null) {
			String StatusBicicleta = bicicletaProcurada.getStatus();
			if (trancaProcurada.getStatus().equals(TrancaStatus.LIVRE.getStatus())
					&& (StatusBicicleta.equals(BicicletaStatus.NOVA.getStatus())
							|| StatusBicicleta.equals(BicicletaStatus.EM_REPARO.getStatus())
							|| StatusBicicleta.equals(BicicletaStatus.EM_USO.getStatus()))) {

				if (StatusBicicleta.equals(BicicletaStatus.EM_USO.getStatus())) {
					// Passa para UC04 - devolver bicicleta (A4)
				} else {

					bicicletaProcurada.setStatus(BicicletaStatus.DISPONIVEL.getStatus());
					trancaProcurada.setBicicleta(bicicletaProcurada.getId());
					trancaProcurada.setStatus(TrancaStatus.OCUPADA.getStatus());

					redeDeTotems.stream()
							.filter(totem -> totem.getTrancas().stream().map(Tranca::getId).anyMatch(Ids[1]::equals))
							.findFirst().ifPresent(totem -> {

								totem.addBicicleta(bicicletaProcurada);
							});
					// enviar email se falhar codigo de erro, se não sucesso ao cadastrar
					ctx.status(200);
					ctx.result(JsonHelper.jsonCodigo(Ids[0], "200", DADOS_CADASTRADOS));
				}
			} else {
				ctx.status(422);
				ctx.result(JsonHelper.jsonCodigo(Ids[0], "422", DADOS_INVALIDOS));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(Ids[0], "404", DADOS_INVALIDOS));
		}

	}

	public static void postBicicletaRetirarRede(Context ctx) {
		// procurar bicicleta na rede de totems e excluir
		String body = ctx.body();
		String[] Ids = JsonHelper.jsonParseTrancaBicicleta(body);
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(Ids[0], bicicletas);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
		if (trancaProcurada != null && bicicletaProcurada != null) {
			if (bicicletaProcurada.getStatus().equals(BicicletaStatus.REPARO_SOLICITADO.getStatus())
					&& trancaProcurada.getBicicleta().equals(bicicletaProcurada.getId())) {
				trancaProcurada.setStatus(TrancaStatus.LIVRE.getStatus());
				trancaProcurada.setBicicleta(null);

				redeDeTotems.forEach(totem -> totem.getBicicletas().remove(bicicletaProcurada));
				// enviar email se falhar codigo de erro, se não sucesso ao cadastrar
				ctx.status(200);
				ctx.result(JsonHelper.jsonCodigo(Ids[0], "200", DADOS_CADASTRADOS));
			} else {
				ctx.status(422);
				ctx.result(JsonHelper.jsonCodigo(Ids[0], "422",
						"Bicicleta não precisa de reparo ou não há nenhuma bicicleta na tranca"));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(Ids[0], "404", "Bicicleta ou Tranca não existe"));
		}
	}

	public static void getBicicletaById(Context ctx) {

		String id = ctx.pathParam("id");
		Bicicleta result = BicicletaService.acharBicicletaPorId(id, bicicletas);
		if (result != null) {
			ctx.status(200);
			ctx.result(result.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", "Id da bicicleta não existe"));
		}
	}

	public static void putBicicleta(Context ctx) {
		String id = ctx.pathParam("id");
		String body = ctx.body();
		Bicicleta temp = BicicletaService.acharBicicletaPorId(id, bicicletas);
		if (temp != null) {
			Bicicleta atualizada = BicicletaService.atualizarBicicleta(body, temp);
			// TODO checar o status se disponível procurar na rede de totens e atualizar
			BicicletaService.atualizarListaBicicletas(atualizada, bicicletas);
			redeDeTotems.stream().filter(
					totem -> totem.getBicicletas().stream().map(Bicicleta::getId).anyMatch(atualizada.getId()::equals))
					.findFirst().ifPresent(totem -> {
						for (int i = 0; i < totem.getTrancas().size(); i++) {
							if (totem.getBicicletas().get(i).getId().equals(atualizada.getId()))
								totem.getBicicletas().set(i, atualizada);
						}
					});
			ctx.status(200);
			ctx.result(atualizada.toString());

		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void deleteBicicleta(Context ctx) {
		String id = ctx.pathParam("id");
		Bicicleta temp = BicicletaService.acharBicicletaPorId(id, bicicletas);

		if (temp != null || trancas.isEmpty()) {
			if (!TrancaService.trancaComBicicleta(trancas, id)) {
				if (temp.getStatus().equals(BicicletaStatus.APOSENTADA.getStatus())) {

					BicicletaService.deletarBicicleta(temp, bicicletas);
					ctx.status(200);
					ctx.result(JsonHelper.jsonCodigo(id, "200", "Bicicleta removida"));
					// TODO excluir a bicicleta da rede
				} else {
					ctx.status(403);
					ctx.result(JsonHelper.jsonCodigo(id, "403",
							"Operação não pode ser conclúida, a bicicleta não está aposentada"));
				}
			} else {
				ctx.status(403);
				ctx.result(JsonHelper.jsonCodigo(id, "403", "Bicicleta está em uma tranca"));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}

	}

	public static void postBicicletaAlterarStatus(Context ctx) {
		String id = ctx.pathParam("id");
		String status = ctx.pathParam("acao").toUpperCase();
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(id, bicicletas);
		if (bicicletaProcurada != null) {
			switch (status) {
			case "DISPONIVEL":
				bicicletaProcurada.setStatus(BicicletaStatus.DISPONIVEL.getStatus());
				ctx.status(200);
				ctx.result(bicicletaProcurada.toString());
				break;
			case "EM_USO":
				bicicletaProcurada.setStatus(BicicletaStatus.EM_USO.getStatus());
				ctx.status(200);
				ctx.result(bicicletaProcurada.toString());
				break;
			case "REPARO_SOLICITADO":
				bicicletaProcurada.setStatus(BicicletaStatus.REPARO_SOLICITADO.getStatus());
				ctx.status(200);
				ctx.result(bicicletaProcurada.toString());
				break;
			case "EM_REPARO":
				bicicletaProcurada.setStatus(BicicletaStatus.EM_REPARO.getStatus());
				ctx.status(200);
				ctx.result(bicicletaProcurada.toString());
				break;
			case "APOSENTADA":
				bicicletaProcurada.setStatus(BicicletaStatus.APOSENTADA.getStatus());
				ctx.status(200);
				ctx.result(bicicletaProcurada.toString());
				break;
			default:
				ctx.status(422);
				ctx.result(JsonHelper.jsonCodigo(id, "422", DADOS_INVALIDOS));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void getTotem(Context ctx) {
		ctx.result(redeDeTotems.toString());

	}

	public static void postTotem(Context ctx) {
		String body = ctx.body();
		Totem validateResponse = TotemService.validarPostTotem(body);
		ctx.status(200);
		ctx.result(TotemService.imprimirTotem(validateResponse));
		redeDeTotems.add((Totem) validateResponse);

	}

	public static void putTotem(Context ctx) {
		String id = ctx.pathParam("id");
		String body = ctx.body();
		Totem temp = TotemService.acharTotemPorId(id, redeDeTotems);
		if (temp != null) {
			Totem atualizado = TotemService.atualizarTotem(body, temp);
			TotemService.atualizarListaTotems(atualizado, redeDeTotems);
			ctx.status(200);
			ctx.result(atualizado.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}

	}

	public static void deleteTotem(Context ctx) {

		String id = ctx.pathParam("id");
		Totem temp = TotemService.acharTotemPorId(id, redeDeTotems);
		if (temp != null) {
			if (temp.getTrancas().isEmpty()) {
				TotemService.deletarTotem(id, redeDeTotems);
				ctx.status(200);
				ctx.result(JsonHelper.jsonCodigo(id, "200", "Totem removido"));
			} else {
				ctx.status(403);
				ctx.result(JsonHelper.jsonCodigo(id, "403",
						"O Totem possui trancas cadastradas, não é possível concluir a operação"));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void getTotemTrancas(Context ctx) {
		String id = ctx.pathParam("id");
		List<Tranca> temp = null;
		Totem totemProcurado = redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null);
		if (totemProcurado != null) {
			temp = totemProcurado.getTrancas();
			ctx.status(200);
			ctx.result(temp.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void getTotemBicicletas(Context ctx) {
		String id = ctx.pathParam("id");
		List<Bicicleta> temp = null;
		Totem totemProcurado = redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null);
		if (totemProcurado != null) {
			temp = totemProcurado.getBicicletas();
			ctx.status(200);
			ctx.result(temp.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void getTranca(Context ctx) {
		ctx.result(trancas.toString());
	}

	public static void postTranca(Context ctx) {
		String body = ctx.body();
		Tranca validateResponse = TrancaService.validarPostTranca(body);
		if (validateResponse != null) {
			ctx.status(200);
			ctx.json(validateResponse);
			trancas.add(validateResponse);
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo("-", "422", DADOS_INVALIDOS));
		}
	}

	public static void getTrancaById(Context ctx) {
		String id = ctx.pathParam("id");
		Tranca result = TrancaService.acharTrancaPorId(id, trancas);
		if (result != null) {
			ctx.status(200);
			ctx.result(result.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void putTranca(Context ctx) {
		String id = ctx.pathParam("id");
		String body = ctx.body();
		Tranca temp = TrancaService.acharTrancaPorId(id, trancas);
		if (temp != null) {
			Tranca atualizada = TrancaService.atualizarTranca(body, temp);

			TrancaService.atualizarListaTrancas(atualizada, trancas);
			// Atualizar na Rede de Totems
			redeDeTotems.stream().filter(
					totem -> totem.getTrancas().stream().map(Tranca::getId).anyMatch(atualizada.getId()::equals))
					.findFirst().ifPresent(totem -> {
						for (int i = 0; i < totem.getTrancas().size(); i++) {
							if (totem.getTrancas().get(i).getId().equals(atualizada.getId()))
								totem.getTrancas().set(i, atualizada);
						}
					});
			ctx.status(200);
			ctx.result(atualizada.toString());

		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void deleteTranca(Context ctx) {
		String id = ctx.pathParam("id");
		Tranca temp = TrancaService.acharTrancaPorId(id, trancas);
		if (temp != null) {
			if (temp.getBicicleta() == null) {
				TrancaService.deletarTranca(temp, trancas);
				ctx.status(200);
				ctx.result(JsonHelper.jsonCodigo(id, "200", "Tranca removida"));
			} else {
				ctx.status(403);
				ctx.result(JsonHelper.jsonCodigo(id, "403",
						"A tranca possui uma bicicleta guardada, não é possível concluir a operação"));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void getTrancaBicicleta(Context ctx) {
		String id = ctx.pathParam("id");
		Tranca tranca = TrancaService.acharTrancaPorId(id, trancas);
		Bicicleta temp = bicicletas.stream().filter(bicicleta -> tranca.getBicicleta().equals(bicicleta.getId()))
				.findFirst().orElse(null);

		if (tranca != null) {
			if (temp != null) {
				ctx.status(200);
				ctx.result(temp.toString());
			} else {
				ctx.status(404);
				ctx.result(JsonHelper.jsonCodigo(id, "404", "Bicicleta não encontrada"));
			}
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo(id, "422", "Id da tranca inválido"));
		}

	}

	public static void postTrancaAlterarStatus(Context ctx) {
		String id = ctx.pathParam("id");
		String status = ctx.pathParam("acao").toUpperCase();
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(id, trancas);
		if (trancaProcurada != null) {
			switch (status) {
			case "LIVRE":
				trancaProcurada.setStatus(TrancaStatus.LIVRE.getStatus());
				ctx.status(200);
				ctx.result(trancaProcurada.toString());
				break;
			case "OCUPADA":
				trancaProcurada.setStatus(TrancaStatus.OCUPADA.getStatus());
				ctx.status(200);
				ctx.result(trancaProcurada.toString());
				break;
			case "REPARO_SOLICITADO":
				trancaProcurada.setStatus(TrancaStatus.REPARO_SOLICITADO.getStatus());
				ctx.status(200);
				ctx.result(trancaProcurada.toString());
				break;
			case "EM_REPARO":
				trancaProcurada.setStatus(TrancaStatus.EM_REPARO.getStatus());
				ctx.status(200);
				ctx.result(trancaProcurada.toString());
				break;
			case "APOSENTADA":
				trancaProcurada.setStatus(TrancaStatus.APOSENTADA.getStatus());
				ctx.status(200);
				ctx.result(trancaProcurada.toString());
				break;
			default:
				ctx.status(422);
				ctx.result(JsonHelper.jsonCodigo(id, "422", DADOS_INVALIDOS));
			}
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void postTrancaNaRede(Context ctx) {

		String body = ctx.body();
		String[] Ids = JsonHelper.jsonParseTrancaTotem(body);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
		if (trancaProcurada != null) {
			trancaProcurada.setStatus(TrancaStatus.LIVRE.getStatus());
			redeDeTotems.stream().filter(totem -> Ids[0].equals(totem.getId())).findFirst()
					.ifPresent(totem -> totem.addTranca(trancaProcurada));
			// enviar email se falhar codigo de erro, se não sucesso ao cadastrar
			ctx.status(200);
			ctx.result(JsonHelper.jsonCodigo(Ids[1], "200", DADOS_CADASTRADOS));
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo(Ids[1], "422", DADOS_INVALIDOS));
		}
	}

	public static void postTrancaRetirarRede(Context ctx) {
		String body = ctx.body();
		String[] Ids = JsonHelper.jsonParseTrancaTotem(body);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
		if (trancaProcurada != null) {
			if (trancaProcurada.getBicicleta() == null) {
				if (trancaProcurada.getStatus().equals(TrancaStatus.REPARO_SOLICITADO.getStatus())) {
					redeDeTotems.forEach(totem -> totem.getTrancas().remove(trancaProcurada));
					// enviar email se falhar codigo de erro, se não sucesso ao cadastrar
					ctx.status(200);
					ctx.result(JsonHelper.jsonCodigo(Ids[1], "200", DADOS_CADASTRADOS));
				} else {
					ctx.result(JsonHelper.jsonCodigo(Ids[1], "403",
							"A tranca precisa estar aposentada ou com reparo solicitado"));
				}
			} else {
				ctx.status(403);
				ctx.result(JsonHelper.jsonCodigo(Ids[1], "403", "Tranca possui uma bicicleta presa nela"));
			}
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo(Ids[1], "422", DADOS_INVALIDOS));
		}
	}

	public static Bicicleta bicicletaUnicaMock() {
		Bicicleta bicicletaT = new Bicicleta("teste", "teste2", "2000", 3);
		bicicletaT.setStatus(BicicletaStatus.APOSENTADA.getStatus());
		bicicletas.add(bicicletaT);
		return bicicletaT;
	}

	public static Object bicicletaAddMock(String objeto) {
		Bicicleta bicicletaT = new Bicicleta("teste3", "teste4", "2000", 3);
		bicicletas.add(bicicletaT);
		Tranca trancaT = new Tranca(1, "Esquina1", "2000", "n sei");
		trancaT.setBicicleta(bicicletaT.getId());
		trancas.add(trancaT);
		Totem totemT = new Totem("Esquina1");
		totemT.addBicicleta(bicicletaT);
		totemT.addTranca(trancaT);
		redeDeTotems.add(totemT);
		if (objeto.equals("bicicleta")) {
			return bicicletas.get(bicicletas.size() - 1);
		} else if (objeto.equals("tranca")) {
			return trancas.get(trancas.size() - 1);
		} else if (objeto.equals("totem")) {
			return redeDeTotems.get(redeDeTotems.size() - 1);
		} else {
			return null;
		}
	}

	public static String bicicletaRedeMock() {
		Bicicleta bicicletaT = new Bicicleta("test5e", "teste6", "2000", 3);
		bicicletas.add(bicicletaT);
		Tranca trancaT = new Tranca(1, "Esquina2", "2000", "n sei");
		trancaT.setStatus(TrancaStatus.LIVRE.getStatus());
		trancas.add(trancaT);
		Totem totemT = new Totem("Esquina2");
		totemT.addTranca(trancaT);
		redeDeTotems.add(totemT);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> items = new LinkedHashMap<>();

		items.put("idTranca", trancaT.getId());
		items.put("idBicicleta", bicicletaT.getId());

		return gson.toJson(items);
	}

}
