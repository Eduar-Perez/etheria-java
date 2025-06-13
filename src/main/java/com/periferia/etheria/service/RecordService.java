package com.periferia.etheria.service;

import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.util.Response;

public interface RecordService {
	
	public Response<?> consultRecords(String cedula, String token);
	public RecordDto saveRecord(String question, String response, String uuid);
	public Response<Boolean> deleteRecord(String id, String token);
	public void updateRecord(RecordEntity recordEntity);
}
