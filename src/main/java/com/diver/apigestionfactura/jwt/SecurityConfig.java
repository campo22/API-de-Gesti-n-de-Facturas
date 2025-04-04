package com.diver.apigestionfactura.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ❌ Deshabilita CSRF (Solo en desarrollo)
                .authorizeHttpRequests(auth -> auth // ✅ Permite acceso a los endpoints
                        .requestMatchers("/user/registrar").permitAll() // ✅ Permite acceso público
                        .anyRequest().authenticated() // 🔒 Requiere autenticación en otros endpoints
                )
                // Configuración de sesiones para JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
