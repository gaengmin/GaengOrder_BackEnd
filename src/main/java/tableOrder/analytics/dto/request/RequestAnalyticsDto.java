package tableOrder.analytics.dto.request;

import lombok.*;
import tableOrder.analytics.dto.enums.TimePeriodType;

import java.time.LocalDate;

public class RequestAnalyticsDto {


    /**
     * 기간별 매출 분석
     *  DAILY,
     *  WEEKLY,
     *  MONTHLY
     * */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class salesDto{
        private LocalDate from;
        private LocalDate to;
        private TimePeriodType period;
    }


    /**오늘날짜 기준 지난 주 Top 5 판매량*/
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesTop5Dto {
        private Integer topN;
    }
}
