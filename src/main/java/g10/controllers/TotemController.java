package g10.controllers;

import java.util.List;

import g10.entities.Bicicleta;
import g10.entities.Totem;
import g10.entities.Tranca;
import g10.services.TotemService;
import g10.util.JsonHelper;
import io.javalin.http.Context;

public class TotemController {
	private TotemController() {}
	
	private static final String NAO_ENCONTRADO = "Não encontrado";
	
	public static void getTotem(Context ctx) {
		ctx.result(TotemService.getAllTotems());

	}

	public static void postTotem(Context ctx) {
		String body = ctx.body();
		Totem validateResponse = TotemService.validarPostTotem(body);
		TotemService.addTotem(validateResponse);
		ctx.status(200);
		ctx.result(TotemService.imprimirTotem(validateResponse));
	}

	public static void putTotem(Context ctx) {
		String id = ctx.pathParam("id");
		String body = ctx.body();
		Totem temp = TotemService.acharTotemPorId(id);
		if (temp != null) {
			Totem atualizado = TotemService.atualizarTotem(body, temp);
			TotemService.atualizarListaTotems(atualizado);
			ctx.status(200);
			ctx.result(atualizado.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}

	}

	public static void deleteTotem(Context ctx) {

		String id = ctx.pathParam("id");
		Totem temp = TotemService.acharTotemPorId(id);
		if (temp != null) {
			if (temp.getTrancas().isEmpty()) {
				TotemService.deletarTotem(id);
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

	public static void getTotemTrancas(Context ctx) /*passar para service*/ {
		String id = ctx.pathParam("id");
		List<Tranca> temp = null;
		Totem totemProcurado = TotemService.getTrancas(id);
		if (totemProcurado != null) {
			temp = totemProcurado.getTrancas();
			ctx.status(200);
			ctx.result(temp.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}

	public static void getTotemBicicletas(Context ctx) /*passar para service*/ {
		String id = ctx.pathParam("id");
		List<Bicicleta> temp = null;
		Totem totemProcurado = TotemService.getBicicletas(id);
		if (totemProcurado != null) {
			temp = totemProcurado.getBicicletas();
			ctx.status(200);
			ctx.result(temp.toString());
		} else {
			ctx.status(404);
			ctx.result(JsonHelper.jsonCodigo(id, "404", NAO_ENCONTRADO));
		}
	}
}
