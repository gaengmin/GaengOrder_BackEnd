package tableOrder.analytics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tableOrder.analytics.dto.request.RequestAnalyticsDto;
import tableOrder.analytics.dto.response.ResponseAnalyticsDto;
import tableOrder.analytics.service.AdminAnalyticsService;


import java.util.List;

@Tag(name = "관리자 분석 API", description = "매출 집계, 메뉴별 판매량, 시간대 분석")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminAnalyticsController {
    private final AdminAnalyticsService adminAnalyticsService;

    @Operation(
            summary = "ADMIN 관리하는 일간(최대 30일) / 주간(기준일 기준 직전 12주) / 월간 매출 비교 (기준일 기준 직전 11개월)",
            description = "ordersItemsNo가 필요하고, 그 데이터를 가져오고, 거기서 취소하는 데이터 생각"
    )
    @SecurityRequirement(name = "Access")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/salesAnalytics")
    public ResponseEntity<List<ResponseAnalyticsDto.SalesDto>> get(@ModelAttribute RequestAnalyticsDto.salesDto salesDto){ //@ModelAttribute ->Setter로 받아야함 쿼리파미터를 DTO 객체에 자동 매핑 됨.
        System.out.println(salesDto.getPeriod() + " " + salesDto.getFrom() + " " + salesDto.getTo());
        List<ResponseAnalyticsDto.SalesDto> listSalesDto = adminAnalyticsService.getSalesAnalyticsData(salesDto);
        return ResponseEntity.ok(listSalesDto);
    }


}
