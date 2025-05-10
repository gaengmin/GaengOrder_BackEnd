package tableOrder.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tableOrder.security.jwt.JWTFilter;
import tableOrder.security.jwt.JWTUtil;
import tableOrder.security.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    //JWT UTIL 주입
    private final JWTUtil jwtUtil;

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
     * SUPERADMIN 전용 보안 필터 체인
     * - /api/superAdmin/** 경로에만 적용
     * - 로그인 경로: /api/superAdmin/login (permitAll)
     * - 나머지 경로: SUPERADMIN 권한 필요
     * - JWT 기반 REST API에 맞는 공통 보안 정책 적용
     */
    // SUPERADMIN 전용 필터 체인
    @Bean
    @Order(1)
    public SecurityFilterChain superAdminFilterChain(HttpSecurity http) throws Exception {
        LoginFilter superAdminLoginFilter = new LoginFilter(authenticationManager(), jwtUtil); // Bean 주입
        superAdminLoginFilter.setFilterProcessesUrl("/api/superAdmin/login"); // 원하는 로그인 경로 지정
        http.securityMatcher("/api/superAdmin/**"); // 경로 지정 필수

        // ======= 공통 보안 정책 적용 =======
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // ======= 공통 보안 정책 적용 =======
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/superAdmin/login").permitAll() // 로그인 허용
                .anyRequest().hasAuthority("SUPERADMIN"));
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(superAdminLoginFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // ADMIN 전용 필터 체인
    @Bean
    @Order(2)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        LoginFilter adMinLoginFilter = new LoginFilter(authenticationManager(), jwtUtil);
        adMinLoginFilter.setFilterProcessesUrl("/api/admin/login"); // 원하는 로그인 경로 지정
        http.securityMatcher("/api/admin/**"); // 경로 지정 필수
        // ======= 공통 보안 정책 적용 =======
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // ==================================
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/login").permitAll()
                .anyRequest().hasAuthority("ADMIN")
        );
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(adMinLoginFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 공용 필터 체인 (예: ORDERS)
    @Bean
    @Order(3)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        // ======= 공통 보안 정책 적용 =======
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // ==================================
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/orders/**").hasAnyAuthority("ADMIN", "ORDERS")
                .anyRequest().permitAll()
        );
        return http.build();
    }

}
