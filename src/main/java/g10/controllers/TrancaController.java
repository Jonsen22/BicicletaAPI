package g10.controllers;

import g10.entities.Bicicleta;
import g10.entities.Tranca;
import g10.entities.TrancaStatus;
import g10.services.BicicletaService;
import g10.services.TotemService;
import g10.services.TrancaService;
import g10.util.JsonHelper;
import io.javalin.http.Context;

public class TrancaController {
	private TrancaController() {}
	
	private static final String DADOS_INVALIDOS = "Dados inválidos";
	private static final String DADOS_CADASTRADOS = "Dados cadastrados";
	private static final String NAO_ENCONTRADO = "Não encontrado";
	
	public static void getTranca(Context ctx) {
		ctx.result(TrancaService.getAllTrancas());
	}

	public static void postTranca(Context ctx) {
		String body = ctx.body();
		Tranca validateResponse = TrancaService.validarPostTranca(body);
		if (validateResponse != null) {
			ctx.status(200);
			ctx.json(validateResponse);
		} else {
			ctx.status(422);
			ctx.result(JsonHelper.jsonCodigo("-", "422", DADOS_INVALIDOS));
		}
	}

	public static void getTrancaById(Context ctx) {
		String id = ctx.pathParam("id");
		Tranca result = TrancaService.acharTrancaPorId(id);
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
		Tranca temp = TrancaService.acharTrancaPorId(id);
		if (temp != null) {
			Tranca atualizada = TrancaService.atualizarTranca(body, temp);

			TrancaService.atualizarListaTrancas(atualizada);
			// Atualizar na Rede de Totems		
			TotemService.atualizarTrancaRede(atualizada);	
			ctx.status(200);
			ctx.result(atualizada.toString());

		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void deleteTranca(Context ctx) {
		String id = ctx.pathParam("id");
		Tranca temp = TrancaService.acharTrancaPorId(id);
		if (temp != null) {
			if (temp.getBicicleta() == null) {
				TrancaService.deletarTranca(temp);
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
		Tranca tranca = TrancaService.acharTrancaPorId(id);
		if (tranca != null) {	
			Bicicleta temp = BicicletaService.getTrancaBicicleta(tranca.getBicicleta());	
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
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(id);
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
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1]);
		if (trancaProcurada != null) {
			trancaProcurada.setStatus(TrancaStatus.LIVRE.getStatus());
			TotemService.addTrancaRede(Ids[0], trancaProcurada);
			
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
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1]);
		if (trancaProcurada != null) {
			if (trancaProcurada.getBicicleta() == null) {
				if (trancaProcurada.getStatus().equals(TrancaStatus.REPARO_SOLICITADO.getStatus())) {
					TotemService.deleteTrancaRede(trancaProcurada);					
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
}
