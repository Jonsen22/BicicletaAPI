package g10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import g10.entities.Bicicleta;
import g10.entities.Tranca;
import g10.util.JavalinApp;
import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class ControllerTest {
	private static JavalinApp app = new JavalinApp();
	private Context ctx = mock(Context.class);

	@BeforeAll
	static void init() {
		app.start(7010);
	}

	@AfterAll
	static void afterAll() {
		app.stop();
	}

//	@Test
//	void getEchoTest() {
//		HttpResponse response = Unirest.get("http://localhost:7010/xd").asString();
//		assertEquals(200, response.getStatus());
//		assertEquals("xd xd xd", response.getBody());
//	}

	@Test
	void getAllBicicletasCorrect() {
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/bicicleta")
				.header("accept", "application/json").asJson();
		assertEquals(200, response.getStatus());

	}

	@Test
	void postBicicletaCorrect() {
		Bicicleta bicicleta = new Bicicleta("Caloi", "c500", "2010", 2);
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta").body(bicicleta).asJson();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void postBicicletaIncorrect422() {
		Bicicleta bicicleta = new Bicicleta("Caloi", "c500", "dawdwa", 2);
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta").body(bicicleta).asJson();
		assertEquals(422, response.getStatus());
	}

	@Test
	void getBicicletaByIdIncorrect404() {

		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/bicicleta/321321")
				.header("accept", "application/json").asJson();

		assertEquals(404, response.getStatus());
	}
	
	@Test 
	void getBicicletaByIdCorrect() {
		String id = ((Bicicleta) Controller.bicicletaAddMock("bicicleta")).getId();
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/bicicleta/"+id)
				.asJson();
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void putBicicletaCorrect() {
		Bicicleta test = (Bicicleta) Controller.bicicletaAddMock("bicicleta");
		test.setMarca("nsei");
		test.setAno("3000");
		HttpResponse<String> response = Unirest.put("http://localhost:7010/bicicleta/"+test.getId())
				.body(test.toString())
				.asString();
		assertEquals(200, response.getStatus());
		
		String jsonTest = test.toString();
		assertEquals(jsonTest, response.getBody());
	}
	
	@Test
	void putBicicletaIncorret404() {
		HttpResponse<String> response = Unirest.put("http://localhost:7010/bicicleta/"+UUID.randomUUID())
				.asString();
		assertEquals(404, response.getStatus());
	}

	@Test
	void deleteBicicletaCorrect() {
		Bicicleta test = Controller.bicicletaUnicaMock();
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+test.getId())
				.asString();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void deleteBicicletaIncorrect403Presa() {
		Bicicleta test = (Bicicleta) Controller.bicicletaAddMock("bicicleta");
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+test.getId())
				.asString();
		assertEquals(403, response.getStatus());
	}


//	@Test
//	void deleteBicicletaIncorret403Status() {
//		Bicicleta test = Controller.bicicletaAddMock();
//		test.setStatus(BicicletaStatus.EM_USO.getStatus());
//		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+test.getId())
//				.asString();
//		assertEquals(403, response.getStatus());
//	}
	
	@Test
	void deleteBicicletaIncorret404() {
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+UUID.randomUUID())
				.asString();
		assertEquals(404, response.getStatus());
	}
	
	@Test
	void postBicicletaNaRedeCorrect() {
		String ids = Controller.bicicletaRedeMock();
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta/integrarNaRede")
				.body(ids)
				.asJson();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void postBicicletaNaRedeIncorrect404() {
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta/integrarNaRede")
				.body("{\"idTranca\":\"6417b751-ad3a-4bcd-b4bc-1bb5fc9850b0\",\"idBicicleta\":\"614e3884-e95a-4b52-97f1-f9600e6723a7\"}")
				.asJson();
		
		assertEquals(404, response.getStatus());
	}
	
	@Test
	void postBicicletaAlterarStatusCorrect() {
		Bicicleta test = Controller.bicicletaUnicaMock();
		String[] status = {"DISPONIVEL","EM_USO","REPARO_SOLICITADO","EM_REPARO","APOSENTADA"};
		for(int i = 0; i < status.length; i++) {
			HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta/"+test.getId()+"/status/"+status[i])
					.asJson();
			assertEquals(200, response.getStatus());			
		}
	}
	
	@Test
	void postBicicletaAlterarStatusIncorrect422() {
		Bicicleta test = Controller.bicicletaUnicaMock();
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta/"+test.getId()+"/status/quebrada")
				.asJson();
		assertEquals(422, response.getStatus());
	}
	
	@Test
	void postBicicletaAlterarStatusIncorrect404() {
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta/"+UUID.randomUUID()+"/status/disponivel")
				.asJson();
		assertEquals(404, response.getStatus());
	}
	@Test
	void getAllTrancasCorrect() {
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/tranca")
				.header("accept", "application/json").asJson();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void postTrancaCorect() {
		Tranca trancaT = new Tranca(1, "teste","3000","nsei");
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/tranca").body(trancaT).asJson();
		assertEquals(200, response.getStatus());
	}
	@Test
	void postTrancaIncorect422() {
		Tranca trancaT = new Tranca(1, "teste","adwad","nsei");
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/tranca").body(trancaT).asJson();
		assertEquals(422, response.getStatus());
	}
	
	@Test 
	void getTrancaByIdCorrect() {
		String id = ((Tranca) Controller.bicicletaAddMock("tranca")).getId();
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/tranca/"+id)
				.asJson();
		
		assertEquals(200, response.getStatus());
	}
	@Test 
	void getTrancaByIdIncorrect404() {
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/tranca/3214123")
				.asJson();
		
		assertEquals(404, response.getStatus());
	}
}