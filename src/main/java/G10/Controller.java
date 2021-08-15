package G10;

import io.javalin.http.Context;

public class Controller {

	private Controller() {}
	
	public static void getTest(Context ctx) {
		String test = ctx.pathParam("test");
		ctx.result(test + " " + test + " " + test );
		ctx.status(200);
	}
	
	public static void getRoot(Context ctx) {
		ctx.status(200);
		ctx.result("Isto é um eco, digite algo a mais no caminho.");
	}
	
}
