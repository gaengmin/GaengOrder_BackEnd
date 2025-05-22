package tableOrder.ordersStatusLog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import tableOrder.ordersStatusLog.dto.request.RequestOrdersStatusLogDto;

@Mapper
public interface OrdersStatusLogMapper {
    @Insert("INSERT INTO orders_status_log " +
            "(order_no, previous_status, new_status, user_id) " +
            "VALUES (#{orderNo}, #{previousStatus}, #{newStatus}, #{userId})")
    void saveLogData(RequestOrdersStatusLogDto logDto);
}
