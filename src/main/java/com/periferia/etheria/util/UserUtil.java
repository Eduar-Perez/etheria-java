package com.periferia.etheria.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUtil {

	private UserUtil(){}

	public static UserEntity convertDtoToEntity(UserDto userDto) {
		UserEntity userEntity = new UserEntity(null, null, null, null, null);
		userEntity.setCedula(userDto.getCedula());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		userEntity.setPassword(userDto.getPassword());
		userEntity.setEmail(userDto.getEmail());

		return userEntity;
	}

	public static UserDto convertEntityToDto(UserEntity userEntity) {
		UserDto userDto = new UserDto(null, null, null, null, null);
		userDto.setCedula(userEntity.getCedula());
		userDto.setFirstName(userEntity.getFirstName());
		userDto.setLastName(userEntity.getLastName());
		userDto.setEmail(userEntity.getEmail());

		return userDto;
	}

}
