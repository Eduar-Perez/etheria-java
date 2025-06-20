package com.periferia.etheria.repository;

import java.util.List;

import com.periferia.etheria.entity.RecordEntity;

public interface RecordRepository {

	public List<RecordEntity> getRecords(String cedula);
	public RecordEntity getRecord(Long id);
	public RecordEntity saveRecords(String text, String cedula, String uuid);
	public boolean existById(String cedula);
	public void deleteById(String id);
	public void updateRecord(RecordEntity recordEntity);
	public boolean existByModule(String uuid);
	
}
