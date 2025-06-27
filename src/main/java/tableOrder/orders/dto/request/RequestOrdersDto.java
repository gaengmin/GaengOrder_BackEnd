package tableOrder.orders.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.ordersStatusLog.dto.request.RequestOrdersStatusLogDto;

import java.util.List;

public class RequestOrdersDto {

    //주문 취소
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CancelOrderDto {
        private String cancelReason;
    }


    //사용자가 요청하는 데이터 값
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalMenuDto {
        @NotEmpty(message = "주문 항목은 최소 한 개 이상 포함되어야 합니다.")
        @Valid
        private List<OrderItemDto> orderItems;
    }

    //사용자가 요청하는 데이터 값
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateOrderDto {
        @NotNull(message = "테이블 번호는 필수입니다.")
        private Long tableNo;
        @NotNull(message = "사업장 번호는 필수입니다")
        private Long storeNo;
        @NotEmpty(message = "주문 항목은 최소 한 개 이상 포함되어야 합니다.")
        @Valid
        private List<OrderItemDto> orderItems;
    }

    //서비스단에서 데이터 처리하는 것
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateOrderDtoWithTotalPrice {
        private Long orderNo;
        private Long tableNo;
        private Long storeNo;
        private Long totalPrice;

        public static CreateOrderDtoWithTotalPrice of(Long tableNo, Long storeNo, Long totalPrice) {
            return CreateOrderDtoWithTotalPrice.builder()
                    .tableNo(tableNo)
                    .storeNo(storeNo)
                    .totalPrice(totalPrice)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalUpdateMenuDto {
        private Long orderNo;
        private Long totalPrice;
        private String orderStatus;
        private String reason;
        private int additionalOrder;

        public static AdditionalUpdateMenuDto of(Long orderNo, Long totalPrice, String orderStatus, String reason, int additionalOrder) {
            return AdditionalUpdateMenuDto.builder()
                    .orderNo(orderNo)
                    .totalPrice(totalPrice)
                    .orderStatus(orderStatus)
                    .reason(reason)
                    .additionalOrder(additionalOrder)
                    .build();
        }
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
        private char isFree = 'N';          // 서비스 제공 여부
        private char isCanceled = 'N';      // 취소 여부
        private String cancelReason;     // 취소 사유
    }
}
