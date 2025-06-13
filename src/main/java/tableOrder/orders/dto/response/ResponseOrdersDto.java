package tableOrder.orders.dto.response;

import lombok.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class ResponseOrdersDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderStatusWithUpdateAtDto {
        private String status;
        private LocalDateTime updateAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalNeedData {
        private Integer additionalOrder;
        private Long totalPrice;
        private String orderStatus;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReceiptDto {
        private Long orderNo;
        private String storeName;
        private String tableCode;
        private Integer totalPrice;
        private Integer discountPrice;
        private Integer finalPrice;
        private LocalDateTime orderTime; // MySQL의 TIMESTAMP는 LocalDateTime으로 변환 가능
        private List<MenuLine> menuList;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class MenuLine {
            private String menuName;
            private Integer menuPrice;
            private Integer quantity;
            private Integer menuLineTotal;
            private Boolean isCanceled;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderDetailDto {

    }

}
