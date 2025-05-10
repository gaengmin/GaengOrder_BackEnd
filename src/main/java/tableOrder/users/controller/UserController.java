package tableOrder.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.dto.response.ResponseUsersDto;
import tableOrder.users.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/admin")
    public String adminTest() {

        return "admin";
    }
    /**
     * 시스템 관리자가 매장관리자 회원가입해주기
     * */
    @PostMapping("/superAdmin/users")
    public ResponseEntity<?> joinUsers(@RequestBody @Valid RequestUsersDto.requestAdminJoinDto adminJoinDto) {

        log.info(adminJoinDto.getUserId());
        userService.joinUsers(adminJoinDto);

        return ResponseEntity.ok("회원가입 성공");
    }
}
