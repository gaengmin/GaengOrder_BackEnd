package tableOrder.security.jwt;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.dto.security.CustomUserDetails;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    //JWTUtil을 주입해줘야함
    private final JWTUtil jwtUtil;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //1. json 바디를 읽어서 requestLoginDto로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            RequestUsersDto.requestLoginDto loginDto = objectMapper.readValue(request.getInputStream(), RequestUsersDto.requestLoginDto.class);

            String userId = loginDto.getUserId();
            String pwd = loginDto.getPwd();

            System.out.println("이거 테스트 userId: " + userId +  " pwd: " + pwd);

            //인증 토큰 새성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, pwd, null);

            //3. 인증 처리
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패");
        }



       /* //클라이언트 요청에서 userId, pwd 추출
        String userId = request.getParameter("userId");
        String pwd = request.getParameter("pwd");


        System.out.println(userId + "   " + pwd);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, pwd, null);

        //스프링 시큐리티에서 userid와 password 검증하기 위한 토큰
        return authenticationManager.authenticate(authToken); //검증 담당*/
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }

    //로그인 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {


        // 인증된 사용자 정보를 CustomUserDetails 타입으로 변환
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        // 사용자 ID(여기서는 username)를 가져옴
        String userId = customUserDetails.getUsername();

        // 사용자의 권한 목록을 가져옴
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        // 권한 목록의 반복자 생성
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();

        // 첫 번째 권한을 가져옴 (여러 권한이 있을 경우 첫 번째만 사용)
        GrantedAuthority auth = iterator.next();

        // 권한 이름(ROLE_XXX 등)을 추출
        String role = auth.getAuthority();
        // JWT 토큰 생성 (userId, role, 만료시간(밀리초 단위))
        String token = jwtUtil.createJwt(userId, role, 60 * 60 * 100L);

        // 응답 헤더에 JWT 토큰을 추가 ("Authorization" : "Bearer {token}")
        response.addHeader("Authorization", "Bearer " + token);
    }
}
