package com.diver.apigestionfactura.service.impl;

import com.diver.apigestionfactura.constantes.FacturaConstante;
import com.diver.apigestionfactura.dao.UserRepository;
import com.diver.apigestionfactura.pojo.User;
import com.diver.apigestionfactura.service.UserService;
import com.diver.apigestionfactura.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Servicio para el registro de usuarios en la aplicación.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param requestMap Mapa con los datos del usuario (nombre, email, número de contacto, password).
     * @return ResponseEntity con el resultado de la operación:
     *         - 201 (CREATED) si el usuario se registró con éxito.
     *         - 400 (BAD_REQUEST) si los datos son inválidos o el email ya está registrado.
     *         - 500 (INTERNAL_SERVER_ERROR) si ocurre un error inesperado.
     */
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Registro interno de un usuario {}", requestMap);
        try {
            if (!validateSignUp(requestMap)) {
                return FacturaUtils.getResponseEntity(FacturaConstante.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

            return processUserSignUp(requestMap);

        } catch (Exception e) {
            log.error("Error en el registro de usuario: ", e);
            return FacturaUtils.getResponseEntity(FacturaConstante.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Valida que el mapa de datos contenga los campos requeridos y que no estén vacíos.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return true si los datos son válidos, false en caso contrario.
     */
    private boolean validateSignUp(Map<String, String> requestMap) {
        return requestMap.containsKey("nombre") && !requestMap.get("nombre").trim().isEmpty() &&
                requestMap.containsKey("numeroDeContacto") && !requestMap.get("numeroDeContacto").trim().isEmpty() &&
                requestMap.containsKey("email") && !requestMap.get("email").trim().isEmpty() &&
                requestMap.containsKey("password") && !requestMap.get("password").trim().isEmpty();
    }

    /**
     * Procesa el registro del usuario verificando si ya existe y guardándolo en la base de datos si es nuevo.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return ResponseEntity con el resultado de la operación:
     *         - 201 (CREATED) si el usuario fue registrado exitosamente.
     *         - 400 (BAD_REQUEST) si el usuario ya existe.
     */
    private ResponseEntity<String> processUserSignUp(Map<String, String> requestMap) {
        log.info("Verificando si el user con email {} ya existe", requestMap.get( "email"));
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
     * Convierte un Map<String, String> en un objeto User.
     *
     * @param requestMap Mapa con los datos del usuario.
     * @return Objeto User con los datos extraídos del mapa.
     */
    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("nombre"));
        user.setNumeroContacto(requestMap.get("numeroDeContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false"); // Estado inicial del usuario
        user.setRole("user"); // Rol por defecto del usuario
        return user;
    }
}
