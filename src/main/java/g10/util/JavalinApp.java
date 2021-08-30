package g10.util;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import java.util.List;

import g10.Controller;
import g10.entities.Bicicleta;
import io.javalin.Javalin;

public class JavalinApp {
	
	private Javalin app =
			Javalin.create(config -> config.defaultContentType = "application/json")
			.routes(() -> {				
				//bicicleta
				path("/bicicleta", () -> get(Controller::getBicicleta));
				path("/bicicleta", () -> post(Controller::postBicicleta));
				path("/bicicleta/:id", () -> get(Controller::getBicicletaById));
				path("/bicicleta/:id", () -> put(Controller::putBicicleta));
				path("/bicicleta/:id", () -> delete(Controller::deleteBicicleta));
				path("/bicicleta/:id/status/:acao", () -> post(Controller::postBicicletaAlterarStatus));
				path("/bicicleta/integrarNaRede", () -> post(Controller::postBicicletaNaRede));
				path("/bicicleta/retirarDaRede", () -> post(Controller::postBicicletaRetirarRede));
				
				//totem
				path("/totem", () -> get(Controller::getTotem));
				path("/totem", () -> post(Controller::postTotem));
				path("/totem/:id", () -> put(Controller::putTotem));
				path("/totem/:id", () -> delete(Controller::deleteTotem));
				path("/totem/:id/trancas", () -> get(Controller::getTotemTrancas));
				path("/totem/:id/bicicletas", () -> get(Controller::getTotemBicicletas));
				
				//tranca
				path("/tranca", () -> get(Controller::getTranca));
				path("/tranca", () -> post(Controller::postTranca));
				path("/tranca/:id", () -> get(Controller::getTrancaById));
				path("/tranca/:id", () -> put(Controller::putTranca));
				path("/tranca/:id", () -> delete(Controller::deleteTranca));
				path("/tranca/:id/bicicleta", () -> get(Controller::getTrancaBicicleta));
				path("/tranca/:id/status/:acao", () -> post(Controller::postTrancaAlterarStatus));
				path("/tranca/integrarNaRede", () -> post(Controller::postTrancaNaRede));
				path("/tranca/retirarDaRede", () -> post(Controller::postTrancaRetirarRede));
			});
	
	public void start(int port) {
		this.app.start(port);
	}
	
	public void stop() {
		this.app.stop();
	}
}
