package com.ar.invoices.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private String expiresDate;

    public JwtUtil(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String encode(String subject) {
        Date vencimiento = new Date(System.currentTimeMillis() + 360000000); // 3600000
        this.expiresDate = vencimiento.toString();
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(vencimiento)
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }

    public JwtUtil() {
    }
    
    
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(String expiresDate) {
        this.expiresDate = expiresDate;
    }

}
