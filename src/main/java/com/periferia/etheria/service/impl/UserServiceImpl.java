package com.periferia.etheria.service.impl;

import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.entity.UserEntity;
import com.periferia.etheria.exception.UserException;
import com.periferia.etheria.repository.UserRepository;
import com.periferia.etheria.security.JwtService;
import com.periferia.etheria.security.LdapService;
import com.periferia.etheria.service.UserService;
import com.periferia.etheria.util.Response;
import com.periferia.etheria.util.UserUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final LdapService ldapService;

	public UserServiceImpl(UserRepository userRepository, JwtService jwtService, LdapService ldapService) {
		this.userRepository = userRepository;
		this.jwtService = jwtService;
		this.ldapService = ldapService;
	}

	@Override
	public Response<Boolean> registerUser(UserDto dto) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			if (userRepository.existsById(dto.getCedula()))
				throw new UserException("Usuario ya registrado con la cédula: " + dto.getCedula(), 400, "El usuario ya existe.");

			dto.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
			userRepository.save(UserUtil.convertDtoToEntity(dto));
			return new Response<>(201, "Registro exitoso", true);
		}
		catch(UserException e) {
			log.error(Constants.ERROR_REGISTER + e.getMessage());
			return new Response<>(e.getErrorCode(), e.getMessage() + e.getErrorDetail(), false);
		}
		catch (Exception e) {
			log.error(Constants.ERROR_REGISTER + e.getMessage());
			return new Response<>(500, "Error interno del servidor", false);
		}
	}

	@Override
	public Response<UserDto> loginUser(UserDto userDto) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			if(userDto.getAuthType().equalsIgnoreCase("LOCAL")) {
				var userOptional = userRepository.findByEmail(userDto.getEmail());
				if (userOptional.isEmpty()) 
					throw new UserException("Usuario no encontrado con el correo: " + userDto.getEmail(), 400, "No se encontró el usuario.");

				UserEntity user = userOptional.get();
				if (!BCrypt.checkpw(userDto.getPassword(), user.getPassword()))
					throw new UserException("Contraseña incorrecta para el correo: " + userDto.getEmail(), 400, "Contraseña inválida.");

				String token = jwtService.generateToken(user.getEmail() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getCedula());

				userDto = UserUtil.convertEntityToDto(user);
				userDto.setToken(token);
				return new Response<>(200, "Login exitoso ", userDto);
			}
			else {
				return new Response<>(200, "Login exitoso con LDAP", validateUserLdap(userDto));
			}
		} 
		catch (UserException e) {
			log.info(Constants.ERROR_LOGIN + e.getMessage());
			return new Response<>(e.getErrorCode(), e.getMessage() + e.getErrorDetail(), null);
		}
		catch (Exception e) {
			log.info(Constants.ERROR_LOGIN + e.getMessage());
			return new Response<>(500, "Error interno del servidor ", null);
		}
	}

	private UserDto validateUserLdap(UserDto userDto) { 
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());
		Map<String, String> attributes = ldapService.authenticate(userDto.getEmail(), userDto.getPassword());
		String token = jwtService.generateToken(attributes.get("email") + " " + attributes.get("firstName") + " " + attributes.get("lastName") + " " + attributes.get("cedula"));
		userDto.setCedula(attributes.get("cedula"));
		userDto.setFirstName(attributes.get("firstName"));
		userDto.setLastName(attributes.get("lastName"));
		userDto.setImage(attributes.get("image"));
		userDto.setToken(token);

		return userDto;
	}

}
