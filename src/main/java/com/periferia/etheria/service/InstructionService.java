package com.periferia.etheria.service;

import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.util.Response;

public interface InstructionService {

	public Response<?> interactueInstruction(InstructionDto instructionDto, String token);
	
}
