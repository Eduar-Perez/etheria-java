package com.periferia.etheria.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.constants.ConstantsSql;
import com.periferia.etheria.entity.InstructionEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.InstructionRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstructionRepositoryImpl implements InstructionRepository{

	private final DBConfig dataBaseConnection;

	public InstructionRepositoryImpl(DBConfig dataBaseConnection) {
		this.dataBaseConnection = dataBaseConnection;
	}

	@Override
	public void deleteInstruction(Long id, String idUser) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder sqlBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_DELETE_INSTRUCTION.getValue());

		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
			preparedStatement.setLong(1, id);
			preparedStatement.setString(2, idUser);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new UserException(Constants.ERROR_SQL_DELETE_INSTRUCTION + e.getMessage(), 500, e.getMessage());
		}
	}

	@Override
	public void createInstruction(InstructionEntity instruction) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_CREATE_INSTRUCTION.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try(PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
				preparedStatement.setString(1, instruction.getName());
				preparedStatement.setString(2, instruction.getInstruction());
				preparedStatement.setString(3, instruction.getDescription());
				preparedStatement.setBoolean(4, instruction.getGeneral());
				preparedStatement.setString(5, instruction.getIdUser());
				preparedStatement.executeUpdate();

				connection.commit();
			}

		} catch (Exception e) {
			if(connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					log.error("Error en el rolback", ex);
				}
			}
			throw new UserException(Constants.ERROR_SQL_SAVE_INSTRUCTION + e.getMessage(), 500, e.getMessage());

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
	public void updateInstruction(InstructionEntity instruction) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_UPDATE_INSTRUCTION.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try(PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
				preparedStatement.setString(1, instruction.getName());
				preparedStatement.setString(2, instruction.getInstruction());
				preparedStatement.setString(3, instruction.getDescription());
				preparedStatement.setBoolean(4, instruction.getGeneral());
				preparedStatement.setString(5, instruction.getIdUser());
				preparedStatement.setLong(6, instruction.getId());
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
			throw new UserException(Constants.ERROR_SQL_UPDATE_RECORD + e.getMessage(), 500, e.getMessage());
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
	public List<InstructionEntity> getInstructions(String idUser) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_GET_INSTRUCTION.getValue());
		List<InstructionEntity> instructionsResponse = new ArrayList<>();

		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
			preparedStatement.setString(1, idUser);
			ResultSet resultSet = preparedStatement.executeQuery();

			while(resultSet.next()) {
				InstructionEntity instructionResponse = new InstructionEntity();
				instructionResponse.setId(resultSet.getLong("id"));
				instructionResponse.setDescription(resultSet.getString("description"));
				instructionResponse.setInstruction(resultSet.getString("instruction"));
				instructionResponse.setGeneral(resultSet.getBoolean("general"));
				instructionResponse.setName(resultSet.getString("name"));
				instructionResponse.setIdUser(resultSet.getString("id_user"));
				instructionsResponse.add(instructionResponse);
			}

		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_GET_UPDATE + e.getMessage(), 400, e.getMessage());
		}
		getInstructionsGeneral(instructionsResponse);
		return instructionsResponse;

	}

	@Override
	public List<InstructionEntity> getInstructionsGeneral(List<InstructionEntity> instructionsResponse) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_GET_INSTRUCTION_GENERAL.getValue());

		try(Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
			preparedStatement.setBoolean(1, true);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				InstructionEntity instructionResponse = new InstructionEntity();
				instructionResponse.setId(resultSet.getLong("id"));
				instructionResponse.setDescription(resultSet.getString("description"));
				instructionResponse.setInstruction(resultSet.getString("instruction"));
				instructionResponse.setGeneral(resultSet.getBoolean("general"));
				instructionResponse.setName(resultSet.getString("name"));
				instructionResponse.setIdUser(resultSet.getString("id_user"));
				instructionsResponse.add(instructionResponse);
			}

		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_GET_UPDATE + e.getMessage(), 400, e.getMessage());
		}
		return instructionsResponse;
	}
}
