package com.jennyduarte.sis.config;

import com.jennyduarte.sis.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder; // Inyectado automáticamente

    public SecurityConfig(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder); // Usa el PasswordEncoder inyectado
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/css/**", "/layout/**",
                                "/js/**", "/registro", "/resources/**",
                                "/contactos/**", "/usuarios/**", "/productos/**"
                        ).permitAll()
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                        .anyRequest().hasRole("ADMINISTRADOR")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // <-- Asegurarse de especificarlo
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .clearAuthentication(true)
                        .permitAll()
                );
        return http.build();
    }



}