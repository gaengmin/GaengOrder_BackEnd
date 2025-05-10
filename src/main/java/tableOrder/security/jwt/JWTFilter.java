package tableOrder.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.dto.security.CustomUserDetails;
import tableOrder.users.entity.Users;

import java.io.IOException;


@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");

            filterChain.doFilter(request, response);

            return;
        }

        System.out.println("authorization now");

        String token = authorization.substring(7); // "Bearer " 다음부터 끝까지 추출

        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return;
        }


        String userId = jwtUtil.getUserId(token);
        Role role = Role.valueOf(jwtUtil.getRole(token));

        Users users = Users.builder()
                .userId(userId)
                .pwd("temppassword")
                .role(role)                 // role 값 설정
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(users);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
