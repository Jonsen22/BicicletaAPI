package g10.controllers;

import g10.entities.Bicicleta;
import g10.entities.BicicletaStatus;
import g10.entities.Tranca;
import g10.entities.TrancaStatus;
import g10.services.BicicletaService;
import g10.services.TotemService;
import g10.services.TrancaService;
import g10.util.JsonHelper;
import io.javalin.http.Context;
import kong.unirest.Unirest;

public class BicicletaController {

	private BicicletaController() {
	}

	private static final String DADOS_INVALIDOS = "Dados inválidos";
	private static final String DADOS_CADASTRADOS = "Dados cadastrados";
	private static final String NAO_ENCONTRADO = "Não encontrado";

	public static void getBicicleta(Context ctx) {
		ctx.status(200);
		ctx.result(BicicletaService.getAllBicicletas());
	}

	public static void postBicicleta(Context ctx) {
		String body = ctx.body();
		Bicicleta validateResponse = BicicletaService.validarPostBicicleta(body);
		if (validateResponse != null) {
			ctx.status(200);
			ctx.json(validateResponse);
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo("-", "422", DADOS_INVALIDOS));
		}

	}

	public static void postBicicletaNaRede(Context ctx) {

		String body = ctx.body();
		String[] Ids = JsonHelper.jsonParseTrancaBicicleta(body);
		// Procura a Bicicleta e a tranca
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(Ids[0]);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1]);
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
					Unirest.post("https://g11-pm.herokuapp.com/devolucao")
							.body("{\"idTranca\":\" " + Ids[1] + " \",\"idBicicleta\":\"" + Ids[0] + " \"}").asJson();
				}
				bicicletaProcurada.setStatus(BicicletaStatus.DISPONIVEL.getStatus());
				trancaProcurada.setBicicleta(bicicletaProcurada.getId());
				trancaProcurada.setStatus(TrancaStatus.OCUPADA.getStatus());

				TotemService.addBicicletaRede(Ids[1], bicicletaProcurada);
				// enviar email
				String emailBody = BicicletaService.emailBody(bicicletaProcurada, trancaProcurada.getLocalizacao(),
						"Inclusão");
				Unirest.post("https://uniriobike.herokuapp.com/enviarEmail").body(emailBody).asJson()
						.ifFailure(response -> {
							ctx.result(JsonHelper.jsonCodigo(Ids[0], "500", response.getParsingError().toString()));
						}).ifSuccess(response -> {
							ctx.status(200);
							ctx.result(JsonHelper.jsonCodigo(Ids[0], "200", DADOS_CADASTRADOS));
						});

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
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(Ids[0]);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1]);
		if (trancaProcurada != null && bicicletaProcurada != null) {
			if (bicicletaProcurada.getStatus().equals(BicicletaStatus.REPARO_SOLICITADO.getStatus())
					&& trancaProcurada.getBicicleta().equals(bicicletaProcurada.getId())) {
				trancaProcurada.setStatus(TrancaStatus.LIVRE.getStatus());
				trancaProcurada.setBicicleta(null);

				TotemService.excluirBicicletaRede(bicicletaProcurada);

				// enviar email se falhar codigo de erro, se não sucesso ao cadastrar
				String emailBody = BicicletaService.emailBody(bicicletaProcurada, trancaProcurada.getLocalizacao(),
						"Retirada");
				Unirest.post("https://uniriobike.herokuapp.com/enviarEmail").body(emailBody).asJson()
						.ifFailure(response -> {
							ctx.result(JsonHelper.jsonCodigo(Ids[0], "500", response.getParsingError().toString()));
						}).ifSuccess(response -> {
							ctx.status(200);
							ctx.result(JsonHelper.jsonCodigo(Ids[0], "200", DADOS_CADASTRADOS));
						});
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
		Bicicleta result = BicicletaService.acharBicicletaPorId(id);
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
		Bicicleta temp = BicicletaService.acharBicicletaPorId(id);
		if (temp != null) {
			Bicicleta atualizada = BicicletaService.atualizarBicicleta(body, temp);
			BicicletaService.atualizarListaBicicletas(atualizada);
			TotemService.atualizarBicicletaRede(atualizada);

			ctx.status(200);
			ctx.result(atualizada.toString());

		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void deleteBicicleta(Context ctx) {
		String id = ctx.pathParam("id");
		Bicicleta temp = BicicletaService.acharBicicletaPorId(id);

		if (temp != null) {
			if (!TrancaService.trancaComBicicleta(id)) {
				if (temp.getStatus().equals(BicicletaStatus.APOSENTADA.getStatus())) {

					BicicletaService.deletarBicicleta(temp);
					ctx.status(200);
					ctx.result(JsonHelper.jsonCodigo(id, "200", "Bicicleta removida"));
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
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(id);
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
}
