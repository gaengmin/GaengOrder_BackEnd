package tableOrder.ordersItem.mapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tableOrder.orders.dto.request.RequestOrdersDto;

import java.util.List;

@Mapper
public interface OrdersItemsMapper {
    void saveOrdersItems(@Param("orderNo") Long orderNo, @Param("orderItems") List<RequestOrdersDto.OrderItemDto> orderItems);
}
