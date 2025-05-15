package tableOrder.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import tableOrder.auth.filter.CustomLogoutFilter;
import tableOrder.refresh.repository.RefreshTokenRepository;
import tableOrder.auth.filter.JWTFilter;
import tableOrder.auth.util.JWTUtil;
import tableOrder.auth.filter.LoginFilter;
import tableOrder.users.repository.UserRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    //JWT UTIL 주입
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /**
     * AuthenticationManager Bean 등록
     * - Spring Security 인증 처리의 핵심 객체
     * - 커스텀 LoginFilter에서 사용
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     *
     */
    // 필터 체인
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LoginFilter loginFilter = new LoginFilter(authenticationManager(), jwtUtil, userRepository, refreshTokenRepository); // Bean 주입
        loginFilter.setFilterProcessesUrl("/api/login"); // 원하는 로그인 경로 지정
        http.securityMatcher("/api/**");

        // ======= 공통 보안 정책 적용 =======
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /*
         *
         * hasAuthority("ADMIN") → 권한 문자열이 "ADMIN"이어야 합니다.
         *  hasRole("ADMIN") → 실제 권한은 "ROLE_ADMIN"으로 저장되어야 합니다.
         * */
        // ======= 공통 보안 정책 적용 =======
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/json/all-apis").permitAll() // ← Swagger 문서 경로 허용
                        .requestMatchers("/api/login").permitAll() // 1.로그인 허용(모든 사용자 접근 가능)
                        .requestMatchers("/api/auth/reissue").permitAll() // 2. 토큰 재발급 허용
                        .requestMatchers("/api/logout").authenticated() // 3. 로그아웃은 인증된 사용자만
                        .requestMatchers("/api/superAdmin/**").hasAuthority("SUPERADMIN") // 4. SUPERADMIN 권한 필요
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN") // 5. ADMIN 권한 필요
                        .requestMatchers("/api/orders/**").hasAnyAuthority("ORDERS", "ADMIN") // 6. ORDERS 또는 ADMIN 권한 필요
                        .requestMatchers("/api/categories/**").hasAuthority("ADMIN") // 그 외 메서드는 ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/menus/**").permitAll() // GET은 모든 사용자 허용
                        .requestMatchers("/api/menus/**").hasAnyRole("ADMIN", "ORDERS")
//                .requestMatchers("/api/categories/**").hasAnyAuthority("ADMIN") // 7. ADMIN 권한 필요
                        .anyRequest().authenticated() // 이외의 경로는 권한이 필요함.
        );

        /**
         * 1. addFilterBefore(Filter, Class) : 기존 필터 앞에 추가
         * 2. addFilterAt(Filter, Class)` 기존 필터의 **위치에** 커스텀 필터를 대체합니다.
         * */
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);

        return http.build();
    }


}
