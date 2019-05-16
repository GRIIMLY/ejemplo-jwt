package co.samtel.autenticacion.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.common.security.dto.LoginDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

@RestController
public class LoginController {
		
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test (@RequestBody LoginDTO loginDTO) {
		System.out.println("usuario: " + loginDTO.getUsuario() +"  contraseña: " + loginDTO.getContrasenia());
		
		
		
		String rolesStr = "ROLE_ADMIN,ROLE_USER";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(rolesStr);
		
		
		String jwt = "";
		Instant now = Instant.now();
		
		jwt = Jwts.builder().setSubject(loginDTO.getUsuario())
				.claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plus(1,ChronoUnit.HOURS)))
				.signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.encode("a6e21884876f43819523c31033fa697e")).compact();
		return jwt;
	}
}
