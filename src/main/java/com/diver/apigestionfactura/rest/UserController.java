package com.diver.apigestionfactura.rest;

import com.diver.apigestionfactura.constantes.FacturaConstante;
import com.diver.apigestionfactura.service.UserService;
import com.diver.apigestionfactura.util.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserService userService;



    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody Map<String, String> requestMap) {
        try {
            // ✅ Retorna la respuesta de userService.signUp()
            return userService.signUp(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            return FacturaUtils.getResponseEntity(FacturaConstante.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestMap) {
        try {
            // ✅ Retorna la respuesta de userService.login()
            return userService.login(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            return FacturaUtils.getResponseEntity(FacturaConstante.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
