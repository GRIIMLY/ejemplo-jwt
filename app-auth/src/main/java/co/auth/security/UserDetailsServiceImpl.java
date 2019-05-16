package co.auth.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService{
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// hard coding the users. All passwords must be encoded.
		final List<AppUser> users = Arrays.asList(
			new AppUser(1, "omar", encoder.encode("12345"), "USER"),
			new AppUser(2, "admin", encoder.encode("12345"), "ADMIN")
		);
		

		for(AppUser appUser: users) {
			if(appUser.getUsername().equals(username)) {
				

				// Recuerda que Spring necesita roles para estar en este formato: "ROLE_" + userRole (es decir, "ROLE_ADMIN")
				// Por lo tanto, debemos establecerlo en ese formato, para poder verificar y comparar roles (es decir, hasRole ("ADMIN")).
				List<GrantedAuthority> grantedAuthorities = AuthorityUtils
		                	.commaSeparatedStringToAuthorityList("ROLE_" + appUser.getRole());
					

				// La clase "User" es proporcionada por Spring y representa una clase modelo para que UserDetailsService devuelva al usuario
				// Y utilizado por el administrador de autenticación para verificar y verificar la autenticación del usuario.
				return new User(appUser.getUsername(), appUser.getPassword(), grantedAuthorities);
			}
		}
		
		// If user not found. Throw this exception.
		throw new UsernameNotFoundException("Username: " + username + " not found");
	}

}
