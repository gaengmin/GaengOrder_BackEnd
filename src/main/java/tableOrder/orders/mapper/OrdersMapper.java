package tableOrder.orders.mapper;

import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.orders.dto.request.RequestOrdersDto.CreateOrderDtoWithTotalPrice;
import tableOrder.orders.dto.response.ResponseOrdersDto;

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

    //ORDERS 권한을 가지고 사용자가 삭제함.
    @Update("update orders " +
            "set order_status = #{status}, reason = #{cancelReason} " +
            "where orders_no = #{orderNo}")
    void cancelOrdersStatus(@Param("status") String status, @Param("orderNo") Long orderNo, @Param("cancelReason") String cancelReason);

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

}
