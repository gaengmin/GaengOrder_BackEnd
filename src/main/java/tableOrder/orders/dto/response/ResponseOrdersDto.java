package tableOrder.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ResponseOrdersDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalNeedData{
        private Integer additionalOrder;
        private Long totalPrice;
        private String orderStatus;
    }
}
