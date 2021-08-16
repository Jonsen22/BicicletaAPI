package g10;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Console;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import g10.util.JavalinApp;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;


public class ControllerTest {
	private static JavalinApp app = new JavalinApp();
	
	@BeforeAll
	static void init() {
		app.start(7010);
	}
	
	@AfterAll
	static void afterAll() {
		app.stop();
	}
	
	@Test
	void getEchoTest() {
		HttpResponse response = Unirest.get("http://localhost:7010/xd").asString();
		assertEquals(200, response.getStatus());
		assertEquals("xd xd xd", response.getBody());
	}
	
	@Test
	void getRootTest() {
		HttpResponse response = Unirest.get("http://localhost:7010/").asString();
		assertEquals(200, response.getStatus());
		assertEquals("Isto é um eco, digite algo a mais no caminho.",response.getBody());
	}
}
