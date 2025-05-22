package tableOrder.orders.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RequestOrdersDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateOrderDto{
        @NotNull(message = "테이블 번호는 필수입니다.")
        private Long tableNo;
        @NotEmpty(message = "주문 항목은 최소 한 개 이상 포함되어야 합니다.")
        @Valid
        private List<OrderItemDto> orderItems;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDto {
        private Long menuNo;
        private String menuName;         // 메뉴명
        private Long menuPrice;          // 단가
        private Integer quantity;        // 수량
        private Boolean isFree =false;          // 서비스 제공 여부
        private Boolean isCanceled =false ;      // 취소 여부
        private String cancelReason;     // 취소 사유
    }
}
