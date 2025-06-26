package com.periferia.etheria.service;

import com.periferia.etheria.dto.QueryAgentDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.util.Response;

public interface AgentQueryService {

	public Response<TitleRecordDto> requestQuery(String token, QueryAgentDto queryAgentDto);
	
}
