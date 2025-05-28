package tableOrder.stores.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tableOrder.common.enums.SoftDelete;

import java.time.LocalDateTime;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Stores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeNo;
    @Column(length = 20)
    private String businessNo;
    @Column(length = 20)
    private String storeName;
    @Column(length = 20)
    private String storeTel;
    @Column(length = 200)
    private String storeAddress;
    @Enumerated(EnumType.STRING)
    private SoftDelete softDelete;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDt;

    @LastModifiedDate
    private LocalDateTime updateDt;
}
