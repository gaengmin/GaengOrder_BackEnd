package tableOrder.analytics.dto.request;

import lombok.*;
import tableOrder.analytics.dto.enums.TimePeriodType;

import java.time.LocalDate;

public class RequestAnalyticsDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class salesDto{
        private TimePeriodType period;
        private LocalDate from;
        private LocalDate to;
    }
}
