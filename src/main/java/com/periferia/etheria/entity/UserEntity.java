package com.periferia.etheria.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {

	private String cedula;
	private String firstName;
	private String lastName;
	private String email;
	private String password;

	public UserEntity(String cedula, String firstName, String lastName, String email, String password) {
		this.cedula = cedula;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}
}
