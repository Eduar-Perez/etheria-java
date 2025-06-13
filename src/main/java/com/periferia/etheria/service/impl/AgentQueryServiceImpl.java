package com.periferia.etheria.service.impl;

import java.util.Map;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordUserRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.AgentIAClientService;
import com.periferia.etheria.service.AgentQueryService;
import com.periferia.etheria.service.RecordService;
import com.periferia.etheria.util.RecordUtil;
import com.periferia.etheria.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentQueryServiceImpl implements AgentQueryService {

	private final JwtService jwtService;
	private final RecordService recordService;
	private final RecordUserRepository recordUserRepository;
	private final AgentIAClientService agentClient;

	public AgentQueryServiceImpl(JwtService jwtService, RecordServiceImpl recordService, 
			RecordUserRepository recordUserRepository, AgentIAClientService agentClient) {
		this.jwtService = jwtService;
		this.recordService = recordService;
		this.recordUserRepository = recordUserRepository;
		this.agentClient = agentClient;
	}

	@Override
	public Response<RecordDto> requestQuery(String token, String question, String response, String cedula, String uuid, String model, String agent, byte[] fileBase64) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		RecordDto responseQuestion = new RecordDto(null, question, response, null, uuid);
		try {
			Map<String, String> modelsAgents = Constants.getModelsAgents();
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token))) {
				RecordDto recordDto = recordService.saveRecord(question, response, uuid);
				if(recordDto.getId() != null) {
					recordUserRepository.saveRecordUserEntity(recordDto.getId(), cedula);
				}
				response = agentClient.sendQuestionToAgent(question, modelsAgents.get(model), agent, fileBase64);
				recordDto.setResponse(response);
				recordService.updateRecord(RecordUtil.convertDtoToEntity(recordDto));
				responseQuestion.setId(recordDto.getId());
				responseQuestion.setDateCreate(recordDto.getDateCreate());
				responseQuestion.setResponse(response);
				return new Response<>(200, "Se conecta con el agente y se guarda el historial", responseQuestion);
			}
			else {
				return new Response<>(401, "Token no valido para la sesión", null);
			}
		} catch (Exception e) {
			log.error(Constants.ERROR_QUERY_AGENT + e.getMessage());
			throw new UserException(Constants.ERROR_QUERY_AGENT, 500, e.getMessage()); 
		}
	}

	public void setResponseRecord(RecordDto recordDto, String response) {
		recordDto.setResponse(response);

	}

}
