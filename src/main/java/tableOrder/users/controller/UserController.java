package tableOrder.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.dto.response.ResponseUsersDto;
import tableOrder.users.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "회원 정보 관련 API", description = "매장 관련 조회,회원 가입 API, 회원 정보 수정(비번 수정, 휴대폰번호 수정, 회원 삭제 기능")
public class UserController {

    private final UserService userService;

    /**
     * 매장 직원 확인
     */
    @Operation(
            summary = "회원 목록 조회",
            description = "관리자(ADMIN)만 전체 회원 목록을 조회할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공 시 회원 목록 반환"),
                    @ApiResponse(responseCode = "403", description = "권한 부족")})
    @GetMapping("/users/list")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<List<ResponseUsersDto.StoresEmployeeDto>> getListUsers() {
        List<ResponseUsersDto.StoresEmployeeDto> listUsersData = userService.getUsersListData();
        return ResponseEntity.ok(listUsersData);
    }

    /**
     * 매장 직원 확인
     */
    @Operation(
            summary = "내 정보 조회",
            description = "내 정보 조회")
    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<ResponseUsersDto.UsersData> getUsers() {
        ResponseUsersDto.UsersData usersData = userService.getUsersData();
        return ResponseEntity.ok(usersData);
    }

    /**
     * 회원정보 삭제
     */
    @Operation(
            summary = "회원 삭제 (Soft Delete)",
            description = "로그인된 사용자를 soft-delete 처리합니다. 권한: SUPERADMIN, ADMIN, ORDERS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "접근 권한 없음")
            }
    )
    @PatchMapping("/softDeleteUsers")
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'ORDERS')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<?> softDeleteUsers() {

        userService.softDeleteUsers();

        return ResponseEntity.ok("회원 정보를 삭제 했습니다.");
    }


    /**
     * 회원정보 수정
     */
    @Operation(
            summary = "회원 정보 수정",
            description = "회원의 비밀번호, 휴대폰 번호 등 기본 정보 수정. 권한: SUPERADMIN, ADMIN, ORDERS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 완료"),
                    @ApiResponse(responseCode = "400", description = "입력값 오류"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @PatchMapping("/updateUsers")
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'ORDERS')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<?> updateUsers(@RequestBody @Valid RequestUsersDto.updateUsersDto updateUsersDto) {

        userService.updateUsers(updateUsersDto);

        return ResponseEntity.ok("회원 정보를 수정완료했습니다.");
    }


    /**
     * 회원가입 관련
     */
    @Operation(
            summary = "매장 관리자(ADMIN) 등록 (SUPERADMIN 권한)",
            description = "SUPERADMIN 권한을 가진 사용자가 매장관리자(ADMIN)을 신규 등록합니다. " +
                    "사용자 정보(아이디, 비밀번호, 매장번호 등)를 입력해야 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "403", description = "SUPERADMIN 권한 없음"),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패 등")
            }
    )
    @PostMapping("/superAdmin/join")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<?> joinSuperAdminUsers(@RequestBody @Valid RequestUsersDto.requestAdminJoinDto adminJoinDto) {

        log.info(adminJoinDto.getUserId());
        log.info(adminJoinDto.getBusinessNo());
        userService.joinSuperAdminUsers(adminJoinDto);

        return ResponseEntity.ok("ADMIN 계정 - 회원가입 성공");
    }

    @Operation(
            summary = "직원(ORDERS) 등록 (ADMIN 권한)",
            description = "ADMIN(매장 관리자) 권한을 가진 사용자가 매장 직원(ORDERS) 계정을 신규 등록합니다. " +
                    "사용자 정보(아이디, 비밀번호 등)를 입력해야 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "403", description = "ADMIN 권한 없음"),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패 등")
            }
    )
    @PostMapping("/orders/join")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<?> joinAdminUsers(@RequestBody @Valid RequestUsersDto.requestOrdersJoinDto ordersJoinDto) {

        log.info(ordersJoinDto.getUserId());
        userService.joinAdminUsers(ordersJoinDto);

        return ResponseEntity.ok("Orders 계정 - 회원가입 성공");
    }


}
