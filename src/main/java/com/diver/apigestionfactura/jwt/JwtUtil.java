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

    // Clave secreta utilizada para firmar y verificar los tokens JWT
    private String secret = "mySecretKey";

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     * @param token el JWT del cual se extraerá el usuario.
     * @return el nombre de usuario contenido en el token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * @param token el JWT del cual se extraerá la fecha de expiración.
     * @return la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token utilizando una función de resolución.
     * @param token el JWT del cual se extraerá la información.
     * @param claimsResolver función que toma un objeto Claims y devuelve un dato específico.
     * @param <T> el tipo de dato a retornar.
     * @return el valor del claim extraído.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims (reclamaciones) del token JWT.
     * @param token el JWT del cual se extraerán los claims.
     * @return un objeto Claims que contiene toda la información del token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret) // Usa la clave secreta para validar el token
                .parseClaimsJws(token) // Parsea y valida el token
                .getBody(); // Obtiene el contenido del token
    }

    /**
     * Verifica si un token ha expirado comparando su fecha de expiración con la fecha actual.
     * @param token el JWT a verificar.
     * @return true si el token ha expirado, false en caso contrario.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un nuevo token JWT para un usuario con su rol.
     * @param username el nombre de usuario que se incluirá en el token.
     * @param role el rol del usuario que se incluirá en el token.
     * @return el token JWT generado.
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Se añade el rol del usuario a los claims
        return createToken(claims, username);
    }

    /**
     * Crea un token JWT con los claims y el usuario proporcionados.
     * @param claims un mapa con los claims a incluir en el token.
     * @param subject el nombre de usuario que será el subject del token.
     * @return el token JWT generado.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Se establecen los claims personalizados
                .setSubject(subject) // Se asigna el nombre de usuario como subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Se establece la fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira en 10 horas
                .signWith(SignatureAlgorithm.HS256, secret) // Firma el token con el algoritmo HS256 y la clave secreta
                .compact(); // Genera el token como una cadena compacta
    }

    /**
     * Valida un token JWT verificando si pertenece al usuario y si no ha expirado.
     * @param token el JWT a validar.
     * @param userDetails los detalles del usuario autenticado.
     * @return true si el token es válido, false en caso contrario.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
