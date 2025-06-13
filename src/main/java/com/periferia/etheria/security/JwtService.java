package com.periferia.etheria.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.periferia.etheria.constants.Constants;

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
			log.error("El token no coincide " + e.getMessage());
			return false;
		}
	}
	
	public Boolean logout(String token) {
		
		return null;
	}

}
