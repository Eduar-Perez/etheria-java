package com.periferia.etheria.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.exception.UserException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConfig {

	public DBConfig() {} 

	public Connection getConnection() {
		Connection connection = null;

		try {
			String jdbc = System.getenv("DB_URL");
			String userName = System.getenv("DB_USERNAME");
			String password = System.getenv("DB_PASSWORD");

			connection = DriverManager.getConnection(jdbc, userName, password);
			log.info(Constants.CONECTION_OK);
		}
		catch (SQLException e) {
			throw new UserException(Constants.CONECTION_ERROR, 500, e.getMessage());
		}
		return connection;
	}
}
