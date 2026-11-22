package com.trackorjargh.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.externallogin.FacebookConnectionSignup;
import com.trackorjargh.externallogin.FacebookSignInAdapter;
import com.trackorjargh.javarepository.UserRepository;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	public UserRepositoryAuthenticationProvider authenticationProvider;
	
	@Autowired
	public CustomAuthFailureHandlerConfigurer customAuthFailureHandlerConfigurer;
	
    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;
 
    @Autowired
    private UsersConnectionRepository usersConnectionRepository;
 
    @Autowired
    private FacebookConnectionSignup facebookConnectionSignup;
    
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserComponent userComponent;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Public pages
		http.authorizeRequests().antMatchers("/busqueda/**").permitAll();
		http.authorizeRequests().antMatchers("/peliculas").permitAll();
		http.authorizeRequests().antMatchers("/series").permitAll();
		http.authorizeRequests().antMatchers("/libros").permitAll();

		// Public Resources
		http.authorizeRequests().antMatchers("/css/**", "/img/**", "/js/**", "/lib/**").permitAll();
		
		//Authorized pages
        http.authorizeRequests().antMatchers("/miperfil").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/administracion").hasAnyRole("ADMIN");
        http.authorizeRequests().antMatchers("/subirContenido").hasAnyRole("ADMIN");
        
        http.authorizeRequests().antMatchers("/pelicula/borrarcomentario/**").hasAnyRole("MODERATOR", "ADMIN");
        http.authorizeRequests().antMatchers("/serie/borrarcomentario/**").hasAnyRole("MODERATOR", "ADMIN");
        http.authorizeRequests().antMatchers("/libro/borrarcomentario/**").hasAnyRole("MODERATOR", "ADMIN");

		// Login form
		http.formLogin().loginPage("/login");
		http.formLogin().usernameParameter("username");
		http.formLogin().passwordParameter("password");
		http.formLogin().defaultSuccessUrl("/");
		http.formLogin().failureHandler(customAuthFailureHandlerConfigurer);

		// Logout
		http.logout().logoutUrl("/logout");
		http.logout().logoutSuccessUrl("/");

		//Test with h2-database
		http.headers().frameOptions().disable();
		http.csrf().disable();
	}
	
    @Bean
    public ProviderSignInController providerSignInController() {
        ((InMemoryUsersConnectionRepository) usersConnectionRepository)
          .setConnectionSignUp(facebookConnectionSignup);
        
        return new ProviderSignInController(
          connectionFactoryLocator, 
          usersConnectionRepository, 
          new FacebookSignInAdapter(userRepository, userComponent));
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Database authentication provider
		auth.authenticationProvider(authenticationProvider);
	}

}
