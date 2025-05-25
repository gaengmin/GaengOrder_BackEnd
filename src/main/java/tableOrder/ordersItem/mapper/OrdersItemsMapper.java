package tableOrder.ordersItem.mapper;

import org.apache.ibatis.annotations.*;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.ordersItem.dto.response.ResponseOrderItemsDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersItemsMapper {
    void saveOrdersItems(@Param("orderNo") Long orderNo, @Param("orderItems") List<RequestOrdersDto.OrderItemDto> orderItems, @Param("addedOrder") Long addedOrder);

    List<ResponseOrderItemsDto.MenuQuantityDto> findMenuNamesAndQuantitiesByOrdersNo(@Param("orderNo") Long orderNo, @Param("menuNames") List<String> menuNames);

    //취소할 데이터 주문별 메뉴에 따른 수량 가져오기
    @Select("SELECT " +
            "orders_items_no as ordersItemsNo, menu_no as menuNo ,orders_no as ordersNo, menu_price as menuPrice, menu_name as menuName, quantity as quantity " +
            "FROM orders_items " +
            "WHERE orders_no = #{orderNo} " +
            "AND menu_name = #{menuName} " +
            "AND is_canceled = 'N' " +
            "AND is_free='N' " +
            "ORDER BY orders_items_no DESC")
    List<ResponseOrderItemsDto.GetCancelDataDto> findMenuItemsByOrdersNoAndMenuName(@Param("orderNo") Long orderNo, @Param("menuName") String menuName);

    //전체수량 변경 된 것
    @Update("update orders_items " +
            "set is_canceled = 'Y', " +
            "cancel_reason = #{cancelReason}, " +
            "cancel_at = now() " +
            "where orders_items_no = #{ordersItemsNo}")
    void updateAsCanceled(@Param("ordersItemsNo") Long ordersItemsNo, @Param("cancelReason") String cancelReason);

    //수량만 변경
    @Update("""
                UPDATE orders_items
                SET quantity = #{quantity},
                    cancel_reason = #{cancelReasonWithQty}
                WHERE orders_items_no = #{ordersItemsNo}
            """)
    void updateQuantity(
            @Param("ordersItemsNo") Long ordersItemsNo,
            @Param("quantity") int quantity,
            @Param("cancelReasonWithQty") String cancelReasonWithQty
    );

    @Insert("""
                INSERT INTO orders_items (
                    orders_no,
                    menu_no,
                    menu_name,
                    menu_price,
                    quantity,
                    cancel_reason,
                    is_canceled,
                    cancel_at
                ) VALUES (
                    #{ordersNo},
                    #{menuNo},
                    #{menuName},
                    #{menuPrice},
                    #{quantity},
                    #{cancelReason},
                    'Y',
                    now()
                )
            """)
    void insertCancelHistory(ResponseOrderItemsDto.InsertCancelDto insertCancelDto);
}
