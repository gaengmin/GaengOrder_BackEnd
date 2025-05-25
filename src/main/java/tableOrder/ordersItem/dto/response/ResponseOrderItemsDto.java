package tableOrder.ordersItem.dto.response;

import lombok.*;

import java.time.LocalDateTime;

public class ResponseOrderItemsDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MenuQuantityDto {
        private String menuName;
        private Integer quantity;
    }

    //부분 취소 관련 Query를 통해 얻어오는 Dto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetCancelDataDto {
        private Long ordersItemsNo;
        private Long menuNo;
        private Long ordersNo;
        private Integer menuPrice;
        private String menuName;
        private Integer quantity;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsertCancelDto {
        private Long ordersNo;
        private Long menuNo;
        private String menuName;
        private Integer menuPrice;
        private Integer quantity;
        private String cancelReason;
        private LocalDateTime cancelAt;

        public static InsertCancelDto of(
                Long ordersNo,
                Long menuNo,
                String menuName,
                Integer menuPrice,
                Integer quantity,
                String cancelReason
        ) {
            return InsertCancelDto.builder()
                    .ordersNo(ordersNo)
                    .menuNo(menuNo)
                    .menuName(menuName)
                    .menuPrice(menuPrice)
                    .quantity(quantity)
                    .cancelReason(cancelReason)
                    .build();
        }

    }
}
