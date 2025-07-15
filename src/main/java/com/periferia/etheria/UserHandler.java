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
import com.periferia.etheria.dto.FilesDto;
import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.dto.QueryAgentDto;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.impl.InstructionRepositoryImpl;
import com.periferia.etheria.repository.impl.RecordRepositoryImpl;
import com.periferia.etheria.repository.impl.RecordUserRepositoryImpl;
import com.periferia.etheria.repository.impl.UserRepositoryImpl;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.security.LdapService;
import com.periferia.etheria.service.AgentQueryService;
import com.periferia.etheria.service.impl.AgentIAClientServiceImpl;
import com.periferia.etheria.service.impl.AgentQueryServiceImpl;
import com.periferia.etheria.service.impl.InstructionServiceImpl;
import com.periferia.etheria.service.impl.RecordServiceImpl;
import com.periferia.etheria.service.impl.UserServiceImpl;
import com.periferia.etheria.util.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ObjectMapper mapper = new ObjectMapper();
	private final UserServiceImpl userServiceImpl;
	private final AgentQueryService agentQueryService;
	private final RecordServiceImpl recordServiceImpl;
	private final InstructionServiceImpl instructionService;

	public UserHandler() {
		String jwtSecret = System.getenv(Constants.JWT_SECRET);
		DBConfig dataBaseConnection = new DBConfig();
		UserRepositoryImpl userRepository = new UserRepositoryImpl(dataBaseConnection);
		RecordRepositoryImpl recordRepository = new RecordRepositoryImpl(dataBaseConnection);
		RecordUserRepositoryImpl recordUserRepositoryImpl = new RecordUserRepositoryImpl(dataBaseConnection, recordRepository);
		InstructionRepositoryImpl instructionRepositoryImpl = new  InstructionRepositoryImpl(dataBaseConnection);
		JwtService jwtService = new JwtService(jwtSecret);
		RecordServiceImpl recordService = new RecordServiceImpl(recordRepository, jwtService);
		AgentIAClientServiceImpl agenClient = new AgentIAClientServiceImpl();
		LdapService ldapService = new LdapService();

		this.mapper.registerModule(new JavaTimeModule());
		this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		this.userServiceImpl = new UserServiceImpl(userRepository, jwtService, ldapService);
		this.agentQueryService = new AgentQueryServiceImpl(jwtService, recordService, recordUserRepositoryImpl, agenClient);
		this.recordServiceImpl = new RecordServiceImpl(recordRepository, jwtService);
		this.instructionService = new InstructionServiceImpl(instructionRepositoryImpl, jwtService);
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		try {
			Map<String, Object> body = mapper.readValue(request.getBody(), new TypeReference<>() {});
			String route = (String) body.get("action");
			Map<String, String> headers = request.getHeaders();
			String token = headers != null ? headers.get("Authorization") : null;

			Response<?> response = switch (route) {
			case "register" -> handleRegister(body);
			case "login" -> handleLogin(body);
			case "queryAgent" -> handleQueryAgent(body, token);
			case "getRecords" -> handleGetRecords(body, token);
			case "instructions/general" -> handleInstruction(body, token);
			case "deleteHistory" -> handleDeleteHistory(body, token);
			case "updateTitle" -> handleUpdateTitle(body, token);
			default -> new Response<>(400, "Acción no válida", null);
			};

			return buildResponse(response);

		} catch (UserException e) {
			log.error(Constants.ERROR_REQUEST, e);
			return buildErrorResponse(e.getErrorCode(), e.getErrorDetail());
		} catch (JsonProcessingException e) {
			log.error(Constants.ERROR_REQUEST, e);
			return buildErrorResponse(500, e.getMessage());
		}
	}

	private Response<?> handleUpdateTitle(Map<String, Object> body, String token) {
		Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if(isNullOrEmpty(data.get(Constants.ID)) || isNullOrEmpty(data.get(Constants.TITLE)) || token == null) {
			log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER);
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER, null);
		}
		return recordServiceImpl.updateTitleRecord(Long.parseLong(data.get(Constants.ID)), data.get(Constants.TITLE), token);
	}

	private Response<?> handleRegister(Map<String, Object> body) {
		Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if (isNullOrEmpty(data.get(Constants.EMAIL)) || isNullOrEmpty(data.get(Constants.PASSWORD)) ||
				isNullOrEmpty(data.get(Constants.CC)) || isNullOrEmpty(data.get(Constants.ROLE))) {
			log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER);
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER, null);
		}

		UserDto userDto = new UserDto(
				data.get(Constants.CC),
				data.get(Constants.FIRST_NAME),
				data.get(Constants.LAST_NAME),
				data.get(Constants.EMAIL),
				data.get(Constants.PASSWORD),
				data.get(Constants.ROLE),
				data.get(Constants.IMAGE),
				data.get(Constants.AUTHTYPE));

		return userServiceImpl.registerUser(userDto);
	}

	private Response<?> handleLogin(Map<String, Object> body) {
		Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if (isNullOrEmpty(data.get(Constants.EMAIL)) || isNullOrEmpty(data.get(Constants.PASSWORD))) {
			log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_LOGIN);
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_LOGIN, null);
		}

		UserDto userDto = new UserDto(
				data.get(Constants.CC),
				data.get(Constants.FIRST_NAME),
				data.get(Constants.LAST_NAME),
				data.get(Constants.EMAIL),
				data.get(Constants.PASSWORD),
				data.get(Constants.ROLE),
				data.get(Constants.IMAGE),
				data.get(Constants.AUTHTYPE));

		return userServiceImpl.loginUser(userDto);
	}

	private Response<?> handleQueryAgent(Map<String, Object> body, String token) {
		Map<String, Object> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if (data.get(Constants.MODEL) == null || data.get(Constants.QUESTION) == null ||
				data.get(Constants.CC) == null || token == null) {
			log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT);
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT, null);
		}

		List<FilesDto> files = Optional.ofNullable(data.get(Constants.FILES))
				.map(obj -> mapper.convertValue(obj, new TypeReference<List<FilesDto>>() {}))
				.orElse(new ArrayList<>());
		List<InstructionDto> instructions = Optional.ofNullable(data.get(Constants.INSTRUCTIONS))
				.map(obj -> mapper.convertValue(obj, new TypeReference<List<InstructionDto>>() {}))
				.orElse(new ArrayList<>());

		QueryAgentDto dto = new QueryAgentDto();
		dto.setModel((String) data.get(Constants.MODEL));
		dto.setAgentId((String) data.get(Constants.AGENT));
		dto.setQuestion((String) data.get(Constants.QUESTION));
		dto.setUuid((String) data.get(Constants.UUID));
		dto.setCedula((String) data.get(Constants.CC));
		dto.setTitle((String) data.get(Constants.TITLE));
		dto.setTools((Boolean) data.get(Constants.TOOLS));
		dto.setFiles(files);
		dto.setInstructions(instructions);

		return agentQueryService.requestQuery(token, dto);
	}

	private Response<?> handleGetRecords(Map<String, Object> body, String token) {
		Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if (isNullOrEmpty(data.get(Constants.CC)) || token == null) {
			log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT);
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_QUERYAGENT, null);
		}
		return recordServiceImpl.consultRecords(data.get(Constants.CC), token);
	}

	private Response<?> handleInstruction(Map<String, Object> body, String token) {
		Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if (isNullOrEmpty(data.get(Constants.ID_USER)) || isNullOrEmpty(data.get(Constants.ACTION))) {
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_REGISTER, null);
		}

		InstructionDto instructionDto = new InstructionDto();
		if (data.get(Constants.ID) != null) instructionDto.setId(Long.parseLong(data.get(Constants.ID)));
		instructionDto.setName(data.get(Constants.NAME));
		instructionDto.setDescription(data.get(Constants.DESCRIPTION));
		instructionDto.setInstruction(data.get(Constants.INSTRUCTION));
		instructionDto.setGeneral(Boolean.parseBoolean(data.get(Constants.GENERAL)));
		instructionDto.setIdUser(data.get(Constants.ID_USER));
		instructionDto.setAction(data.get(Constants.ACTION));

		return instructionService.interactueInstruction(instructionDto, token);
	}

	private Response<?> handleDeleteHistory(Map<String, Object> body, String token) {
		Map<String, String> data = mapper.convertValue(body.get("data"), new TypeReference<>() {});
		if (data.get("id") == null || token == null) {
			log.error(Constants.RESPONSE_GENERIC + Constants.RESPONSE_DELETE);
			return new Response<>(400, Constants.RESPONSE_GENERIC + Constants.RESPONSE_DELETE, null);
		}
		return recordServiceImpl.deleteRecord(data.get("id"), token);
	}

	private APIGatewayProxyResponseEvent buildResponse(Response<?> response) {
		return new APIGatewayProxyResponseEvent()
				.withStatusCode(response.getStatusCode())
				.withHeaders(Map.of(
						Constants.CONTENT_TYPE, Constants.RESPONSE_CONTENT_TYPE,
						Constants.ACCES_CONTROL_ALLOW_ORIGIN, Constants.RESPONSE_ACCES_CONTROL_ALLOW_ORIGIN,
						Constants.ACCES_CONTROL_ALLOW_METHODS, Constants.RESPONSE_CONTROL_ALLOW_METHODS,
						Constants.ACCES_CONTROL_ALLOW_HEADERS, Constants.RESPONSE_CONTROL_ALLOW_HEADERS
						))
				.withBody(toJson(response));
	}

	private APIGatewayProxyResponseEvent buildErrorResponse(int statusCode, String message) {
		return new APIGatewayProxyResponseEvent()
				.withStatusCode(statusCode)
				.withHeaders(Map.of(
						Constants.ACCES_CONTROL_ALLOW_ORIGIN, Constants.RESPONSE_ACCES_CONTROL_ALLOW_ORIGIN,
						Constants.ACCES_CONTROL_ALLOW_METHODS, Constants.RESPONSE_CONTROL_ALLOW_METHODS,
						Constants.ACCES_CONTROL_ALLOW_HEADERS, Constants.RESPONSE_CONTROL_ALLOW_HEADERS
						))
				.withBody("{\"error\":\"Error interno: " + message + "\"}");
	}

	private String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Error serializando JSON", e);
			return "{}";
		}
	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

}
