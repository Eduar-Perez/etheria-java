package com.periferia.etheria.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.InstructionDto;
import com.periferia.etheria.entity.InstructionEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.InstructionRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.InstructionService;
import com.periferia.etheria.util.InstructionUtil;
import com.periferia.etheria.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstructionServiceImpl implements InstructionService{

	private final InstructionRepository instructionRepository;
	private final JwtService jwtService;

	public InstructionServiceImpl(InstructionRepository instructionRepository, JwtService jwtService) {
		this.instructionRepository = instructionRepository;
		this.jwtService = jwtService;
	}

	@Override
	public Response<?> interactueInstruction(InstructionDto instructionDto, String token) {
		try {
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token))) {
				if(instructionDto.getAction().equalsIgnoreCase("delete")) {
					deleteInstruction(instructionDto.getId(), instructionDto.getIdUser());
					return new Response<>(200, "Instrucción eliminada con exito... id: " + instructionDto.getId(), true);
				}
				else if(instructionDto.getAction().equalsIgnoreCase("create")) {
					createInstruction(instructionDto);
					return new Response<>(200, "Instrucción creada con exito... ", true);
				}
				else if (instructionDto.getAction().equalsIgnoreCase("update")) {
					updateInstruction(instructionDto);
					return new Response<>(200, "Instrucción modificada con exito... ", true);
				}
				else if (instructionDto.getAction().equalsIgnoreCase("get")) {
					return new Response<>(200, "Instrucciónes consultadas con exito... ", getInstructionByUsers(instructionDto.getIdUser()));
				}
				else {
					throw new UserException("Acción no soportada: " + instructionDto.getAction(), 400, "Acción inválida");
				}
			}
			else {
				throw new UserException("Contraseña incorrecta: ", 400, Constants.ERROR_CONTRASENA);
			}
		} catch (Exception e) {
			throw new UserException("Error procesando la petición al servicio: " + e.getMessage(), 400, e.getMessage());
		}
	}

	private Boolean deleteInstruction(Long id, String idUser) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			instructionRepository.deleteInstruction(id, idUser);
			return true;
		} catch (Exception e) {
			throw new UserException("Error eliminado la instrucción: " + e.getMessage(), 500, e.getMessage());
		}
	}

	private Boolean createInstruction(InstructionDto instructionDto) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			List<InstructionEntity> instructionsGenerals = new ArrayList<>();
			instructionRepository.getInstructionsGeneral(instructionsGenerals);
			instructionsGenerals.forEach(instructionGeneral -> {
				if(instructionGeneral.getName().equals(instructionDto.getName()))
					throw new UserException("la instrucción con el nombre " +  instructionDto.getName() + 
							" Ya existe en las instucciones generales", 400, null);
			});
			instructionRepository.createInstruction(InstructionUtil.convertDtoToEntity(instructionDto));				
			return true;
		} catch (Exception e) {
			throw new UserException("Error creando la instrucción: " + e.getMessage(), 500, e.getMessage());
		}
	}

	private boolean updateInstruction(InstructionDto instructionDto) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			instructionRepository.updateInstruction(InstructionUtil.convertDtoToEntity(instructionDto));
			return true;
		} catch (Exception e) {
			throw new UserException("Error modificando la instrucción: " + e.getMessage(), 500, e.getMessage());
		}
	}

	private List<InstructionDto> getInstructionByUsers(String idUser) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			return InstructionUtil.convertEntityListToDtoList(instructionRepository.getInstructions(idUser));
		} catch (Exception e) {
			throw new UserException("Error obteniendo las instrucciones del usuario en sesión: " + e.getMessage(), 500, e.getMessage());
		}
	}

}
