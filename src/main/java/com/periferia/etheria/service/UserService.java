package com.periferia.etheria.service;

import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.util.Response;

public interface UserService {

	public Response<Boolean> registerUser(UserDto userDto);
	public Response<UserDto> loginUser(UserDto userDto);
	public Response<UserDto> updateDataUser(UserDto userDto, String token);
	
}
