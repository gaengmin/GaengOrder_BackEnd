package tableOrder.orders.mapper;

import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import tableOrder.analytics.dto.response.ResponseAnalyticsDto;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.orders.dto.request.RequestOrdersDto.CreateOrderDtoWithTotalPrice;
import tableOrder.orders.dto.response.ResponseOrdersDto;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrdersMapper {
    @Insert("insert into orders (table_no, store_no, total_price) values (#{tableNo}, #{storeNo}, #{totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "orderNo")
    void saveOrders(RequestOrdersDto.CreateOrderDtoWithTotalPrice createOrderDto);

    //주문 상태 조히
    @Select("select order_status from orders where orders_no = #{orderNo}")
    String getOrderStatusByOrderNo(@Param("orderNo") Long orderNo);

    //주문 존재여부
    @Select("select count(*) from orders where orders_no = #{orderNo}")
    int existOrdersByOrderNo(Long orderNo);

    //테이블의 주문상태 확인
    @Select("select order_status " +
            "from orders o " +
            "inner join tables t " +
            "on o.table_no = t.table_no " +
            "where t.soft_delete = 'N' " +
            "and o.table_no = #{tableNo} " +
            "order by orders_no desc LIMIT 1")
    String checkOrdersStatusByTableNo(@Param("tableNo") Long tableNo);

    //ORDERS 권한을 가지고 사용자가 수정함.
    @Update("update orders " +
            "set order_status = #{nextStatus}" +
            "where orders_no = #{orderNo}")
    void updateOrdersStatus(@Param("nextStatus") String nextStatus, @Param("orderNo") Long orderNo);

    //ORDERS 권한을 가지고 사용자가 삭제함. 젠체 취소시 상태뿐 아닌 금액도 0원 처리
    @Update("""
            UPDATE orders
            SET
                orders_status = #{orderStatus},
                cancel_reason = #{cancelReason},
                total_price = 0,
                update_dt = NOW()
            WHERE orders_no = #{orderNo}
            """)
    void cancelOrderAndResetAmount(
            @Param("orderStatus") String orderStatus,
            @Param("orderNo") Long orderNo,
            @Param("cancelReason") String cancelReason
    );
    @Select("SELECT additional_order as additionalOrder, total_price as totalPrice, order_status as orderStatus FROM ORDERS " +
            "WHERE ORDERS_NO = #{orderNo}")
    ResponseOrdersDto.AdditionalNeedData getTotalPriceByOrdersNo(@Param("orderNo") Long orderNo);

    //추가 메뉴에 대한 데이터 넣고 업데이트
    @Update("UPDATE orders SET " +
            "total_price = #{totalPrice}, " +
            "order_status = #{orderStatus}, " +
            "reason = #{reason}, " +
            "additional_order = #{additionalOrder} " +
            "WHERE orders_no = #{orderNo}")
    void updateAdditionMenu(RequestOrdersDto.AdditionalUpdateMenuDto addMenuDto);

    @Select("select count(*) from orders where orders_no = #{orderNo} and store_no = #{storeNo}")
    int existOrdersByOrderNoAndStoreNo(@Param("orderNo") Long orderNo, @Param("storeNo") Long userStoreNo);

    //주문 부분 취소시 금액 변경
    @Update("""
                UPDATE orders
                SET 
                    total_price = total_price - #{totalCancelAmount}
                WHERE orders_no = #{orderNo}
            """)
    void updateOrderAmount(
            @Param("orderNo") Long orderNo,
            @Param("totalCancelAmount") int totalCancelAmount
    );

    //주문데이터 조회
    ResponseOrdersDto.ReceiptDto getReceiptData(@Param("ordersNo") Long ordersNo,@Param("storeNo") Long storeNo);

    ResponseOrdersDto.OrderDetailDto getOrdersDetailsData(Long ordersNo);

    /*일간 데이터*/
    List<ResponseAnalyticsDto.SalesDto> getDailyData(@Param("to")LocalDate to,@Param("from") LocalDate from, @Param("storeNo") Long storedNo);

    /*주간 데이터*/
    List<ResponseAnalyticsDto.SalesDto> getWeeklyData(@Param("weekStart")LocalDate weekStart, @Param("weekEnd")LocalDate weekEnd, @Param("storeNo") Long storedNo);

    /*월간 데이터*/
    List<ResponseAnalyticsDto.SalesDto> getMonthlyData(@Param("baseMonth")LocalDate baseMonth, @Param("startMonth")LocalDate startMonth, @Param("storeNo") Long storedNo);
}
