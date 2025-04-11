package com.diver.apigestionfactura.service.impl;

import com.diver.apigestionfactura.Security.Jwt.JwtUtil;
import com.diver.apigestionfactura.Security.UserDeailsService;
import com.diver.apigestionfactura.constantes.FacturaConstante;
import com.diver.apigestionfactura.dao.UserRepository;
import com.diver.apigestionfactura.pojo.User;
import com.diver.apigestionfactura.service.UserService;
import com.diver.apigestionfactura.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Implementación de UserService para manejar la lógica de autenticación
 * y registro de usuarios.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDeailsService customDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return ResponseEntity con el resultado del registro.
     */
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Registro interno de un usuario {}", requestMap);
        try {
            if (!validateSignUp(requestMap)) {
                return FacturaUtils.getResponseEntity(
                        FacturaConstante.INVALID_DATA,
                        HttpStatus.BAD_REQUEST
                );
            }

            return processUserSignUp(requestMap);

        } catch (Exception e) {
            log.error("Error en el registro de usuario: ", e);
            return FacturaUtils.getResponseEntity(
                    FacturaConstante.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Autentica al usuario y genera un token JWT si las credenciales son válidas.
     *
     * @param requestMap Mapa con email y password.
     * @return ResponseEntity con el token o mensaje de error.
     */
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Login interno de un usuario {}", requestMap);
        try {
            // Verifica si los campos email y password están presentes
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestMap.get("email"),
                            requestMap.get("password")
                    )
            );

            if (authentication.isAuthenticated()) {
                // Si la autenticación es exitosa, se obtiene el usuario autenticado
                if (customDetailsService.getUserDetails().getStatus().equals("true")) {
                    String token = jwtUtil.generateToken(
                            customDetailsService.getUserDetails().getEmail(),
                            customDetailsService.getUserDetails().getRole()
                    );

                    return new ResponseEntity<>(
                            "{\"token\": \"" + token + "\"}",
                            HttpStatus.OK
                    );
                }

                return new ResponseEntity<>(
                        "{\"message\": \"El usuario no está activo\"}",
                        HttpStatus.BAD_REQUEST
                );
            }

        } catch (Exception e) {
            log.error("Error en el login de usuario: ", e);
        }

        return new ResponseEntity<>(
                "{\"message\": \"Error en el login de usuario\"}",
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Valida que los campos requeridos estén presentes y no vacíos.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return true si los datos son válidos, false si falta alguno.
     */
    private boolean validateSignUp(Map<String, String> requestMap) {
        return requestMap.containsKey("nombre") && !requestMap.get("nombre").trim().isEmpty()
                && requestMap.containsKey("numeroDeContacto") && !requestMap.get("numeroDeContacto").trim().isEmpty()
                && requestMap.containsKey("email") && !requestMap.get("email").trim().isEmpty()
                && requestMap.containsKey("password") && !requestMap.get("password").trim().isEmpty();
    }

    /**
     * Verifica si el usuario ya existe y lo guarda si no existe previamente.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return ResponseEntity con el resultado del proceso.
     */
    private ResponseEntity<String> processUserSignUp(Map<String, String> requestMap) {
        log.info("Verificando si el usuario con email {} ya existe", requestMap.get("email"));
        User user = userRepository.findByEmail(requestMap.get("email"));

        if (Objects.isNull(user)) {
            userRepository.save(getUserFromMap(requestMap));
            log.info("Usuario registrado con éxito: {}", requestMap.get("email"));
            return FacturaUtils.getResponseEntity("Usuario registrado con éxito", HttpStatus.CREATED);
        } else {
            log.warn("El usuario con email {} ya existe", requestMap.get("email"));
            return FacturaUtils.getResponseEntity("El usuario con este email ya existe", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Convierte un mapa en un objeto User.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return Instancia de User.
     */
    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("nombre"));
        user.setNumeroContacto(requestMap.get("numeroDeContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false"); // Estado inicial del usuario
        user.setRole("user");    // Rol por defecto
        return user;
    }
}
