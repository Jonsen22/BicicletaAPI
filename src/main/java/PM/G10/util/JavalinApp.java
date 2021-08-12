package PM.G10.util;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
import PM.G10.Controller;

public class JavalinApp {
	private Javalin app =
			Javalin.create(config -> config.defaultContentType = "application/json")
			.routes(() -> {
				path("/:test", () -> get(Controller::getTest));
				path("/", () -> get(Controller::getRoot));
			});
	
	public void start(int port) {
		this.app.start(port);
	}
	
	public void stop() {
		this.app.stop();
	}
}
