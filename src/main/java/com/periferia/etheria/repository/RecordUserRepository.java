package com.periferia.etheria.repository;

import com.periferia.etheria.entity.RecordUserEntity;

public interface RecordUserRepository {

	public boolean existByIdRecordUser(String cedula);
	public RecordUserEntity saveRecordUserEntity(Long idRecord, String cedula);
	
}
