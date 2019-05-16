package co.zuul.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import co.common.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenAuthenticationFilter extends  OncePerRequestFilter {
    
	private final JwtConfig jwtConfig;
	
	public JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		// 1. obtener el encabezado de autenticación. Se supone que los tokens se pasan en el encabezado de autenticación
		String header = request.getHeader(jwtConfig.getHeader());
		
		// 2. validar el encabezado y verificar el prefijo
		if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
			chain.doFilter(request, response);  		// Si no es válido, vaya al siguiente filtro.
			return;
		}
		

		// Si no se proporciona un token y, por lo tanto, el usuario no se autenticará.
		// Está bien. Tal vez el usuario acceda a una ruta pública o solicite un token.
		
		// Todas las rutas de acceso seguras que necesitan un token ya están definidas y protegidas en la clase de configuración.
		// Y si el usuario intentó acceder sin el token de acceso, no se autenticará y se lanzará una excepción.
		
		// 3. Obtener el token
		String token = header.replace(jwtConfig.getPrefix(), "");
		
		try {	// se pueden generar excepciones al crear las notificaciones si, por ejemplo, el token ha caducado
			
			// 4. Validar el token
			Claims claims = Jwts.parser()
					.setSigningKey(jwtConfig.getSecret().getBytes())
					.parseClaimsJws(token)
					.getBody();
			
			String username = claims.getSubject();
			if(username != null) {
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claims.get("authorities");
				
				// 5. Crear objeto de autenticación
				// UsernamePasswordAuthenticationToken: un objeto incorporado, utilizado por spring para representar al usuario autenticado / autenticado actual.
				// Necesita una lista de autoridades, que tiene un tipo de interfaz GrantedAuthority, donde SimpleGrantedAuthority es una implementación de esa interfaz
				 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
								 username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
				 
				 // 6. Autenticar al usuario
				 // Ahora, el usuario está autenticado
				 SecurityContextHolder.getContext().setAuthentication(auth);
			}
			
		} catch (Exception e) {
			// En caso de fallo. Asegúrate de que esté claro; así que garantizamos que el usuario no será autenticado
			SecurityContextHolder.clearContext();
		}
		
		// ir al siguiente filtro en la cadena de filtros
		chain.doFilter(request, response);
	}

}