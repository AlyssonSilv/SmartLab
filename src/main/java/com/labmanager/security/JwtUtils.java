package com.labmanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders; // Ainda importado, mas não usado para a geração da chave principal
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import jakarta.annotation.PostConstruct; // Corrigido para jakarta.annotation

/**
 * Utilitário para operações com JWT (JSON Web Tokens)
 * Responsável por gerar, validar e extrair informações dos tokens
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Tempo de expiração dos tokens em milissegundos (vem do application.properties)
    @Value("${labmanager.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // --- CORREÇÃO AQUI: A CHAVE SECRETA SERÁ GERADA AUTOMATICAMENTE E SERÁ FORTE ---
    // Removemos a injeção direta de jwtSecretString, pois usaremos uma chave gerada.
    // Se você ainda precisar do valor de 'jwtSecret' do properties para outros fins,
    // mantenha a linha @Value, mas saiba que ela NÃO será usada para criar a SecretKey.
    // @Value("${labmanager.app.jwtSecret}")
    // private String jwtSecretConfigValue; // Renomeado para não confundir que é a chave de fato

    private Key secretKey; // A chave real a ser usada para assinatura

    @PostConstruct // Este método será executado logo após as dependências serem injetadas
    public void init() {
        // Opção 1 (Recomendada): Gerar uma chave criptograficamente forte e aleatória
        // para o algoritmo HS512. Isso garante que o tamanho da chave é seguro (512 bits).
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // Opção 2 (Alternativa, se quiser usar valor de properties):
        // Se você QUISER usar o valor do application.properties para gerar a chave,
        // o valor em 'labmanager.app.jwtSecret' DEVE ser uma string Base64 LONGA o suficiente.
        // Por exemplo, para HS512, precisa ser 64 bytes (approx. 86 chars em Base64).
        // Se usar esta opção, COMENTE a linha acima (secretKey = Keys.secretKeyFor...).
        // try {
        //     secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretConfigValue));
        // } catch (Exception e) {
        //     logger.error("Erro ao decodificar a chave JWT do application.properties. Gerando uma chave padrão.", e);
        //     secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Fallback seguro
        // }
    }

    // Método auxiliar para retornar a chave secreta segura
    private Key key() {
        return secretKey;
    }
    // --- FIM DA CORREÇÃO ---


    /**
     * Gera um token JWT para o usuário autenticado
     * @param authentication Objeto de autenticação do Spring Security
     * @return Token JWT como string
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // RA do usuário como subject
                .setIssuedAt(new Date()) // Data de emissão
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Data de expiração
                .signWith(key(), SignatureAlgorithm.HS512) // Assinatura com a chave segura e HS512
                .compact();
    }

    /**
     * Extrai o RA (username) do token JWT
     * @param token Token JWT
     * @return RA do usuário
     */
    public String getUserNameFromJwtToken(String token) {
        // Usar parserBuilder().setSigningKey(key()).build()
        return Jwts.parserBuilder().setSigningKey(key()).build()
                   .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Valida se o token JWT é válido
     * @param authToken Token JWT a ser validado
     * @return true se válido, false caso contrário
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Usar parserBuilder().setSigningKey(key()).build()
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string está vazia: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) { // Captura especificamente erro de assinatura
            logger.error("Assinatura JWT inválida: {}", e.getMessage());
        }

        return false;
    }
}