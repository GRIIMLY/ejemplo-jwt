package co.auth.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import co.common.security.JwtConfig;

@EnableWebSecurity
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;

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
	        // Agregar un filtro para validar las credenciales de usuario y agregar el token en el encabezado de respuesta
			
		    // ¿Qué es el authenticationManager ()?
		    // Un objeto proporcionado por WebSecurityConfigurerAdapter, usado para autenticar al usuario que pasa las credenciales del usuario
		    // El filtro necesita este administrador de autenticación para autenticar al usuario.
		     .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig))	
		     .authorizeRequests()
		   // permitir todas las solicitudes POST
		    .antMatchers(HttpMethod.POST, "/auth/**").permitAll()
		    // cualquier otra solicitud debe ser autenticada
		    .anyRequest().authenticated();
	}
	
		// Spring tiene una interfaz UserDetailsService, que puede ser anulada para proporcionar nuestra implementación para obtener usuarios de la base de datos (o cualquier otra fuente).
		// El administrador de autenticación utiliza el objeto UserDetailsService para cargar al usuario desde la base de datos.
		// Además, necesitamos definir el codificador de contraseña también. Por lo tanto, el administrador de autenticación puede comparar y verificar las contraseñas.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public JwtConfig jwtConfig() {
        	return new JwtConfig();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
