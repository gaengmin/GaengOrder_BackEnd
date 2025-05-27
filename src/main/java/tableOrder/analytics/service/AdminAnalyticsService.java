package tableOrder.analytics.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tableOrder.analytics.dto.enums.TimePeriodType;
import tableOrder.analytics.dto.request.RequestAnalyticsDto;
import tableOrder.analytics.dto.response.ResponseAnalyticsDto;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.orders.mapper.OrdersMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AdminAnalyticsService extends AbstractAuthValidator {

    private final OrdersMapper ordersMapper;

    public AdminAnalyticsService(CategoriesMapper categoriesMapper, OrdersMapper ordersMapper) {
        super(categoriesMapper);
        this.ordersMapper = ordersMapper;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ResponseAnalyticsDto.SalesDto> getSalesAnalyticsData(RequestAnalyticsDto.salesDto salesDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "관리자 페이지 기능");

        List<ResponseAnalyticsDto.SalesDto> sales;
        //지난 일주일 간 기준일 기준 당일 매출액
        switch (salesDto.getPeriod()) {
            case MONTHLY:
                validateDateCheck(salesDto.getFrom());
                LocalDate baseMonth = salesDto.getFrom().withDayOfMonth(1); // 기준월의 1일
                LocalDate startMonth = baseMonth.minusMonths(11); // 12개월 전 1일
                sales = ordersMapper.getMonthlyData(baseMonth, startMonth, userStoreNo);
                break;
            case WEEKLY:
                //체크
                validateDateCheck(salesDto.getFrom());
                LocalDate weekStart = salesDto.getFrom().minusWeeks(11).with(DayOfWeek.SUNDAY);    // 12주 전 일요일
                LocalDate weekEnd = salesDto.getFrom().with(DayOfWeek.SATURDAY);                   // 기준일 속한 주의 토요일
                sales = ordersMapper.getWeeklyData(weekStart, weekEnd, userStoreNo);
                break;
            case DAILY:
                validateDateCheck(salesDto.getFrom(), salesDto.getTo());
                sales = ordersMapper.getDailyData(salesDto.getTo(), salesDto.getFrom(), userStoreNo);
                break;
            default:
                throw new IllegalArgumentException("제공되지 않는 기간입니다.");
        }

        return sales;
    }

    private void validateDateCheck(LocalDate from) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        if (from.isAfter(tomorrow)) {
            throw new IllegalArgumentException("시작 날짜 날짜 범위가 유효하지 않습니다.");
        }
    }

    private void validateDateCheck(LocalDate from, LocalDate to) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        long days = ChronoUnit.DAYS.between(from, to);


        if (from.isAfter(tomorrow)) {
            throw new IllegalArgumentException("시작 날짜 날짜 범위가 유효하지 않습니다.");
        }
        if (to.isAfter(tomorrow)) {
            throw new IllegalArgumentException("종료 날짜 날짜 범위가 유효하지 않습니다.");

        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("종료 날짜가 시작 날짜보다 앞서있습니다.");
        }

        if (days > 30) {
            throw new IllegalArgumentException("일간 조회는 최대 30일까지 가능합니다.");
        }
    }
}
