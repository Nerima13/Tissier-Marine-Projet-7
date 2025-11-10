package com.nnk.springboot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Autorisations
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/app/login", "/app/error", "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/user/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                // Form login (page custom) - on aligne sur ton LoginController
                .formLogin(form -> form
                        .loginPage("/app/login")
                        .loginProcessingUrl("/app/login")   // le POST du formulaire
                        .defaultSuccessUrl("/bidList/list", true)
                        .permitAll())

                // Logout simple
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/app/login?logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
