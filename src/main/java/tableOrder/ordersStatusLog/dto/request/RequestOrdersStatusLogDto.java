package tableOrder.ordersStatusLog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RequestOrdersStatusLogDto {
    @NotBlank(message = "이전 상태는 필수 입력값입니다.")
    private String previousStatus;
    @NotBlank(message = "새 상태는 필수 입력값입니다.")
    private String newStatus;
    @NotBlank(message = "변경한 사용자의 번호는 필수 입력값입니다.")
    private Long userNo;
    private Long orderNo;

}
