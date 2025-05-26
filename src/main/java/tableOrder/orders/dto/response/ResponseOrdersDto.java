package tableOrder.orders.dto.response;

import lombok.*;

import java.sql.Timestamp;

public class ResponseOrdersDto {
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
    public static class OrderBaseDto {
        private Long orderNo;
        private String storeName;
        private String tableCode;
        private Timestamp orderTime;
        private String orderStatus;
        private Integer totalPrice;
        private java.util.List<MenuLine> menuList;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class MenuLine {
            private Long orderItemNo;
            private Long menuNo;
            private String menuName;
            private Integer unitPrice;
            private Integer quantity;
            private Integer lineTotal;
            private Boolean isCanceled;
            private String menuCategory;
            private String cancelReason;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ReceiptDto extends OrderBaseDto {
        // Receipt 만의 특별 필드가 있으면 정의 (없으면 생략)
        @Builder
        public ReceiptDto(Long orderNo, String storeName, Long tableCode, String tableName,
                          Timestamp orderTime, String orderStatus, Integer totalPrice,
                          java.util.List<MenuLine> menuList) {
            super(orderNo, storeName, tableCode, tableName, orderTime, orderStatus, totalPrice, menuList);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderDetailDto extends OrderBaseDto {
        private String cancelReason; // 전체주문 취소 사유

        @Builder
        public OrderDetailDto(Long orderNo, String storeName, Long tableCode, String tableName,
                              Timestamp orderTime, String orderStatus, Integer totalPrice,
                              java.util.List<MenuLine> menuList, String cancelReason) {
            super(orderNo, storeName, tableCode, tableName, orderTime, orderStatus, totalPrice, menuList);
            this.cancelReason = cancelReason;
        }
    }
}
