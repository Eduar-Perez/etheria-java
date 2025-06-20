package com.periferia.etheria.service.impl;

import java.util.Map;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.QueryAgentDto;
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
	public Response<RecordDto> requestQuery(String token, QueryAgentDto queryAgentDto) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		RecordDto recordDto = new RecordDto(null, queryAgentDto.getQuestion(), null, null, queryAgentDto.getUuid());
		try {
			Map<String, String> modelsAgents = Constants.getModelsAgents();
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token))) {
				recordDto = recordService.saveRecord(queryAgentDto.getQuestion(), null, queryAgentDto.getUuid());
				if(recordDto.getId() != null) {
					recordUserRepository.saveRecordUserEntity(recordDto.getId(), queryAgentDto.getCedula());
				}
				recordDto.setResponse(agentClient.sendQuestionToAgent(queryAgentDto.getQuestion(), modelsAgents.get(queryAgentDto.getModel()),
						queryAgentDto.getAgentId(), queryAgentDto.getFiles()));
				recordService.updateRecord(RecordUtil.convertDtoToEntity(recordDto));
				return new Response<>(200, "Se conecta con el agente y se guarda el historial", recordDto);
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
