package tableOrder.security.jwt;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * @Date 25.05.09
 * <p>
 * JWT 12.3 버전
 * <p>
 * 저장해야 할 정보
 * userId, ROLE, 생성일, 만료일
 * <p>
 * 구현해야 할 메소드
 * <p>
 * userId 확인 메소드
 * role 확인 메소드
 * 만료일 확인 메소드
 */

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 검증을 할 메소드
     */
    public String getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    /**
     * 생성을 할 메소드
     */
    public String createJwt(String userId, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) //현재 토큰 발행시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //토근 소멸 시간
                .signWith(secretKey)
                .compact();
    }

}


