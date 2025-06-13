package com.periferia.etheria.config;

import com.periferia.etheria.repository.UserRepository;
import com.periferia.etheria.repository.impl.UserRepositoryImpl;
import com.periferia.etheria.security.JwtService;


public class AppConfig {

	public UserRepository userRepository(DBConfig dataSource) {
		return new UserRepositoryImpl(dataSource);
	}

	public JwtService jwtService() {
		String jwtSecrete = System.getenv("JWT_SECRET");
		if(jwtSecrete == null || jwtSecrete.isBlank()) {
			throw new IllegalStateException(jwtSecrete);
		}
		return new JwtService(jwtSecrete);
	}

}
