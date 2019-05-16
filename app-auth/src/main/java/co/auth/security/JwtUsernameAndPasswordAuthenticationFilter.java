package co.auth.security;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.common.security.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUsernameAndPasswordAuthenticationFilter  extends UsernamePasswordAuthenticationFilter   {
	
	// Usamos el administrador de autenticación para validar las credenciales del usuario
	private AuthenticationManager authManager;
	
	private final JwtConfig jwtConfig;
    
	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
		this.authManager = authManager;
		this.jwtConfig = jwtConfig;
		
		// Por defecto, UsernamePasswordAuthenticationFilter escucha la ruta  "/ login" .
		// En nuestro caso, usamos "/ auth". Por lo tanto, tenemos que anular los valores predeterminados.
				
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			
			// 1. Obtener credenciales de la solicitud
			UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
			
			// 2. Crear objeto de autenticación (contiene credenciales) que será utilizado por el administrador de autenticación
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					creds.getUsername(), creds.getPassword(), Collections.emptyList());
			
			// 3. El administrador de autenticación autentica al usuario y usa el método UserDetialsServiceImpl :: loadUserByUsername () para cargar al usuario.
			return authManager.authenticate(authToken);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	// Tras la autenticación exitosa, generar un token.
		// El 'auth' pasado a successAuthentication () es el usuario autenticado actual.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
			.setSubject(auth.getName())	
			// Convertir a lista de cadenas.
			// Esto es importante porque afecta la forma en que los recuperamos en la puerta de enlace.
			.claim("authorities", auth.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
			.setIssuedAt(new Date(now))
			.setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // in milliseconds
			.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
			.compact();
		System.out.println(token);
		// Añadir token al encabezado
		response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
	}
	
	private static class UserCredentials {
	    private String username, password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	    
	}
}