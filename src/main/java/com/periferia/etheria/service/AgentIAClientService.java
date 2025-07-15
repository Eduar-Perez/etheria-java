package com.periferia.etheria.service;

import java.util.List;

import com.periferia.etheria.dto.FilesDto;
import com.periferia.etheria.dto.InstructionDto;

public interface AgentIAClientService {

	public String sendQuestionToAgent(String questiong, String model, String agent, Boolean tools, List<FilesDto> fileBase64, List<InstructionDto> instructions);

}
