package com.periferia.etheria.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.QueryAgentDto;
import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.entity.TitleRecordEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordUserRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.AgentIAClientService;
import com.periferia.etheria.service.AgentQueryService;
import com.periferia.etheria.service.RecordService;
import com.periferia.etheria.util.Response;
import com.periferia.etheria.util.TitleRecordUtil;

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
	public Response<TitleRecordDto> requestQuery(String token, QueryAgentDto queryAgentDto) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		TitleRecordDto titleRecordResponse = new TitleRecordDto();
		try {
			Map<String, String> modelsAgents = Constants.getModelsAgents();
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token))) {
				String response = agentClient.sendQuestionToAgent(queryAgentDto.getQuestion(), modelsAgents.get(queryAgentDto.getModel()),
						queryAgentDto.getAgentId(), queryAgentDto.getFiles(), queryAgentDto.getInstructions());

				TitleRecordEntity titleRecordEntity = recordUserRepository.getTitleRecord(queryAgentDto.getUuid(), queryAgentDto.getCedula());
				if(titleRecordEntity.getTitle() == null) {
					titleRecordResponse = TitleRecordUtil.convertTitleRecordEntityToDto(recordUserRepository.saveTitleRecordEntity(
							queryAgentDto.getTitle(),
							queryAgentDto.getCedula(), 
							queryAgentDto.getUuid(),
							queryAgentDto.getQuestion(),
							response));
					Long idRecord = null;
					for(RecordDto recordDto : titleRecordResponse.getRecordDto()) {
						idRecord = recordDto.getId();
					}
					recordUserRepository.saveRecordUser(idRecord, titleRecordResponse.getId());
				}
				else {
					RecordDto recordDto = recordService.saveRecord(queryAgentDto.getQuestion(), response);
					List<RecordDto> listRecordDto = new ArrayList<>();
					listRecordDto.add(recordDto);
					titleRecordResponse = TitleRecordUtil.convertTitleRecordEntityToDto(titleRecordEntity);
					titleRecordResponse.setRecordDto(listRecordDto);
					recordUserRepository.saveRecordUser(recordDto.getId(), titleRecordEntity.getId());
				}

				return new Response<>(200, "Se conecta con el agente y se guarda el historial", titleRecordResponse);
			}
			else {
				return new Response<>(401, "Token no valido para la sesión", null);
			}
		} catch (Exception e) {
			log.error(Constants.ERROR_QUERY_AGENT + e.getMessage());
			throw new UserException(Constants.ERROR_QUERY_AGENT, 500, e.getMessage()); 
		}
	}

}
