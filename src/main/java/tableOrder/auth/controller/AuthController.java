package tableOrder.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tableOrder.auth.dto.response.ResponseTokenDto;

import static tableOrder.common.utils.CookieUtil.createCookie;
import static tableOrder.common.utils.CookieUtil.extractRefreshToken;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login/Logout API", description = "필터단에서 작동하기에, 이것은 가짜 컨틀로러 입니다..")
@RequestMapping("/api")
public class AuthController {


    @Operation(
            summary = "Login",
            description = """
                    이 엔드포인트는 Swagger 문서를 위한 용도로만 존재합니다.
                    실제 JWT 인증, 토큰 발급 과정은 Security Filter에서 처리되며,
                    클라이언트는 `/api/login`으로 POST 요청을 보내 인증을 시도해야 합니다.
                    성공 시 AccessToken과 RefreshToken이 응답으로 전달됩니다.
                    """
    )
    @PostMapping("/login")
    public String login() {

        return "login";
    }
    @Operation(
            summary = "Logout",
            description = """
                    이 엔드포인트는 Swagger 문서를 위한 용도로만 존재합니다.
                    실제 로그아웃 처리는 SecurityFilter나 별도의 쿠키/토큰 삭제 로직에서 수행됩니다.
                    클라이언트는 `/api/logout`으로 POST 요청을 보내 토큰 삭제를 요청할 수 있습니다
                  """
    )
    @PostMapping("/logout")
    public String logout() {

        return "logout";
    }


}

