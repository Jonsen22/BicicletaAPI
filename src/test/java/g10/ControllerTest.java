package g10;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import g10.entities.Bicicleta;
import g10.entities.BicicletaStatus;
import g10.entities.Totem;
import g10.entities.Tranca;
import g10.services.BicicletaService;
import g10.services.TotemService;
import g10.services.TrancaService;
import g10.util.JavalinApp;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class ControllerTest {
	private static JavalinApp app = new JavalinApp();
//	private Context ctx = mock(Context.class);

	@BeforeAll
	static void init() {
		app.start(7010);
	}

	@AfterAll
	static void afterAll() {
		app.stop();
	}

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
		String id = ((Bicicleta) BicicletaService.addMock("bicicleta")).getId();
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/bicicleta/"+id)
				.asJson();
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void putBicicletaCorrect() {
		Bicicleta test = (Bicicleta) BicicletaService.addMock("bicicleta");
		HttpResponse<String> response = Unirest.put("http://localhost:7010/bicicleta/"+test.getId())
				.body("{\"ano\":\"4555\"}")
				.asString();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	void deleteBicicletaCorrect() {
		Bicicleta test = BicicletaService.bicicletaUnicaMock();
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+test.getId())
				.asString();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void deleteBicicletaIncorrect403Presa() {
		Bicicleta test = (Bicicleta) BicicletaService.addMock("bicicleta");
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+test.getId())
				.asString();
		assertEquals(403, response.getStatus());
	}


	@Test
	void deleteBicicletaIncorret403Status() {
		Bicicleta test = (Bicicleta) BicicletaService.addMock("bicicleta");
		test.setStatus(BicicletaStatus.EM_USO.getStatus());
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/bicicleta/"+test.getId())
				.asString();
		assertEquals(403, response.getStatus());
	}
	
	
	@Test
	void postBicicletaNaRedeCorrect() {
		String ids = BicicletaService.bicicletaRedeMock();
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
		Bicicleta test = BicicletaService.bicicletaUnicaMock();
		String[] status = {"DISPONIVEL","EM_USO","REPARO_SOLICITADO","EM_REPARO","APOSENTADA"};
		for(int i = 0; i < status.length; i++) {
			HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/bicicleta/"+test.getId()+"/status/"+status[i])
					.asJson();
			assertEquals(200, response.getStatus());			
		}
	}
	
	@Test
	void postBicicletaAlterarStatusIncorrect422() {
		Bicicleta test =BicicletaService.bicicletaUnicaMock();
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
		String id = ((Tranca) BicicletaService.addMock("tranca")).getId();
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
	
	@Test
	void putTrancaCorrect() {
		Tranca test = (Tranca) BicicletaService.addMock("tranca");
		HttpResponse<String> response = Unirest.put("http://localhost:7010/tranca/"+test.getId())
				.body("{\"anoDeFabricacao\":\"2000\"}")
				.asString();
		assertEquals(200, response.getStatus());

	}
	
	
	@Test
	void getTrancaBicicletaCorrect() {
		Tranca test = (Tranca) BicicletaService.addMock("tranca");
		HttpResponse<String> response = Unirest.get("http://localhost:7010/tranca/"+test.getId()+"/bicicleta")
				.asString();
		assertEquals(200, response.getStatus());
	}

	@Test
	void getTrancaBicicletaIncorrect422() {

		HttpResponse<String> response = Unirest.get("http://localhost:7010/tranca/"+UUID.randomUUID()+"/bicicleta")
				.asString();
		assertEquals(422, response.getStatus());
	}
	
	@Test
	void deleteTrancaCorrect() {
		Tranca test = TrancaService.trancaUnicaMock();
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/tranca/"+test.getId())
				.asString();
		assertEquals(200, response.getStatus());
	}
	@Test
	void deleteTrancaIncorrect403() {
		Tranca test = TrancaService.trancaUnicaMock();
		test.setBicicleta(UUID.randomUUID().toString());
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/tranca/"+test.getId())
				.asString();
		assertEquals(403, response.getStatus());
	}
	
	@Test
	void postTrancaAlterarStatusCorrect() {
		Tranca test = TrancaService.trancaUnicaMock();
		String[] status = {"LIVRE","OCUPADA","REPARO_SOLICITADO","EM_REPARO","APOSENTADA"};
		for(int i = 0; i < status.length; i++) {
			HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/tranca/"+test.getId()+"/status/"+status[i])
					.asJson();
			assertEquals(200, response.getStatus());			
		}
	}
	@Test
	void postTrancaAlterarStatusIncorrect422() {
		Tranca test = TrancaService.trancaUnicaMock();

			HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/tranca/"+test.getId()+"/status/quebrada")
					.asJson();
			assertEquals(422, response.getStatus());			
	}
	@Test
	void postTrancaAlterarStatusIncorrect404() {
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/tranca/"+UUID.randomUUID()+"/status/quebrada")
				.asJson();
		assertEquals(404, response.getStatus());			
	}
	
	@Test 
	void getAllTotemsCorrect() {
		HttpResponse<JsonNode> response = Unirest.get("http://localhost:7010/totem")
				.header("accept", "application/json").asJson();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void postTotemCorrect() {
		Totem totemT = new Totem("teste3232");
		HttpResponse<JsonNode> response = Unirest.post("http://localhost:7010/totem").body(totemT).asJson();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void deleteTotemCorrect() {
		Totem test = TotemService.totemUnicoMock();
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/totem/"+test.getId())
				.asString();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void deleteTotemIncorrect403() {
		Totem test = TotemService.totemUnicoMock();
		test.addTranca(new Tranca(1, "teste3213","21312","dawdwadwa"));
		HttpResponse<String> response = Unirest.delete("http://localhost:7010/totem/"+test.getId())
				.asString();
		assertEquals(403, response.getStatus());
	}
	@Test
	void deleteObjectIncorrect404() {
		String[] obj = {"bicicleta", "tranca", "totem"};
		for(int i = 0; i < obj.length; i++) {
			HttpResponse<String> response = Unirest.delete("http://localhost:7010/"+obj[i]+"/"+UUID.randomUUID())
					.asString();
			assertEquals(404, response.getStatus());			
		}
	}
	
	@Test
	void putTotemCorrect() {
		Totem test = (Totem) BicicletaService.addMock("totem");
		HttpResponse<String> response = Unirest.put("http://localhost:7010/totem/"+test.getId())
				.body("{\"localizacao\":\"novo\"}")
				.asString();
		assertEquals(200, response.getStatus());
	}
	@Test
	void putTotemIncorrect404() {
		String[] obj = {"bicicleta", "tranca", "totem"};
		for(int i = 0; i < obj.length; i++) {
		HttpResponse<String> response = Unirest.put("http://localhost:7010/"+obj[i]+"/"+UUID.randomUUID())
				.body("{\"localizacao\":\"novo\"}")
				.asString();
		assertEquals(404, response.getStatus());
		}
	}
	
	@Test
	void getTotemTrancasCorrect() {
		Totem test = (Totem) BicicletaService.addMock("totem");
		HttpResponse<String> response = Unirest.get("http://localhost:7010/totem/"+test.getId()+"/trancas")
				.asString();
		assertEquals(200, response.getStatus());
	}
	
	
	@Test
	void getTotemBicicletasCorrect() {
		Totem test = (Totem) BicicletaService.addMock("totem");
		HttpResponse<String> response = Unirest.get("http://localhost:7010/totem/"+test.getId()+"/bicicletas")
				.asString();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	void getTotemBicicletasIncorrect404() {
		HttpResponse<String> response = Unirest.get("http://localhost:7010/totem/"+UUID.randomUUID()+"/bicicletas")
				.asString();
		assertEquals(404, response.getStatus());
	}
	
}