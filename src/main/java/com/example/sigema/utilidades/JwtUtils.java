package com.example.sigema.utilidades;

import com.example.sigema.models.CustomUserDetails;
import io.jsonwebtoken.io.Decoders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String base64Key = "U2lnZW1hVG9rZW5KV1RfcHJveWVjdG8tT1JUMjAyNV9KUy1GS19DVg==";
    private static final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Key));
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 10;

    public String generateToken(UserDetails userDetails) {
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();
        Long idUnidad = ((CustomUserDetails) userDetails).getIdUnidad();
        Long idUsuario = ((CustomUserDetails) userDetails).getIdUsuario();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("rol", rol)
                .claim("idUnidad", idUnidad)
                .claim("idUsuario", idUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.before(new Date());
    }

    public String extractRol(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }

    public Long extractIdUnidad(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("idUnidad", Long.class);
    }

    public Long extractIdUsuario(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("idUsuario", Long.class);
    }
}