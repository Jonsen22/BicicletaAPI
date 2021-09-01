package g10.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelper {
	public static String jsonCodigo(String id, String codigo, String mensagem) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Map<String, String> items = new LinkedHashMap<>();

		items.put("id", id);
		items.put("codigo", codigo);
		items.put("mensagem", mensagem);

		return gson.toJson(items);
	}
	
	public static String[] jsonParseTrancaBicicleta(String body) {

		@SuppressWarnings("deprecation")
		JsonParser parser = new JsonParser();
		@SuppressWarnings("deprecation")
		JsonElement rootNode = parser.parse(body);
		JsonObject details = rootNode.getAsJsonObject();
		String IdBicicleta = details.get("idBicicleta").getAsString();
		String IdTranca = details.get("idTranca").getAsString();
		String[] stringArray = { IdBicicleta, IdTranca };
		return stringArray;
	}

	public static String[] jsonParseTrancaTotem(String body) {

		@SuppressWarnings("deprecation")
		JsonParser parser = new JsonParser();
		@SuppressWarnings("deprecation")
		JsonElement rootNode = parser.parse(body);
		JsonObject details = rootNode.getAsJsonObject();
		String IdTotem = details.get("idTotem").getAsString();
		String IdTranca = details.get("idTranca").getAsString();
		String[] stringArray = { IdTotem, IdTranca };
		return stringArray;
	}
}
