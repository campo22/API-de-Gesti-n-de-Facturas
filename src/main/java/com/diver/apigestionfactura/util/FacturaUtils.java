package com.diver.apigestionfactura.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FacturaUtils {

    private FacturaUtils() {
    }


    //  este metodo es para lan la respuesta de la peticion
    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus httpStatus){
        return new ResponseEntity<String>( "Mensaje : "+ message,httpStatus);
    }


}
