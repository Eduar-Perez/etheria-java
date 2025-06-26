package com.periferia.etheria.service;

import java.util.List;

import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.util.Response;

public interface RecordService {
	
	public Response<List<TitleRecordDto>> consultRecords(String cedula, String token);
	public RecordDto saveRecord(String question, String response);
	public Response<Boolean> deleteRecord(String uuid, String token);
	public Response<TitleRecordDto> updateTitleRecord(Long id, String title, String token);
}
