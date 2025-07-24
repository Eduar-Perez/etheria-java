package com.periferia.etheria.repository.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.constants.ConstantsSql;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.RecordUserEntity;
import com.periferia.etheria.entity.TitleRecordEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordRepository;
import com.periferia.etheria.repository.RecordUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordUserRepositoryImpl implements RecordUserRepository {

	private final DBConfig dataBaseConnection;
	private final RecordRepository recordRepository;

	public RecordUserRepositoryImpl(DBConfig dataBaseConnection, RecordRepository recordRepository) {
		this.dataBaseConnection = dataBaseConnection;
		this.recordRepository = recordRepository;
	}

	@Override
	public TitleRecordEntity getTitleRecord(String title, String idUser) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_GET_TITLE_RECORD.getValue());
		
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {

			preparedStatement.setString(1, title);
			preparedStatement.setString(2, idUser);
			ResultSet generatedKeys = preparedStatement.executeQuery();

			if(generatedKeys.next()) {
				TitleRecordEntity recordUserEntity = new TitleRecordEntity();
				recordUserEntity.setId(generatedKeys.getLong("id"));
				recordUserEntity.setTitle(generatedKeys.getString("title"));
				recordUserEntity.setDateCreate(generatedKeys.getDate("date_create").toLocalDate());
				recordUserEntity.setIdUser(generatedKeys.getString("id_user"));
				recordUserEntity.setUuid(generatedKeys.getString("uuid"));

				return recordUserEntity;
			}
			else {
				return new TitleRecordEntity();
			}
		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_USER_EXIST, 1002, e.getMessage());
		}
	}

	@Override
	public TitleRecordEntity saveTitleRecordEntity(String title, String idUser, String uuid, String question, String response) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_SAVE_TITLE_RECORD.getValue());
		
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS)) {
			List<RecordEntity> recordsEntity = new ArrayList<>();
			recordsEntity.add(recordRepository.saveRecords(question, response));

			preparedStatement.setString(1, title);
			preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
			preparedStatement.setString(3, idUser);
			preparedStatement.setString(4, uuid);
			preparedStatement.executeUpdate();

			ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			generatedKeys.next();
			TitleRecordEntity recordUserEntity = new TitleRecordEntity();
			recordUserEntity.setId(generatedKeys.getLong(1));
			recordUserEntity.setTitle(generatedKeys.getString(2));
			recordUserEntity.setDateCreate(generatedKeys.getDate(3).toLocalDate());
			recordUserEntity.setIdUser(generatedKeys.getString(4));
			recordUserEntity.setUuid(generatedKeys.getString(5));
			recordUserEntity.setRecordEntity(recordsEntity);
			
			return recordUserEntity;

		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_SAVE_RECORD + e.getMessage(), 1003, e.getMessage());
		}
	}

	@Override
	public RecordUserEntity saveRecordUser(Long idRecord, Long idTitleRecord) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_SAVE_RECORD_USER.getValue());
		
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setLong(1, idRecord);
			preparedStatement.setLong(2, idTitleRecord);
			preparedStatement.executeUpdate();

			ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			generatedKeys.next();
			RecordUserEntity recordEntity = new RecordUserEntity();
			recordEntity.setId(generatedKeys.getLong(1));
			recordEntity.setIdRecord(generatedKeys.getLong(2));
			recordEntity.setIdTitleRecord(generatedKeys.getString(3));
			
			return recordEntity;

		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_SAVE_RECORD + e.getMessage(), 1003, e.getMessage());
		}
	}

}
