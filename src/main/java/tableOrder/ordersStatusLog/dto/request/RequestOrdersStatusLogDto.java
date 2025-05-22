package tableOrder.ordersStatusLog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import tableOrder.orders.dto.enums.OrdersStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrdersStatusLogDto {
    private String previousStatus;
    private String newStatus;
    private String userId;
    private Long orderNo;

    public static RequestOrdersStatusLogDto of(Long orderNo, OrdersStatusEnum previousStatus, OrdersStatusEnum newStatus, String userId) {
        return RequestOrdersStatusLogDto.builder()
                .orderNo(orderNo)
                .previousStatus(previousStatus.name())
                .newStatus(newStatus.name())
                .userId(userId)
                .build();
    }
}
