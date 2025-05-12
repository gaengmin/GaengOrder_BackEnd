package tableOrder.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tableOrder.auth.util.JWTUtil;
import tableOrder.refresh.entity.RefreshTokenEntity;
import tableOrder.refresh.repository.RefreshTokenRepository;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.entity.Users;
import tableOrder.users.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;

import static tableOrder.common.utils.CookieUtil.createCookie;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    //JWTUtil을 주입해줘야함
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;





    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //1. json 바디를 읽어서 requestLoginDto로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            RequestUsersDto.requestLoginDto loginDto = objectMapper.readValue(request.getInputStream(), RequestUsersDto.requestLoginDto.class);

            String userId = loginDto.getUserId();
            String pwd = loginDto.getPwd();


            //인증 토큰 새성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, pwd, null);

            //3. 인증 처리
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패");
        }

    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }


    /**
     * @리프레쉬 토큰 발급 25.05.11
     * <p>
     * [설명]
     * 로그인 인증에 성공했을 때 실행되는 메서드입니다.
     * 1. 인증된 사용자의 ID와 권한 정보를 추출합니다.
     * 2. 추출한 정보를 바탕으로 Access Token(10분)과 Refresh Token(24시간)을 생성합니다.
     * 3. Access Token은 HTTP 응답 헤더에, Refresh Token은 쿠키에 담아 클라이언트로 전달합니다.
     * 4. 응답 상태 코드를 200(OK)으로 설정합니다.
     * <p>
     * [response 처리]
     * - Access Token: response.setHeader("access", access)로 응답 헤더에 추가합니다.
     * 클라이언트는 이 토큰을 Authorization 헤더 등에 담아 인증 요청에 사용합니다.
     * - Refresh Token: response.addCookie(createCookie("refresh", refresh))로 쿠키에 추가합니다.
     * Access Token이 만료됐을 때 클라이언트가 이 쿠키를 사용해 토큰을 재발급받을 수 있습니다.
     * - 응답 상태: response.setStatus(HttpStatus.OK.value())로 인증 성공을 알립니다.
     */

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 1. 인증된 사용자의 ID 추출
        String userId = authResult.getName();

        // 2. 인증된 사용자의 권한(ROLE) 추출
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 3. JWT 토큰 생성 (Access: 10분, Refresh: 24시간)
        String access = jwtUtil.createJwt("access", userId, role, 600000L); // 10분(600,000ms)
        String refresh = jwtUtil.createJwt("refresh", userId, role, 86400000L); // 24시간(86,400,000ms)

        //4. 리프래쉬 토큰 저장
        addRefreshEntity(userId, refresh, 8640000L);

        // 5. 응답 상태 코드 설정 (200 OK)
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }


    /**
     * addRefreshEntity
     * * 사용자 ID, 리프레시 토큰, 만료 기간(ms)을 받아 RefreshToken 엔티티를 생성하고 저장한다.
     *  * <p>
     *  * 1. 전달받은 만료 기간(ms)을 현재 시간에 더해 만료 시각을 계산한다.
     *  * 2. userId로 Users 엔티티를 조회한다. 사용자가 없으면 예외를 발생시킨다.
     *  * 3. 조회한 Users 객체, 리프레시 토큰, 만료 시각으로 RefreshToken 엔티티를 생성한다.
     *  * 4. 생성된 엔티티를 저장소에 저장한다.
     *  *
     *
     */
    private void addRefreshEntity(String userId, String refresh, Long expiredMs) {
        // 1. 만료 시간 계산 (LocalDateTime에 밀리초 더하기)
        LocalDateTime expiration = LocalDateTime.now().plus(expiredMs, ChronoUnit.MILLIS);

        // 2. userId로 Users 엔티티 조회 (userNo가 아니라 Users 객체로!)
        Users user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 빌더 패턴으로 RefreshToken 엔티티 생성
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)                  // Users 엔티티 객체
                .refresh(refresh)            // 리프레시 토큰 문자열
                .expiration(expiration)      // 만료 시간
                .build();

        // 4. 저장
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
