package tableOrder.analytics.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tableOrder.analytics.dto.request.RequestAnalyticsDto;
import tableOrder.analytics.dto.response.ResponseAnalyticsDto;
import tableOrder.analytics.service.AdminAnalyticsService;

import java.util.List;

@Tag(name = "관리자 분석 API", description = "매출 집계, 메뉴별 판매량, 시간대 분석")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminAnalyticsController {
    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/salesAnalytics")
    public ResponseEntity<List<ResponseAnalyticsDto.SalesDto>> get(@ModelAttribute RequestAnalyticsDto.salesDto salesDto){ //@ModelAttribute ->Setter로 받아야함 쿼리파미터를 DTO 객체에 자동 매핑 됨.
        System.out.println(salesDto.getPeriod() + " " + salesDto.getFrom() + " " + salesDto.getTo());
        List<ResponseAnalyticsDto.SalesDto> listSalesDto = adminAnalyticsService.getSalesAnalyticsData(salesDto);
        return ResponseEntity.ok(listSalesDto);
    }
}
