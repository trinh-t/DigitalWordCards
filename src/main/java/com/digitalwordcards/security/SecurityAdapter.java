package com.digitalwordcards.security;

import com.digitalwordcards.controllers.UserController;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import java.util.HashSet;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityAdapter extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder encoder;
    private final UserController userService;
    private final UserRepository repository;

    @Bean
    public static PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login", "/user/sign-in").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        http.cors(h -> new CorsConfiguration().setExposedHeaders(List.of("Authorization")));
        http.csrf().disable().sessionManagement().sessionCreationPolicy(STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(getProvider());
    }

    @Bean
    public DaoAuthenticationProvider getProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(userService);
        final User entity = new User();
        entity.setRole(Role.ADMIN);
        entity.setName("Admin");
        entity.setClazz("none");
        entity.setEmail("admin@gmail.com");
        entity.setViewedCards(new HashSet<>());
        entity.setPassword(encoder.encode("password"));
        repository.saveAndFlush(entity);
        return provider;
    }

}
