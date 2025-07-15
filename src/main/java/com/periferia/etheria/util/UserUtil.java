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
		return new UserEntity(
				userDto.getCedula(),
				userDto.getFirstName(),
				userDto.getLastName(),
				userDto.getEmail(),
				userDto.getPassword(),
				userDto.getRole(),
				userDto.getImage());
	}

	public static UserDto convertEntityToDto(UserEntity userEntity) {
		return new UserDto(
				userEntity.getCedula(),
				userEntity.getFirstName(),
				userEntity.getLastName(),
				userEntity.getEmail(), 
				null,
				userEntity.getRole(),
				userEntity.getImage(),
				null);

	}

}
