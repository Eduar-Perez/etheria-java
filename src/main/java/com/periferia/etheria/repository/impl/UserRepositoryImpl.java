package com.periferia.etheria.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.periferia.etheria.config.DBConfig;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.constants.ConstantsSql;
import com.periferia.etheria.entity.UserEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserRepositoryImpl implements UserRepository {

	private final DBConfig dataBaseConnection;

	public UserRepositoryImpl(DBConfig dataBaseConnection) {
		this.dataBaseConnection = dataBaseConnection;
	}

	@Override
	public Optional<UserEntity> findByEmail(String email) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_EMAIL.getValue());

		try (Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {

			preparedStatement.setString(1, email);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					UserEntity user = new UserEntity(
							resultSet.getString("cedula"),
							resultSet.getString("first_name"),
							resultSet.getString("last_name"),
							resultSet.getString("email"),
							resultSet.getString("password"),
							resultSet.getString("role"),
							resultSet.getString("image"));
					return Optional.of(user);
				}
			}

		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_GET_USER_EMAIL + e.getMessage(), 1001, e.getMessage());
		}

		return Optional.of(new UserEntity(null, null, null, null, null, null, null));
	}

	@Override
	public boolean existsById(String cedula) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_EXIST_BY_ID_USER.getValue());

		try (Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {

			preparedStatement.setString(1, cedula);
			return preparedStatement.executeQuery().next();
		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_USER_EXIST + e.getMessage(), 1002, e.getMessage());
		}
	}

	@Override
	public UserEntity save(UserEntity user) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_SAVE_USER.getValue());

		try (Connection connection = dataBaseConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
			preparedStatement.setString(1, user.getCedula());
			preparedStatement.setString(2, user.getFirstName());
			preparedStatement.setString(3, user.getLastName());
			preparedStatement.setString(4, user.getEmail());
			preparedStatement.setString(5, user.getPassword());
			preparedStatement.setString(6, user.getRole());
			preparedStatement.setString(7, user.getImage());
			preparedStatement.executeUpdate();
			return user;
		} catch (SQLException e) {
			throw new UserException(Constants.ERROR_SQL_SAVE_USER + e.getMessage(), 1003, e.getMessage());
		}
	}

	@Override
	public UserEntity update(UserEntity user) {
		log.info(Constants.LOGIN_SQL, Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuilder stringBuilder = new StringBuilder(ConstantsSql.VAR_SENTENCIA_SQL_UPDATE_USER.getValue());
		Connection connection = null;

		try {
			connection = dataBaseConnection.getConnection();
			connection.setAutoCommit(false);

			try(PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
				preparedStatement.setString(1, user.getCedula());
				preparedStatement.setString(2, user.getFirstName());
				preparedStatement.setString(3, user.getLastName());
				preparedStatement.setString(4, user.getEmail());
				preparedStatement.setString(5, user.getPassword());
				preparedStatement.setString(6, user.getRole());
				preparedStatement.setString(7, user.getImage());
				preparedStatement.setString(8, user.getCedula());

				ResultSet resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					UserEntity userEntity = new UserEntity(
							resultSet.getString("cedula"), 
							resultSet.getString("first_name"), 
							resultSet.getString("last_name"), 
							resultSet.getString("email"), 
							resultSet.getString("password"),
							resultSet.getString("role"), 
							resultSet.getString("image"));

					connection.commit();
					return userEntity;
				} else {
					connection.rollback();
					throw new UserException("Error actualizando la información del usuario", 400, "No se logro actualizar la información");
				}
			}
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
					log.error("Error en rollback", ex);
				}
			}
			throw new UserException(Constants.ERROR_SQL_UPDATE_USER + e.getMessage(), 500, e.getMessage());
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
