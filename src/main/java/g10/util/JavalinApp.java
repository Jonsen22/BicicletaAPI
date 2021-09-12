package g10.util;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import g10.controllers.BicicletaController;
import g10.controllers.TotemController;
import g10.controllers.TrancaController;
import io.javalin.Javalin;

public class JavalinApp {
	
	private static final String BICICLETA_ID = "/bicicleta/:id";
	private static final String TRANCA_ID = "/tranca/:id";
	
	private Javalin app =
			Javalin.create(config -> config.defaultContentType = "application/json")
			.routes(() -> {				
				//bicicleta
				path("/bicicleta", () -> get(BicicletaController::getBicicleta));
				path("/bicicleta", () -> post(BicicletaController::postBicicleta));
				path(BICICLETA_ID, () -> get(BicicletaController::getBicicletaById));
				path(BICICLETA_ID, () -> put(BicicletaController::putBicicleta));
				path(BICICLETA_ID, () -> delete(BicicletaController::deleteBicicleta));
				path("/bicicleta/:id/status/:acao", () -> post(BicicletaController::postBicicletaAlterarStatus));
				path("/bicicleta/integrarNaRede", () -> post(BicicletaController::postBicicletaNaRede));
				path("/bicicleta/retirarDaRede", () -> post(BicicletaController::postBicicletaRetirarRede));
				
				//totem
				path("/totem", () -> get(TotemController::getTotem));
				path("/totem", () -> post(TotemController::postTotem));
				path("/totem/:id", () -> put(TotemController::putTotem));
				path("/totem/:id", () -> delete(TotemController::deleteTotem));
				path("/totem/:id/trancas", () -> get(TotemController::getTotemTrancas));
				path("/totem/:id/bicicletas", () -> get(TotemController::getTotemBicicletas));
				
				//tranca
				path("/tranca", () -> get(TrancaController::getTranca));
				path("/tranca", () -> post(TrancaController::postTranca));
				path(TRANCA_ID, () -> get(TrancaController::getTrancaById));
				path(TRANCA_ID, () -> put(TrancaController::putTranca));
				path(TRANCA_ID, () -> delete(TrancaController::deleteTranca));
				path("/tranca/:id/bicicleta", () -> get(TrancaController::getTrancaBicicleta));
				path("/tranca/:id/status/:acao", () -> post(TrancaController::postTrancaAlterarStatus));
				path("/tranca/integrarNaRede", () -> post(TrancaController::postTrancaNaRede));
				path("/tranca/retirarDaRede", () -> post(TrancaController::postTrancaRetirarRede));
			});
	
	public void start(int port) {
		this.app.start(port);
	}
	
	public void stop() {
		this.app.stop();
	}
}
