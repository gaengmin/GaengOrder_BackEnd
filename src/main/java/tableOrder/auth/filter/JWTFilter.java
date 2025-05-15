package tableOrder.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tableOrder.auth.util.JWTUtil;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.dto.security.CustomUserDetails;
import tableOrder.users.entity.Users;

import java.io.IOException;
import java.io.PrintWriter;


@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    /**
     * @Date 2025.05.11
     * JWT 인증 필터
     * <p>
     * [설명]
     * 1. HTTP 요청 헤더에서 Access Token을 추출합니다.
     * 2. Access Token이 없으면 인증 절차를 건너뛰고 다음 필터로 요청을 넘깁니다.
     * 3. Access Token이 만료되었거나 유효하지 않으면 401(UNAUTHORIZED) 상태와 메시지를 응답합니다.
     * 4. 토큰이 access 타입이 아닐 경우에도 401(UNAUTHORIZED) 상태와 메시지를 응답합니다.
     * 5. 토큰이 정상적이면 사용자 정보를 추출해 SecurityContext에 인증 정보를 저장합니다.
     * 6. 인증이 완료된 상태로 다음 필터로 요청을 넘깁니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 "access" 키로 Access Token 추출
        String accessToken = request.getHeader("access");

        // 2. Access Token이 없으면 인증 없이 다음 필터로 진행
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Access Token 만료 여부 확인 (만료 시 401 응답 및 필터 종료)
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            // 응답 바디에 만료 메시지 출력
            PrintWriter writer = response.getWriter();
            writer.print("토큰 만료");
            // 401 UNAUTHORIZED 상태 코드 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 4. 토큰이 "access" 타입인지 확인 (아닐 경우 401 응답 및 필터 종료)
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            PrintWriter writer = response.getWriter();
            writer.print("액세스 토큰이 아님");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 5. 토큰에서 userId, role 추출
        String userId = jwtUtil.getUserId(accessToken);
        Role role = Role.valueOf(jwtUtil.getRole(accessToken));
        Long storeNo = Long.valueOf(jwtUtil.getStoredNo(accessToken));

        // 6. 사용자 정보로 UserDetails 및 Authentication 객체 생성
        Users users = Users.builder()
                .userId(userId)
                .role(role)
                .storeNo(storeNo)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(users);

        // 7. 인증 객체를 SecurityContext에 저장 (인증 완료 처리)
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 8. 다음 필터로 요청 전달 (인증된 상태)
        filterChain.doFilter(request, response);
    }
}
