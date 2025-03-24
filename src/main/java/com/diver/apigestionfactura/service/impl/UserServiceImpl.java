package com.diver.apigestionfactura.service.impl;

import com.diver.apigestionfactura.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

// Anotación de Lombok para generar automáticamente un Logger basado en SLF4J.
@Slf4j

// Anotación de Spring que indica que esta clase es un servicio y puede ser inyectado en otras partes de la aplicación.
@Service
public class UserServiceImpl implements UserService { // Implementa la interfaz UserService

    /**
     * Metodo signUp para registrar un usuario.
     * Recibe un Map<String, String> como parámetro, que representa los datos enviados en la solicitud.
     * Retorna un ResponseEntity<String>, que es la respuesta HTTP que se enviará al cliente.
     */
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        return null;
    }
}
