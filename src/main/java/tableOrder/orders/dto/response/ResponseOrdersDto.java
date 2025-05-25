package tableOrder.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

public class ResponseOrdersDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalNeedData{
        private Integer additionalOrder;
        private Long totalPrice;
        private String orderStatus;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class getOrdersDetailData{
        private Long orderNo;
        private String storesName; //
        private Long tableCode;
        private Integer totalPrice;
        private Timestamp ordersTime;
        private String ordersMenu;
        private Integer ordersQuantity;
        private Integer menuPrice;
    }
}
