package tableOrder.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static tableOrder.common.utils.CookieUtil.createCookie;
import static tableOrder.common.utils.CookieUtil.extractRefreshToken;

@RestController
@RequiredArgsConstructor
@Tag(name = "Token Reissue API", description = "리프레시 토큰을 사용해 새로운 액세스 토큰과 리프레시 토큰을 발급받기 위한 API입니다.")
@RequestMapping("/api/auth")
public class ReissueController {

    private final ReissueService reissueService;

    /**
     * [컨트롤러 책임]
     * 1. HTTP 요청에서 쿠키 추출
     * 2. 서비스 호출
     * 3. 응답 헤더/쿠키 설정
     */
    @Operation(
            summary = "JWT 토큰 재발급",
            description = """
                            클라이언트는 기존 리프레시 토큰을 사용하여 유효한 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다. 
                            요청 시 'refresh'라는 이름으로 저장된 쿠키에서 리프레시 토큰을 추출하여 처리합니다.
                   
                            **처리 과정:** 
                            1. 클라이언트가 요청하면 서버는 쿠키에서 리프레시 토큰을 추출합니다.
                            2. 추출된 리프레시 토큰을 서비스에 전달하여 새로운 액세스 토큰과 리프레시 토큰을 생성합니다.
                            3. 새로 생성된 액세스 토큰은 응답 헤더에 추가되며, 리프레시 토큰은 응답 쿠키에 설정됩니다.
                    """
    )
    @SecurityRequirement(name = "Access")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. 쿠키에서 refresh 토큰 추출
        String refreshToken = extractRefreshToken(request.getCookies(), "refresh");

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

}
