package com.pestrisk.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pestrisk.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long validityInSeconds;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(
            @Value("${pestrisk.jwt.secret:}") String secretKey,
            @Value("${pestrisk.jwt.expiration-seconds:7200}") long validityInSeconds,
            ObjectMapper objectMapper) {
        if (secretKey == null || secretKey.trim().length() < 32) {
            throw new IllegalStateException(
                    "PESTRISK_JWT_SECRET environment variable is missing or insecure. " +
                    "It must be configured outside tests with at least 256 bits (32+ characters) of cryptographically random secret material."
            );
        }
        this.secretKey = secretKey.trim();
        this.validityInSeconds = validityInSeconds;
        this.objectMapper = objectMapper;
    }

    public String generateToken(String username, UserRole role) {
        try {
            long nowEpoch = Instant.now().getEpochSecond();
            long expEpoch = nowEpoch + validityInSeconds;

            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", username);
            payload.put("role", role.name());
            payload.put("iat", nowEpoch);
            payload.put("exp", expEpoch);

            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String dataToSign = encodedHeader + "." + encodedPayload;

            String signature = sign(dataToSign, secretKey);
            return dataToSign + "." + signature;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String dataToSign = parts[0] + "." + parts[1];
            String expectedSignature = sign(dataToSign, secretKey);

            if (!MessageDigest.isEqual(
                    parts[2].getBytes(StandardCharsets.UTF_8),
                    expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payloadJson = objectMapper.readTree(payloadBytes);

            if (!payloadJson.has("exp")) {
                return false;
            }

            long exp = payloadJson.get("exp").asLong();
            return exp > Instant.now().getEpochSecond();
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaim(token, "sub");
    }

    public String getRole(String token) {
        return getClaim(token, "role");
    }

    private String getClaim(String token, String claimKey) {
        try {
            String[] parts = token.split("\\.");
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payloadJson = objectMapper.readTree(payloadBytes);
            return payloadJson.has(claimKey) ? payloadJson.get(claimKey).asText() : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse JWT claim", e);
        }
    }

    private String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hmac);
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
