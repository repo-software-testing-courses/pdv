package com.trackorjargh.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@Order(1)
public class SecurityConfigurerApiRest extends WebSecurityConfigurerAdapter {

	@Autowired
	public UserRepositoryAuthenticationProvider authenticationProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.antMatcher("/api/**");	
		
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/login").authenticated();
		
		//Security Films
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/peliculas").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/peliculas/comentarios/*").hasAnyRole("USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/peliculas/puntos/*").hasAnyRole("USER");

		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/peliculas/**").hasAnyRole("ADMIN");
		
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/peliculas/*").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/peliculas/comentarios/*").hasAnyRole("MODERATOR", "ADMIN");

		//Security Books
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/libros").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/libros/comentarios/*").hasAnyRole("USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/libros/puntos/*").hasAnyRole("USER");

		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/libros/**").hasAnyRole("ADMIN");

		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/libros/*").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/libros/comentarios/*").hasAnyRole("MODERATOR", "ADMIN");
	
		//Security Shows
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/series").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/series/comentarios/*").hasAnyRole("USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/series/puntos/*").hasAnyRole("USER");

		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/series/*").hasAnyRole("ADMIN");

		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/series/*").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/series/comentarios/*").hasAnyRole("MODERATOR", "ADMIN");
		
		//Security Users
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/usuarios/*").hasAnyRole("ADMIN");
		
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/usuarios").hasAnyRole("ADMIN");

		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/usuarios/*").hasAnyRole("USER");

		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/usuarios/*").hasAnyRole("ADMIN");
		
		//Security Lists
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/listas").hasAnyRole("USER");
		
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/listas").hasAnyRole("USER");
		
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/listas").hasAnyRole("USER");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/listas/contenido").hasAnyRole("USER");
		
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/listas/**").hasAnyRole("USER");
		
		//Security Image
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/subirimagen").hasAnyRole("USER");
			
		// Other URLs can be accessed without authentication
		http.authorizeRequests().anyRequest().permitAll();

		// Disable CSRF protection (it is difficult to implement with ng2)
		http.csrf().disable();

		// Use Http Basic Authentication
		http.httpBasic();

		// Do not redirect when logout
		http.logout().logoutSuccessHandler((rq, rs, a) -> {	});
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Database authentication provider
		auth.authenticationProvider(authenticationProvider);
	}
}
