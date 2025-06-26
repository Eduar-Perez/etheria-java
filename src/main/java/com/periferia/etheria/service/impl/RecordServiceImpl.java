package com.periferia.etheria.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.RecordDto;
import com.periferia.etheria.dto.TitleRecordDto;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.service.RecordService;
import com.periferia.etheria.util.RecordUtil;
import com.periferia.etheria.util.Response;
import com.periferia.etheria.util.TitleRecordUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordServiceImpl implements RecordService {

	private final RecordRepository recordRepository;
	private final JwtService jwtService;

	public RecordServiceImpl(RecordRepository recordRepository, JwtService jwtService) {
		this.recordRepository = recordRepository;
		this.jwtService = jwtService;
	}

	@Override
	public Response<List<TitleRecordDto>> consultRecords(String cedula, String token) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		List<TitleRecordDto> response = new ArrayList<>();
		try {
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token)))
				response = TitleRecordUtil.convertTitleRecordEntityListToDtoList(recordRepository.getRecords(cedula));
			else
				throw new UserException("Contraseña incorrecta: ", 400, Constants.ERROR_CONTRASENA);
		} catch (Exception e) {
			log.error(Constants.ERROR_GET_RECORDS);
			throw new UserException("Error consultando el historial del chat: " + e.getMessage(), 500, e.getMessage());			
		}
		return new Response<>(200, "Registro consultado con exito", response);

	}

	@Override
	public RecordDto saveRecord(String question, String response) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			if(question != null) {				
				return RecordUtil.convertEntityToDto(recordRepository.saveRecords(question, response));
			}
			else { 
				throw new UserException(Constants.DATA_OBLIGATORIA, 400, "El texto y la cédula con obligatorios");
			}
		} catch (Exception e) {
			log.error(Constants.ERROR_SAVE_RECORDS);
			throw new UserException(Constants.ERROR_SAVE_RECORDS + e.getMessage(), 400, e.getMessage());
		}
	}

	@Override
	public Response<Boolean> deleteRecord(String uuid, String token) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token))) {
				if(!recordRepository.existByModule(uuid)) {
					return new Response<>(200, "El registro a eliminar no se encuentra en la base de datos", false);
				}
				recordRepository.deleteById(uuid);
				return new Response<>(200, "Historial eliminado con exito", true);
			}
			else {
				return new Response<>(401, "Token no valido para la sesión", null);
			}
		} catch (UserException e) {
			log.error(Constants.ERROR_DELETE_RECORDS + e.getMessage());
			return new Response<>(e.getErrorCode(), e.getMessage() + e.getErrorDetail(), null);
		}
	}

	@Override
	public Response<TitleRecordDto> updateTitleRecord(Long id, String title, String token) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			token = token.substring(7);
			if(Boolean.TRUE.equals(jwtService.validateToken(token))) {
				TitleRecordDto response = TitleRecordUtil.convertTitleRecordEntityToDto(recordRepository.updateTitleRecord(id, title));
				return new Response<>(200, "Registro actualizado con exito: ", response);
			}
			else {
				return new Response<>(401, "Token no valido para la sesión", null);			
			}
		} catch (Exception e) {
			log.error(Constants.ERROR_UPDATE_RECORDS + e.getMessage());
			throw new UserException(Constants.ERROR_UPDATE_RECORDS + e.getMessage(), 500, e.getMessage());
		}
	}

}
