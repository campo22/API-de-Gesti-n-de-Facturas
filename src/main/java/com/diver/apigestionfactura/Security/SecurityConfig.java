package com.diver.apigestionfactura.Security;

import com.diver.apigestionfactura.Security.Jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;


    @Autowired
    private UserDeailsService userDetailsService;

    // ✅ No realiza codificación de contraseñas (solo para pruebas)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // ✅ Devuelve el administrador de autenticaciones que utiliza Spring Security
    @Bean
    public AuthenticationManager authenticatedAuthorizationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ✅ Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Configuración de CORS (permite solicitudes cruzadas por defecto) ejemplo: http://localhost:3000 para React
                .cors(cors -> cors
                        .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                )
                // ✅ Desactiva protección CSRF (para APIs REST)
                .csrf(csrf -> csrf.disable())

                // ✅ Define las rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user/forgotPassword", "/user/register").permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ Política de sesiones: stateless (sin mantener sesiones en el servidor)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


        // ✅ Inserta el filtro JWT antes del filtro de login por nombre de usuario y contraseña
        // el addFilterBefore() se utiliza para agregar un filtro personalizado antes de otro filtro específico
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
