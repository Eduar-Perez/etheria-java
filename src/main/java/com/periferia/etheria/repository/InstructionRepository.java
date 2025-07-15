package com.periferia.etheria.repository;

import java.util.List;

import com.periferia.etheria.entity.InstructionEntity;

public interface InstructionRepository {

	public void deleteInstruction(Long id, String idUser);
	public void createInstruction(InstructionEntity instruction);
	public void updateInstruction(InstructionEntity instruction);
	public List<InstructionEntity> getInstructions(String idUser);
	public List<InstructionEntity> getInstructionsGeneral(List<InstructionEntity> instructionsResponse);

}
