package tableOrder.tables.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tableOrder.common.enums.SoftDelete;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "tables")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Tables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="table_no")
    private Long tableNo;

    @NotBlank
    @Column(name = "store_no")
    private Long storeNo;
    @NotBlank(message = "필수 입력 사항입니다.")
    @Column(name="table_code")
    private String tableCode;
    @Enumerated(EnumType.STRING)
    @Column(name="soft_delete")
    private SoftDelete softDelete = SoftDelete.N;
    @CreatedDate
    @Column(name="create_dt", updatable = false)
    private LocalDateTime createDt;
    @LastModifiedDate
    @Column(name="update_dt")
    private LocalDateTime updateDt;


    public void changeTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public void changeSoftDelete(SoftDelete softDelete) {
        this.softDelete = softDelete;
    }
}