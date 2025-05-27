package tableOrder.analytics.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ResponseAnalyticsDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesDto{
        private String period;
        private Integer orderCount;
        private Integer totalPrice;
        private Integer discountPrice;
        private Integer finalPrice;
    }

}
