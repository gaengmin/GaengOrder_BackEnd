package tableOrder.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import tableOrder.auth.dto.response.ResponseTokenDto;
import tableOrder.refresh.entity.RefreshTokenEntity;
import tableOrder.refresh.repository.RefreshTokenRepository;
import tableOrder.auth.util.JWTUtil;
import tableOrder.users.entity.Users;
import tableOrder.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /**
     * [서비스 책임]
     * 1. 토큰 유효성 검증
     * 2. 새 토큰 생성
     */
    public ResponseTokenDto.TokenPair reissueTokens(String refreshToken) {
        // 1. 토큰 존재 여부 검증
        if (refreshToken == null) {
            throw new IllegalArgumentException("refresh token null");
        }

        // 2. 토큰 만료 검증
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("refresh token expired");
        }

        // 3. 토큰 타입 검증 (refresh인지 확인)
        String category = jwtUtil.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            throw new IllegalArgumentException("invalid refresh token");
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            //response body
            throw new IllegalArgumentException("invalid refresh token");
        }

        // 4. 사용자 정보 추출
        String userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 5. 새 토큰 생성
        String newAccess = jwtUtil.createJwt("access", userId, role, 600_000L); // 10분
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, 86_400_000L); // 24시간

        //Refresh 토큰 저장 DB에 기존 Refresh삭제 후 새 Refresh토즌 저장
        refreshTokenRepository.deleteByRefresh(refreshToken);
        addRefreshTokenEntity(userId, newRefresh, 86_400_000L);

        return new ResponseTokenDto.TokenPair(newAccess, newRefresh);
    }


    /**
     * Refresh 토큰 저장소에서 기한이 지난 토큰 삭제
     * TTL 설정을 통해 자동으로 Refresh 토큰이 삭제되면 무방하지만 계속해서 토큰이 쌓일 경우 용량 문제가 발생할 수 있다.
     *
     * 따라서 스케줄 작업을 통해 만료시간이 지난 토큰은 주기적으로 삭제하는 것이 올바르다.
     * */

    private void addRefreshTokenEntity(String userId, String refresh, Long expiredMs) {


        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 만료 시간 계산 (expiredMs 활용)
        LocalDateTime expiration = LocalDateTime.now().plus(expiredMs, ChronoUnit.MILLIS);

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)         // 사용자 ID 설정
                .refresh(refresh)       // 리프레시 토큰 설정
                .expiration(expiration) // 만료 시간 설정 (예: 24시간 후)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }
}