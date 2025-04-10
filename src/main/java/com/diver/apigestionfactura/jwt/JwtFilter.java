package com.diver.apigestionfactura.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro personalizado que intercepta cada petición HTTP entrante.
 * Se encarga de validar el token JWT y autenticar al usuario en el contexto de Spring Security.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Servicio encargado de cargar los datos del usuario desde la base de datos.
    @Autowired
    private UserDeailsService userDetailsService;

    // Variables auxiliares para almacenar los claims del token y el nombre de usuario
    Claims claims = null;
    String userName = null;

    /**
     * Método sobrescrito que se ejecuta con cada petición.
     * Verifica si la petición requiere autenticación y valida el token JWT si es necesario.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Si la ruta es pública (login, recuperar contraseña o registro), se permite el paso sin validar token
        if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(request, response);
        } else {
            // Se intenta obtener el header Authorization que contiene el token
            String authorizationHeader = request.getHeader("Authorization");
            String token = null;

            // Se verifica que el header no sea nulo y que comience con "Bearer "
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // Se extrae el token eliminando el prefijo "Bearer "
                token = authorizationHeader.substring(7);

                // Se obtiene el nombre de usuario (subject) desde el token
                userName = jwtUtil.extractUsername(token);

                // Se extraen todos los claims para posible uso posterior (roles, expiración, etc.)
                claims = jwtUtil.extractAllClaims(token);

                // Verifica si el usuario no está autenticado actualmente
                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Se cargan los detalles del usuario desde la base de datos
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                    // Se valida el token usando los datos del usuario
                    if (jwtUtil.validateToken(token, userDetails)) {

                        // Si el token es válido, se crea un token de autenticación con los detalles del usuario
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Se añaden los detalles de la solicitud actual (como IP y user-agent)
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Se establece el usuario autenticado en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
                // Finalmente, continúa con el siguiente filtro en la cadena
                filterChain.doFilter(request, response);
            }
        }
    }

    public Boolean isAdmin() {
        return "admin".equalsIgnoreCase ((String) claims.get("role"));
    }

    public Boolean isUser() {
        return "user".equalsIgnoreCase ((String) claims.get("role"));
    }
    public String getCurrentUser() {
        return userName;
    }

}
