package com.periferia.etheria.repository;

import java.util.List;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.TitleRecordEntity;

public interface RecordRepository {

	public List<TitleRecordEntity> getRecords(String cedula);
	public RecordEntity getRecord(Long id);
	public RecordEntity saveRecords(String question, String response);
	public boolean existById(String uuid);
	public void deleteById(String uuid);
	public TitleRecordEntity updateTitleRecord(Long id, String title);
	public boolean existByModule(String uuid);
	
}
