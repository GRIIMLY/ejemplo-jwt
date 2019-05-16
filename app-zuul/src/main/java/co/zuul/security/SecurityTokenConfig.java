package co.zuul.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import co.common.security.JwtConfig;

@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private JwtConfig jwtConfig;
 
	@Override
  	protected void configure(HttpSecurity http) throws Exception {
    	   http
		.csrf().disable()
	    // asegúrese de que usamos sesión sin estado; La sesión no se utilizará para almacenar el estado del usuario.
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
	    // manejar un intento autorizado
			.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)) 	
		.and()
		   // Añadir un filtro para validar los tokens con cada solicitud
		   .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
			// solicitud de autorización config
		.authorizeRequests()
		   // permitir que todos los que están accediendo al servicio "auth"
		.antMatchers(HttpMethod.POST, "/auth/**").permitAll()
		.antMatchers("/auto/**").permitAll()
		   // debe ser un administrador si intenta acceder al área de administración (aquí también se requiere autenticación)
		 .antMatchers("/gallery" + "/admin/**").hasRole("ADMIN")
		   // Cualquier otra solicitud debe ser autenticada
		   .anyRequest().authenticated(); 
	}
	
	@Bean
  	public JwtConfig jwtConfig() {
    	   return new JwtConfig();
  	}
}