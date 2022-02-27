package com.digitalwordcards.security;

import java.util.HashSet;
import java.util.List;

import com.digitalwordcards.controllers.UserController;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.UserDto;
import com.digitalwordcards.services.UserService;
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

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityAdapter extends WebSecurityConfigurerAdapter {

    private final UserService userService;

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
        provider.setPasswordEncoder(getEncoder());
        provider.setUserDetailsService(userService);

        return provider;
    }
}
