package com.periferia.etheria.service;

import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.util.Response;

public interface AgentQueryService {

	public Response<RecordDto> requestQuery(String token, String question, String response, 
			String cedula, String uuid, String model, String agent, byte[] fileBase64);
	
}
