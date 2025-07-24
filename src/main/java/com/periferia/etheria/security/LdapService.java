package com.periferia.etheria.security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.exception.UserException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LdapService {

	private static final String TENANT_ID = "TU_TENANT_ID"; //Identificador del directorio Entra ID de la empresa
	private static final String CLIENT_ID = "TU_CLIENT_ID"; //	ID de la aplicación registrada que usarás para acceder
	private static final String CLIENT_SECRET = "TU_CLIENT_SECRET"; // Secreto de autenticación (tipo contraseña de app)
	private static final String TOKEN_ENDPOINT = "https://login.microsoftonline.com/" + TENANT_ID + "/oauth2/v2.0/token";
	private static final String GRAPH_API_URL = "https://graph.microsoft.com/v1.0/users/"; //Endpoint de graph API fija para todos los TENANT_ID Se debe agregar a la url el {email}
	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, String> authenticate(String email) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());

		try {
			String accessToken = obtenerAccessToken();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(GRAPH_API_URL + email))
					.header("Authorization", "Bearer " + accessToken)
					.header("Accept", "application/json")
					.GET()
					.build();

			HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				Map<String, Object> userData = objectMapper.readValue(response.body(), Map.class);

				Map<String, String> userAttributes = new HashMap<>();
				userAttributes.put("id", (String) userData.get("id"));
				userAttributes.put("displayName", (String) userData.get("displayName"));
				userAttributes.put("mail", (String) userData.get("mail"));
				userAttributes.put("userPrincipalName", (String) userData.get("userPrincipalName"));
				return userAttributes;

			} else if (response.statusCode() == 404) {
				throw new UserException("Usuario no encontrado en Entra ID", 404, response.body());
			} else {
				throw new UserException("Error al consultar Graph API", response.statusCode(), response.body());
			}

		} catch (UserException e) {
			throw e;
		} catch (Exception e) {
			throw new UserException("Error inesperado al autenticar con Entra ID: " + e.getMessage(), 500, e.getMessage());
		}
	}


	private String obtenerAccessToken() throws Exception {
		String form = "client_id=" + CLIENT_ID +
				"&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default" +
				"&client_secret=" + CLIENT_SECRET +
				"&grant_type=client_credentials";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(TOKEN_ENDPOINT))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(form))
				.build();

		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

		if (response.statusCode() != 200) {
			throw new UserException("No se pudo obtener token de Entra ID", response.statusCode(), response.body());
		}

		Map<String, Object> json = objectMapper.readValue(response.body(), Map.class);
		return (String) json.get("access_token");
	}

}
