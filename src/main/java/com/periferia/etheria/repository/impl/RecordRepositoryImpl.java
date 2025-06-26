package com.periferia.etheria.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.constants.ConstantsSql;
import com.periferia.etheria.entity.RecordEntity;
import com.periferia.etheria.entity.TitleRecordEntity;
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
	public List<TitleRecordEntity> getRecords(String cedula) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_GET_CHAT.getValue());
		List<TitleRecordEntity> titleRecords = new ArrayList<>();
		Map<Long, TitleRecordEntity> titleMap = new HashMap<>();

		try (Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setString(1, cedula);
			ResultSet resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				Long titleId = resultSet.getLong("id_title");

				TitleRecordEntity titleRecordEntity = titleMap.get(titleId);
				if(titleRecordEntity == null) {
					titleRecordEntity= new TitleRecordEntity();
					titleRecordEntity.setId(titleId);
					titleRecordEntity.setTitle(resultSet.getString("title"));
					titleRecordEntity.setDateCreate(resultSet.getDate("date_create").toLocalDate());
					titleRecordEntity.setIdUser(resultSet.getString("id_user"));
					titleRecordEntity.setUuid(resultSet.getString("uuid"));
					titleRecordEntity.setRecordEntity(new ArrayList<>());
					titleMap.put(resultSet.getLong("id_title"), titleRecordEntity);
					titleRecords.add(titleRecordEntity);
				}
				RecordEntity recordEntity = new RecordEntity();
				recordEntity.setId(resultSet.getLong("id_record"));
				recordEntity.setQuestion(resultSet.getString("question"));
				recordEntity.setResponse(resultSet.getString("response"));

				titleRecordEntity.getRecordEntity().add(recordEntity);
			}

		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_GET_RECORD + e.getMessage(), 400, e.getMessage());
		}

		return titleRecords;
	}

	@Override
	public RecordEntity saveRecords(String question, String response) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_SAVE_CHAT.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS)) {
				preparedStatement.setString(1, question);
				preparedStatement.setString(2, response);
				preparedStatement.executeUpdate();

				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				generatedKeys.next();
				Long generatedId = generatedKeys.getLong(1);
				connection.commit();
				RecordEntity recordEntityresponse = new RecordEntity();
				recordEntityresponse.setId(generatedId);
				recordEntityresponse.setQuestion(question);
				recordEntityresponse.setResponse(response);

				return recordEntityresponse;
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
			}

		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_GET_RECORD + e.getMessage(), 500, e.getMessage());
		}
		return recordEntity;
	}

	@Override
	public void deleteById(String uuid) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_DELETE_BY_MODULE.getValue());

		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setString(1, uuid);
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
	public TitleRecordEntity updateTitleRecord(Long id, String title) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_UPDATE_RECORD.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
				preparedStatement.setString(1, title);
				preparedStatement.setLong(2, id);

				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					TitleRecordEntity updatedTitle = new TitleRecordEntity();
					updatedTitle.setId(resultSet.getLong("id"));
					updatedTitle.setTitle(resultSet.getString("title"));
					updatedTitle.setDateCreate(resultSet.getDate("date_create").toLocalDate());
					updatedTitle.setIdUser(resultSet.getString("id_user"));
					updatedTitle.setUuid(resultSet.getString("uuid"));

					connection.commit();
					return updatedTitle;
				} else {
					connection.rollback();
					throw new UserException("No se encontró el registro con id: " + id, 404, "ID no encontrado");
				}
			}

		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException rollbackEx) {
					log.error("Error al hacer rollback", rollbackEx);
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
