package g10;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

	private static List<Bicicleta> bicicletas = new ArrayList<Bicicleta>();
	private static List<Tranca> trancas = new ArrayList<Tranca>();
	private static List<Totem> redeDeTotems = new ArrayList<Totem>();

	private static Bicicleta bicicletaM = new Bicicleta("Caloi", "c200", "2000", 1, "Nova");
	private static Bicicleta bicicletaM2 = new Bicicleta("Caloi", "c500", "2010", 2, "Nova");
	private static Tranca trancaM = new Tranca(1, "Avenida Princesa Isabel", "2005", "nsei", "Nova");
	private static Totem totemM = new Totem("Avenida Princesa Isabel");

	public static void getBicicleta(Context ctx) {
		totemM.addBicicleta(bicicletaM);
		totemM.addTranca(trancaM);
		trancaM.setBicicleta(bicicletaM.getId());
		bicicletas.add(bicicletaM);
		bicicletas.add(bicicletaM2);
		trancas.add(trancaM);
		redeDeTotems.add(totemM);
		try {
			ctx.result(bicicletas.toString());

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void postBicicleta(Context ctx) {

//		https://gitlab.com/luizfcneto1/alguel-bicicleta-servicos-externos/-/tree/master/src/main/java
		try {

			String body = ctx.body();
			Bicicleta validateResponse = BicicletaService.validarPostBicicleta(body);
			ctx.json(validateResponse);
			bicicletas.add(validateResponse);

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}

	}

	public static void postBicicletaNaRede(Context ctx) {
		try {
			String body = ctx.body();
			String[] Ids = JsonHelper.jsonParseTrancaBicicleta(body);
			// Procura a Bicicleta e a tranca
			Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(Ids[0], bicicletas);
			Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
			// Identifica se o status está de acordo com a regra de negócio(Livre/Nova/Em
			// reparo)
			if (trancaProcurada.getStatus() == TrancaStatus.LIVRE.getStatus()
					&& (bicicletaProcurada.getStatus() == BicicletaStatus.NOVA.getStatus()
							|| bicicletaProcurada.getStatus() == BicicletaStatus.EM_REPARO.getStatus())) {
				if (trancaProcurada != null && bicicletaProcurada != null) {
					bicicletaProcurada.setStatus(BicicletaStatus.DISPONIVEL.getStatus());
					trancaProcurada.setBicicleta(bicicletaProcurada.getId());
					trancaProcurada.setStatus(TrancaStatus.OCUPADA.getStatus());
					
//					for (int i = 0; i < trancas.size(); i++) {
//						if (trancas.get(i).getId().equals(Ids[1])) {
//							trancas.get(i).setBicicleta(bicicletaProcurada.getId());
//							trancas.get(i).setStatus(TrancaStatus.OCUPADA.getStatus());
//						}
//					}

					// atualiza bicicleta na lista de bicicletas
//					bicicletas.stream().filter(bicicleta -> bicicleta.getId().equals(Ids[0])).findFirst()
//							.ifPresent(bicicleta -> bicicleta.setStatus(BicicletaStatus.DISPONIVEL.getStatus()));

					// Adiciona a bicicleta na rede(lista) de totems
					redeDeTotems.stream()
							.filter(totem -> totem.getTrancas().stream().map(Tranca::getId).anyMatch(Ids[1]::equals))
							.findFirst().ifPresent(totem -> {
//								for (int i = 0; i < totem.getTrancas().size(); i++) {
//									if (totem.getTrancas().get(i).getId().equals(Ids[1]))
//										totem.getTrancas().get(i).setStatus(TrancaStatus.OCUPADA.getStatus());
//								}
								totem.addBicicleta(bicicletaProcurada);
							});

					ctx.result(JsonHelper.jsonCodigo(Ids[0], Integer.toString(ctx.status()), "Dados cadastrados"));
				} else {
					ctx.result(JsonHelper.jsonCodigo(Ids[0], "422", "Dados inválidos"));
				}
			} else {
				ctx.result(JsonHelper.jsonCodigo(Ids[0], "422", "Dados inválidos"));
			}

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void postBicicletaRetirarRede(Context ctx) {
		// procurar bicicleta na rede de totems e excluir
		String body = ctx.body();
		String[] Ids = JsonHelper.jsonParseTrancaBicicleta(body);
		Bicicleta bicicletaProcurada = BicicletaService.acharBicicletaPorId(Ids[0], bicicletas);
		Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
		bicicletaProcurada.setStatus(BicicletaStatus.APOSENTADA.getStatus());
		
	}

	public static void getBicicletaById(Context ctx) {
		try {
			String id = ctx.pathParam("id");
			String result = BicicletaService.acharBicicletaPorId(id, bicicletas).toString();
			ctx.result(result);
		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void putBicicleta(Context ctx) {
		try {
			String id = ctx.pathParam("id");
			String body = ctx.body();
			Bicicleta temp = BicicletaService.acharBicicletaPorId(id, bicicletas);
			if (temp != null) {
				temp = BicicletaService.atualizarBicicleta(body, temp);
				// TODO checar o status se disponível procurar na rede de totens e atualizar
//				BicicletaService.atualizarListaBicicletas(atualizada, bicicletas);
//				redeDeTotems.stream().filter(totem -> totem.getBicicletas().stream().map(Bicicleta::getId)
//						.anyMatch(atualizada.getId()::equals)).findFirst().ifPresent(totem -> {
//							for (int i = 0; i < totem.getTrancas().size(); i++) {
//								if (totem.getBicicletas().get(i).getId().equals(atualizada.getId()))
//									totem.getBicicletas().set(i, atualizada);
//							}
//						});
				ctx.result(temp.toString());

			} else {
				ctx.result(JsonHelper.jsonCodigo(id, "404", "Não encontrado"));
			}

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void deleteBicicleta(Context ctx) {
		String id = ctx.pathParam("id");
		Bicicleta temp = BicicletaService.acharBicicletaPorId(id, bicicletas);

		if (temp != null) {
			if (!TrancaService.trancaComBicicleta(trancas, id)) {
				if (temp.getStatus() == BicicletaStatus.APOSENTADA.getStatus()) {

					BicicletaService.deletarBicicleta(temp, bicicletas);
					ctx.result(JsonHelper.jsonCodigo(id, "200", "Bicicleta removida"));
					// TODO excluir a bicicleta da rede
				} else {
					ctx.result(JsonHelper.jsonCodigo(id, "403",
							"Operação não pode ser conclúida, a bicicleta não está aposentada"));
				}
			} else {
				ctx.result(JsonHelper.jsonCodigo(id, "403", "Bicicleta está em uma tranca"));
			}
		} else {
			ctx.result(JsonHelper.jsonCodigo(id, "404", "Não encontrado"));
		}

	}

	public static void postBicicletaAlterarStatus(Context ctx) {
		String id = ctx.pathParam("id");
		String status = ctx.pathParam("acao").toUpperCase();
		//TODO a fazer
		// alterar o status da bicicleta na lista de bicicletas
		// e alterar o status na bicicleta na rede de totems
	}

	public static void getTotem(Context ctx) {
		try {
			ctx.result(redeDeTotems.toString());

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void postTotem(Context ctx) {
		try {

			String body = ctx.body();
			Totem validateResponse = TotemService.validarPostTotem(body);
			ctx.result(TotemService.imprimirTotem(validateResponse));
			redeDeTotems.add((Totem) validateResponse);

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}

	}

	public static void putTotem(Context ctx) {
		try {
			String id = ctx.pathParam("id");
			String body = ctx.body();
			Totem temp = TotemService.acharTotemPorId(id, redeDeTotems);
			if (temp != null) {
				Totem atualizado = TotemService.atualizarTotem(body, temp);
				TotemService.atualizarListaTotems(atualizado, redeDeTotems);
				ctx.result(atualizado.toString());
			} else {
				ctx.result(JsonHelper.jsonCodigo(id, "404", "Não encontrado"));
			}

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void deleteTotem(Context ctx) {
		try {
			String id = ctx.pathParam("id");
			Totem temp = TotemService.acharTotemPorId(id, redeDeTotems);
			if (temp != null) {
				if (temp.getTrancas().isEmpty()) {
					TotemService.deletarTotem(id, redeDeTotems);
					ctx.result(JsonHelper.jsonCodigo(id, "200", "Totem removido"));
				} else {
					ctx.result(JsonHelper.jsonCodigo(id, "403",
							"O Totem possui trancas cadastradas, não é possível concluir a operação"));
				}
			} else {
				ctx.result(JsonHelper.jsonCodigo(id, "404", "Não encontrado"));
			}
		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void getTotemTrancas(Context ctx) {
		try {
			String id = ctx.pathParam("id");
			List<Tranca> temp = null;
			temp = redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null).getTrancas();
			ctx.result(temp.toString());

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void getTotemBicicletas(Context ctx) {
		try {
			String id = ctx.pathParam("id");
			List<Bicicleta> temp = null;
			temp = redeDeTotems.stream().filter(totem -> id.equals(totem.getId())).findAny().orElse(null)
					.getBicicletas();
			ctx.result(temp.toString());

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void getTranca(Context ctx) {
		try {
			ctx.result(trancas.toString());

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void postTranca(Context ctx) {

		try {

			String body = ctx.body();
			Tranca validateResponse = TrancaService.validarPostTranca(body);
			ctx.json(validateResponse);
			trancas.add(validateResponse);

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void getTrancaById(Context ctx) {
		String id = ctx.pathParam("id");
		String result = TrancaService.acharTrancaPorId(id, trancas).toString();
		ctx.result(result);
	}

	public static void putTranca(Context ctx) {

		try {
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
				ctx.result(atualizada.toString());

			} else {
				ctx.result(JsonHelper.jsonCodigo(id, "404", "Não encontrado"));
			}
		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void deleteTranca(Context ctx) {
		String id = ctx.pathParam("id");
		Tranca temp = TrancaService.acharTrancaPorId(id, trancas);
		if (temp != null) {
			if (temp.getBicicleta() == null) {
				TrancaService.deletarTranca(temp, trancas);
				ctx.result(JsonHelper.jsonCodigo(id, "200", "Tranca removida"));
			} else {
				ctx.result(JsonHelper.jsonCodigo(id, "403",
						"A tranca possui uma bicicleta guardada, não é possível concluir a operação"));
			}
		} else {
			ctx.result(JsonHelper.jsonCodigo(id, "404", "Não encontrado"));
		}
	}

	public static void getTrancaBicicleta(Context ctx) {
		String id = ctx.pathParam("id");
		String idBicicleta = TrancaService.acharTrancaPorId(id, trancas).getBicicleta();
		Bicicleta temp = bicicletas.stream().filter(bicicleta -> idBicicleta.equals(bicicleta.getId())).findFirst()
				.orElse(null);

		ctx.result(temp.toString());
	}

	public static void postTrancaAlterarStatus(Context ctx) {
		String id = ctx.pathParam("id");
		String status = ctx.pathParam("acao").toUpperCase();
	}

	public static void postTrancaNaRede(Context ctx) {
		// proximo a implementar
		try {
			String body = ctx.body();
			String[] Ids = JsonHelper.jsonParseTrancaTotem(body);
			Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);
			if (trancaProcurada != null) {
				trancaProcurada.setStatus(TrancaStatus.LIVRE.getStatus());
				redeDeTotems.stream().filter(totem -> Ids[0].equals(totem.getId())).findFirst()
						.ifPresent(totem -> totem.addTranca(trancaProcurada));
				// enviar email se falhar codigo de erro, se não sucesso ao cadastrar
				ctx.result(JsonHelper.jsonCodigo(Ids[1], Integer.toString(ctx.status()), "Dados cadastrados"));
			} else {
				ctx.result(JsonHelper.jsonCodigo(Ids[1], "422", "Dados Inválidos"));
			}

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

	public static void postTrancaRetirarRede(Context ctx) {
		try {
			String body = ctx.body();
			String[] Ids = JsonHelper.jsonParseTrancaTotem(body);
			Tranca trancaProcurada = TrancaService.acharTrancaPorId(Ids[1], trancas);

		} catch (Exception err) {
			ctx.json(err.getMessage());
			err.printStackTrace();
		}
	}

}
