package tableOrder.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tableOrder.auth.dto.response.ResponseTokenDto;
import tableOrder.auth.service.ReissueService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ReissueController {

    private final ReissueService reissueService;

    /**
     * [컨트롤러 책임]
     * 1. HTTP 요청에서 쿠키 추출
     * 2. 서비스 호출
     * 3. 응답 헤더/쿠키 설정
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. 쿠키에서 refresh 토큰 추출
        String refreshToken = extractRefreshToken(request.getCookies());

        try {
            // 2. 서비스 호출 (비즈니스 로직 위임)
            ResponseTokenDto.TokenPair tokenPair = reissueService.reissueTokens(refreshToken);

            // 3. 응답 설정
            response.setHeader("access", tokenPair.getAccessToken());
            response.addCookie(createCookie("refresh", tokenPair.getRefreshToken()));

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // 4. 예외 처리
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 쿠키에서 refresh 토큰 추출 메소드
    private String extractRefreshToken(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("refresh".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // 쿠키 생성 메소드 (컨트롤러 책임)
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/"); // 모든 경로에서 쿠키 유효
        cookie.setHttpOnly(true);
        return cookie;
    }
}
