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

        // ======= 공통 보안 정책 적용 =======
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login").permitAll() // 로그인 허용
                .requestMatchers("/api/auth/reissue").permitAll()
                .requestMatchers("/api/logout").authenticated()
                .requestMatchers("/api/superAdmin/**").hasAuthority("SUPERADMIN")
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/orders/**").hasAnyAuthority("ORDERS", "ADMIN")
                .anyRequest().authenticated());
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
