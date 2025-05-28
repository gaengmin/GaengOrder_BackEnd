package tableOrder.users.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.common.enums.SoftDelete;
import tableOrder.stores.entity.Stores;
import tableOrder.stores.mapper.StoresMapper;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.dto.response.ResponseUsersDto;
import tableOrder.users.entity.Users;
import tableOrder.users.mapper.UsersMapper;
import tableOrder.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService extends AbstractAuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //패스워드 인코더
    private final StoresMapper storesMapper;
    private final UsersMapper usersMapper;

    public UserService(CategoriesMapper categoriesMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, StoresMapper storesMapper, UsersMapper usersMapper) {
        super(categoriesMapper);
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.storesMapper = storesMapper;
        this.usersMapper = usersMapper;
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public void joinSuperAdminUsers(RequestUsersDto.requestAdminJoinDto adminJoinDto) {
        existValidateUserId(adminJoinDto.getUserId());
        //아이디 중복 검사

        Long storeNo = null;

        try {
            storeNo = storesMapper.findByStoreNo(adminJoinDto.getBusinessNo());
            if (storeNo == null) {
                throw new IllegalArgumentException("해당 사업자번호에 해당하는 매장이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("매장 정보 조회 중 오류가 발생했습니다.", e);
        }

        //매장별 ADMIN 계정 유일성 검사(사업자 번호에 따른 아이디가 하나인가?)
        if (adminJoinDto.getRole() == Role.ADMIN && userRepository.existsByRoleAndStoreNo(Role.ADMIN, storeNo)) {
            throw new IllegalArgumentException("이 매장에는 ADMIN 계정이 존재합니다.");
        }

        //중복검사 체크 후 비번 인코딩 하기
        String encodedPwd = passwordEncoder.encode(adminJoinDto.getPwd());

        //엔티티 생성 및 저장
        Users user = Users.builder()
                .userId(adminJoinDto.getUserId())
                .pwd(encodedPwd)
                .name(adminJoinDto.getName())
                .phoneNumber(adminJoinDto.getPhoneNumber())
                .storeNo(storeNo)
                .softDelete(SoftDelete.N)
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    public void joinAdminUsers(RequestUsersDto.@Valid requestOrdersJoinDto ordersJoinDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "직원 회원 가입");

        //아이디 중복 검사
        existValidateUserId(ordersJoinDto.getUserId());

        String encodedPwd = passwordEncoder.encode(ordersJoinDto.getPwd());

        //엔티티 생성 및 저장
        Users user = Users.builder()
                .userId(ordersJoinDto.getUserId())
                .pwd(encodedPwd)
                .name(ordersJoinDto.getName())
                .phoneNumber(ordersJoinDto.getPhoneNumber())
                .storeNo(userStoreNo)
                .softDelete(SoftDelete.N)
                .role(Role.ORDERS)
                .build();

        userRepository.save(user);

    }

    private void existValidateUserId(@NotBlank String userId) {
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'ORDERS')")
    public void softDeleteUsers() {
        String userId = SecurityUtil.getCurrentUserId();
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();

        if (!SecurityUtil.getCurrentUserRole().contains("SUPERADMIN")) {
            verifyStoreOwner(userStoreNo, userId, "정보 삭제");
        }
        Users users = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        if (users.getSoftDelete() == SoftDelete.N) {
            users.setSoftDelete(SoftDelete.Y);
        }
    }

    //정보수정
    @Transactional
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'ORDERS')")
    public void updateUsers(RequestUsersDto.updateUsersDto updateUsersDto) {
        String userId = SecurityUtil.getCurrentUserId();
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();

        if (!SecurityUtil.getCurrentUserRole().contains("SUPERADMIN")) {
            verifyStoreOwner(userStoreNo, userId, "정보 수정");
        }
        Users users = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
        String encodedPwd = null;

        if (updateUsersDto.getPwd() != null) {
            encodedPwd = passwordEncoder.encode(updateUsersDto.getPwd());
            users.setPwd(encodedPwd);
        }
        if (updateUsersDto.getPhoneNumber() != null) {
            users.setPhoneNumber(updateUsersDto.getPhoneNumber());
        }
    }


    /*매장 내 직원 조회*/
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ResponseUsersDto.StoresEmployeeDto> getUsersListData() {
        String userId = SecurityUtil.getCurrentUserId();
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        verifyStoreOwner(userStoreNo, userId, "정보 조회");

        List<ResponseUsersDto.UsersDto> usersList = usersMapper.getUserList(userStoreNo);

        return ResponseUsersDto.StoresEmployeeDto.from(usersList);
    }

    /*매장 내 직원 조회*/
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseUsersDto.UsersData getUsersData() {
        String userId = SecurityUtil.getCurrentUserId();
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        if (!SecurityUtil.getCurrentUserRole().contains("SUPERADMIN")) {
            verifyStoreOwner(userStoreNo, userId, "정보 조회");
        }
        ResponseUsersDto.UsersDto usersDto = usersMapper.getUserData(userId,userStoreNo);
        return ResponseUsersDto.UsersData.from(usersDto);
    }
}
