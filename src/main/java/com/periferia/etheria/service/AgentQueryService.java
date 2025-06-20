package com.periferia.etheria.service;

import com.periferia.etheria.dto.QueryAgentDto;
import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.util.Response;

public interface AgentQueryService {

	public Response<RecordDto> requestQuery(String token, QueryAgentDto queryAgentDto);
	
}
