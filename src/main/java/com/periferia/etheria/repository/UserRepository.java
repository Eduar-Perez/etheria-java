package com.periferia.etheria.repository;

import java.util.Optional;

import com.periferia.etheria.entity.UserEntity;

public interface UserRepository {

	public Optional<UserEntity> findByEmail(String email);
	public boolean existsById(String cedula);
	public UserEntity save(UserEntity user);	
	public UserEntity update(UserEntity user);
}
