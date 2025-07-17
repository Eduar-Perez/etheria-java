package com.periferia.etheria.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.FilesDto;
import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.service.AgentIAClientService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentIAClientServiceImpl implements AgentIAClientService {

	private static final HttpClient client = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(5))
			.build();

	@Override
	public String sendQuestionToAgent(String question, String model, String agent, Boolean tools , List<FilesDto> fileBase64, List<InstructionDto> instructions) {
		log.info(Thread.currentThread().getStackTrace()[1].getMethodName());

		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode requestBody = mapper.createObjectNode();
			requestBody.put("question", question);
			requestBody.put("model", model);
			requestBody.put("agent_id", agent);
			requestBody.put("tool", tools);

			ArrayNode filesArray = mapper.createArrayNode();
			for(FilesDto fileDto: fileBase64) {
				ObjectNode fileObject = mapper.createObjectNode();
				fileObject.put("file", fileDto.getFile());
				fileObject.put("fileName", fileDto.getFileName());
				filesArray.add(fileObject);
			}
			requestBody.set("files", filesArray);

			ArrayNode instructionsArray = mapper.createArrayNode();
			for(InstructionDto instruction: instructions) {
				ObjectNode instructionObject = mapper.createObjectNode();
				instructionObject.put("intruction", instruction.getInstruction());
				instructionObject.put("description", instruction.getDescription());
				instructionsArray.add(instructionObject);
			}
			requestBody.set("instructions", instructionsArray);
			String body = mapper.writeValueAsString(requestBody);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(System.getenv(Constants.ENDPOINT_AGENTIA)))
					.timeout(Duration.ofSeconds(180))
					.header("Accept", Constants.RESPONSE_CONTENT_TYPE)
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if(response.statusCode() == 200)
				return extractAnswerFromResponse(response.body());
			else {
				String detailError = response.body();
				detailError = new ObjectMapper().readTree(detailError).path("detail").asText(detailError);
				throw new UserException("Error del agente: HTTP: " + response.statusCode() + " - " + detailError, 500, response.body());
			}
		}

		catch (IOException e) {
			log.error("Error al comunicar con el agente IA: " + e.getMessage());
			throw new UserException("Fallo en la comunicación con el agente IA: " + e.getMessage(), 500, e.getMessage());
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new UserException("La comunicación fue interrumpida: " + e.getMessage(), 500, e.getMessage());
		}
	}

	private String extractAnswerFromResponse(String responseBody) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(responseBody);
			JsonNode responseNode = root.get("response");

			if (responseNode == null || responseNode.isNull()) {
				throw new UserException("No se encontró el nodo 'response' en la respuesta del agente IA", 500, "");
			}

			return responseNode.asText();
		} catch (Exception e) {
			throw new UserException("Error al extraer la respuesta del agente IA", 500, e.getMessage());
		}
	}
}

