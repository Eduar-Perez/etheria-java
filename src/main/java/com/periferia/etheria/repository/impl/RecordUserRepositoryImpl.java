package com.periferia.etheria.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.constants.ConstantsSql;
import com.periferia.etheria.entity.RecordUserEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordUserRepositoryImpl implements RecordUserRepository {

	private final DBConfig dataBaseConnection;

	public RecordUserRepositoryImpl(DBConfig dataBaseConnection) {
		this.dataBaseConnection = dataBaseConnection;
	}

	@Override
	public boolean existByIdRecordUser(String cedula) {

		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_EXIST_BY_ID_RECORD_USER.getValue());
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {

			preparedStatement.setString(1, cedula);
			return preparedStatement.executeQuery().next();
		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_USER_EXIST, 1002, e.getMessage());
		}
	}

	@Override
	public RecordUserEntity saveRecordUserEntity(Long idRecord, String cedula) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_SAVE_RECORD_USER.getValue());
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setLong(1, idRecord);
			preparedStatement.setString(2, cedula);
			preparedStatement.executeUpdate();

			ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			generatedKeys.next();
			Long generatedId = generatedKeys.getLong(1);
			RecordUserEntity recordUserEntity = new RecordUserEntity();
			recordUserEntity.setId(generatedId);
			recordUserEntity.setIdRecord(idRecord);
			recordUserEntity.setCedula(cedula);
			return recordUserEntity;

		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_SAVE_RECORD + e.getMessage(), 1003, e.getMessage());
		}
	}

}
