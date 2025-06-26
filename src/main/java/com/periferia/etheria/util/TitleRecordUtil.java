package com.periferia.etheria.util;

import java.util.ArrayList;
import java.util.List;

import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.entity.TitleRecordEntity;

public class TitleRecordUtil {

	private TitleRecordUtil() {}

	public static TitleRecordDto convertTitleRecordEntityToDto(TitleRecordEntity titleRecordEntity) {
		TitleRecordDto response = new TitleRecordDto();
		response.setId(titleRecordEntity.getId());
		response.setTitle(titleRecordEntity.getTitle());
		response.setDateCreate(titleRecordEntity.getDateCreate());
		response.setIdUser(titleRecordEntity.getIdUser());
		response.setUuid(titleRecordEntity.getUuid());
		if(titleRecordEntity.getRecordEntity() != null) {
			response.setRecordDto(RecordUtil.convertEntityToDtoList(titleRecordEntity.getRecordEntity()));
		}
		return response;
	}

	public static List<TitleRecordDto> convertTitleRecordEntityListToDtoList(List<TitleRecordEntity> titlesRecordEntity) {
		List<TitleRecordDto> titleRecordResponse = new ArrayList<>();
		for(TitleRecordEntity titleRecordEntity: titlesRecordEntity) {
			TitleRecordDto titleRecordDto = new TitleRecordDto();
			titleRecordDto.setId(titleRecordEntity.getId());
			titleRecordDto.setTitle(titleRecordEntity.getTitle());
			titleRecordDto.setDateCreate(titleRecordEntity.getDateCreate());
			titleRecordDto.setIdUser(titleRecordEntity.getIdUser());
			titleRecordDto.setUuid(titleRecordEntity.getUuid());
			titleRecordDto.setRecordDto(RecordUtil.convertEntityToDtoList(titleRecordEntity.getRecordEntity()));
			titleRecordResponse.add(titleRecordDto);
		}

		return titleRecordResponse;
	}

}
