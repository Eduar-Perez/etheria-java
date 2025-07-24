package com.periferia.etheria.security;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.dto.UserDto;
import com.periferia.etheria.exception.UserException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtService {

	private final SecretKey secretKey;

	public JwtService(String secret) {
		log.info(Constants.INTINIALIZER_KEY);
		this.secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	}

	public String generateToken(String email) {
		log.info(Constants.GENERATE_TOKEN);
		Long nowInSeconds = Instant.now().getEpochSecond();
		Long expirationInSeconds = nowInSeconds + 14400; //Expirará cada 4 horas
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date(nowInSeconds * 1000))	
				.setExpiration(new Date(expirationInSeconds * 1000))
				.signWith(secretKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public Boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token);

			return true;
		} catch (JwtException e) {
			log.error("Error validando el toquen: " + e.getMessage());
			throw new UserException(Constants.TOKEN_EXPIRATED + e.getMessage(), 400, e.getMessage());
		}
	}

	public UserDto jwtDecoder(String token) {
		try {
			validateToken(token);
			String[] partsToken = token.split("\\.");
			String headers = new String(Base64.getUrlDecoder().decode(partsToken[1]));
			String rol = headers.split("Rol: ")[1].split(",\"iat\"")[0].trim();
			rol = rol.replace("\"", "").trim();

			return new UserDto(
					headers.split("Cedula:")[1].split("Rol:")[0].trim(), 
					headers.split("Nombres: ")[1].split("Apellidos:")[0].trim(), 
					headers.split("Apellidos: ")[1].split("Cedula:")[0].trim(), 
					headers.split("Correo:")[1].split("Nombres:")[0].trim(), 
					null, 
					rol, 
					null, 
					null);

		} 
		catch (UserException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Error descifrando el token");
			throw new UserException(Constants.TOKEN_DECOUDER + e.getMessage(), 400, e.getMessage());
		}
	}
}
