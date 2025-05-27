package tableOrder.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Reissue API", description = "매출 집계, 메뉴별 판매량, 시간대 분석")
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
            summary = "ADMIN 관리하는 일간(최대 30일) / 주간(기준일 기준 직전 12주) / 월간 매출 비교 (기준일 기준 직전 11개월)",
            description = "ordersItemsNo가 필요하고, 그 데이터를 가져오고, 거기서 취소하는 데이터 생각"
    )
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
