package com.diver.apigestionfactura.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private  String secret ="mySecretKey";

    public String extracUsername(String token){
        return  extractClaim(token, Claims::getSubject); // extraer el nombre de usuario del token
    }
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);// extraer todos los claims del token
        return claimsResolver.apply(claims); // aplicar la funcion que se le pasa como argumento
    }

    public Claims extractAllClaims(String token){
        // extraer todos los claims del token
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String generateToken( String username, String role){
        Map<String,Object> claims= new HashMap<>();
        claims.put("role",role);
        return createToken(claims,username);
    }
    private String createToken(Map<String,Object> claims, String subject){
        return  Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis())) // fecha de emision
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10)) // fecha de expiracion
                .signWith(SignatureAlgorithm.HS256,secret).compact();  // algoritmo de firma y secret key
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String extractedUsername = extracUsername(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token)); // validar el token
    }

}
