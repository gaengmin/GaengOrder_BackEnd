package tableOrder.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tableOrder.common.enums.SoftDelete;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.dto.request.RequestUsersDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo; // user_no

    @Column(nullable = false, unique = true, length = 100)
    private String userId; // 아이디

    @Column(nullable = false, length = 100)
    private String pwd; // 비밀번호

    @Column(length = 20)
    private String name; // 이름

    @Column(length = 20)
    private String phoneNumber; // 전화번호

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private Role role; // 역할 (ENUM: SUPERADMIN, ADMIN, ORDERS 등)

    @Column
    private Long storeNo; // 매장번호 (nullable)

    @CreatedDate
    private LocalDateTime createDt; // 생성일

    @LastModifiedDate
    private LocalDateTime updateDt; // 수정일

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(1) default 'N'")
    private SoftDelete softDelete = SoftDelete.N; // 소프트 삭제 여부

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setSoftDelete(SoftDelete softDelete) {
        this.softDelete = softDelete;
    }

    public static Users createAdminUser(RequestUsersDto.requestAdminJoinDto adminJoinDto, String encodedPwd, Long userStoreNo) {
        return Users.builder()
                .userId(adminJoinDto.getUserId())
                .pwd(encodedPwd)
                .name(adminJoinDto.getName())
                .phoneNumber(adminJoinDto.getPhoneNumber())
                .storeNo(userStoreNo)
                .softDelete(SoftDelete.N)
                .role(Role.ADMIN)
                .build();
    }

    public static Users createOrdersUser(RequestUsersDto.requestOrdersJoinDto ordersJoinDto, String encodedPwd, Long userStoreNo) {
        return Users.builder()
                .userId(ordersJoinDto.getUserId())
                .pwd(encodedPwd)
                .name(ordersJoinDto.getName())
                .phoneNumber(ordersJoinDto.getPhoneNumber())
                .storeNo(userStoreNo)
                .softDelete(SoftDelete.N)
                .role(Role.ORDERS)
                .build();
    }
}