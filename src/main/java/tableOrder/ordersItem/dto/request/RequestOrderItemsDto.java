package tableOrder.ordersItem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

public class RequestOrderItemsDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrdersMenuCancelDto {
        private List<MenuCancelRequestDto> cancelRequestsDto;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MenuCancelRequestDto {
            @NotBlank(message = "메뉴명은 필수입니다")
            private String menuName;
            @NotNull(message = "취소 수량은 필수입니다.")
            @Min(value = 1, message = "취소 수량은 1개 이상")
            private Integer cancelQuantity;
            @NotBlank(message = "취소 사유는 필수")
            private String cancelReason;
        }
    }
}
