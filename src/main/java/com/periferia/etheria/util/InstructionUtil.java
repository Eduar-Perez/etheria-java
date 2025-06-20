package com.periferia.etheria.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.entity.InstructionEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructionUtil {

	private InstructionUtil() {}

	public static InstructionEntity convertDtoToEntity (InstructionDto instructionDto) {
		InstructionEntity instructionResponse = new InstructionEntity();
		instructionResponse.setId(instructionDto.getId());
		instructionResponse.setName(instructionDto.getName());
		instructionResponse.setDescription(instructionDto.getDescription());
		instructionResponse.setInstruction(instructionDto.getInstruction());
		instructionResponse.setGeneral(instructionDto.getGeneral());
		instructionResponse.setIdUser(instructionDto.getIdUser());

		return instructionResponse;
	}

	public static InstructionDto convertEntityToDto (InstructionEntity instructionEntity) {
		InstructionDto instructionResponse = new InstructionDto();
		instructionResponse.setId(instructionEntity.getId());
		instructionResponse.setName(instructionEntity.getName());
		instructionResponse.setDescription(instructionEntity.getDescription());
		instructionResponse.setInstruction(instructionEntity.getInstruction());
		instructionResponse.setGeneral(instructionEntity.getGeneral());
		instructionResponse.setIdUser(instructionEntity.getIdUser());

		return instructionResponse;
	}

	public static List<InstructionDto> convertEntityListToDtoList(List<InstructionEntity> instructionsEntity) {
		List<InstructionDto> instructionsDto = new ArrayList<>();
		instructionsEntity.forEach(instructionEntity -> {
			InstructionDto instructionDto = new InstructionDto();
			instructionDto.setId(instructionEntity.getId());
			instructionDto.setInstruction(instructionEntity.getInstruction());
			instructionDto.setDescription(instructionEntity.getDescription());
			instructionDto.setIdUser(instructionEntity.getIdUser());
			instructionDto.setGeneral(instructionEntity.getGeneral());
			instructionDto.setName(instructionEntity.getName());
			instructionsDto.add(instructionDto);
		});

		return instructionsDto;
	}

}
