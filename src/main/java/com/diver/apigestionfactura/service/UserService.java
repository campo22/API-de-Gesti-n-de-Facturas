package com.diver.apigestionfactura.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    // Metodo signUp para registrar un usuario.
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

}
