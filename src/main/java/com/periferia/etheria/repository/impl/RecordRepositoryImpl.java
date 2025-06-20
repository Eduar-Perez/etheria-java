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
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.RecordRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordRepositoryImpl implements RecordRepository {

	private final DBConfig dataBaseConnection;

	public RecordRepositoryImpl(DBConfig dataBaseConnection) {
		this.dataBaseConnection = dataBaseConnection;
	}

	@Override
	public List<RecordEntity> getRecords(String cedula) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_GET_CHAT.getValue());
		List<RecordEntity> records = new ArrayList<>();

		try (Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setString(1, cedula);
			ResultSet resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				RecordEntity recordEntity = new RecordEntity();
				recordEntity.setId(resultSet.getLong("id"));
				recordEntity.setQuestion(resultSet.getString("question"));
				recordEntity.setResponse(resultSet.getString("response"));
				recordEntity.setDateCreate(resultSet.getDate("date_create").toLocalDate());
				recordEntity.setUuid(resultSet.getString("uuid"));
				records.add(recordEntity);
			}

		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_GET_RECORD + e.getMessage(), 400, e.getMessage());
		}

		return records;
	}

	@Override
	public RecordEntity saveRecords(String question, String response, String uuid) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_SAVE_CHAT.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS)) {
				preparedStatement.setString(1, question);
				preparedStatement.setString(2, response);
				preparedStatement.setDate(3, Date.valueOf(LocalDate.now()));
				preparedStatement.setString(4, uuid);
				preparedStatement.executeUpdate();

				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				generatedKeys.next();
				Long generatedId = generatedKeys.getLong(1);
				connection.commit();
				
				return getRecord(generatedId);
			}

		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					log.error("Error en rollback", ex);
				}
			}
			throw new UserException(Constants.ERROR_SQL_SAVE_RECORD + e.getMessage(), 400, e.getMessage());

		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException ex) {
					log.warn("No se pudo cerrar la conexión", ex);
				}
			}
		}
	}

	@Override
	public boolean existById(String cedula) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_EXIST_BY_ID_USER.getValue());
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setString(1, cedula);
			return preparedStatement.executeQuery().next();
		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_USER_EXIST + e.getMessage(), 400, e.getMessage());
		}
	}

	@Override
	public RecordEntity getRecord(Long id) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_FIND_BY_ID_RECORD.getValue());
		RecordEntity recordEntity = new RecordEntity();
		
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setLong(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				recordEntity.setId(resultSet.getLong("id"));
				recordEntity.setQuestion(resultSet.getString("question"));
				recordEntity.setResponse(resultSet.getString("response"));
				recordEntity.setDateCreate(resultSet.getDate("date_create").toLocalDate());
				recordEntity.setUuid(resultSet.getString("uuid"));
			}

		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_GET_RECORD + e.getMessage(), 500, e.getMessage());
		}
		return recordEntity;
	}

	@Override
	public void deleteById(String id) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_DELETE_BY_MODULE.getValue());
		
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setString(1, id);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_DELETE_RECORDS + e.getMessage(), 400, e.getMessage());
		}
	}
	
	@Override
	public boolean existByModule(String module) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_EXIST_BY_MODULE.getValue());
		
		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setString(1, module);
			return preparedStatement.executeQuery().next();
		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_USER_EXIST + e.getMessage(), 400, e.getMessage());
		}
	}

	@Override
	public void updateRecord(RecordEntity recordEntity) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_UPDATE_RECORD.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
				preparedStatement.setString(1, recordEntity.getResponse());
				preparedStatement.setLong(2, recordEntity.getId());
				preparedStatement.executeUpdate();
			}

			connection.commit();
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					log.error("Error en rollback", ex);
				}
			}
			throw new UserException(Constants.ERROR_SQL_UPDATE_RECORD + e.getMessage(), 400, e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException ex) {
					log.warn("No se pudo cerrar la conexión", ex);
				}
			}
		}
	}

}
