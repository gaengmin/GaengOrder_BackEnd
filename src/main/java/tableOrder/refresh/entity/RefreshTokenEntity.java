package tableOrder.refresh.entity;

import jakarta.persistence.*;
import lombok.*;
import tableOrder.users.entity.Users;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_no")
    private Long refreshNo; // 리프레시 토큰 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private Users user; // 유저 엔티티와 외래키 연결

    @Column(name = "refresh", nullable = false, length = 255)
    private String refresh; // 리프레시 토큰 값

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration; // 만료 일시

    @Column(name = "device_info", length = 100)
    private String deviceInfo; // (선택) 기기 정보

    // 필요하다면 토큰 갱신 메서드 등 추가 가능
    public void updateToken(String newRefresh, LocalDateTime newExpiration) {
        this.refresh = newRefresh;
        this.expiration = newExpiration;
    }
}
