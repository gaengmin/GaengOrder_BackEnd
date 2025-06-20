package tableOrder.analytics.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ResponseAnalyticsDto {

    /**지난 한 주간 판매량*/
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesMenuDto{
        private String period; //기간
        private String menuName;
        private String categoryName;
        private Long orderCount;
        private Integer freeCount;
        private Long amount;
        private Integer rank;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesDto{
        private String period;
        private Integer orderCount;
        private Long totalPrice;
        private Long discountPrice;
        private Long finalPrice;
    }


}
