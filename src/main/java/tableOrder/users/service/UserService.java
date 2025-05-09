package tableOrder.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tableOrder.common.enums.SoftDelete;
import tableOrder.stores.mapper.StoresMapper;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.entity.Users;
import tableOrder.users.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //패스워드 인코더
    private final StoresMapper storesMapper;

    public void joinUsers(RequestUsersDto.requestAdminJoinDto adminJoinDto) {
        //아이디 중복 검사
        if (userRepository.existsByUserId(adminJoinDto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
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
                .role(Role.ADMIN)
                .storeNo(storeNo)
                .softDelete(SoftDelete.N)
                .build();

        userRepository.save(user);
    }
}
