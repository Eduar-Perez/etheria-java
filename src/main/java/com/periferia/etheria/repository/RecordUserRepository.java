package com.periferia.etheria.repository;

import com.periferia.etheria.entity.RecordUserEntity;
import com.periferia.etheria.entity.TitleRecordEntity;

public interface RecordUserRepository {

	public TitleRecordEntity getTitleRecord(String title, String idUser);
	public TitleRecordEntity saveTitleRecordEntity(String title, String idUser, String uuid, String question, String response);
	public RecordUserEntity saveRecordUser(Long idRecord, Long titleRecord);
	
}
