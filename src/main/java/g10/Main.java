package g10;

import g10.util.JavalinApp;

public class Main {

	public static void main(String[] args) {
		JavalinApp app = new JavalinApp();
		app.start(getHerokuAssignedPort());
	}

	private static int getHerokuAssignedPort() {
		String herokuPort = System.getenv("PORT");
		if (herokuPort != null) {
			return Integer.parseInt(herokuPort);
		}
		return 7000;
	}
}