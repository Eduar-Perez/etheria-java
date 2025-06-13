package com.periferia.etheria;

import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.impl.RecordRepositoryImpl;
import com.periferia.etheria.repository.impl.RecordUserRepositoryImpl;
import com.periferia.etheria.repository.impl.UserRepositoryImpl;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.AgentQueryService;
import com.periferia.etheria.service.impl.AgentIAClientServiceImpl;
import com.periferia.etheria.service.impl.AgentQueryServiceImpl;
import com.periferia.etheria.service.impl.RecordServiceImpl;
import com.periferia.etheria.service.impl.UserServiceImpl;
import com.periferia.etheria.util.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ObjectMapper mapper = new ObjectMapper();
	private final UserServiceImpl userServiceImpl;
	private final AgentQueryService agentQueryService;
	private final RecordServiceImpl recordServiceImpl;

	public UserHandler() {
		String jwtSecret = System.getenv(Constants.JWT_SECRET);
		DBConfig dataBaseConnection = new DBConfig();
		UserRepositoryImpl userRepository = new UserRepositoryImpl(dataBaseConnection);
		RecordRepositoryImpl recordRepository = new RecordRepositoryImpl(dataBaseConnection);
		RecordUserRepositoryImpl recordUserRepositoryImpl = new RecordUserRepositoryImpl(dataBaseConnection);
		JwtService jwtService = new JwtService(jwtSecret);
		RecordServiceImpl recordService = new RecordServiceImpl(recordRepository, jwtService);
		AgentIAClientServiceImpl agenClient = new AgentIAClientServiceImpl();

		this.mapper.registerModule(new JavaTimeModule());
		this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		this.userServiceImpl = new UserServiceImpl(userRepository, jwtService);
		this.agentQueryService = new AgentQueryServiceImpl(jwtService, recordService, recordUserRepositoryImpl, agenClient);
		this.recordServiceImpl = new RecordServiceImpl(recordRepository, jwtService);
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		try {
			Map<String, Object> body = mapper.readValue(request.getBody(), new TypeReference<>() {});
			String route = (String) body.get("action");
			Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
			Map<String, String> headers = request.getHeaders();
			String token = null;
			if(headers != null) token = headers.get("Authorization");

			Response<?> response = switch (route) {
			case "login" -> {
				if (data.get(Constants.EMAIL) == null || data.get(Constants.EMAIL).isEmpty() || data.get(Constants.PASSWORD) == null) {
					log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_LOGIN);
					yield new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_LOGIN, null);
				}
				UserDto userDto = new UserDto(
						data.get(Constants.CC), 
						data.get(Constants.FIRST_NAME), 
						data.get(Constants.LAST_NAME), 
						data.get(Constants.EMAIL), 
						data.get(Constants.PASSWORD));

				yield userServiceImpl.loginUser(userDto);
			}
			case "register" -> {
				if (data.get(Constants.EMAIL) == null || data.get(Constants.EMAIL).isEmpty() || data.get(Constants.PASSWORD) == null ||
						data.get(Constants.PASSWORD).isEmpty() || data.get(Constants.CC) == null || data.get(Constants.CC).isEmpty()) {
					log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER);
					yield new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER, null);
				}
				UserDto userDto = new UserDto(
						data.get(Constants.CC), 
						data.get(Constants.FIRST_NAME), 
						data.get(Constants.LAST_NAME), 
						data.get(Constants.EMAIL), 
						data.get(Constants.PASSWORD));

				yield userServiceImpl.registerUser(userDto);
			}
			case "deleteHistory" -> {
				if(data.get("id") == null || token == null) {
					log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_DELETE);
					yield new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_DELETE, null);
				}
				yield recordServiceImpl.deleteRecord(data.get("id"), token);

			}
			case "queryAgent" -> {
				String base64String = data.get("fileBase64");
				byte[] filByts = null;
				if(base64String != null) {
					filByts = Base64.getDecoder().decode(base64String);
				}

				if(	data.get(Constants.MODEL) == null || data.get(Constants.MODEL).isEmpty() ||
						data.get(Constants.QUESTION) == null || data.get(Constants.QUESTION).isEmpty() || 
						data.get(Constants.CC) == null || data.get(Constants.CC).isEmpty() || token == null) {
					log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT);
					yield new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT, null);
				}
				yield agentQueryService.requestQuery(token, data.get(Constants.QUESTION), data.get("response"), data.get(Constants.CC),
						data.get("uuid"), data.get(Constants.MODEL), data.get(Constants.AGENT), filByts);
			}
			case "getRecords" -> {
				if(data.get(Constants.CC) == null || data.get(Constants.CC).isEmpty() || token == null) {
					log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT);
					yield new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT, null);
				}
				yield recordServiceImpl.consultRecords(data.get(Constants.CC), token);
			}

			case "logout" -> {
				if(token == null) {
					log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_LOGOUT);
					yield new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_LOGOUT, null);
				}
				yield userServiceImpl.logout(token);
			}
			default -> new Response<>(400, "Acción no válida", null);
			};

			return new APIGatewayProxyResponseEvent()
					.withStatusCode(response.getStatusCode())
					.withHeaders(Map.of(
							Constants.CONTENT_TYPE, Constants.RESPONSE_CONTENT_TYPE,
							Constants.ACCES_CONTROL_ALLOW_ORIGIN, Constants.RESPONSE_ACCES_CONTROL_ALLOW_ORIGIN,
							Constants.ACCES_CONTROL_ALLOW_METHODS, Constants.RESPONSE_CONTROL_ALLOW_METHODS,
							Constants.ACCES_CONTROL_ALLOW_HEADERS, Constants.RESPONSE_CONTROL_ALLOW_HEADERS
							))
					.withBody(mapper.writeValueAsString(response));
		} catch (UserException e) {
			log.error(Constants.ERROR_REQUEST, e);
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(e.getErrorCode())
					.withHeaders(Map.of(
							Constants.ACCES_CONTROL_ALLOW_ORIGIN, Constants.RESPONSE_ACCES_CONTROL_ALLOW_ORIGIN,
							Constants.ACCES_CONTROL_ALLOW_METHODS, Constants.RESPONSE_CONTROL_ALLOW_METHODS,
							Constants.ACCES_CONTROL_ALLOW_HEADERS, Constants.RESPONSE_CONTROL_ALLOW_HEADERS
							))
					.withBody("{\"error\":\"Error interno: " + e.getErrorDetail() + "\"}");
		} catch (JsonProcessingException e) {
			log.error(Constants.ERROR_REQUEST, e);
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(500)
					.withHeaders(Map.of(
							Constants.ACCES_CONTROL_ALLOW_ORIGIN, Constants.RESPONSE_ACCES_CONTROL_ALLOW_ORIGIN,
							Constants.ACCES_CONTROL_ALLOW_METHODS, Constants.RESPONSE_CONTROL_ALLOW_METHODS,
							Constants.ACCES_CONTROL_ALLOW_HEADERS, Constants.RESPONSE_CONTROL_ALLOW_HEADERS
							))
					.withBody("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
		}
	}

}

