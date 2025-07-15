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
	private String role;
	private String image;

	public UserEntity(String cedula, String firstName, String lastName, String email, String password, String role, String image) {
		this.cedula = cedula;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.image = image;
	}
}
